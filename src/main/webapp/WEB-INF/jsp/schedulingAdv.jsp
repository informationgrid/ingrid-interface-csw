<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tr>
	<td class="leftCol">Cron Pattern:</td>
	<td>
	    <c:if test="${empty pattern}">
	      <c:set var="pattern" value="* * * * *" />
	    </c:if>
		<input type="text" name="pattern" value="${pattern}" />
		<br/>
		<label>Minute(0-59) Hour(0-23) Day(1-31) Month(0-11) Week day(1-7)</label>
		<br />
		<span>A Cron Pattern. More information <a target="_blank" href="http://help.sap.com/saphelp_xmii120/helpdata/de/44/89a17188cc6fb5e10000000a155369/content.htm">here</a>.</span>
	</td>
</tr>