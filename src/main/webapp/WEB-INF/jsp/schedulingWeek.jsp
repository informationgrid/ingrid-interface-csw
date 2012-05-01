<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tr>
	<td class="leftCol">Wochentag und Uhrzeit:</td>
	<td>
		<label>jeden&nbsp;</label>
		<select class="auto" name="dayOfWeek">
            <option value="1">Montag</option>
            <option value="2">Dienstag</option>
            <option value="3">Mittwoch</option>
            <option value="4">Donnerstag</option>
            <option value="5">Freitag</option>
            <option value="6">Samstag</option>
            <option value="7">Sonntag</option>
        </select>
        <label>&nbsp;um&nbsp;</label>
	
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
		<label>&nbsp;Uhr</label>
	</td>
</tr>