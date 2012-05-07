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
		<img src="images/logo.gif" width="168" height="60" alt="InGrid" />
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
	
	<c:set var="active" value="search" scope="request"/>
	<c:import url="subNavi.jsp"></c:import>
	
	<div id="contentBox" class="contentMiddle">
		<h1 id="head">Test Search</h1>
		<div id="content">
			<br />
			<h2>Test the search capabilities! Please form your request.</h2>
			<form method="POST" action="search.html">
				<table id="konfigForm">
					<tr>
						<td class="leftCol">Filter query:</td>
						<td><textarea id="query" name="query" rows="5" cols="30">${query}</textarea></td>
					</tr>
					<tr>
						<td class="leftCol">&nbsp;</td>
						<td><input type="submit" value="Suchen"/></td>
					</tr>
				</table>
			</form>
			
			<c:if test="${!empty hits}">
				<div class="hitCount">Ergebnisse 1-${hitCount} von ${totalHitCount}</div>
				
				<c:forEach items="${hits}" var="hit">
					<div class="searchResult">
					   <h3>
    					   <a href="#">${hit['title']}</a>
					   </h3>
					   <span>${hit['abstract']}</span>
					</div>
				</c:forEach>
				<br /><br />
			</c:if>
		</div>
	</div>
	<div id="footer" style="height:100px; width:90%"></div>
</body>
</html>