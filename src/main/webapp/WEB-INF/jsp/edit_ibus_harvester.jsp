<%--
  **************************************************-
  ingrid-interface-csw
  ==================================================
  Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
        <form:form method="post" action="edit_ibus_harvester.html" modelAttribute="harvester">

        <form:hidden path="id" />

		<table id="konfigForm">
		<tbody>
		<tr>
			<td colspan="2">
				<h3>Edit a harvester:</h3>
			</td>
			
		</tr>		
        <tr>
        	<td class="leftCol">Name</td>
            <td>
            	<form:input path="name" />
            	<br>
            	<span>The name of the harvester.</span>
            	<form:errors path="name" cssClass="error" element="div" />
            </td>
        </tr>
        <tr>
    		<td class="leftCol">Working directory</td>
    		<td>
        	<form:input path="workingDirectory" />
        	<br>
        	<span>The name of the workingDirectory.</span>
        	<form:errors path="workingDirectory" cssClass="error" element="div" />
    		</td>
    	</tr>
        <tr>
    	<td class="leftCol">&nbsp;</td>
        <td>
            <button type="submit" name="back" value="back">Back</button>
			<button type="submit" name="next" value="next">Next</button>
        </td>
        </tr>
        </table>
            
        </form:form>
			
		</div>
	</div>
	<div id="footer" style="height:100px; width:90%"></div>
</body>
</html>
