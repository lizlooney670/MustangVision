import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opencv.core.Rect;
 
/**
 * @author vsharma8363
 */
 
public class JSON {
 
	//Write the JSON array to some text file of your choice
	public static void writeToPath(String path, JSONArray bounding)
	{
		JSONObject obj = new JSONObject();
		obj.put("Bounding_Box", bounding);
 
		try (FileWriter file = new FileWriter(path)) {
			file.write(obj.toJSONString());
			System.out.println("Successful...");
			System.out.println("Data: " + obj);
		}catch(IOException e){}
	}
	
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
	
	public static void sendData(Rect r, double distance)
	{
		
	}
}