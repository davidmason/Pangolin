<% String pageTitleKey = "rule_editor_title";
   Boolean pageRequiresDoc = true; %>
<%@ include file="/includes/header.jsp" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="pangolin.objects.AnnotatedRule" %>
<%@ page import="static pangolin.util.TextUtils.*" %>

<% 
   if (languageRuleName == null) {
	   response.sendRedirect("editor.jsp?error=no-language-selected");
	   return;
   }
   
   boolean newRule = (request.getParameter("add-rule") != null);
   boolean editRule = (request.getParameter("edit-rule") != null);
   boolean removeRule = (request.getParameter("remove-rule") != null);
   boolean moveUp = (request.getParameter("move-rule-up") != null);
   boolean moveDown = (request.getParameter("move-rule-down") != null);
   String ruleName = "";
   String localeCode = "";
   String beforeBreak = "";
   String afterBreak = "";
   String ruleComment = "";
   Map<String, String> annotations = null;
   
   
   if (moveUp) {
	   response.sendRedirect("EditRule?move-rule-up=move-rule-up");
	   return;
   }
   if (moveDown) {
	   response.sendRedirect("EditRule?move-rule-down=move-rule-down");
	   return;
   }
   
   if (removeRule) { %>
	   <p><%= uiStrings.getString("ask-remove-rule") %></p>
	   <a href="EditRule?remove-rule=remove-rule"><%= uiStrings.getString("confirm-remove-rule") %></a>
	   <a href="editor.jsp?cancel-remove"><%= uiStrings.getString("cancel-remove-rule") %></a>
   <%
   
   } else { //adding or editing a rule
	   
	   Rule existingRule = null;
	   boolean breakingRule = true;
	   if (editRule) {
		   if (selectedRuleIndex > -1) {
			   existingRule = srxDoc.getLanguageRules(languageRuleName).get(selectedRuleIndex);
		   } else {
			   response.sendRedirect("editor.jsp?error=no-rule-selected");
			   return;
		   }
		   if (existingRule == null) {
			   response.sendRedirect("editor.jsp?error=problem-loading-rule");
			   return;
		   }
	   }
	   
	   if (editingRule != null) {
		   existingRule = editingRule;
	   }
	   
	   if (existingRule == null) {
		   existingRule = new AnnotatedRule();
	   }
	   
	   breakingRule = existingRule.isBreak();
	   beforeBreak = nullToEmptyString(existingRule.getBefore());
	   afterBreak = nullToEmptyString(existingRule.getAfter());
	   ruleComment = nullToEmptyString(existingRule.getComment());
	   
	   if (existingRule instanceof AnnotatedRule) {
		   AnnotatedRule annotRule = (AnnotatedRule)existingRule;
		   ruleName = nullToEmptyString(annotRule.getRuleName());
		   localeCode = nullToEmptyString(annotRule.getLocaleCode());
		   annotations = annotRule.getAnnotations();
	   }
	   
	   
	   
   
%>
<form action="EditRule" method="POST" name="edit-rule" accept-charset="UTF-8">
  <div id="rule-details">
    <div style="float:right;" id="repo-buttons">
      <input type="submit" name="add-to-repo" value="<%= uiStrings.getString("add_rule_to_repository") %>" /><br/>
      <input type="submit" name="update-in-repo" value="<%= uiStrings.getString("update_rule_in_repository") %>" />
	</div>
    <h2><%= uiStrings.getString("rule_details_heading") %></h2>
	<% if (newRule) { %>
	     <input type="hidden" name="add-rule" value="add-rule" />
	<% }
	  if (editRule) { %>
	     <input type="hidden" name="edit-rule" value="edit-rule" />
	<% } %>
	<table>
	  <thead>
	  </thead>
	  <tbody>
	    <tr>
		  <td><%= uiStrings.getString("language_rules_group") %></td>
		  <td><input type="text" name="language-rule" readonly="readonly" value="<%= languageRuleName %>" /></td>
		</tr>
		<tr>
		  <td><%= uiStrings.getString("rule_name") %></td>
		  <td><input type="text" name="rule-name" value="<%= ruleName %>" /></td>
		</tr>
		<tr>
		  <td><%= uiStrings.getString("locale_code") %></td>
		  <td><input type="text" name="locale-code" value="<%= localeCode %>" /></td>
		</tr>
		<tr>
		  <td><%= uiStrings.getString("rules_table_type") %></td>
		  <td>
		    <input type="radio" name="breaking-rule" value="no-break" <%= breakingRule ? "" : "checked=\"checked\" " %>/> <%= uiStrings.getString("no_break") %>
			<input type="radio" name="breaking-rule" value="break" <%= breakingRule ? "checked=\"checked\"" : "" %> /> <%= uiStrings.getString("break") %>
          </td>
		</tr>
		<tr>
		  <td><%= uiStrings.getString("rules_table_before_break") %></td>
		  <td><input type="text" name="pattern-before" value="<%= beforeBreak %>" /></td>
		</tr>
		<tr>
		  <td><%= uiStrings.getString("rules_table_after_break") %></td>
		  <td><input type="text" name="pattern-after" value="<%= afterBreak %>" /></td>
		</tr>
		<tr>
		  <td><%= uiStrings.getString("rule_comment") %></td>
		  <td><textarea name="rule-comment"><%= ruleComment %></textarea></td>
		</tr>
	  </tbody>
	</table>
	<input type="submit" value="<%= uiStrings.getString(editRule ? "button_edit_rule" : "button_add_rule") %>" />
	
	<div id="annotations-box">
	  <h2><%= uiStrings.getString("annotation_heading") %></h2>
	  <table>
	    <thead>
	    </thead>
	    <tbody>
	      <tr>
		    <td><%= uiStrings.getString("annotation_key") %></td>
	        <td><input type="text" name="annotation-key" value="" /></td>
	      </tr>
	      <tr>
		    <td><%= uiStrings.getString("annotation_value") %></td>
	        <td><input type="text" name="annotation-value" value="" /></td>
	      </tr>
	      <tr>
		    <td></td>
	        <td><input type="submit" name="add-annotation" value="<%= uiStrings.getString("add_annotation") %>" /></td>
	      </tr>
	    </tbody>
	  </table>
	  
	  <!-- output all the annotations -->
	  <% if (annotations != null) { 
		  //output table start
		  %><br/><%
		     int count = 0;
		     for (Entry<String, String> annot : annotations.entrySet()) {
		    	 count++;%>
		    	 <%= annot.getKey() %> = <%= annot.getValue() %><br/>
		<%   }
		  //output table finish
	     } %>
	</div>

	
  </div>
  
  <% if (true) { %>
  
  <div id="repository-rules">
    <input type="submit" name="refresh-repository" value="<%= uiStrings.getString("button_refresh_repository_rules") %>" />
    <!-- display repository rules -->
    <% ArrayList<String> repoRules;
       try {
           repoRules = (ArrayList<String>)request.getSession().getAttribute("repository-rules");
       } catch (ClassCastException e) {
    	   repoRules = null;
       } %>
    
    <div id="repo-rules-table">
      <table>
        <thead>
          <tr>
            <th></th>
            <th><%= uiStrings.getString("repo_rules_table_doc_name") %></th>
          </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <% //logic to populate list of documents
           if (repoRules == null) { %>
             <tr><td colspan="2"><%= uiStrings.getString("no_repository_rules") %></td></tr>
        <% } else {
	           for (String repoRuleName : repoRules) { %>
                 <tr>
                   <td><input type="radio" name="selected-repo-rule" value="<%= repoRuleName %>" /></td>
                   <td><%= repoRuleName %></td>
                 </tr>
        <%     }
	       } %>
        </tbody>
      </table>
    </div>
    <input type="submit" name="load-repository" value="<%= uiStrings.getString("button_load_rule_from_repository") %>" />
  
  </div>
  
  <% } %>
  
</form>

<% } %>

<%@ include file="/includes/footer.jsp" %>