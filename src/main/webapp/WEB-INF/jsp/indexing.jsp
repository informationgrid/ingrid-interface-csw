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
<link rel="StyleSheet" href="css/ingrid.css" type="text/css" media="all" />
<script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
<script>
function getState(){
	$.get("indexState.html", function(data){
		  if(data == 'false'){
            document.getElementById('dialog').style.display = 'none';
            document.getElementById('dialog_done').style.display = '';
            document.getElementById('harvest').style.display = '';
		  }else{
            document.getElementById('dialog').style.display = '';
            document.getElementById('harvest').style.display = 'none';
			setTimeout(getState, 1000);
		  }
	}, "text");
}
</script>
<c:if test="${triggerResult == 'success'}">
	<script>getState();</script>
</c:if>
</head>
<body>
	<div id="header">
		<img src="images/logo.gif" width="168" height="60" alt="Portal U" />
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
	
	<div id="help"><a href="#">[?]</a></div>
	
	<c:set var="active" value="indexing" scope="request"/>
	<c:import url="subNavi.jsp"></c:import>
	
	<div id="contentBox" class="contentMiddle">
		<h1 id="head">Start harvesting manually.</h1>
		<div id="content">
			<h2>You can start the harvesting process manually.</h2>
			
            <c:if test="${triggerResult == 'error'}">
                <div class="error">Could not start harvesting.</div>
            </c:if>
			
			<form action="indexing.html" method="post" id="indexing">
				<table id="konfigForm">
					<tr>
						<td>
							Depending on your data sources this step can consume some time (minutes or hours).
							
							<br/><br/>
							
							<button type="submit" name="harvest" id="harvest" value="harvest" <c:if test="${isScheduled == 'true'}">style="display:none"</c:if>>Harvest now!</button>
							
						</td>
					</tr>
							
				</table>
			</form> 
				
		</div>
		
		<div class="dialog" id="dialog" <c:if test="${isScheduled != 'true'}">style="display:none"</c:if>>
			<div class="content">Harvesting in progress, please be patient.</div>
		</div>	
        <div class="dialog" id="dialog_done" style="display:none">
            <div class="content">All data has been harvested.</div>
        </div>  
	</div>
	<div id="footer" style="height:100px; width:90%"></div>
</body>
</html>