import org.opencv.core.Rect;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkTablesObject {
	
	NetworkTable table;
	
	public NetworkTablesObject(String key)
	{
		table = NetworkTable.getTable(key);
	}
	
	public void sendData(Rect r)
	{
		table.putNumber("x", r.x);
		table.putNumber("y", r.y);
		table.putNumber("width", r.width);
		table.putNumber("height", r.height);
	}

}
