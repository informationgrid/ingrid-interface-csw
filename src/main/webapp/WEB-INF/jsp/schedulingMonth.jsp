<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tr>
	<td class="leftCol">Day and time:</td>
	<td>
		<label>at&nbsp;</label>
		<select class="auto" name="dayOfMonth">
            <c:forEach var="i" begin="1" end="31">
                <option value="${i}">${i}.</option>
            </c:forEach>
        </select>
        <label>&nbsp;Day of month at&nbsp;</label>
	
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
<tr>
	<td class="leftCol">At multiple days:</td>
	<td>
		<input type="hidden" name="_dayOfMonth" value=""/>
		<table id="multiple">
			<c:forEach var="w" begin="0" end="4">
				<tr>
					<c:forEach var="d" begin="1" end="7">
						<c:choose>
							<c:when test="${w == 4}">
								<c:if test="${d <= 3}">
									<td><div value="${w*7 + d}"></div></td>
								</c:if>
							</c:when>
							<c:otherwise>
								<td><div value="${w*7 + d}"></div></td>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
	</td>
</tr>