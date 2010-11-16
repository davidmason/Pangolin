package pangolin.services;

import java.util.regex.PatternSyntaxException;

import net.sf.okapi.common.ISegmenter;
import net.sf.okapi.common.resource.TextContainer;
import net.sf.okapi.common.filterwriter.GenericContent;
import net.sf.okapi.lib.segmentation.SRXDocument;

/**Encapsulates methods for performing segmentation on text
 * 
 * @author David Mason, dr.d.mason@gmail.com
 *
 */
public class SegmentationService {
	
	/**Segments a string using rules from an SRX document
	 * 
	 * @param sampleString the string to segment
	 * @param doc the document containing the segmentation rules
	 * @param languageRule the set of rules to use for segmentation
	 * 
	 * @return a string with square brackets [] marking the beginning and end of segments
	 */
	public static String segment(String sampleString, SRXDocument doc, String languageRule) {
		
		//TODO catch segmentation exception and output a message about it not working
		//possibly to tell them which rule went wrong?
		
		if (sampleString == null) {
			return "";
		}
		if (sampleString.equals("") || doc == null || languageRule == null) {
			return sampleString;
		}
		
		String segmented;
		try {
			ISegmenter segmenter = doc.compileSingleLanguageRule(languageRule, null);
			TextContainer sample = new TextContainer(sampleString);
			GenericContent gc = new GenericContent();
			
			segmenter.computeSegments(sample);
			sample.getSegments().create(segmenter.getRanges());
			segmented = gc.printSegmentedContent(sample, true, true);
		} catch (PatternSyntaxException e) {
			//TODO localised string for segmentation error
			segmented = "segmentation error: " + e.getLocalizedMessage();
		}
		
		return segmented;
	}

}