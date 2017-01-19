$(document).ready(function(){
	hideAll();
	$("#education-info").show();
	$("#education-pic").show();
	$("#about-me-nav").addClass("active");
	
	$("#education").on("click", function(){
		hideAll();
		$("#education-info").show();
		$("#education-pic").show();
	});
	$("#work-history").on("click", function(){
		hideAll();
		$("#work-history-info").show();
		$("#work-history-pic").show();
	});
	$("#cs-langs").on("click", function(){
		hideAll();
		$("#cs-langs-info").show();
		$("#cs-langs-pic").show();
	});
	$("#achievements").on("click", function(){
		hideAll();
		$("#achievements-info").show();
		$("#achievements-pic").show();
	});
	
	function hideAll(){
		$("#education-info").hide();
		$("#work-history-info").hide();
		$("#cs-langs-info").hide();
		$("#achievements-info").hide();
		
		$("#education-pic").hide();
		$("#work-history-pic").hide();
		$("#cs-langs-pic").hide();
		$("#achievements-pic").hide();
	}
});