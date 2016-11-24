import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opencv.core.Rect;
 
/**
 * @author vsharma8363
 */
 
public class JSON {
	
	//Convert the boundingbox to a JSON array
	public static JSONArray boundingBox(Rect r)
	{
		JSONArray rectangle = new JSONArray();
		rectangle.add(r.x);
		rectangle.add(r.y);
		rectangle.add(r.width);
		rectangle.add(r.height);
		return rectangle;
	}
	
<<<<<<< HEAD
	public static String sendData(Rect r, double distance)
	{
		JSONArray bounding = boundingBox(r);
		JSONObject obj = new JSONObject();
		obj.put("Bounding_Box", bounding);
		obj.put("Distance", distance);
		
		return obj.toJSONString();
=======
	public static JSONObject sendData(Rect r, double distance)
	{
		JSONArray bounding_box = boundingBox(r);
		JSONObject obj = new JSONObject();
		obj.put("Bounding_Box", bounding_box);
		obj.put("Distance", distance);
		return obj;
>>>>>>> origin/master
	}
}