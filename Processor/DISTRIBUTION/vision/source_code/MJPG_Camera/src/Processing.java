import java.awt.Canvas;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;

public class Processing {

	private static int portNumber, cameraPort;
	private static Envelope e;
	private static Scalar lowerHSV, upperHSV;
	private static boolean runServer;
	private static VideoCapture camera;
	private static Window window;
	private static Canvas status;
	private static Rectangle bounds;
	private static boolean sending;
	
    public static void main(String[] args) throws IOException{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        //Define envelope sending system and declare boolean to enable sending
        e = new Envelope("vision");
        sending = true;
        
        //Check if ports file and scalar file exists
        boolean exists = new File("ports.txt").exists();
        boolean scalars = new File("scalars.txt").exists();
        
        //Check if scalar.txt and ports.txt exist, if not bring error
        if(exists && scalars)
        {
        	setScalars();
        	setPorts();
        	instantiateServer();
        }
        else
        	noFile();
    }
    
    public static void setScalars(){
    	String s = getText("scalars.txt");
    	
    	String lower = s.substring(s.indexOf("{") + 1, s.indexOf("}"));
    	String[] larray = lower.split(",");
    	lowerHSV = new Scalar(Integer.parseInt(larray[0]), Integer.parseInt(larray[1]), Integer.parseInt(larray[2]));
    	
    	String upper = s.substring(s.lastIndexOf("{") + 1, s.lastIndexOf("}"));
    	String[] uarray = upper.split(",");
    	upperHSV = new Scalar(Integer.parseInt(uarray[0]), Integer.parseInt(uarray[1]), Integer.parseInt(uarray[2]));

    }
    public static void setPorts()
    {
    	String s = getText("ports.txt");
    	cameraPort = Integer.parseInt(s.substring(s.indexOf("{") + 1, s.indexOf("}")));
    	portNumber = Integer.parseInt(s.substring(s.lastIndexOf("{") + 1, s.lastIndexOf("}")));
    }

    public static void noFile()
    {
    	JOptionPane.showMessageDialog(null,"You are missing one/both of the \"scalars.txt\" or \"ports.txt\" files");
    }
    
    //Create all processes in the server
    public static void instantiateServer() throws IOException{
    	runServer = true;
    	
        //Code to create the MJPG server at a defined port number also define camera port number
        ServerSocket serverSocket = new ServerSocket(portNumber);
        
        //Create simple monitor
        createWindow();
        
		MJPG_Server server = new MJPG_Server(serverSocket, "ServerMJPG");
		
        //Define usb port at which to listen for camera
        camera = new VideoCapture(cameraPort);

    	//Run camera check
    	runServer = cameraCheck();
            
        //Run loop to capture and process images as well as pushing the images in byte form to the MJPG server

        Runnable r = new Runnable() {
            public void run() {
            	while(true) { 
                	if(runServer){
        	        	Mat frame = new Mat();
        	            if(camera.read(frame)) {        	            	
        	            	status.setBackground(Color.GREEN);
        	            	window.repaint();

        	            	//Find bounding rectangle of image
        	            	Rect r = getBoundingRectangle(frame);
        	            	Imgproc.rectangle(frame, new Point(r.x, r.y), new Point(r.x+r.width, r.y+r.height), new Scalar(0, 0, 255));
        	            	
        	            	//Send values to robot
        	            	bounds = new Rectangle(r.x, r.y, r.width, r.height);
        	            	
        	            	//Upload the frame to the server for viewing on any other computer
        	            	byte[] frame2byte = extractBytes(frame);
        	            	server.writeToServer(frame2byte);     
        	            }
                	}
                	else
                	{
                        status.setBackground(Color.RED);
    	            	window.repaint();
                	}
                }
            }
        };

        new Thread(r).start(); 
        
        Runnable sender = new Runnable() {
            public void run() {
            	while(sending) { 
            		sendRect2Robot(bounds);
            	  }
            }
        };new Thread(r).start(); 
    }
    
    public static void sendRect2Robot(Rectangle rectangle)
    {
    	e.sendData(rectangle);
    }
    
    //Create frame to monitor application
    public static void createWindow() throws UnknownHostException
    {
    	String connection = Inet4Address.getLocalHost().getHostAddress() + ":" + portNumber;
    	String cameraS = "Camera at port: " + cameraPort;
    	
    	File sourceimage = new File("icon.png");
			
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						Image image = ImageIO.read(sourceimage);
						window = new Window();
						window.setBounds(0, 0, 1000, 500);
						status = new Canvas();
						status.setBounds(0, 0, window.getWidth(), 35);
						
						status.setBackground(Color.RED);
						
						//Add ip address label
						JLabel ipAddress = new JLabel(connection);
						JLabel cameraPort = new JLabel(cameraS);
						
						ipAddress.setFont(new Font(ipAddress.getName(), Font.PLAIN, 52));
					    ipAddress.setBounds(0, 30, window.getWidth(), 100);
					    
					    cameraPort.setFont(new Font(ipAddress.getName(), Font.PLAIN, 52));
					    cameraPort.setBounds(0, 100, window.getWidth(), 150);
					    
					    //Add port boxes					    
					    window.setVisible(true);
					    window.setResizable(false);
					    window.setIconImage(image);
						window.add(status);					    
						window.add(ipAddress);
						window.add(cameraPort);
		
					}
					catch(IOException e)
					{
						
					}
				}
			});
    }
    
    //Get the camera and ip address ports from the text file
    public static String getText(String file)
    {
    	String everything = "";
		    try{
		    	BufferedReader br = new BufferedReader(new FileReader(file));
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();
		
		        while (line != null) {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        everything = sb.toString();
		    }catch(IOException e){}
		    
    	return everything;
    }
    
    //Check to see if Camera stream is possible
    public static boolean cameraCheck()
    {
    	//Check if camera is plugged into port number
        if(!camera.isOpened())
        {
        	status.setBackground(Color.RED);
        	window.repaint();
        	return false;
        }
        else 
        	return true;
    }
    
    //Get bounding rectangle of image
    public static Rect getBoundingRectangle(Mat frame)
    {
    	Mat processed = new Mat();
    	Mat mHierarchy = new Mat();
    	Rect bound = new Rect();
    	Core.inRange(frame, lowerHSV, upperHSV, processed);
    	List<MatOfPoint> contours = new ArrayList<MatOfPoint>();   
    	Imgproc.findContours(processed, contours, mHierarchy, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);
    	MatOfPoint2f approxCurve = new MatOfPoint2f();
    	for (int i = 0; i<contours.size(); i++)
    	    {
    	        MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
    	        double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
    	        Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
    	        MatOfPoint points = new MatOfPoint( approxCurve.toArray() );
    	        bound = Imgproc.boundingRect(points);
    	    }
    	return bound;
    }
    
    //Turn an opencv mat to a byte array
	public static byte[] extractBytes(Mat frame){
		MatOfByte bytemat = new MatOfByte();
		Imgcodecs.imencode(".jpg", frame, bytemat);
		byte[] bytes = bytemat.toArray();

		return bytes;
	}

}