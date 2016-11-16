import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class Processing {

	private static int portNumber, cameraPort;
	private static Scalar lowerHSV, upperHSV;
	private static boolean runServer, stream, running;
	private static Rect bound;
	private static double proportion;
	private static NetworkTablesObject networktable;
	private static MJPG_Server server;
	private static ServerSocket serverSocket;
	
    public static void main(String[] args) throws IOException{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        stream = false;
        
        networktable = new NetworkTablesObject("vision");
        
        proportion = 1;
        
        //Check if ports file and scalar file exists
        boolean exists = new File("ports.txt").exists();
        boolean scalars = new File("scalars.txt").exists();
        
        //Check if scalar.txt and ports.txt exist, if not bring error
        if(exists && scalars)
        {
        	setAllPorts();
        	instantiateServer();
        }
        else
        	noFile();
    }

    public static void setAllPorts()
    {
    	Scalar[] s = DataTool.getScalars();
    	lowerHSV = s[0];
    	upperHSV = s[1];
    	int[] i = DataTool.setPorts();
    	cameraPort = i[0];
    	portNumber = i[1];
    }
    
    public static void noFile()
    {
    	JOptionPane.showMessageDialog(null,"You are missing one/both of the \"scalars.txt\" or \"ports.txt\" files");
    }
    
    //Create all processes in the server
    public static void instantiateServer() throws IOException{
    	runServer = true;
    	
        //Code to create the MJPG server at a defined port number also define camera port number
        serverSocket = new ServerSocket(portNumber);
        serverSocket.setSoTimeout(5000);
        
        //Create simple monitor
        displayData();
		
        //Define usb port at which to listen for camera
        VideoCapture camera = new VideoCapture(cameraPort);

    	//Run camera check
    	runServer = cameraCheck(camera);
            
        //Run loop to capture and process images as well as pushing the images in byte form to the MJPG server

        Runnable processor = new Runnable() {
            public void run() {
            	while(true) { 
                	if(runServer){
        	        	Mat frame = new Mat();
        	            if(camera.read(frame)) {        	            	
        	            	running = true;
        	            	
        	            	//Find bounding rectangle of image
        	            	bound = ImageUtility.getBoundingRectangle(frame, lowerHSV, upperHSV);
        	            	double distanceInInches = ImageUtility.getDistance(bound, proportion);
        	            	
        	            	Imgproc.rectangle(frame, new Point(bound.x, bound.y), new Point(bound.x+bound.width, bound.y+bound.height), new Scalar(255, 0, 0));
        	            	
        	            	Color c = Color.RED;
        	            	
        	            	Imgproc.putText(frame, "Distance: " + distanceInInches, new Point(10, frame.height()-20), Core.FONT_HERSHEY_DUPLEX, 1, new Scalar(c.getRed(), c.getGreen(), c.getBlue()));
        	            	
        	            	stream = !serverSocket.isClosed();
        	            	
        	            	if(stream){
	        	            	//Upload the frame to the server for viewing on any other computer
	        	            	byte[] frame2byte = ImageUtility.extractBytes(frame);
	        	            	server.writeToServer(frame2byte);  
        	            	}
        	            }
                	}
                	else
                	{
                		running = false;
                	}
                }
            }
        };
        //Start Thread to image process
        new Thread(processor).start();  
        
       statusPrinter();
        
	   server = new MJPG_Server(serverSocket, "ServerMJPG");
	   
       stream = true; 
    }
    
    private static void statusPrinter() {
   	 Runnable checks = new Runnable() {
            public void run() {
            	long start = System.currentTimeMillis();
            	while(true)
            	{
                	long current = System.currentTimeMillis();
                	double seconds = (current - start)/1000;
	            	networktable.sendData(bound);
	            		if(seconds > 2)
	            	{
	            			if(running)
	            				System.out.println("Running...");
	            			else
	            				System.out.println("Nothing...");
	            		start = System.currentTimeMillis();
	            	}
            	}
            }
	   };
	        
	   //Start thread to send data to robot
	   new Thread(checks).start();  	
	}
    
    //Create text console panel to monitor application
    public static void displayData() throws UnknownHostException
    {
    	System.out.println(Inet4Address.getLocalHost().getHostAddress() + ":" + portNumber);
    	System.out.println("Camera at port: " + cameraPort);
    }
    
    //Check to see if camera is connected to pc
    public static boolean cameraCheck(VideoCapture c)
    {
        if(!c.isOpened())
        {
        	System.out.println("Camera connection error...");
        	return false;
        }
        else 
        	return true;
    }
}