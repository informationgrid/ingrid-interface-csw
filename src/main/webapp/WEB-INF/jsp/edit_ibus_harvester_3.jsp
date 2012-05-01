<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="de.ingrid.interfaces.csw.admin.IngridPrincipal"%>
<%@page import="de.ingrid.utils.PlugDescription"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
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
			<div id="language"><a href="/auth/logout.html">Logout</a></div>
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
		
        <form:form method="post" action="edit_ibus_harvester_3.html" modelAttribute="harvester">

		<table id="konfigForm">
		<tbody>
		<tr>
			<td colspan="2">
				<h3>Edit iPlugs:</h3>
			</td>
		</tr>		
        <tr>
			<td class="leftCol">iBus proxy id</td>
			<td>
	    	<form:input path="iBusProxyId" readonly="true"/>
	    	<br>
	    	<span>The proxy id of the iBus.</span>
			</td>
		</tr>
		</tbody>
		</table>
		
		<h3>iPlugs:</h3>
		
        <table class="data">
        <tr>
            	<th class="leftCol">iPlug</th>
            	<th>Name</th>
            	<th>Query</th>
            	<th>&nbsp;</th>
        </tr>
		<tr>
			<td colspan="4">
				<h3>Enabled iPlugs:</h3>
			</td>
		</tr>		
		<c:set var="i" value="0" />
        <c:forEach items="${enabledIPlugs}" var="enabledIPlug">
        <tr>
	    	<td>${enabledIPlug.plugId}</td>
	    	<td>${enabledIPlug.dataSourceName}</td>
	    	<td>${enabledIPlug.queryString}</td>
	        <td>
	        	<button type="submit" name="edit" value="${enabledIPlug.plugId}" id="${enabledIPlug.plugId}">Edit</button>
	        	<button type="submit" name="disable" value="${enabledIPlug.plugId}" id="${enabledIPlug.plugId}">Disable</button>
	        </td>
	    </tr>
        <c:set var="i" value="${i + 1}" />
        </c:forEach>
        
		<tr>
			<td colspan="4">
				<h3>Available iPlugs:</h3>
			</td>
		</tr>		
		
		<c:set var="i" value="0" />
        <c:forEach items="${availableIPlugs}" var="plugDescription">
        <tr>
	    	<td><%= ((PlugDescription)pageContext.findAttribute("plugDescription")).getPlugId() %></td>
	    	<td><%= ((PlugDescription)pageContext.findAttribute("plugDescription")).getDataSourceName() %></td>
	    	<td>&nbsp;</td>
	        <td>
	        	<button type="submit" name="enable" value="<%= ((PlugDescription)pageContext.findAttribute("plugDescription")).getPlugId() %>" id="<%= ((PlugDescription)pageContext.findAttribute("plugDescription")).getPlugId() %>">Enable</button>
	        </td>
	    </tr>
        <c:set var="i" value="${i + 1}" />
        </c:forEach>
        
        </table>

		<table id="konfigForm">
		<tbody>
        <tr>
    	<td class="leftCol">&nbsp;</td>
        <td>
        	<button type="submit" name="back" value="back">Back</button>
        	<button type="submit" name="save" value="save">Save</button>
        </td>
        </tr>
        </tbody>
        </table>
            
        </form:form>
			
		</div>
	</div>
	<div id="footer" style="height:100px; width:90%"></div>
</body>
</html>