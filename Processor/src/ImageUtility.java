import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageUtility{

	//Get bounding rectangle with defined HSV values
	public static Rect getBoundingRectangle(Mat frame, Scalar lowerHSV, Scalar upperHSV)
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
	
	//Get distance to the bounding box in inches
	//The proportion is AreaInPixels/DistanceFromObjectInInchesAtSpecifiedArea
	public static double getDistance(Rect r, double prop)
	{
		return r.area()/prop;
	}
	
	//Turn an opencv mat to a byte array
	public static byte[] extractBytes(Mat frame){
		frame = compressMat(frame);
		MatOfByte bytemat = new MatOfByte();
		Imgcodecs.imencode(".jpg", frame, bytemat);
		byte[] bytes = bytemat.toArray();
		return bytes;
	}
	
	//Method to compress the quality of a mat file
	public static Mat compressMat(Mat m)
	{
		Size s = new Size(640, 360);
		Imgproc.resize(m, m, s);
		return m;
	}
	
}
