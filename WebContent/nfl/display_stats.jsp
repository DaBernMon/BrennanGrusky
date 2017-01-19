<html>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.Map"%>
<%@ page import="bg.nfl.utilities.Player"%>
<%@ page import="bg.nfl.utilities.StatFields"%>
<jsp:include page="/header.jsp"></jsp:include>


<body align="left">
	<div class="container">
		<div class="well">The table will be displayed here.</div>
	</div>
	<div class="main-content">
		<div class="container">
			<%
				@SuppressWarnings("unchecked")
				ArrayList<Map<String, Object>> dataMap = (ArrayList<Map<String, Object>>) request.getAttribute("dataMap");
			%>
			<table id="player-stats">
				<thead>
					<tr>
						<th>Player</th>
						<th>Fantasy Points</th>
						<%
							String[] cleanKey;
							for(String key: dataMap.get(0).keySet()){
								if(key.equals("name") || key.equals("ff-pts")){
									continue;
								}
								cleanKey = StatFields.cleanValue(key);
								if(cleanKey[1].equals("N/A")){
									%>
									<th><%= cleanKey[0]%></th>
									<%
								}
								else{
									%>
									<th><%= cleanKey[0] + cleanKey[1]%></th>
									<%
								}
							}
						%>
					</tr>
				</thead>
				
				<tbody>
					<%
						for(Map<String,Object> row : dataMap){
							%>
							<tr>
								<td><%=row.get("name") %></td>
								<td><%=String.format("%.2f", row.get("ff-pts")) %></td>
								<%
									Object keyVal;
									for(String key: dataMap.get(0).keySet()){
										if(key.equals("name") || key.equals("ff-pts")){
											continue;
										}
										keyVal = row.get(key);
										%>
										<td><%=(keyVal == null)? "N/A": keyVal %></td>
										<%
									} 
								%>
							</tr>
							<%
						}
						%>
				</tbody>
			</table>
		</div>
	</div>

	<script src="/resources/js/nfl/display_stats.js"></script>
	<jsp:include page="/footer.jsp"></jsp:include>

</body>
</html>