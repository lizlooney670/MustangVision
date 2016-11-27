import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opencv.core.Rect;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
 
/**
 * @author vsharma8363
 */
 
public class NetworkTableJSON {
	
	private static NetworkTable table;
	private static String tableName;
	
	public NetworkTableJSON(String string) 
	{
		tableName = "vision";
		table = NetworkTable.getTable(tableName);
	}

	//Convert the boundingbox to a JSON array
	public JSONArray boundingBox(Rect r)
	{
		JSONArray rectangle = new JSONArray();
		rectangle.add(r.x);
		rectangle.add(r.y);
		rectangle.add(r.width);
		rectangle.add(r.height);
		return rectangle;
	}
	
	public void sendData(Rect r, double distance)
	{
		JSONArray bounding = boundingBox(r);
		JSONObject obj = new JSONObject();
		obj.put("Bounding_Box", bounding);
		obj.put("Distance", distance);
		String data = obj.toJSONString();
		System.out.println(data);
		table.putString("data", data);
	}
	
	public boolean isConnected()
	{
		return table.isConnected();
	}
	
}