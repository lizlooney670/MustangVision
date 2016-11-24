import java.awt.Color;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class MustangMain {

	private static Scalar lowerHSV, upperHSV;
	private static int portNumber, cameraPort, JSONportNumber;
	private static boolean runStream, runProcessor, isRunningProcessor;
	private static double distanceProportion = 0, distanceInInches = 0;
	private static Rect boundingBox;
	private static ServerSocket serverSocket;
	private static MJPG_Server server;
	private static Socket socket;
	private static int JSON_Socket_Port = 8080;

	public static void main(String[] args) throws IOException
	{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        runStream = false;
        runProcessor = true;
        isRunningProcessor = false;
        
    	//The proportion is AreaInPixels/DistanceFromObjectInInchesAtSpecifiedArea
        distanceProportion = 1;
        
        setAllData();
        runServer();
	}
	
	public static void runServer() throws IOException{    	
        //Code to create the MJPG server at a defined port number also define camera port number
        serverSocket = new ServerSocket(portNumber);
        
        //Create simple monitor
        displayData();
		
        //Define USB port at which to listen for camera
        VideoCapture camera = new VideoCapture(cameraPort);

    	//Run camera check
    	runProcessor = cameraCheck(camera);
            
        //Run loop to capture and process images as well as pushing the images in byte form to the MJPG server
    	runProcessingThread(camera);
    	
        //Print out the current status of the server (MJPG SERVER) every 2 seconds
      // sendData();
       
       //Check if client is connected and create MJPG Server to stream over
       while(true)
    	   setupServerSocket();
	}
	
	//Main processing thread to process all images and write data when server is enabled
	private static void runProcessingThread(VideoCapture camera)
	{
		Runnable processor = new Runnable() {
            public void run() {
            	while(true) { 
                	if(runProcessor){
    	            	isRunningProcessor = true;
        	        	Mat frame = new Mat();
        	            if(camera.read(frame)) {       
        	            	
        	            	isRunningProcessor = true;
        	    			System.out.println("Running...");
        	            	
        	            	//Find bounding rectangle of image
        	            	boundingBox = ImageUtility.getBoundingRectangle(frame, lowerHSV, upperHSV);
        	            	frame = ImageUtility.drawRectangle(frame, boundingBox);
        	            	
        	            	//Find distance to object and write data on Mat
        	            	Color c = Color.RED;
        	            	distanceInInches = ImageUtility.getDistance(boundingBox, distanceProportion);
        	            	Imgproc.putText(frame, "Distance: " + distanceInInches, new Point(10, frame.height()-20), Core.FONT_HERSHEY_DUPLEX, 1, new Scalar(c.getRed(), c.getGreen(), c.getBlue()));
        	            	      
        	            	if(runStream)
        	            	{
        	            		//Compress and send image to server
        	            		frame = ImageUtility.compressMat(frame);
	        	            	byte[] frame2byte = ImageUtility.extractBytes(frame);
	        	            	server.writeToServer(frame2byte);  
        	            	}
        	            }
                	}
                	else
                	{
                		isRunningProcessor = false;
            			System.out.println("Nothing...");
                	}
                }
            }
        };
        new Thread(processor).start();  
	}
	
	//If client has disconnected, open up port for reconnection, else do nothing
	private static void setupServerSocket() throws IOException
	{
		socket = new Socket();
			System.out.println("Server Open");
			socket = serverSocket.accept();
			socket.setSoTimeout(5000);
			System.out.println("Client Connected");
		server = new MJPG_Server(socket, "HomesteadVision");
		runStream = true; 	
	}
	
	//Print out constant server status and send data to roboRIO
	/*private static void sendData() throws IOException{
	   	 Runnable r = new Runnable() {
	            public void run() {
<<<<<<< HEAD
	            	while(true)
	            	{
		            	if(boundingBox == null)
		            		boundingBox = new Rect(0,0,0,0);
		            	String data = JSON.sendData(boundingBox, distanceInInches);
	            	}
=======
					try {
						Server s = new Server(JSON_Socket_Port);
						
						String data = JSON.sendData(boundingBox, distanceInInches).toJSONString();

		        		String output = "";
		        		while(true)
		        		{
		        			if(s.isConnected())
		        			{
		        				s.writeData(data);
		        				
		        				byte[] b = s.readData(120);
		        				output = new String(b, "UTF-8");
		        				System.out.println(output);
		        			}
		        		}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
>>>>>>> origin/master
	            }
		   };
		        
		   //Start thread to send data to robot
		   new Thread(r).start();  	
	}*/
	
	//Set the port numbers and HSV scalar barriers
	private static void setAllData()
    {
    	Scalar[] s = TextIOToolbox.getScalars();
    	lowerHSV = s[0];
    	upperHSV = s[1];
    	int[] i = TextIOToolbox.setPorts();
    	cameraPort = i[0];
    	portNumber = i[1];
    }
	
    //Run this method at start of program to print out:
		//1. camera connection port 
		//2. network IP address
    private static void displayData() throws UnknownHostException
    {
    	System.out.println(Inet4Address.getLocalHost().getHostAddress() + ":" + portNumber);
    	System.out.println("Camera at port: " + cameraPort);
    }
    
    //Run this method at start of program to:
  		//1. Check if camera is connected to PC 
  		//2. return false value if no camera is connected
    private static boolean cameraCheck(VideoCapture c)
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
