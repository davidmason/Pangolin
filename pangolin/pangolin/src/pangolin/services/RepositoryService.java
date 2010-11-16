package pangolin.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static javax.ws.rs.core.MediaType.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;


import net.sf.okapi.lib.segmentation.SRXDocument;

import pangolin.i18n.ILocalStringResource;
import pangolin.i18n.ResourceBundleWrapper;
import pangolin.objects.AnnotatedRule;
import pangolin.parsers.XMLParser;
import pangolin.writers.XMLWriter;

/**Encapsulates communication with a repository.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class RepositoryService {
    //TODO define a repository interface to allow easy use with different repositories
	
	//private static String srxService;
	private static String patternURL;
	private static String documentURL;
	private static String baseDocURL;
	private static String basePatternURL;
	
	//load repository URLs
	static {
		ILocalStringResource URLs = new ResourceBundleWrapper("Repository", Locale.ENGLISH);
		//srxService = URLs.getString("baseURL");
		patternURL = URLs.getString("patternURL");
		basePatternURL = URLs.getString("basePatternURL");
		documentURL = URLs.getString("documentURL");
		baseDocURL = URLs.getString("baseDocURL");
		
	}
	
	/**Returns the names of rules available in this repository
	 * 
	 * @return a list of all rule names available in this repository
	 */
	public static ArrayList<String> getRuleList() throws MalformedURLException, IOException {
		return XMLParser.parseRuleCollection(new URL(patternURL).openStream());
	}
	
	//TODO think about exception handling here
	/**Fetches a rule from the repository
	 * 
	 * @param ruleName the name of the rule to retrieve from the repository
	 * @return the rule associated with the given name in this repository
	 */
	public static AnnotatedRule getRepositoryRule(String ruleName) throws MalformedURLException, IOException {
		return XMLParser.parseAnnotatedRule(new URL(basePatternURL+ruleName.replaceAll(" ", "%20")).openStream());
	}
	
	/**Returns the names of documents available in this repository
	 * 
	 * @return a list of all document names available in this repository
	 */
	public static ArrayList<String> getDocList() throws MalformedURLException, IOException {
		return XMLParser.parseDocumentCollection(new URL(documentURL).openStream());
	}
	
//	//TODO think about exception handling here
//	//TODO implementation. Currently getDocURL() is being used to load documents
//  //     using the generic URL loading function.
//	public static SRXDocument getRepositoryDoc(String docName) throws MalformedURLException, IOException {
//		new URL(baseDocURL+docName).openStream();
//		return null;
//	}
	
	
	/**Returns the URL for a document in this repository.
	 * 
	 * @param docName the name of the document in the repository
	 * @return
	 */
	public static String getDocURL(String docName) {
		return baseDocURL+docName.replaceAll(" ", "%20");
	}
	
	/**Removes an SRX document from this repository.
	 * 
	 * @param documentName the name of the document to delete
	 * @return a HttpStatus status code indicating whether the operation was successful
	 */
	public static int deleteRepositoryDoc(String documentName) {
		HttpDelete method = new HttpDelete(baseDocURL + documentName.replaceAll(" ", "%20"));
		method.setHeader("QUT", "localization");
		
		//Setup an emulator to fire our request
		HttpClient client = new DefaultHttpClient();
		
		int statusCode = -1;
		
		HttpResponse r;
		try {
			r = client.execute(method);
			statusCode = r.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			return -1;
		} catch (IOException e) {
			return -1;
		}
		
		client.getConnectionManager().closeExpiredConnections();

		return statusCode;
	}
	
	/**Adds an SRX document to this repository.
	 * 
	 * @param srxDoc the document to add to the repository
	 * @param documentName the name to associate with the document in the repository
	 * @param existingDoc true if this document is already in the repository and
	                      should be replaced, false otherwise.
	 * @return a HttpStatus status code indicating whether the operation was successful
	 */
	public static int addToRepository(SRXDocument srxDoc, String documentName, boolean existingDoc) {
			
		//Setup an emulator to fire our request
		HttpClient client = new DefaultHttpClient();
		
		
		//Declare a method to save the desired SRX document
		HttpEntityEnclosingRequestBase method;
		if (existingDoc) {
			method = new HttpPut(baseDocURL + documentName.replaceAll(" ", "%20"));
		} else {
			method = new HttpPost(documentURL);
			method.setHeader("Identifier", documentName);
		}
		
		
		
		try {
			method.setEntity(srxToInputStreamEntity(srxDoc));
		} catch (UnsupportedEncodingException e1) {
			return -1;
		}
		method.setHeader("QUT", "localization");
		
		int statusCode = -1;
		
		HttpResponse r;
		try {
			r = client.execute(method);
			statusCode = r.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			return -1;
		} catch (IOException e) {
			return -1;
		}
		client.getConnectionManager().closeExpiredConnections();

		return statusCode;
	}
	
	/**Adds a rule to this repository.
	 * 
	 * @param rule the rule to add, with appropriate annotations
	 * @param existingRule true if this rule is already in the repository and
	                       should be replaced, false otherwise.
	 * @return a HttpStatus status code indicating whether the operation was successful
	 */
	public static int addToRepository(AnnotatedRule rule, boolean existingRule) {
			
		//Setup an emulator to fire our request
		HttpClient client = new DefaultHttpClient();
		
		
		//Declare a method to save the desired SRX document
		HttpEntityEnclosingRequestBase method;
		if (existingRule) {
			method = new HttpPut(basePatternURL + rule.getRuleName().replaceAll(" ", "%20"));
		} else {
			method = new HttpPost(patternURL);
			//method.setHeader("Identifier", rule.getRuleName());
		}
		
		
		
		try {
			method.setEntity(ruleToInputStreamEntity(rule));
		} catch (UnsupportedEncodingException e1) {
			return -1;
		}
		method.setHeader("QUT", "localization");
		
		int statusCode = -1;
		
		HttpResponse r;
		try {
			r = client.execute(method);
			statusCode = r.getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			return -1;
		} catch (IOException e) {
			return -1;
		}
		client.getConnectionManager().closeExpiredConnections();

		return statusCode;
	}
	
	
	
	
	private static InputStreamEntity srxToInputStreamEntity(SRXDocument doc) throws UnsupportedEncodingException {
		String file = doc.saveRulesToString(true, false);
		InputStream is = new ByteArrayInputStream(file.getBytes("UTF-8"));
		InputStreamEntity entity = new InputStreamEntity(is, file.length());
		entity.setContentType(TEXT_XML);
		
		return entity;
	}
	
	private static InputStreamEntity ruleToInputStreamEntity(AnnotatedRule rule) throws UnsupportedEncodingException {
		//convert rule to string
		StringWriter sWriter = new StringWriter();
		XMLWriter xWriter = new XMLWriter(sWriter);
		xWriter.writeRule(rule);
		String file = sWriter.toString();
		
		InputStream is = new ByteArrayInputStream(file.getBytes("UTF-8"));
		InputStreamEntity entity = new InputStreamEntity(is, file.length());
		entity.setContentType(TEXT_XML);
		
		return entity;
	}

}
