<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="pangolin.services.*" %>
<%@ page import="net.sf.okapi.lib.segmentation.*" %>
<%@ include file="/includes/loadLocalStrings.jsp" %>
<%@ include file="/includes/loadSRX.jsp" %>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en-gb" lang="en-gb" dir="ltr" >
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="description" content="" />
	
	<title><%= uiStrings.getString(pageTitleKey) %></title>
	<!-- insert link to stylesheet -->
	<link href="styles/general.css" rel="stylesheet" type="text/css" />
	
	<!-- insert links to IE7 and IE 6 stylesheets -->
</head>

<body>

<div id="title">
  <h1><%= uiStrings.getString("pangolin_title") %> SRX Editor</h1>
  
  <% if (pageRequiresDoc) { %>
    <div id="doc-management">
      <div id="doc-name">
        <p class="filename"><%= (fileName == null) ? uiStrings.getString("untitled_doc") : fileName %></p>
        <form action="SetDocName" method="post" accept-charset="UTF-8">
          <input type="text" name="new-doc-name" /> <input type="submit" value="<%= uiStrings.getString("change_doc_name") %>" />
        </form>
	  </div>
	  <div id="doc-controls">
        <a href="SaveSRXDoc"><%= uiStrings.getString("save_document") %></a>
        <a href="UploadSRXDoc"><%= uiStrings.getString("upload_document") %></a>
        <a href="UploadSRXDoc?replace=replace"><%= uiStrings.getString("upload_replace_document") %></a>
        <a href="chooseFile.jsp"><%= uiStrings.getString("change_document") %></a>
	  </div>
    </div>
  <% } %>
  
</div>

<% String error = request.getParameter("error");
   if (error != null) { %>
     <div id="error-box"><%= uiStrings.getString("error_label") %> <%= uiStrings.getString(error) %></div>
<% } %>