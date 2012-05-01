<%@ include file="/WEB-INF/jsp/include.jsp" %>
<tr>
	<td class="leftCol">Cron Pattern:</td>
	<td>
	    <c:if test="${empty pattern}">
	      <c:set var="pattern" value="* * * * *" />
	    </c:if>
		<input type="text" name="pattern" value="${pattern}" />
		<br/>
		<label>Minute(0-59) Stunde(0-23) Tag(1-31) Monat(0-11) Wochetag(1-7)</label>
		<br />
		<span>Ein  erweitertes Cron Patter. Weitere Informationen <a target="_blank" href="http://help.sap.com/saphelp_xmii120/helpdata/de/44/89a17188cc6fb5e10000000a155369/content.htm">hier</a>.</span>
	</td>
</tr>