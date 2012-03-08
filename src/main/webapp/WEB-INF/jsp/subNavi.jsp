<%@ include file="/WEB-INF/jsp/include.jsp" %>
<div id="navi_vertical">
	<div class="konf">
		<p class="no">1</p>
		<h2>Configuration</h2>
	</div>
	<ul>
		<li class="active"><a href="${pageContext.request.contextPath}">Manage Harvester</a></li>
        <li><a href="${pageContext.request.contextPath}">Control Scheduler</a></li>
        <li><a href="${pageContext.request.contextPath}">Manually issue harvesting</a></li>
	</ul>
	<div class="konf">
		<p class="no">2</p>
		<h2>Statistics</h2>
	</div>
    <ul>
        <li><a href="${pageContext.request.contextPath}">See Interface Stats</a></li>
        <li><a href="${pageContext.request.contextPath}">See Harvester Stats</a></li>
    </ul>
</div>