package pangolin.objects;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.okapi.lib.segmentation.Rule;

/**Rule class with added name, locale code and annotations for use with rule
 * repositoy.
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public class AnnotatedRule extends Rule {
	
	private Map<String, String> annotations = new LinkedHashMap<String, String>();
	private String ruleName;
	private String localeCode;
	
	public AnnotatedRule() {
		super();
	}
	
	public AnnotatedRule(String patternBefore, String patternAfter, boolean breakingRule) {
		super(patternBefore, patternAfter, breakingRule);
	}
	
	public void setRuleName(String name) {
		ruleName = name;
	}
	public String getRuleName() {
		return ruleName;
	}
	
	public void setLocaleCode(String code) {
		localeCode = code;
	}
	public String getLocaleCode() {
		return localeCode;
	}
	
	//should be able to update without set
	public Map<String, String> getAnnotations() {
		return annotations;
	}

}
