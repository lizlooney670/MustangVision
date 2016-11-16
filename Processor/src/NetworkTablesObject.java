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
		if(r != null)
		{
			table.putNumber("x", r.x);
			table.putNumber("y", r.y);
			table.putNumber("width", r.width);
			table.putNumber("height", r.height);
		}
		else
		{
			table.putNumber("x", 0);
			table.putNumber("y", 0);
			table.putNumber("width", 0);
			table.putNumber("height", 0);
		}
	}

}
