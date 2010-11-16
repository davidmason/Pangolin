package pangolin.parsers;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import pangolin.objects.AnnotatedRule;

import net.sf.okapi.lib.segmentation.Rule;

/**Utility class for parsing XML into appropriate objects for pangolin.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class XMLParser {
	
	private static XMLInputFactory xif = XMLInputFactory.newInstance();
	
	/**Returns a list of document names given an InputStream of XML that describes
	 * the collection.
	 * 
	 * @param stream an InputStream containing the XML that describes a
	 *               collecton of documents
	 * @return a list of document names
	 */
	public static ArrayList<String> parseDocumentCollection(InputStream stream) {
		return parseCollection(stream, "srx");
	}
	
	/**Returns a list of rule names given an InputStream of XML that describes
	 * the collection.
	 * 
	 * @param stream an InputStream containing the XML that describes a
	 *               collecton of rules
	 * @return a list of rule names
	 */
	public static ArrayList<String> parseRuleCollection(InputStream stream) {
		return parseCollection(stream, "pattern");
	}
	
	/* generates a list of names of elements in a collection element
	 */
	private static ArrayList<String> parseCollection(InputStream stream, String elementName) {
		ArrayList<String> collectionItems = new ArrayList<String>();
		
		XMLStreamReader xr;
		try {
			xr = xif.createXMLStreamReader(stream);
		} catch (XMLStreamException e) {
			xr = null;
			//TODO throw exception
		}
		
		if (xr != null) {
			try {
				while(xr.hasNext()) {
					xr.next();
					if (xr.isStartElement()) {
						if (elementName.equals(xr.getName().getLocalPart())) {
							int numAttribs = xr.getAttributeCount();
							for (int i=0; i<numAttribs; i++) {
								if ("name".equals(xr.getAttributeLocalName(i))) {
									collectionItems.add(xr.getAttributeValue(i));
								}
							}
						}
					}
				}
			} catch (XMLStreamException e) {
				//TODO probably throw an exception here
			}
		}
		
		return collectionItems;
	}
	
	/**Generates a Rule from an input stream of XML that describes the rule
	 * 
	 * @param stream an InputStream containing the XML that describes a rule
	 */
	public static Rule parseRule(InputStream stream) {
		return parseAnnotatedRule(stream);
	}
	
	/**Generates an AnnotatedRule from an input stream of XML that describes the rule
	 * 
	 * @param stream an InputStream containing the XML that describes a rule
	 */
	public static AnnotatedRule parseAnnotatedRule(InputStream stream) {
		XMLStreamReader xr;
		try {
			xr = xif.createXMLStreamReader(stream);
		} catch (XMLStreamException e) {
			xr = null;
			//TODO throw exception
		}
		
		AnnotatedRule theRule = new AnnotatedRule();
		
		if (xr == null) {
			return theRule;
		}
		
		String annotationKey = null;
		
		try {
			while(xr.hasNext()) {
				xr.next();
				if (xr.isStartElement()) {
					if ("pattern".equals(xr.getName().getLocalPart())) {
						int numAttribs = xr.getAttributeCount();
						for (int i=0; i<numAttribs; i++) {
							if ("name".equals(xr.getAttributeLocalName(i))) {
								theRule.setRuleName(xr.getAttributeValue(i));
							} else if ("localeCode".equals(xr.getAttributeLocalName(i))) {
								theRule.setLocaleCode(xr.getAttributeValue(i));
							}
						}
					}
					//TODO this will fail miserably if there's a mismatch between annotation keys and values
					// needs to be made more robust, probably by using a proper XML parser
					if ("annotationKey".equals(xr.getName().getLocalPart())) {
						annotationKey = xr.getElementText();
					}
					if ("annotationValue".equals(xr.getName().getLocalPart())) {
						theRule.getAnnotations().put(annotationKey, xr.getElementText());
					}
					
					if ("rule".equals(xr.getName().getLocalPart())) {
						int numAttribs = xr.getAttributeCount();
						for (int i=0; i<numAttribs; i++) {
							if ("break".equals(xr.getAttributeLocalName(i))) {
								theRule.setBreak("yes".equals(xr.getAttributeValue(i)));
							}
						}
					}
					if ("beforebreak".equals(xr.getName().getLocalPart())) {
						theRule.setBefore(xr.getElementText());
					}
					if ("afterbreak".equals(xr.getName().getLocalPart())) {
						theRule.setAfter(xr.getElementText());
					}
				}
			}
		} catch (XMLStreamException e) {
			//TODO probably throw an exception here
		}
		return theRule;
	}

}
