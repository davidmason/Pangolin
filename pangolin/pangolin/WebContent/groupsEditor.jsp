<% String pageTitleKey = "group_editor_title"; %>
<%@ include file="/includes/header.jsp" %>
<%@ include file="/includes/loadSRX.jsp" %>

<a class="button back-button" href="editor.jsp"><%= uiStrings.getString("return_to_editor") %></a>

<form action="EditGroups" method="POST" name="edit-groups" accept-charset="UTF-8">
  <div id="groups-box">
	  <div id="groups-table">
	    <table>
	      <thead>
	        <tr>
	          <th></th>
	          <th><%= uiStrings.getString("groups_table_group_name") %></th>
	        </tr>
	      </thead>
	      <tfoot></tfoot>
	      <tbody>
	      <% //logic to populate list of groups
	        String selectedGroup = request.getParameter("selected-group");
	        for (String groupName : srxDoc.getAllLanguageRules().keySet()) {
	            if (languageRuleName == null) {
	                languageRuleName = groupName;
	                request.getSession().setAttribute("languageRuleName", languageRuleName);
	            } %>
	            <tr>
	              <td><input type="radio" name="selected-group" value="<%= groupName %>" <%= groupName.equals(selectedGroup) ? "checked=\"checked\"" : "" %> /></td>
	              <td <%= groupName.equals(languageRuleName) ? "id=\"working-rule-cell\"" : "" %>><%= groupName %></td>
	            </tr>
	      <% }
	         if (languageRuleName == null) { %>
	            <tr><td colspan="2"><%= uiStrings.getString("no_language_groups") %></td></tr>
	      <% } %>
	      </tbody>
	    </table>
	  </div>
	  <div id="groups-control">
	    <input type="submit" name="use-group" value="<%= uiStrings.getString("button_use_group") %>" /><br/>
	    <input type="submit" name="add-group" value="<%= uiStrings.getString("button_add_group") %>" /> <input type="text" name="new-group-name" /><br/>
	    <input type="submit" name="rename-group" value="<%= uiStrings.getString("button_rename_group") %>" /> <input type="text" name="rename-group-name" /><br/>
	    <input type="submit" name="remove-group" value="<%= uiStrings.getString("button_remove_group") %>" />
	  </div>
  </div>
  <!-- using same form for groups and mappings to allow preservation of selected items -->
  
  <div id="maps-box">
	  <div id="maps-table">
	    <table>
	      <thead>
	        <tr>
	          <th></th>
	          <th><%= uiStrings.getString("maps_table_pattern") %></th>
	          <th><%= uiStrings.getString("maps_table_group") %></th>
	        </tr>
	      </thead>
	      <tfoot></tfoot>
	      <tbody>
	      <% //logic to populate list of maps
	        //String selectedMap = request.getParameter("selected-map");
	        int selectedMapIndex = -1;
	        try {
	        	selectedMapIndex = Integer.parseInt(request.getParameter("selected-map"));
	        } catch (NumberFormatException e) {
	        	selectedMapIndex = -1;
	        }
	        int mapIndex = -1;
	        for (LanguageMap langMap : srxDoc.getAllLanguagesMaps()) {
	             %>
	            <tr>
	              <td><input type="radio" name="selected-map-index" value="<%= ++mapIndex %>" <%= mapIndex == selectedMapIndex ? "checked=\"checked\"" : "" %> /></td>
	              <td><%= langMap.getPattern() %></td><!-- TODO make sure pattern will not disrupt the HTML -->
	              <td><%= langMap.getRuleName() %></td>
	            </tr>
	      <% }
	         if (mapIndex == -1) { %>
	            <tr><td colspan="3"><%= uiStrings.getString("no_maps") %></td></tr>
	      <% } %>
	      </tbody>
	    </table>
	  </div>
	  
	  <div id="maps-control">
	    <input type="submit" name="add-map" value="<%= uiStrings.getString("button_add_map") %>" />
	    <%= uiStrings.getString("label_new_map_pattern") %> <input type="text" name="new-map-pattern" />
	    <%= uiStrings.getString("label_new_map_group") %> <select name="new-map-group">
	      <% if (languageRuleName == null) { %>
	           <option><%= uiStrings.getString("no_language_groups") %></option>
	      <% }
	         for (String groupName : srxDoc.getAllLanguageRules().keySet()) { %>
	          <option><%= groupName %></option>
	      <% } %>
	    </select><br/>
	    
	    <input type="submit" name="update-map" value="<%= uiStrings.getString("button_update_map") %>" />
	    <%= uiStrings.getString("label_edit_map_pattern") %> <input type="text" name="update-map-pattern" />
	    <%= uiStrings.getString("label_edit_map_group") %> <select name="update-map-group">
	      <% if (languageRuleName == null) { %>
	           <option><%= uiStrings.getString("no_language_groups") %></option>
	      <% }
	         for (String groupName : srxDoc.getAllLanguageRules().keySet()) { %>
	          <option><%= groupName %></option>
	      <% } %>
	    </select><br/>
	    
	    <input type="submit" name="remove-map" value="<%= uiStrings.getString("button_remove_map") %>" />
	    <input type="submit" name="move-map-up" value="<%= uiStrings.getString("button_move_map_up") %>" />
	    <input type="submit" name="move-map-down" value="<%= uiStrings.getString("button_move_map_down") %>" />
	  </div>
  </div>
  
  
</form>

<%@ include file="/includes/footer.jsp" %>