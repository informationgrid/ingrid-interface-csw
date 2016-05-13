<%--
  **************************************************-
  ingrid-interface-csw
  ==================================================
  Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
<tr>
	<td class="leftCol">Cron Pattern:</td>
	<td>
	    <c:if test="${empty pattern}">
	      <c:set var="pattern" value="* * * * *" />
	    </c:if>
		<input type="text" name="pattern" value="${pattern}" />
		<br/>
		<label>Minute(0-59)�Hour(0-23)�Day(1-31)�Month(0-11)�Week day(1-7)</label>
		<br />
		<span>A Cron Pattern. More information <a target="_blank" href="http://help.sap.com/saphelp_xmii120/helpdata/de/44/89a17188cc6fb5e10000000a155369/content.htm">here</a>.</span>
	</td>
</tr>
