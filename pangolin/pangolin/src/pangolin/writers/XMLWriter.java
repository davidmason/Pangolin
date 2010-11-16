package pangolin.writers;

import java.io.Writer;
import java.util.Map.Entry;

import pangolin.objects.AnnotatedRule;

import net.sf.okapi.lib.segmentation.Rule;

/**Extended XML writer that allows writing of rules and the standalone attribute
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class XMLWriter extends net.sf.okapi.common.XMLWriter {

	/**Creates a new XML document on disk.
	 * 
	 * @param path the full path of the document to create. If any directory in the
     * path does not exists yet it will be created automatically. The document is
     * always written in UTF-8 and the type of line-breaks is the one of the
     * platform where the application runs.
	 */
	public XMLWriter(String path) {
		super(path);
	}

	/**Creates a new XML document for a given writer object.
     * @param writer the writer to use to output the document. If this writer outputs to
     * bytes it must be set to output in UTF-8.
     */
	public XMLWriter(Writer writer) {
		super(writer);
	}
	
    /**Writes the start of the document.
     * 
     * @param standalone whether this document has external namespace dependencies
     */
    public void writeStartDocument (boolean standalone) {
		//TODO discuss including this method in net.sf.okapi.common.XMLWriter
    	super.
    	writeRawXML("<?xml version=\"1.0\" encoding=\"UTF-8\"");
    	if (standalone) {
    		writeRawXML(" standalone=\"yes\"");
    	}
    	writeRawXML("?>\n");
    }
	
	
	//TODO consider moving this into the AnnotatedRule class and removing this XMLWriter class
	/**Writes a rule as an XML element
	 * 
	 * @param r the rule to write
	 */
	public void writeRule(Rule r) {
		writeStartDocument(true);
		writeStartElement("pattern");
		
		if (r instanceof AnnotatedRule) {
			AnnotatedRule ar = (AnnotatedRule) r;
			writeAttributeString("name", ar.getRuleName());
			writeAttributeString("localeCode", ar.getLocaleCode());
			if (ar.getAnnotations().size() > 0) {
				writeStartElement("Annotations");
				for (Entry<String, String> entry : ar.getAnnotations().entrySet()) {
					writeStartElement("annotation");
					writeElementString("annotationKey", entry.getKey());
					writeLineBreak();
					writeElementString("annotationValue", entry.getValue());
					writeLineBreak();
					writeEndElementLineBreak();//annotation
				}
				writeEndElementLineBreak();//Annotations
			}
		}
		
		if ( r.getComment() != null ) {
			writeComment(r.getComment());
			writeLineBreak();
		}
		writeStartElement("rule");
		writeAttributeString("break", r.isBreak()? "yes" : "no");
		writeElementString("beforebreak", r.getBefore());
		writeLineBreak();
		writeElementString("afterbreak", r.getAfter());
		writeLineBreak();
		writeEndElementLineBreak(); //rule
		writeEndElementLineBreak(); //pattern
		writeEndDocument();
	}
	
}
