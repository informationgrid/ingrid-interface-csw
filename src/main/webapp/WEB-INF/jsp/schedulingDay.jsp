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