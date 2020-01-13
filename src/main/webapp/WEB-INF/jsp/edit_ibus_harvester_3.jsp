<%--
  **************************************************-
  ingrid-interface-csw
  ==================================================
  Copyright (C) 2014 - 2020 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.1 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  http://ec.europa.eu/idabc/eupl5
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="de.ingrid.interfaces.csw.admin.IngridPrincipal"%>
<%@page import="de.ingrid.utils.PlugDescription"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>InGrid Administration</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<link rel="StyleSheet" href="css/ingrid.css" type="text/css" media="all" />
<script language="JavaScript">
<!--
function confirmSubmit(val)
{
var agree=confirm(val);
if (agree)
	return true ;
else
	return false ;
}
// -->
</script>

</head>
<body>
	<div id="header">
		<img src="images/logo.gif" alt="InGrid Portal" />
		<h1>CSW interface configuration</h1>
		<%
		java.security.Principal  principal = request.getUserPrincipal();
		if(principal != null && !(principal instanceof IngridPrincipal.SuperAdmin)) {
		%>
			<div id="language"><a href="logout.html">Logout</a></div>
		<%
		}
		%>
	</div>
	
	<div id="help"></div>
	<c:set var="active" value="harvester" scope="request"/>
	<c:import url="subNavi.jsp"></c:import>
	
	<div id="contentBox" class="contentMiddle">
		<h1 id="head">Harvester configuration</h1>
		
		<div id="content">
		<br/>
		
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
            	<th class="leftCol">iPlug (Name)</th>
            	<th>Query</th>
            	<th>&nbsp;</th>
        </tr>
		<tr>
			<td colspan="3">
				<h3>Enabled iPlugs:</h3>
			</td>
		</tr>		
		<c:set var="i" value="0" />
        <c:forEach items="${enabledIPlugs}" var="enabledIPlug">
        <tr>
	    	<td class="leftCol">${enabledIPlug.dataSourceName}<br/><small>${enabledIPlug.plugId} (harvested: ${enabledIPlug.indexedRecords})</small></td>
	    	<td>${enabledIPlug.queryString}</td>
	        <td>
	        	<button type="submit" name="edit" value="${enabledIPlug.plugId}" id="${enabledIPlug.plugId}">Edit</button>
	        	<button type="submit" name="disable" value="${enabledIPlug.plugId}" id="${enabledIPlug.plugId}" onClick="return confirmSubmit('Really disable this configuration?')">Disable</button>
	        </td>
	    </tr>
        <c:set var="i" value="${i + 1}" />
        </c:forEach>
        
		<tr>
			<td colspan="3">
				<h3>Available iPlugs:</h3>
			</td>
		</tr>		
		
		<c:set var="i" value="0" />
        <c:forEach items="${availableIPlugs}" var="plugDescription">
        <tr>
	    	<td class="leftCol"><%= ((PlugDescription)pageContext.findAttribute("plugDescription")).getDataSourceName() %><br/><small><%= ((PlugDescription)pageContext.findAttribute("plugDescription")).getPlugId() %></small></td>
	    	<td>&nbsp;</td>
	        <td>
	        	<button type="submit" name="enable" value="<%= ((PlugDescription)pageContext.findAttribute("plugDescription")).getPlugId() %>" id="<%= ((PlugDescription)pageContext.findAttribute("plugDescription")).getPlugId() %>">Enable</button>
	        </td>
	    </tr>
        <c:set var="i" value="${i + 1}" />
        </c:forEach>
        
        </table>

    	<br />
        <button type="submit" name="back" value="back">Back</button>
        <br />    
        <br />
        </form:form>
			
		</div>
	</div>
	<div id="footer" style="height:100px; width:90%"></div>
</body>
</html>
