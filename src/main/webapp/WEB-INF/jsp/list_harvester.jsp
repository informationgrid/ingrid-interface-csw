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
		<img src="images/logo.gif" width="168" height="60" alt="Portal U" />
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
	
	
	<c:set var="active" value="harvester" scope="request"/>
	<c:import url="subNavi.jsp"></c:import>
	
	<div id="contentBox" class="contentMiddle">
		<h1 id="head">Harvester configuration</h1>
		<div id="content">
		
		<p><strong>Last execution:</strong> <fmt:formatDate value="${lastExecution}" type="date" pattern="yyyy-MM-dd hh:mm:ss"/></p>
		
		<br/>
		<h2>Manage Harvester:</h2>
		<c:if test="${not empty errorKey}">
		<div class="error"><spring:message code="${errorKey}"/></div>
		</c:if>
		
        <form:form method="post" action="list_harvester.html" modelAttribute="harvester">

        <table class="data">
        <tr>
            	<th class="leftCol">Harvester</th>
            	<th>Harvester Type</th>
            	<th>&nbsp;</th>
        </tr>
        <c:set var="i" value="0" />
        <c:forEach items="${harvesterConfigs}" var="harvesterConfig">
        <tr>
        	<td class="leftCol">${harvesterConfig.name}</td>
        	<td><spring:message code="${harvesterConfig.className}"/></td>
            <td>
            	<button type="submit" name="edit" value="${i}" id="${i}">Edit</button>
                <button type="submit" name="delete" value="${i}" id="${i}" onClick="return confirmSubmit('Really delete this harvester?')">Delete</button>                	
                <c:if test="${harvesterConfig.className == 'de.ingrid.interfaces.csw.harvest.ibus.IBusHarvester' and not empty harvesterConfig.communicationXml}">
                <button type="submit" name="iPlugList" value="${i}" id="${i}">Go To iPlug List</button>
                </c:if>
            </td>
        </tr>
        <c:set var="i" value="${i + 1}" />
        </c:forEach>
        </table>
		
        <br/>
        
		<table id="konfigForm">
		<tbody>
		<tr>
			<td colspan="2">
				<h3>Create a new harvester:</h3>
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
    		<td class="leftCol">Type</td>
    		<td>
    			<form:select path="type">
    				<form:options items="${harvesterTypes}" />
    			</form:select>
    			<br>
    			<span>The type of harvester.</span>
    			<form:errors path="type" cssClass="error" element="div" />
    		</td>
    </tr>
        <tr>
    	<td class="leftCol">&nbsp;</td>
        <td>
        	<button type="submit" name="new" value="new">New</button>
        </td>
        </tr>
        <tr>
        <td class="leftCol">&nbsp;</td>
        <td>
    		<p></p>
    		<div id="help">
    		<p>
    		<strong>Available Harvester Types:</strong>
    		<ul>
    		<li><strong>iBus harvester</strong><br/>
    		Harvests iPlugs connected to an InGrid iBus.
    		</li>
    		<li><strong>GDI-DE test data harvester</strong><br/>
    		Harvests test data for the GDI-DE Testsuite (for test purpose only).
    		</li>
    		</ul>
    		</p>
    		</div>
        </td>
        </tr>
        </table>
        </form:form>
			
		</div>
	</div>
	<div id="footer" style="height:100px; width:90%"></div>
</body>
</html>