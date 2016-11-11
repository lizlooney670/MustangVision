import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import processing.core.PApplet;
import processing.core.PImage;

public class KnobTesting extends PApplet{
	
	private static Scalar lowerHSV, upperHSV;
	private static boolean display;
	private static int[] hsvValue = new int[6];
	private static boolean[] hsv = new boolean[6]; 
	private static Color c, bg;
	String number = "";
	private static VideoCapture camera;
	private static int currentNum;
	private static PImage p, p1;
	private static Mat picture;	
	private static boolean parsy, boxing;
	
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    	setAllFalse();
    	hsv[0] = true;
    	hsvValue[3] = 255;
    	hsvValue[4] = 255;
    	hsvValue[5] = 255;
    	c = Color.RED;
    	bg = Color.WHITE;
    	display = false;
        String porty = JOptionPane.showInputDialog("Please input a camera port number (To test, enter: 670)");
        if(Integer.parseInt(porty) == 670)
        	parsy = true;
        else
        	camera = new VideoCapture(Integer.parseInt(porty));
        PApplet.main("KnobTesting");
    }

    public void settings(){
        size(2500,1500);
    }

    public void setup(){
        fill(120,50,240);
    }

    public void draw(){
    	lowerHSV = new Scalar(hsvValue[0], hsvValue[1], hsvValue[2]);
    	upperHSV = new Scalar(hsvValue[3], hsvValue[4], hsvValue[5]);
    	
    	background(bg.getRGB());
    	fill(bg.getRGB());
    	rect(-10,-10,width+20,height+20);
    	
    	fill(1);
		textSize(50);
    	text("Press 't' to take a photo", 80, 60);
    	
    	if(display)
    		drawImage();
    	
    	if(hsv[0])
    		fill(c.getRGB());
    	else
    		fill(1);
    	//HSV lower H knob
    	rect(width/24, height-width/24, width/24, width/24);
    	rect(width/24, height-(3*width/24), width/24, width/24);
    	
    	if(hsv[1])
    		fill(c.getRGB());
    	else
    		fill(1);
    	//HSV lower S knob
    	rect(3 * width/24, height-width/24, width/24, width/24);
    	rect(3 * width/24, height-(3*width/24), width/24, width/24);
    	
    	if(hsv[2])
    		fill(c.getRGB());
    	else
    		fill(1);
    	//HSV lower V knob
    	rect(5 * width/24, height-width/24, width/24, width/24);
    	rect(5 * width/24, height-(3*width/24), width/24, width/24);
    	
    	if(hsv[3])
    		fill(c.getRGB());
    	else
    		fill(1);
    	//HSV upper H knob
    	rect(13 * width/24, height-width/24, width/24, width/24);
    	rect(13 * width/24, height-(3*width/24), width/24, width/24);
    	
    	if(hsv[4])
    		fill(c.getRGB());
    	else
    		fill(1);
    	//HSV upper S knob
    	rect(15 * width/24, height-width/24, width/24, width/24);
    	rect(15 * width/24, height-(3*width/24), width/24, width/24);
    	
    	if(hsv[5])
    		fill(c.getRGB());
    	else
    		fill(1);
    	//HSV upper V knob
    	rect(17 * width/24, height-width/24, width/24, width/24);
    	rect(17 * width/24, height-(3*width/24), width/24, width/24);
    	
    	//All text
    	fill(1);
    	text(hsvValue[0], width/24, (int)(height- 1.5*width/24));
    	text(hsvValue[1], 3* width/24, (int)(height- 1.5*width/24));
    	text(hsvValue[2], 5 * width/24, (int)(height- 1.5*width/24));
    	text(hsvValue[3], 13* width/24, (int)(height- 1.5*width/24));
    	text(hsvValue[4], 15* width/24, (int)(height- 1.5*width/24));
    	text(hsvValue[5], 17* width/24, (int)(height- 1.5*width/24));
    	
    	if(mousePressed){
    		int x = pmouseX;
    		int y = pmouseY;
    	}
    }
    
    public static PImage getImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);

        m.get(0, 0, ((DataBufferByte)image.getRaster().getDataBuffer()).getData());
       
        PImage c = new PImage(image);
		
        MatOfByte matOfByte = new MatOfByte();
		// TODO Auto-generated method stub
		return c;
    }
    
    private void drawImage()
    {
    	if(!parsy)
	    	picture = getPicture();
    	else
    		picture = Imgcodecs.imread("testing.png", Imgcodecs.CV_LOAD_IMAGE_COLOR);
    	Mat picturefinal = new Mat();
    	
    	Core.inRange(picture, lowerHSV, upperHSV, picturefinal);
    	
    	String boxStatus = "test";	
		if(boxing)
		{
			boxStatus = "disable";
			Rect bound = processBox(picturefinal);
	    	Imgproc.rectangle(picture, new Point(bound.x, bound.y), new Point(bound.x+bound.width, bound.y+bound.height), new Scalar(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue()));
		}
    	
		p = getImage(picture);
		p1 = getImage(picturefinal);
		
		textSize(50);
		text("Original Image", 30, p.height + 50);
		text("Processed Image", p.width + 30, p.height + 50);
		
		image(p, 0, 0);
		image(p1, p.width, 0);
		
		fill(Color.RED.getRGB());
		
		
		text("NOTE: Everything in white will be boxed\nPress 'b' to " + boxStatus + " boxing", p.width - 100, p.height + 150);
    }
 
	private static Rect processBox(Mat picturefinal) {
		Mat processed = new Mat();
    	Mat mHierarchy = new Mat();
    	Rect bound = new Rect();
    	Core.inRange(picturefinal, lowerHSV, upperHSV, processed);
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

	private Mat getPicture() {
		
		Mat frame = new Mat();
		camera.read(frame);
		Size s = new Size(320,180);
		Imgproc.resize(frame, frame, s);
		return frame;
	}
    
    public void keyPressed(){
    	 if (key == CODED) {
    		 this.redraw();
    		    if (keyCode == RIGHT) {
    		    	boolean current = false;
    		    	int i = 0;
    		    	while(!hsv[i])
    		    	{
    		    		if(i >= 5)
    		    		i = 0;
    		    		else
    		    			i++;
    		    	}
    		    	if(i >= 5)
    		    		i = 0;
    		    	else
    		    		i++;
    		    	setAllFalse();
    		    	hsv[i] = true;
    		    	currentNum = i;
    		    }
    		    else if (keyCode == LEFT) {
    		    	boolean current = false;
    		    	int i = 0;
    		    	while(!hsv[i])
    		    	{
    		    		if(i >= 5)
    		    		i = 0;
    		    		else
    		    			i++;
    		    	}
    		    	if(i <= 0)
    		    		i = 5;
    		    	else
    		    		i--;
    		    	setAllFalse();
    		    	hsv[i] = true;
    		    	currentNum = i;
    		    }
    		    else if(keyCode == UP)
    		    {
    		    	if(hsvValue[currentNum] < 255)
    		    		hsvValue[currentNum]++;
    		    }
    		    else if(keyCode == DOWN)
    		    {
    		    	if(hsvValue[currentNum] > 0)
    		    		hsvValue[currentNum]--;
    		    }
    	 }
    	 else
    	 {
			if (key == 't') 
    			 display = true;
    		 else if (key == 'c') 
    			 hsvValue[currentNum] = 0;
    		 else if(key == 'b')
    		 {
    			 if(boxing)
    				 boxing = false;
    			 else
    				 boxing = true;
    		 }
    		 else if(key == 'e')
    		        hsvValue[currentNum] = Integer.parseInt(JOptionPane.showInputDialog("input a number between 0-255:"));
    	 }
 	   }
    
    public static void setAllFalse()
    {
    	for(int i = 0; i < hsv.length; i++)
    		hsv[i] = false;
    }
}

