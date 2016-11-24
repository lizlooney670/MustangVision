import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.opencv.core.Scalar;

public class TextIOToolbox{
		
	public static Scalar[] getScalars()
	{
		Scalar[] hsvs = new Scalar[2];
		String s = getText("scalars.txt");
    	
    	String lower = s.substring(s.indexOf("{") + 1, s.indexOf("}"));
    	String[] larray = lower.split(",");
    	//lower hsv
    	hsvs[0] = new Scalar(Integer.parseInt(larray[0]), Integer.parseInt(larray[1]), Integer.parseInt(larray[2]));
    	
    	String upper = s.substring(s.lastIndexOf("{") + 1, s.lastIndexOf("}"));
    	String[] uarray = upper.split(",");
    	//upper hsv
    	hsvs[1] = new Scalar(Integer.parseInt(uarray[0]), Integer.parseInt(uarray[1]), Integer.parseInt(uarray[2]));
    	
    	return hsvs;
	}

	public static int[] setPorts()
    {
		int[] i = new int[2];
    	String s = getText("ports.txt");
    	//port number for camera
    	i[0] = Integer.parseInt(s.substring(s.indexOf("{") + 1, s.indexOf("}")));
    	//port number for network
    	i[1] = Integer.parseInt(s.substring(s.lastIndexOf("{") + 1, s.lastIndexOf("}")));
    	return i;
    }
	
	public static String getText(String file)
    {
    	String everything = "";
		    try{
		    	BufferedReader br = new BufferedReader(new FileReader(file));
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();
		
		        while (line != null) {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        everything = sb.toString();
		    }catch(IOException e){}
		    
    	return everything;
    }
}
