<% String pageTitleKey = "main_window_title"; %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/loadSRX.jsp" %>

<!-- imports for internationalization -->
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="net.sf.okapi.common.resource.*" %>
<%@ page import="static pangolin.util.TextUtils.*" %>


<div id="doc-management">
  <p class="filename"><%= (fileName == null) ? uiStrings.getString("untitled_doc") : fileName %></p>
  <a class="button" href="SaveSRXDoc"><%= uiStrings.getString("save_document") %></a><br/>
  <a class="button" href="UploadSRXDoc"><%= uiStrings.getString("upload_document") %></a><br/>
  <a class="button" href="UploadSRXDoc?replace=replace"><%= uiStrings.getString("upload_replace_document") %></a><br/>
  <a class="button" href="chooseFile.jsp"><%= uiStrings.getString("change_document") %></a>
  
  <form action="SetDocName" method="post" accept-charset="UTF-8">
    <input type="text" name="new-doc-name" /> <input type="submit" value="<%= uiStrings.getString("change_doc_name") %>" />
  </form>
</div>

<div id="editor">
 <div id="rules-selection">
   <p>
     <%= uiStrings.getString("rules_displayed") %>
     <input type="text" readonly="readonly" value="<%= (languageRuleName != null) ? languageRuleName : uiStrings.getString("no_language_groups") %>" />
     <a class="button" href="groupsEditor.jsp"><%= uiStrings.getString("manage_groups") %></a>
   </p>
 </div>
   
 <form id="rules-form" action="ruleEditor.jsp" method="post" name="rules" accept-charset="UTF-8">
   <div id="rules-table">
     <table>
       <thead>
         <tr>
           <th></th>
           <th><%= uiStrings.getString("rules_table_type") %></th>
           <th><%= uiStrings.getString("rules_table_before_break") %></th>
           <th><%= uiStrings.getString("rules_table_after_break") %></th>
         </tr>
       </thead>
       <tbody>
         <% ArrayList<Rule> rulesToDisplay = srxDoc.getLanguageRules(languageRuleName);
            if (rulesToDisplay != null) {
         	 int count = -1;
              for (Rule r : rulesToDisplay) { %>
               <tr>
                 <td><input type="radio" name="rule-index" value="<%= ++count %>" <%= (count == selectedRuleIndex) ? "checked=\"checked\"" : "" %> /></td>
                 <td><%= uiStrings.getString(r.isBreak() ? "break" : "no_break") %></td>
                 <td><%= r.getBefore() %></td>
                 <td><%= r.getAfter() %></td>
               </tr>
         <%   }
            } else { %>
              <tr><td colspan="4"><%= uiStrings.getString("no_rules_message") %></td></tr>
         <% } %>
       </tbody>
     </table>
   </div>
   <div id="rules-control">
     <input type="submit" name="add-rule" value="<%= uiStrings.getString("button_add_rule") %>" />
     <input type="submit" name="edit-rule" value="<%= uiStrings.getString("button_edit_rule") %>" />
     <input type="submit" name="remove-rule" value="<%= uiStrings.getString("button_remove_rule") %>" />
     <input type="submit" name="move-rule-up" value="<%= uiStrings.getString("button_move_rule_up") %>" />
     <input type="submit" name="move-rule-down" value="<%= uiStrings.getString("button_move_rule_down") %>" />
     
     <!-- This function does not appear possible without client-side processing
     <input type="submit" name="char-info" value="%= uiStrings.getString("button_char_info") %>" /> -->
   </div>
 </form>
 
 <div id="sample-segmentation">
   <h2>
     <%= uiStrings.getString("sample_text") %> 
     <%= uiStrings.getString("sample_text_instructions") %>
   </h2>
   <form id="sample-text-form" action="UpdateMaskRule" method="post" name="mask" accept-charset="UTF-8">
     <input type="submit" name="mask-rule-button" value="<%= uiStrings.getString("button_mask_rule") %>" />
     <input type="text" name="mask-rule-text" value="<%= nullToEmptyString(srxDoc.getMaskRule()) %>" />
   </form>
   <form id="sample-text-form" action="UpdateSample" method="post" name="sample" accept-charset="UTF-8">
     <textarea name="sample-text" class="sample-text" id="sample-text-plain" rows="7" cols="100"><%= srxDoc.getSampleText() %></textarea><br/>
     <input type="submit" name="update-sample" value="Update" /><!--  TODO i18n -->
     <input type="radio" name="test-rules" value="current-rules" checked="checked" /> <%= uiStrings.getString("test_current_rules") %>
     <input type="radio" name="test-rules" value="language-rules" /> <%= uiStrings.getString("test_language_rules") %>
     <input type="text" name="test-rules-lang" style="width:130px;" /><br/>
     <textarea class="sample-text" id="sample-text-segmented" rows="7" cols="100"><%= SegmentationService.segment(srxDoc.getSampleText(), srxDoc, languageRuleName) %></textarea>
   </form>
 </div>
</div>
	
<%@ include file="/includes/footer.jsp" %>