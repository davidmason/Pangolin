package pangolin.i18n;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;

/**Wrapper for a resource bundle that ensures strings use UTF-8 encoding
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class ResourceBundleWrapper implements ILocalStringResource {
	
	private ResourceBundle myBundle;
	private String myBundleName;
	private Locale myLocale;
	
	public ResourceBundleWrapper(String bundleName, Locale userLocale) {
		myBundleName = bundleName;
		myLocale = userLocale;
		loadBundle();
	}


	@Override
	public Locale getLocale() {
		return myLocale;
	}

	@Override
	public void setLocale(Locale locale) {
		if (!myLocale.equals(locale)) {
			myLocale = locale;
			loadBundle();
		}
	}
	
	/**Returns a string from this bundle.
	 * @param key the key associated with the required string in this bundle
	 * @return the string from this resource bundle formatted in utf-8
	 */
	public String getString(String key) {
		try {
			return new String(myBundle.getString(key).getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return key;
		}
	}
	
	/**Returns a string from this bundle with the given replacement used.
	 * 
	 * @param key the key associated with the required string
	 * @param placeholder the placeholder to replace with replacement. May not contain
	 *        the '\' or '$' character
	 * @param replacement the string to replace placeholder with
	 * @return requested string with the first instance of the placeholder replaced.
	 */
	public String getString(String key, String placeholder, String replacement) {
		String parsedString = getString(key).replaceFirst(placeholder, replacement);
		return parsedString;
	}
	
	private void loadBundle() {
		myBundle = ResourceBundle.getBundle(myBundleName, myLocale);
	}

}
