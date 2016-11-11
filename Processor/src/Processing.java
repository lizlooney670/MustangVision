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
	
    public static void main(String[] args) throws IOException{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
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
        	            	Imgproc.rectangle(frame, new Point(bound.x, bound.y), new Point(bound.x+bound.width, bound.y+bound.height), new Scalar(255, 0, 0));
        	            	
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
            	JSONArray array = JSON.boundingBox(bound);
            	JSON.writeToPath("testing.txt", array);
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