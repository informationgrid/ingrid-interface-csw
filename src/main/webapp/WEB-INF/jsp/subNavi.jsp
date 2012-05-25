<%@ include file="/WEB-INF/jsp/include.jsp" %>
<div id="navi_vertical">
	<div class="konf">
		<p class="no">1</p>
		<h2>Configuration</h2>
	</div>
	<ul>
		<li <c:if test="${active == 'harvester'}">class="active"</c:if>><a href="<%=request.getContextPath()%>list_harvester.html">Manage Harvester</a></li>
        <li <c:if test="${active == 'scheduling'}">class="active"</c:if>><a href="<%=request.getContextPath()%>scheduling.html">Control Scheduler</a></li>
        <li <c:if test="${active == 'indexing'}">class="active"</c:if>><a href="<%=request.getContextPath()%>indexing.html">Manually issue harvesting</a></li>
        <li <c:if test="${active == 'search'}">class="active"</c:if>><a href="<%=request.getContextPath()%>search.html">Test Search</a></li>
	</ul>
</div>