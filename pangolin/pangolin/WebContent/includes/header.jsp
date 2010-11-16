<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="pangolin.services.*" %>
<%@ page import="net.sf.okapi.lib.segmentation.*" %>
<%@ include file="/includes/loadLocalStrings.jsp" %>
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

<h1><%= uiStrings.getString("pangolin_title") %> SRX Editor</h1>

<% String error = request.getParameter("error");
   if (error != null) { %>
     <div id="error-box"><%= uiStrings.getString("error_label") %> <%= uiStrings.getString(error) %></div>
<% } %>