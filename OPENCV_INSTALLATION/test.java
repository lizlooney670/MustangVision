import org.opencv.core.Core;

public class test {

	public static void main(String[] args)
	{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("This should have run without errors");
        System.out.println("If there were errors, install a different architecture file");
	}
	
}
