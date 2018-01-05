<%--
  **************************************************-
  ingrid-interface-csw
  ==================================================
  Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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

<%@page import="de.ingrid.interfaces.csw.admin.IngridPrincipal"%><html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>
<title>InGrid Administration</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="description" content="" />
<meta name="keywords" content="" />
<meta name="author" content="wemove digital solutions" />
<meta name="copyright" content="wemove digital solutions GmbH" />
<link rel="StyleSheet" href="css/ingrid.css" type="text/css" media="all" />
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
		
        <form:form method="post" action="edit_ibus_harvester_2.html" modelAttribute="harvester">

		<table id="konfigForm">
		<tbody>
		<tr>
			<td colspan="2">
				<h3>Edit communication to iBus:</h3>
			</td>
		</tr>		
        <tr>
			<td class="leftCol">iBus IP</td>
			<td>
	    	<form:input path="iBusIp" />
	    	<br>
	    	<span>The IP address of the iBus (i.e. 127.0.0.1).</span>
	    	<form:errors path="iBusIp" cssClass="error" element="div" />
			</td>
		</tr>
        <tr>
			<td class="leftCol">iBus port</td>
			<td>
	    	<form:input path="iBusPort" />
	    	<br>
	    	<span>The port of the iBus (i.e. 9900).</span>
	    	<form:errors path="iBusPort" cssClass="error" element="div" />
			</td>
		</tr>
        <tr>
			<td class="leftCol">iBus proxy id</td>
			<td>
	    	<form:input path="iBusProxyId" />
	    	<br>
	    	<span>The proxy id of the iBus (i.e. /ingrid-group:kug-ibus).</span>
	    	<form:errors path="iBusProxyId" cssClass="error" element="div" />
			</td>
		</tr>
        <tr>
    	<td class="leftCol">&nbsp;</td>
        <td>
        	<button type="submit" name="back" value="back">Back</button>
			<button type="submit" name="next" value="next">Save Communication &amp; Next</button>
        </td>
        </tr>
        </table>
            
        </form:form>
			
		</div>
	</div>
	<div id="footer" style="height:100px; width:90%"></div>
</body>
</html>
