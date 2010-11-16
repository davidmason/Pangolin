package pangolin.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.okapi.lib.segmentation.Rule;
import net.sf.okapi.lib.segmentation.SRXDocument;
import net.sf.okapi.lib.segmentation.LanguageMap;

/**Processes data from the groups and maps editor forms
 * 
 * @author David Mason, dr.d.mason@gmail.com
 */
public final class EditGroups extends HttpServlet {

	private static final long serialVersionUID = 5947958397709452734L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    										throws IOException, ServletException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	    									throws IOException, ServletException {
		// Set character encoding
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		
		boolean addGroup = (request.getParameter("add-group") != null);
		boolean useGroup = (request.getParameter("use-group") != null);
		boolean renameGroup = (request.getParameter("rename-group") != null);
		boolean removeGroup = (request.getParameter("remove-group") != null);
		
		boolean addMap = (request.getParameter("add-map") != null);
		boolean editMap = (request.getParameter("update-map") != null);
		boolean removeMap = (request.getParameter("remove-map") != null);
		boolean moveUpMap = (request.getParameter("move-map-up") != null);
		boolean moveDownMap = (request.getParameter("move-map-down") != null);
		
		
		// Get the SRX document
		SRXDocument srxDoc;
		try {
			srxDoc = (SRXDocument)request.getSession().getAttribute("srxDoc");
		} catch (ClassCastException e) {
			srxDoc = null;
		}
		if (srxDoc == null) {
			response.sendRedirect("chooseFile.jsp?error=no-document-loaded");
			return;
		}
		
		String selectedGroup = request.getParameter("selected-group");
		boolean groupSelected = (selectedGroup != null && !"".equals(selectedGroup));
		String newGroupName;
		String newMapPattern;
		String newMapGroup;
		int selectedMapIndex;
		
		if (useGroup) {
			request.getSession().setAttribute("languageRuleName", selectedGroup);
			response.sendRedirect("groupsEditor.jsp?selected-group="+selectedGroup);
			return;
		}
		
		String error = null;
		String success = null;
		
		if (addGroup) {
			newGroupName = request.getParameter("new-group-name");
			if (!validGroupName(newGroupName)) {
				error = "invalid-group-name";
			} else if (srxDoc.getLanguageRules(newGroupName) != null) {
				error = "group-exists";
			} else {
				srxDoc.addLanguageRule(newGroupName, new ArrayList<Rule>());
				srxDoc.setModified(true);
				request.getSession().setAttribute("languageRuleName", newGroupName);
				success = "group-added";
			}
		} else if (renameGroup) {
			newGroupName = request.getParameter("rename-group-name");
			if (!validGroupName(newGroupName)) {
				error = "invalid-group-name";
			} else if (!groupSelected) {
				error = "no-group-selected";
			} else {
				LinkedHashMap<String, ArrayList<Rule>> allLangRules = srxDoc.getAllLanguageRules();
				ArrayList<Rule> temp = allLangRules.remove(selectedGroup);
				allLangRules.put(newGroupName, temp);
				srxDoc.setModified(true);
				request.getSession().setAttribute("languageRuleName", newGroupName);
				success = "group-renamed";
			}
		} else if (removeGroup) {
			if (!groupSelected) {
				error = "no-group-selected";
			} else {
				//TODO store removed groups in a map to allow user to restore groups during a session
				srxDoc.getAllLanguageRules().remove(selectedGroup);
				srxDoc.setModified(true);
				request.getSession().removeAttribute("languageRuleName");
				success = "group-removed";
			}
		} else if (addMap) {
			newMapPattern = request.getParameter("new-map-pattern");
			newMapGroup = request.getParameter("new-map-group");
			if (!validMapPattern(newMapPattern)) {
				error = "invalid-map-pattern";
			} else if (newMapGroup == null) {
				error = "invalid-map-group";
			} else {
				srxDoc.addLanguageMap(new LanguageMap(newMapPattern, newMapGroup));
				srxDoc.setModified(true);
				success = "map-added";
			}
		} else if (editMap || removeMap || moveUpMap || moveDownMap) {
			ArrayList<LanguageMap> allMaps = srxDoc.getAllLanguagesMaps();
			try {
				selectedMapIndex = Integer.parseInt(request.getParameter("selected-map-index"));
			} catch (NumberFormatException e) {
				selectedMapIndex = -1;
			}
			if (selectedMapIndex == -1) {
				error = "no-map-selected";
			} else if (editMap) {
				newMapPattern = request.getParameter("update-map-pattern");
				newMapGroup = request.getParameter("update-map-group");
				if (!validMapPattern(newMapPattern)) {
					error = "invalid-map-pattern";
				} else if (newMapGroup == null) {
					error = "invalid-map-group";
				} else {
					//TODO handle exceptions
					allMaps.set(selectedMapIndex, new LanguageMap(newMapPattern, newMapGroup));
					srxDoc.setModified(true);
					success = "map-updated";
				}
			} else if (removeMap) {
				if (allMaps.remove(selectedMapIndex) != null) {
					srxDoc.setModified(true);
					success = "map-removed";
				} else {
					error = "map-not-removed";
				}
			} else { //if (moveUpMap || moveDownMap) {
				int direction = moveUpMap ? -1 : 1;
				LanguageMap temp;
				try {
					temp = allMaps.get(selectedMapIndex);
					allMaps.set(selectedMapIndex, allMaps.get(selectedMapIndex+direction));
					allMaps.set(selectedMapIndex+direction, temp);
					srxDoc.setModified(true);
					success = "map-moved";
				} catch (IndexOutOfBoundsException e) {
					success = null;
					error = "cant-move-map";
				}
			}
				
		} else {
			error = "not-implemented";
		}
		
		if (success != null) {
			response.sendRedirect("groupsEditor.jsp?success="+success);
		} else if (error != null) {
			response.sendRedirect("groupsEditor.jsp?error="+error);
		} else {
			response.sendRedirect("groupsEditor.jsp");
		}
	}
	
	private boolean validGroupName(String proposedName) {
		//TODO make this validation align with SRX standard for group names
		return proposedName != null && !proposedName.trim().isEmpty();
	}
	
	private boolean validMapPattern(String proposedPattern) {
		//TODO make this validation check for valid regular expressions - compiling a regex may be sufficient
		return proposedPattern != null && !proposedPattern.trim().isEmpty();
	}
}
