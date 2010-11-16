package pangolin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import pangolin.objects.AnnotatedRule;
import pangolin.services.RepositoryService;

import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.SRXDocument;

/**Processes data from the rules editor form
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public final class EditRules extends HttpServlet {

	private static final long serialVersionUID = -2609015357452514813L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    										throws IOException, ServletException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	    									throws IOException, ServletException {
		// Set character encoding
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		
		//different options in the rule editor
		boolean removeRule = "remove-rule".equals(request.getParameter("remove-rule"));
		boolean moveUp = "move-rule-up".equals(request.getParameter("move-rule-up"));
		boolean moveDown = "move-rule-down".equals(request.getParameter("move-rule-down"));
		boolean newRule = "add-rule".equals(request.getParameter("add-rule"));
		boolean editRule = "edit-rule".equals(request.getParameter("edit-rule"));
		boolean loadFromRepo = request.getParameter("load-repository") != null;
		boolean addToRepo = request.getParameter("add-to-repo") != null;
		boolean updateToRepo = request.getParameter("update-in-repo") != null;
		boolean refreshRepoRules = request.getParameter("refresh-repository") != null;
		boolean addAnnotation = request.getParameter("add-annotation") != null;
		
		
		//broad behaviour categories for above options
		boolean modifyingRule = removeRule || moveUp || moveDown || editRule;
		boolean addingRule = newRule;
		boolean generatingRuleFromForm = editRule || newRule || refreshRepoRules
				|| addAnnotation || addToRepo || updateToRepo;
		boolean returningToRuleEditor = loadFromRepo || refreshRepoRules
				|| addAnnotation || addToRepo || updateToRepo;
		
		
		SRXDocument srxDoc = null;
		String languageRule = null;
		int selectedRuleIndex = -1;
		AnnotatedRule generatedRule = null;
		
		if (modifyingRule || addingRule) {
			// Get the SRX document
			srxDoc = loadSrxDoc(request, response);
			if (srxDoc == null) {return;}
			
			// Get the selected language rule
			languageRule = loadLangRule(request, response);
			if (languageRule == null) {return;}
		}
		
		if (modifyingRule) {
			// Get the rule index to modify
			selectedRuleIndex = loadSelectedRuleIndex(request, response);
			if (selectedRuleIndex == -1) {return;}
		}
		
		if (generatingRuleFromForm) {
			generatedRule = generateRule(request, response, true);
			if (!returningToRuleEditor) {
				if (generatedRule == null) {
					return;
				}
			}
		}
		
		
		
		
		if (removeRule) {
			//delete the rule
			srxDoc.getLanguageRules(languageRule).remove(selectedRuleIndex);
			srxDoc.setModified(true);
			
			//clear the selection
			request.getSession().setAttribute("selectedRuleIndex", -1);
			
			//TODO this sort of thing will make it select a rule near the deleted rule
//			if (selectedRuleIndex > (srxDoc.getLanguageRules(languageRule).size()-1)) {
//				selectedRuleIndex = srxDoc.getLanguageRules(languageRule).size()-1;
//			}
			
			response.sendRedirect("editor.jsp?success=rule-removed");
			return;
		}
		
		if (moveUp || moveDown) {
			if ((moveUp && selectedRuleIndex < 1)
					|| (moveDown && (selectedRuleIndex > srxDoc.getLanguageRules(languageRule).size()-2))) {
				//can't move
				response.sendRedirect("editor.jsp?error=cant-move-rule");
			} else {
				int newIndex = selectedRuleIndex + (moveUp ? -1 : 1);
				//perform the move
				swapRules(srxDoc, languageRule, selectedRuleIndex, newIndex);
				request.getSession().setAttribute("selectedRuleIndex", newIndex);
				response.sendRedirect("editor.jsp?success=rule-moved");
			}
			return;
		}
		
		if (refreshRepoRules) {
			request.getSession().setAttribute("repository-rules", RepositoryService.getRuleList());
			request.getSession().setAttribute("editingRule", generatedRule);
			response.sendRedirect("ruleEditor.jsp?add-rule=add-rule");
			return;
		}
		
		if (addAnnotation) {
			String annKey = request.getParameter("annotation-key");
			String annVal = request.getParameter("annotation-value");
			if (annVal == null) {annVal = "";}
			
			if (annKey != null && !"".equals(annKey)) {
				generatedRule.getAnnotations().put(annKey, annVal);
				srxDoc.setModified(true);
			}
			request.getSession().setAttribute("editingRule", generatedRule);
			response.sendRedirect("ruleEditor.jsp?"
					+ (newRule ? "add-rule=add-rule" : "edit-rule=edit-rule"));
			return;
		}
		
		if (newRule && !returningToRuleEditor) {
			srxDoc.getLanguageRules(languageRule).add(generatedRule);
			srxDoc.setModified(true);
			selectedRuleIndex = srxDoc.getLanguageRules(languageRule).size()-1;
			request.getSession().setAttribute("selectedRuleIndex", selectedRuleIndex);
			request.getSession().removeAttribute("editingRule");
			response.sendRedirect("editor.jsp?success=new-rule-created");
			return;
		}
		
		if (loadFromRepo) {
			//TODO exception handling
			AnnotatedRule repoRule = RepositoryService.getRepositoryRule(request.getParameter("selected-repo-rule"));
			
			request.getSession().setAttribute("editingRule", repoRule);
			
			/* changed behaviour so repository rule can be edited before adding
			srxDoc.getLanguageRules(languageRule).add(repoRule);
			srxDoc.setModified(true);
			selectedRuleIndex = srxDoc.getLanguageRules(languageRule).size()-1;
			request.getSession().setAttribute("selectedRuleIndex", selectedRuleIndex);
			
			
			//redirect to editor with success message
			response.sendRedirect("editor.jsp?success=repo-rule-added");
			*/
			
			response.sendRedirect("ruleEditor.jsp?success=repo-rule-added&"
					+ (newRule ? "add-rule=add-rule" : "edit-rule=edit-rule"));
			return;
		}
		
		if (addToRepo || updateToRepo) {
			//TODO add these errors to the properties file (UI strings)
			int statusCode = RepositoryService.addToRepository(generatedRule, updateToRepo);
			String responseStatus;
			if (statusCode == HttpStatus.SC_CREATED) {
				responseStatus = "success=rule-uploaded";
			} else if (statusCode == HttpStatus.SC_OK) {
				responseStatus = "success=repo-rule-replaced";
			} else if (statusCode == HttpStatus.SC_CONFLICT) {
				responseStatus = "error=repository-rule-name-conflict";
			} else if (statusCode == HttpStatus.SC_NOT_FOUND) {
				responseStatus = "error=repo-rule-missing";
			} else {
				responseStatus = "status=" + statusCode;
			}
			response.sendRedirect("ruleEditor.jsp?"
					+ (newRule ? "add-rule=add-rule" : "edit-rule=edit-rule")
					+ "&" + responseStatus);
			return;
		}
		
		if (editRule && !returningToRuleEditor) {
			srxDoc.getLanguageRules(languageRule).set(selectedRuleIndex, generatedRule);
			srxDoc.setModified(true);
			request.getSession().removeAttribute("editingRule");
			response.sendRedirect("editor.jsp?success=rule-modified");
			return;
		}
		
		response.sendRedirect("editor.jsp?error=not-implemented"); //TODO replace with other error when other functions implemented		
	}
	
	

	private int loadSelectedRuleIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int selectedRuleIndex;
		try {
			selectedRuleIndex = (Integer)request.getSession().getAttribute("selectedRuleIndex");
		} catch (NumberFormatException e) {
			selectedRuleIndex = -1;
		} catch (NullPointerException e) {
			selectedRuleIndex = -1;
		}
		if (selectedRuleIndex == -1) {
			response.sendRedirect("editor.jsp?error=no-rule-selected");
		}
		return selectedRuleIndex;
	}

	private String loadLangRule(HttpServletRequest request,	HttpServletResponse response) throws IOException {
		String languageRule;
		try {
			languageRule = (String)request.getSession().getAttribute("languageRuleName");
		} catch (ClassCastException e) {
			languageRule = null;
		}
		if (languageRule == null) {
			response.sendRedirect("chooseFile.jsp?error=no-language-selected");
		}
		return languageRule;
	}

	private SRXDocument loadSrxDoc(HttpServletRequest request, HttpServletResponse response) throws IOException {
		SRXDocument srxDoc;
		try {
			srxDoc = (SRXDocument)request.getSession().getAttribute("srxDoc");
		} catch (ClassCastException e) {
			srxDoc = null;
		}
		if (srxDoc == null) {
			response.sendRedirect("chooseFile.jsp?error=no-document-loaded");
		}
		return srxDoc;
	}
	
	private AnnotatedRule generateRule(HttpServletRequest request, HttpServletResponse response, boolean failOnError) throws IOException {
		String ruleName;
		String localeCode;
		boolean breakingRule;
		String patternBefore;
		String patternAfter;
		String comment;
		AnnotatedRule generatedRule = null;
		
		
		
		if ("no-break".equals(request.getParameter("breaking-rule"))) {
			breakingRule = false;
		} else if ("break".equals(request.getParameter("breaking-rule"))) {
			breakingRule = true;
		} else {
			//TODO redirect to ruleEditor with appropriate parameters
			if (failOnError) {
				response.sendRedirect("editor.jsp?error=rule-creation-error");
				return generatedRule; //null
			} else {
				breakingRule = true;
			}
		}
		
		ruleName = request.getParameter("rule-name");
		localeCode = request.getParameter("locale-code");
		patternBefore = request.getParameter("pattern-before");
		patternAfter = request.getParameter("pattern-after");
		comment = request.getParameter("rule-comment");
		
		//save data for a rule that is currently being edited
		try {
			generatedRule = (AnnotatedRule)request.getSession().getAttribute("editingRule");
		} catch (ClassCastException e) {
			generatedRule = null;
		} catch (NullPointerException e) {
			generatedRule = null;
		}
		if (generatedRule == null) {
			generatedRule = new AnnotatedRule();
		}
		
		if (patternBefore != null/* && !"".equals(patternBefore)*/) {
			generatedRule.setBefore(patternBefore);
		}
		if (patternAfter != null/* && !"".equals(patternAfter)*/) {
			generatedRule.setAfter(patternAfter);
		}
		generatedRule.setBreak(breakingRule);
		
		if (ruleName != null/* && !"".equals(ruleName)*/) {
			generatedRule.setRuleName(ruleName);
		}
		if (localeCode != null/* && !"".equals(localeCode)*/) {
			generatedRule.setLocaleCode(localeCode);
		}
		if (comment != null/* && !"".equals(comment)*/) {
			generatedRule.setComment(comment);
		}
		
		return generatedRule;
	}
	
	private void swapRules(SRXDocument srxDoc, String languageRule, int index1, int index2) {
		Rule temp = srxDoc.getLanguageRules(languageRule).get(index1);
		srxDoc.getLanguageRules(languageRule).set(index1,
												  srxDoc.getLanguageRules(languageRule).get(index2));
		srxDoc.getLanguageRules(languageRule).set(index2, temp);
		srxDoc.setModified(true);
	}
}
