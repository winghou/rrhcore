$(function(){
	$(".tabBtn").click(function(){
		//删除所有tab按钮上的选中样式
		$(".tabBtnSelected").removeClass("tabBtnSelected");
		//隐藏页面上所有的tabContent,根据点击的tab按钮的索引号显示内容
		$(".tabContent").addClass("hidden").eq($(this).addClass("tabBtnSelected").index()).removeClass("hidden");
	});
})