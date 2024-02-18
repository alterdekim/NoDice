function resizeTable() {
	var $window = $(window);
	var height = window.innerHeight;
	var theight = $('.game').height();
	var scale = height / theight;
	$('.game').css('transform', 'scale(' + scale + ')');
}
resizeTable();
$(window).resize(function(evt) {
	resizeTable();
});