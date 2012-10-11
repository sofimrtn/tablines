function tapinosSelectSearch_init(component, select) {
	var html = '<div style="display:none">';
	html += '<div id="datasetSearchDiv">';
	html += '<label for="busqR">Introduzca a continuaci&oacute;n el t&eacute;rmino de su b&uacute;squeda:</label>';
	html += '<input id="busqR"/>';
	html += '</div>';
	html += '</div>';

	var div = $(html);		
    $(component).after(html);
    var datasetOptions = $("option", select);
    var searchInput = $("input", $("#datasetSearchDiv"));
    
    searchInput.autocomplete({
    	source: $.makeArray(datasetOptions.map(function () { return { value: this.value, label: this.text } } )),
    	minLength: 3,
		focus: function(event, ui) { return false; }, // disable default handler
        select: function(event, ui) {
			$.fancybox.close();
			if (select.attr("multiple") == true) {
				// add value to the multiselection
				var values = select.val();
				values.push(ui.item.value);
				select.val(values);
			} else {
				// single value
				select.val(ui.item.value);
			}
	        select.change();
	        return false;
    	}       
    });
	component.css("cursor", "pointer");
    component.fancybox({
    	href: "#datasetSearchDiv",
        onComplete: function() {
        	searchInput.val("");
        	searchInput.focus();
        },
        titleShow: false
    });	
}

(function($) {
    
    // initialization function
	$.fn.tapinosSelectSearch = function(select, settings) {
		this.each(function() {
			tapinosSelectSearch_init($(this), select, settings);
		}); // each
		return this;
	};

})(jQuery);
