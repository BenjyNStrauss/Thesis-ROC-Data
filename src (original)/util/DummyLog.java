package util;

/**
 * THIS CLASS IS A DUMMY!
 * IF SOMETHING DOESN'T WORK WITH JBIO CLASSES, THIS CLASS IS ***PROBABLY*** WHY
 * REPLACE WITH android.util.Log WHEN SOURCE CODE IS FOUND!
 * @author Benjy Strauss
 *
 */

public class DummyLog {
	
	//these values are correct!
	public static final int VERBOSE = 2;
	public static final int DEBUG = 3;
	public static final int INFO = 4;
	public static final int WARN = 5;
	public static final int ERROR = 6;
	
	@Dangerous
	public static boolean isLoggable(String tag, int level) {
		// TODO Auto-generated method stub
		return true;
	}

	@Dangerous
	public static void println(int priority, String name, String message) {
		System.out.println(priority+"|"+name+"|"+message);
	}

	@Dangerous
	public static String getStackTraceString(Throwable throwable) {
		return throwable.getMessage();
	}

}
