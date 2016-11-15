import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class Processing {

	private static int portNumber, cameraPort;
	private static Scalar lowerHSV, upperHSV;
	private static boolean runServer;
	private static Rect bound;
	private static double proportion;
	private static NetworkTablesObject networktable;
	
    public static void main(String[] args) throws IOException{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
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
        ServerSocket serverSocket = new ServerSocket(portNumber);
        
        //Create simple monitor
        displayData();
        
		MJPG_Server server = new MJPG_Server(serverSocket, "ServerMJPG");
		
        //Define usb port at which to listen for camera
        VideoCapture camera = new VideoCapture(cameraPort);

    	//Run camera check
    	runServer = cameraCheck(camera);
            
        //Run loop to capture and process images as well as pushing the images in byte form to the MJPG server

        Runnable r = new Runnable() {
            public void run() {
            	while(true) { 
                	if(runServer){
        	        	Mat frame = new Mat();
        	            if(camera.read(frame)) {        	            	
        	            	System.out.println("Running...");
        	            	
        	            	//Find bounding rectangle of image
        	            	bound = ImageUtility.getBoundingRectangle(frame, lowerHSV, upperHSV);
        	            	double distanceInInches = ImageUtility.getDistance(bound, proportion);
        	            	
        	            	Imgproc.rectangle(frame, new Point(bound.x, bound.y), new Point(bound.x+bound.width, bound.y+bound.height), new Scalar(255, 0, 0));
        	            	
        	            	Color c = Color.BLACK;
        	            	
        	            	Imgproc.putText(frame, "Distance: " + distanceInInches, new Point(10, frame.height()-20), Core.FONT_HERSHEY_DUPLEX, 1, new Scalar(c.getRed(), c.getGreen(), c.getBlue()));
        	            	
        	            	//Upload the frame to the server for viewing on any other computer
        	            	byte[] frame2byte = ImageUtility.extractBytes(frame);
        	            	server.writeToServer(frame2byte);     
        	            }
                	}
                	else
                	{
                        System.out.println("Camera connection error...");
                	}
                }
            }
        };
        
        //Start Thread to image process
        new Thread(r).start();  
        
       Runnable relay = new Runnable() {
            public void run() {
            	networktable.sendData(bound);
            	/*
            	JSONArray array = JSON.boundingBox(bound);
            	JSON.writeToPath("testing.txt", array);*/
            	}	
	        };
	        
	   //Start thread to send data to robot
	   new Thread(relay).start();  
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