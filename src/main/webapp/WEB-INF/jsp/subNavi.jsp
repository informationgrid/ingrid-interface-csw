<%@ include file="/WEB-INF/jsp/include.jsp" %>
<div id="navi_vertical">
	<div class="konf">
		<p class="no">1</p>
		<h2>Configuration</h2>
	</div>
	<ul>
		<li <c:if test="${active == 'harvester'}">class="active"</c:if>><a href="${pageContext.request.contextPath}/list_harvester.html">Manage Harvester</a></li>
        <li <c:if test="${active == 'scheduling'}">class="active"</c:if>><a href="${pageContext.request.contextPath}/scheduling.html">Control Scheduler</a></li>
        <li <c:if test="${active == 'indexing'}">class="active"</c:if>><a href="${pageContext.request.contextPath}/indexing.html">Manually issue harvesting</a></li>
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