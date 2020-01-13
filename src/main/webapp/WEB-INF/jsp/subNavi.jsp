<%--
  **************************************************-
  ingrid-interface-csw
  ==================================================
  Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
<div id="navi_vertical">
	<div class="konf">
		<p class="no">1</p>
		<h2>Configuration</h2>
	</div>
	<ul>
		<li <c:if test="${active == 'cswtIbus'}">class="active"</c:if>><a href="<%=request.getContextPath()%>ibus_cswt.html">CSW-T Communication</a></li>
		<li <c:if test="${active == 'harvester'}">class="active"</c:if>><a href="<%=request.getContextPath()%>list_harvester.html">Manage Harvester</a></li>
        <li <c:if test="${active == 'scheduling'}">class="active"</c:if>><a href="<%=request.getContextPath()%>scheduling.html">Control Scheduler</a></li>
        <li <c:if test="${active == 'indexing'}">class="active"</c:if>><a href="<%=request.getContextPath()%>indexing.html">Manually issue harvesting</a></li>
        <li <c:if test="${active == 'search'}">class="active"</c:if>><a href="<%=request.getContextPath()%>search.html">Test Search</a></li>
	</ul>
</div>
