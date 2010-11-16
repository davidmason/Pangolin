<%@ page import="pangolin.i18n.*" %>
<%@ page import="java.util.Locale" %>
<% //page i18n code
	//TODO add logic for changing locale
	Locale uiLocale = Locale.ENGLISH;
	
	//uncomment to test for properties file
	//(replaces all multilingual strings with zzz)
	//uiLocale = new Locale("zz");

	//TODO store locale in a session variable, or store strings in a session variable
	
	//create resource bundle of locale-appropriate ui strings
	ILocalStringResource uiStrings = new ResourceBundleWrapper("UIStrings", uiLocale);
%>