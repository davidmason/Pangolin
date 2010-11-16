package pangolin.services;

import java.util.ArrayList;

//imports from okapi framework for segmentation
import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.SRXDocument;

/**Encapsulates methods to load SRX documents from various sources
 * including generating new SRX documents.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 *
 */
public class SRXDocumentService {
	
	
	/**Returns a blank SRX document with no groups, mappings or rules
	 * 
	 * @return a blank SRX document
	 */
	public static SRXDocument buildBlankSRXDoc() {
		return  new SRXDocument();
	}
	
	/**Returns a test SRX document with some groups and rules
	 * 
	 * @return the generated SRX document
	 */
	public static SRXDocument buildTestSRXDoc() {
		SRXDocument testDoc = new SRXDocument();
		
		ArrayList<Rule> rulesList = new ArrayList<Rule>();
		rulesList.add(new Rule("[\\.?!]+", "\\s", true)); //break after period
		rulesList.add(new Rule("i", "s", true)); //split between i and s
		testDoc.addLanguageRule("default", rulesList);
		
		rulesList = new ArrayList<Rule>();
		rulesList.add(new Rule("[Ee][Tt][Cc]\\.", "\\s", false));
		rulesList.add(new Rule("\\.", "\\s", true));
		testDoc.addLanguageRule("somethingese", rulesList);
		
		testDoc.setSampleText("This is a sample string. It is for segmentation! So on, etc. and so-forth.");
		
		return testDoc;
	}

}
