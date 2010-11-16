package pangolin.util;

/**Utility class encapsulating methods for manipulating strings
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class TextUtils {
	
	/**Utility method to replace a null string with an empty string
	 * 
	 * @param original the String to check
	 * @return empty string if original is null, otherwise original
	 */
	public static String nullToEmptyString(String original) {
		if (original == null) {
			return "";
		}
		return original;
	}

}
