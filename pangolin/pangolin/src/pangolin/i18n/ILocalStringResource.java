package pangolin.i18n;

import java.util.Locale;

/**Interface to return locale-appropriate strings from a resource. How these
 * resources are loaded will depend on the implementation specifics.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public interface ILocalStringResource {
	
	public void setLocale(Locale locale);
	public Locale getLocale();
	
	//TODO consider whether to define a setResource method, or just
	// leave this to each implementing class (as they could require
	// different parameter counts etc.
	
	/**Returns a localised string from this resource.
	 * @param key the key associated with the required string in this resource
	 * @return the localised string corresponding to key, formatted in utf-8
	 */
	public String getString(String key);
	
	//TODO revise to make more general
	/**Returns a string from this bundle with the given replacement used.
	 * 
	 * @param key the key associated with the required string in this resource
	 * @param placeholder the placeholder to replace with replacement. May not contain
	 *        the '\' or '$' character
	 * @param replacement the string to replace placeholder with
	 * @return requested string with the first instance of the placeholder replaced.
	 */
	public String getString(String key, String placeholder, String replacement);
	
}
