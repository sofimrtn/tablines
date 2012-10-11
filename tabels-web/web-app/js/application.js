var Ajax;
if (Ajax && (Ajax != null)) {
	Ajax.Responders.register({
	  onCreate: function() {
        if($('spinner') && Ajax.activeRequestCount>0)
          Effect.Appear('spinner',{duration:0.5,queue:'end'});
	  },
	  onComplete: function() {
        if($('spinner') && Ajax.activeRequestCount==0)
          Effect.Fade('spinner',{duration:0.5,queue:'end'});
	  }
	});
}

$( "#showHideTransformationProgram" ).click(function() {
	$('#programDiv').toggle('blind');
	return true;
});

$( "div.showHide h2" ).click(function() {
	$('#programDiv').toggle('blind');
	return true;
});

$( ".accordion" ).next('div').hide();

$( ".accordion" ).click(function() {
	$(this).next('div').slideToggle();
	return true;
});

$(".screenshot a").fancybox();