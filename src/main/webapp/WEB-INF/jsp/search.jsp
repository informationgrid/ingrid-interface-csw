<%--
  **************************************************-
  ingrid-interface-csw
  ==================================================
  Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
	
	<c:set var="active" value="search" scope="request"/>
	<c:import url="subNavi.jsp"></c:import>
	
	<div id="contentBox" class="contentMiddle">
		<h1 id="head">Test Search</h1>
		<div id="content">
			<br />
			<h2>Test the search capabilities! Please form your request.</h2>
			<form method="post" action="search.html">
				<table id="konfigForm">
					<tr>
						<td class="leftCol">Filter query:</td>
						<td>
						<textarea id="query" name="query" rows="5" cols="30">${query}</textarea>
                        <br />
                        <span>Add your search here. Single word queries or complete GetRecords XML Request.</span>
						</td>
					</tr>
					<tr>
						<td class="leftCol">&nbsp;</td>
						<td>
                            <input type="submit" value="Search"/>
                            <c:if test="${error != null}">
                                <span class="error">${error}</span>
                            </c:if>
                        </td>
					</tr>
				</table>
			</form>
			
			<c:if test="${!empty hits}">
				<div class="hitCount">Hits 1-${hitCount} of ${totalHitCount}</div>
				
				<c:forEach items="${hits}" var="hit">
					<div class="searchResult">
					   <h3>
    					   <a href="${hit['link']}" target="_new">${hit['title']}</a>
					   </h3>
					   <span>${hit['abstract']}</span>
					</div>
				</c:forEach>
				<br /><br />
			</c:if>
            <c:if test="${empty hits}">
                <div class="hitCount">Hits ${hitCount} of ${totalHitCount}</div>
            </c:if>
		</div>
	</div>
	<div id="footer" style="height:100px; width:90%"></div>
</body>
</html>
