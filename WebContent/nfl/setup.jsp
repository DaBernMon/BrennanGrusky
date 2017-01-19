<html>
<%@ page import="java.util.ArrayList"%>
<%@ page import="bg.nfl.utilities.Player"%>
<%@ page import="bg.nfl.utilities.StatFields"%>
<jsp:include page="/header.jsp"></jsp:include>


<body align="left">
	<div class="container">
		<div class="well">Setup the table you want to have generated.
			Checking a field specifies that you want to view that field. The text
			box allows you to specify how many fantasy points one point of the
			specified metric will award you. Example: For the TD field, if you
			are rewarded 6 points, put 6 in the text box. For the Yards field, if
			10 yards is a point, put 0.1 in the text box.</div>
	</div>
	<div class="container">
		<form id="setup-form" action="/nfl/display_stats" method="post">
			<%
				ArrayList<StatFields> columnNames = Player.getPlayerStatFields();
				if(columnNames == null){
					%>
					<p>Page still initializing, please try again in a moment.</p>
					<%
					columnNames = new ArrayList<StatFields>();
				}
				String category = "";
				int categoryCounter = 0;
				for (int i = 0; i < columnNames.size(); i++) {
					StatFields col = columnNames.get(i);
					String[] cleanedValue = StatFields.cleanValue(col.value);
					String newCategory = cleanedValue[0];
					String attribute = cleanedValue[1];
					if (newCategory.equals("Name") || newCategory.equals("Number")
							|| newCategory.equals("Year")) {
						continue;
					}

					if (categoryCounter == 0) {
						categoryCounter++;
						%>
						<div class="row">
						<div class="col-md-3">
						<%
					}

					if (attribute.equals("N/A")) { // For 'Team', 'Position', 'College'
						%>
						<div class="row">
							<div class="checkbox">
								<input type="checkbox" name="<%=col.value%>" /><%=newCategory%></div>
						</div>
						<%
					}
					else{
						if (!category.equals(newCategory)) {
							category = newCategory;
							categoryCounter++;
							%>
							</div>
							<%
							if (categoryCounter-1 % 4 == 0) {
								%>
								</div>
								<br/>
								<div class="row">
								<%
							}
							%>
							<div class="col-md-3">
							<p>
								<strong><%=newCategory%></strong>
							</p>
							<%
						}
						%>
						<div class="row">
							<div class="checkbox">
								<input type="checkbox" name="<%=col.value%>"
									onclick="var input = document.getElementById('score-<%=col.value%>'); if(this.checked){ input.disabled = false; input.value = 0; input.focus();}else{input.disabled=true; input.value=''}" />
									<%=attribute%>
								<input id="score-<%=col.value%>" name="score-<%=col.value%>"
									disabled="disabled" />
							</div>
						</div>
						<%
					} 
				}
				%>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6">
					<label for="year-input">Year</label>
     		 		<select class="form-control input-sm" id="year-input" name="year-input" form="setup-form">
        				<option>2015</option>
        				<option>2014</option>
        				<option>2013</option>
      				</select>
				</div>
				<div class="col-md-2">
					<input class="btn-info" type="submit" name="submit" value="Submit" />
				</div>
			</div>
		</form>
	</div>

	<script src="/resources/js/nfl/setup.js"></script>
	<jsp:include page="/footer.jsp"></jsp:include>

</body>
</html>