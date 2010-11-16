package pangolin.parsers;

/**Utility class to extract a basic document name from a path.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class DocNameParser {
	
	/**Returns a basic filename from the given string, with the path and
	 * extension removed.
	 */
	public static String extractDocName(String fileNameString) {
		String fileName = fileNameString;
		if (fileName.contains("/")) {
			fileName = fileName.substring(fileName.lastIndexOf("/")+1);
		}
		if (fileName.contains(".")) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		}
		return fileName.replaceAll("%20", " ");
	}
}
