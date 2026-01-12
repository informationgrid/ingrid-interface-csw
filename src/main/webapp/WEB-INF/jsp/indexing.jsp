<%--
  **************************************************-
  ingrid-interface-csw
  ==================================================
  Copyright (C) 2014 - 2026 wemove digital solutions GmbH
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
<script type="text/javascript" src="js/jquery-1.8.0.min.js"></script>
<script>
function getState(){
	$.ajaxSetup({ cache: false });
	$.getJSON("indexState.json", {}, function(statusResponse){
		  if(!statusResponse.isRunning){
            document.getElementById('dialog').style.display = '';
            document.getElementById('dialog_done').style.display = '';
            document.getElementById('harvest').style.display = '';
            $("#dialog .content").html(statusResponse.status.replace(/\n/g,"<br />"));
		  } else {
            document.getElementById('dialog').style.display = '';
            document.getElementById('harvest').style.display = 'none';
            $("#dialog .content").html(statusResponse.status.replace(/\n/g,"<br />"));
			setTimeout(getState, 1000);
		  }
	}, "text");
	$.ajaxSetup({ cache: true });
}
</script>
<c:if test="${triggerResult == 'success'}">
<script>
$(function() {
 getState();
});	
</script>
</c:if>
</head>
<body>
	<div id="header">
		<img src="images/logo.gif" alt="InGrid Portal" />
		<h1>Configuration</h1>
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
	
	<c:set var="active" value="indexing" scope="request"/>
	<c:import url="subNavi.jsp"></c:import>
	
	<div id="contentBox" class="contentMiddle">
		<h1 id="head">Start harvesting manually.</h1>
		<div id="content">
			<c:if test="${isScheduled == 'true'}"><h2>Harvesting in progress...</h2></c:if>
            <c:if test="${isScheduled != 'true'}"><h2>You can start the harvesting process manually.</h2></c:if>
			
            <c:if test="${triggerResult == 'error'}">
                <div class="error">Could not start harvesting.</div>
            </c:if>
			
			<form action="indexing.html" method="post" id="indexing">
            Depending on your data sources this step can consume some time (minutes or hours).
			<br/><br/>
			<button type="submit" name="harvest" id="harvest" value="harvest" <c:if test="${isScheduled == 'true'}">style="display:none"</c:if>>Harvest now!</button>
			</form> 
				
        <div class="status" id="dialog" <c:if test="${isScheduled != 'true'}">style="display:none"</c:if>>
            <div class="content">Harvesting in progress, please be patient.</div>
        </div>  

        <div class="status" id="dialog_done" style="display:none">
            <div class="content">All data has been harvested.</div>
        </div>  

		</div>
		
	</div>
	<div id="footer" style="height:100px; width:90%"></div>
</body>
</html>
