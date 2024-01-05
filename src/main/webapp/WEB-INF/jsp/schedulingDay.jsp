<%--
  **************************************************-
  ingrid-interface-csw
  ==================================================
  Copyright (C) 2014 - 2024 wemove digital solutions GmbH
  ==================================================
  Licensed under the EUPL, Version 1.2 or – as soon they will be
  approved by the European Commission - subsequent versions of the
  EUPL (the "Licence");
  
  You may not use this work except in compliance with the Licence.
  You may obtain a copy of the Licence at:
  
  https://joinup.ec.europa.eu/software/page/eupl
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the Licence is distributed on an "AS IS" basis,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the Licence for the specific language governing permissions and
  limitations under the Licence.
  **************************************************#
  --%>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tr>
	<td class="leftCol">Time:</td>
	<td>
		<label>at&nbsp;</label>
		<select class="auto" name="hour">
			<c:forEach var="i" begin="0" end="23">
				<option value="${i}">${i}</option>
			</c:forEach>
		</select>
		<label>:</label>
		
		<select class="auto" name="minute">
			<c:forEach var="i" begin="0" end="59" step="15">
				<option value="${i}">${i}</option>
			</c:forEach>
		</select>
		<label>&nbsp;</label>
	</td>
</tr>
