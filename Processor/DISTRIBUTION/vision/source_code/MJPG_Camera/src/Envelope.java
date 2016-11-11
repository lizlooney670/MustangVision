import java.awt.Rectangle;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * 
 * @author vsharma8363
 * This class will send data (the bounding rectangle) to the robot over WPI's network tables library in what I call an "envelope" of data
 *
 */
public class Envelope {
	
	NetworkTable vision;
	
	public Envelope(String name)
	{
		vision = NetworkTable.getTable(name);
	}
	
	public void sendData(Rectangle r)
	{
		vision.putNumber("x", r.getX());
		vision.putNumber("y", r.getY());
		vision.putNumber("width", r.getWidth());
		vision.putNumber("height", r.getHeight());
	}
	
}
