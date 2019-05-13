$(document).ready(function(){
	var number = "";
       $('.hiSlider1').hiSlider()
    	$("body").on('click', '.reduce', function() {
    		number = $("#number").text();
    		if (number == 2) {
    			$(".reduce").addClass('nofocus');
    		}
    		if (number>1) {
    			number--;
    		}
    		$("#number").text(number);
            return false;
    	});
    	$("body").on('click', '.add', function() {
    		number = $("#number").text();
    		number++;
    		$(".reduce").removeClass('nofocus');
			$("#number").text(number);
    	   return false;
        });
});

