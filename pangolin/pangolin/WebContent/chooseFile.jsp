<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<% String pageTitleKey = "choose_doc_title"; %>
<%@ include file="/includes/header.jsp" %>

<%@ page import="java.util.ArrayList" %>





<% if (request.getSession().getAttribute("srxDoc") != null) { %>
     <a class="button back-button" href="editor.jsp"><%= uiStrings.getString("return_without_loading") %></a>
<% } %>

<form action="LoadSRXDoc" method="POST" name="choose-document" accept-charset="UTF-8" enctype="multipart/form-data">
  <div id="load-options">
    <div class="load-option">
      <p><%= uiStrings.getString("create_blank_document") %></p>
      <input type="submit" name="load-new" value="<%= uiStrings.getString("load_document") %>" />
    </div>
    <div class="load-option">
      <p><%= uiStrings.getString("create_sample_document") %></p>
	  <input type="submit" name="load-test" value="<%= uiStrings.getString("load_document") %>" />
	</div>
	<div class="load-option">
	  <p><%= uiStrings.getString("load_local_document") %></p>
	  <%= uiStrings.getString("local_document_location") %> <input type="file" name="local-file" />
	  <input type="submit" name="load-local" value="<%= uiStrings.getString("load_document") %>" />
	</div>
	<div class="load-option">
	  <p><%= uiStrings.getString("load_web_document") %></p>
	  <%= uiStrings.getString("web_document_url") %> <input type="text" class="url-box" name="file-url" value="http://" />
	  <input type="submit" name="load-url" value="<%= uiStrings.getString("load_document") %>" />
	</div>
  </div>
  
  <div id="repository-documents">
    <input type="submit" name="refresh-repository" value="<%= uiStrings.getString("button_refresh_repository") %>" />
    <!-- display repository files -->
    <% ArrayList<String> repoDocs;
       try {
           repoDocs = (ArrayList<String>)request.getSession().getAttribute("repository-docs");
       } catch (ClassCastException e) {
    	   repoDocs = null;
       } %>
    
    <div id="repo-docs-table">
      <table>
        <thead>
          <tr>
            <th></th>
            <th><%= uiStrings.getString("docs_table_doc_name") %></th>
          </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <% //logic to populate list of documents
           if (repoDocs == null) { %>
             <tr><td colspan="2"><%= uiStrings.getString("no_repository_docs") %></td></tr>
        <% } else {
	           for (String docName : repoDocs) { %>
                 <tr>
                   <td><input type="radio" name="selected-document" value="<%= docName %>" /></td>
                   <td><%= docName %></td>
                 </tr>
        <%     }
	       } %>
        </tbody>
      </table>
    </div>
    <input type="submit" name="load-repository" value="<%= uiStrings.getString("button_load_from_repository") %>" />
  
  </div>
  
  
  
  
  
</form>

<%@ include file="/includes/footer.jsp" %>