<html>
<jsp:include page="header.jsp"></jsp:include>

<body align="center">
<div class="container">  
	<div class="row">
		<div class="col-xs-2"><img class="img-responsive img-rounded" src="/resources/img/theB.jpg" /></div>
		<div class="col-xs-4">
			<div class="dropdown">
				<div class="btn-group-vertical" aria-expanded="true">		
					<div class="btn-group btn-group-justified">
						<button class="btn btn-success" type="button" id="education">Education</button>
					</div>
					
					<div class="btn-group btn-group-justified">
						<button class="btn btn-info" type="button" id="work-history">Work History</button>
					</div>
					
					<div class="btn-group btn-group-justified">
						<button class="btn btn-warning" type="button" id="cs-langs">Programming Language Skills</button>
					</div>
					
					<div class="btn-group btn-group-justified">
						<button class="btn btn-danger" type="button" id="achievements">Achievements</button>
					</div>
				</div>
			</div>
		</div>
		<div class="col-xs-4">
			<div class="btn-success" id="education-info">
			    <p align="left"><strong>University of North Carolina at Chapel Hill</strong></p>
			    <p align="left">Class of 2014</p>
			    <p align="left">Major: Applied Sciences Computer Engineering</p>
			    <p align="left">Minor: Chemistry</p>
			</div>
			<div class="btn-info" id="work-history-info" style="display: none;">
			    <p align="left"><strong>IBM</strong></p>
			    <p align="left">Software Engineer</p>
			    <p align="left">August 2014 - Present</p>
			</div>
			<div class="btn-warning" id="cs-langs-info" style="display: none;">
			    <p align="left">Java</p>
			    <p align="left">C/C++</p>
			    <p align="left">Python</p>
			    <p align="left">Matlab</p>
			    <p align="left">HTML/CSS</p>
			    <p align="left">JavaScript</p>
			    <p align="left">Assembler</p>
			    <p align="left">SQL</p>
			</div>
			<div class="btn-danger" id="achievements-info" style="display: none;">
			    <p align="left">Eagle Scout</p>
			</div>
		</div>
		<div class="col-xs-2">
			<div>
				<div id="education-pic" style="display: none;"><img class="img-responsive img-rounded" src="/resources/img/unc-old-well.jpg" /></div>
				<div id="work-history-pic" style="display: none;"><img class="img-responsive img-rounded" src="/resources/img/ibm-logo.png" /></div>
				<div id="cs-langs-pic" style="display: none;"><img class="img-responsive img-rounded" src="/resources/img/cs-langs.jpg" /></div>
				<div id="achievements-pic" style="display: none;"><img class="img-responsive img-rounded" src="/resources/img/eagle.jpg" /></div>
			</div>
		</div>
	</div>
</div>

<script src="/resources/js/aboutme.js"></script>
<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>