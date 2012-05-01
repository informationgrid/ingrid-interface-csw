<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="de.ingrid.interfaces.csw.admin.IngridPrincipal"%><html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>Portal U Administration</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<link rel="StyleSheet" href="/css/ingrid.css" type="text/css" media="all" />
</head>
<body>
	<div id="header">
		<img src="/images/logo.gif" width="168" height="60" alt="Portal U" />
		<h1>Konfiguration</h1>
		<%
		java.security.Principal  principal = request.getUserPrincipal();
		if(principal != null && !(principal instanceof IngridPrincipal.SuperAdmin)) {
		%>
			<div id="language"><a href="auth/logout.html">Logout</a></div>
		<%
		}
		%>
	</div>
	
	<div id="help"><a href="#">[?]</a></div>
	<c:set var="active" value="harvester" scope="request"/>
	<c:import url="subNavi.jsp"></c:import>
	
	<div id="contentBox" class="contentMiddle">
		<h1 id="head">iPlug Konfiguration</h1>
		
		<div id="content">
		<br/>
		<h2>Edit Harvester!</h2>
		
        <form:form method="post" action="edit_ibus_harvester_4.html" modelAttribute="rd">
        <form:hidden path="plugId" />
        
		<table id="konfigForm">
		<tbody>
		<tr>
			<td colspan="2">
				<h3>Edit iPlug request definition:</h3>
			</td>
		</tr>		
        <tr>
			<td class="leftCol">Query</td>
			<td>
	    	<form:input path="queryString" />
	    	<br>
	    	<span>The query the iPlugs records will be requested with.</span>
	    	<form:errors path="queryString" cssClass="error" element="div" />
			</td>
		</tr>
        <tr>
			<td class="leftCol">Timeout</td>
			<td>
	    	<form:input path="timeout" />
	    	<br>
	    	<span>The time in msec to wait for a response after sending a request to the harvesting source.</span>
	    	<form:errors path="timeout" cssClass="error" element="div" />
			</td>
		</tr>
        <tr>
			<td class="leftCol">Pause between requests</td>
			<td>
	    	<form:input path="pause" />
	    	<br>
	    	<span>The time in msec to pause between requests to harvesting source.</span>
	    	<form:errors path="pause" cssClass="error" element="div" />
			</td>
		</tr>
        <tr>
			<td class="leftCol">Records per Call</td>
			<td>
	    	<form:input path="recordsPerCall" />
	    	<br>
	    	<span>The number of records to request at once during fetching of records.</span>
	    	<form:errors path="recordsPerCall" cssClass="error" element="div" />
			</td>
		</tr>
        <tr>
    	<td class="leftCol">&nbsp;</td>
        <td>
        	<button type="submit" name="back" value="back">Back</button>
			<button type="submit" name="save" value="save">Save</button>
        </td>
        </tr>
        </table>
            
        </form:form>
			
		</div>
	</div>
	<div id="footer" style="height:100px; width:90%"></div>
</body>
</html>