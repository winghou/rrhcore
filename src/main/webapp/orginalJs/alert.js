$(function(){
	var errorTip=$("<div class='errorTip'></div>");
    var errorwords = $("<div class='errorwords'></div>").appendTo(errorTip);
	$("body").append(errorTip);
});
function alert(errMsg) {	
	// 显示灰色错误色块提示
	$(".errorwords").text(errMsg);
	var eleWidth = $('.errorTip').width()+16;

	$(".errorTip").stop().animate({opacity:'1',bottom:'80px'},400);
	 $(".errorTip").css({'display':'block','left':'50%','margin-left':'-' + eleWidth/2+'px'});
	$(".errorTip").css('display','block');
	setTimeout(function(){
		hideErrTip();
	},3000);
	// 返回1表示出错
	return 1;
	
}
function hideErrTip() {
	//  隐藏灰色错误色块提示
	$(".errorTip").stop().animate({opacity:0 ,bottom:'10px'},500,function(){
		$(".errorTip").css('display','none');
	});
	
	// 返回0表示成功
	return 0;
}
