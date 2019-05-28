
 
//***************************页面的显示隐藏控制开始 ***************************

// 显示默认地图搜索
function showAroundDefaultResultPage(){
	mapAllInfo.setMapSearchType("");//默认页面打印空地图
	jQuery("#print").html("<a href=\"print.html?x="+printObj.print_mapX+"&y="+printObj.print_mapY+"&z="+printObj.print_mapZoom+"&n="+encodeURI(printObj.print_mapCityName)+"\" target=\"_blank\">打印</a>");
	removeAllOverlays();//删除地图上的所有点和线
	jQuery("#left").show().children("div").hide();//隐藏左边列表下全部
	jQuery("#searchTab").show().children(":not(.cc)").hide();//隐藏地图搜索框下面所有子元素（排除布局控制的div）
	jQuery("#searchTab>ul,#searchCont0").show();//显示地图搜索框
	jQuery("#resultAroundDefault").show();//没有内容的返回数，用来控制布局
	var img = jQuery("#mainSearchTip li:eq(0) img")[0].src.split("/");//判断地图搜索是否为当前
	if(img[img.length-1].split(".")[0].indexOf("_sel")>-1){
		img = img[img.length-1].split(".")[0]+".gif";
	}else{
		img = img[img.length-1].split(".")[0]+"_sel.gif";
	}
	
	jQuery("#mainSearchTip li:eq(0) img").attr("src","images/images-2.0/"+img);//地图搜索改为当前的
	jQuery("#mainSearchTip li:not(:eq(0)) img").each(function(){//同辈的图片改成非当前
		var img2 = this.src.split("/");
		var imgSrc = img2[img2.length-1].split(".")[0];
		if(imgSrc.indexOf("_sel")>-1){
			imgSrc = imgSrc.split("_")[0]+".gif";
		}else{
			imgSrc = imgSrc+".gif";
		}
		jQuery(this).attr("src","images/images-2.0/"+imgSrc);
	});
	jQuery("#resultList").show().children("#resultAroundDefault").show().siblings().hide();//显示默认的本地搜索内容
	resizeApp();//自适应的缩放
	jQuery("#mapSearchInput").trigger("focusin").focus();
	mapAllInfo.setCurrentShowPage("0");//默认地图搜索页面
	jQuery("#busStart,#busEnd,#driverStart,#driverEnd").val("请输入查询关键字");//清空公家和驾车的搜索关键字
	//routeS.start_pid="";//清空鼠标右键的开始点
	//routeS.end_pid="";//清空鼠标右键的结束点
	addMenuItem_map();//设置从这里走，到这里去可以用
}


// 显示默认公交搜索
function showBusDefaultResultPage(){
	mapAllInfo.setMapSearchType("");//默认页面打印空地图
	jQuery("#print").html("<a href=\"print.html?x="+printObj.print_mapX+"&y="+printObj.print_mapY+"&z="+printObj.print_mapZoom+"&n="+encodeURI(printObj.print_mapCityName)+"\" target=\"_blank\">打印</a>");
	removeAllOverlays();//删除地图上的所有点和线
	
	jQuery("#left").show().children("div").hide();//隐藏左边列表下全部
	jQuery("#searchTab").show().children(":not(.cc)").hide();//隐藏地图搜索框下面所有子元素（排除布局控制的div）
	jQuery("#searchTab>ul,#searchCont1").show();//显示公交搜索框
	var img = jQuery("#mainSearchTip li:eq(1) img")[0].src.split("/");//判断公交搜索是否为当前
	if(img[img.length-1].split(".")[0].indexOf("_sel")>-1){
		img = img[img.length-1].split(".")[0]+".gif";
	}else{
		img = img[img.length-1].split(".")[0]+"_sel.gif";
	}
		
	jQuery("#mainSearchTip li:eq(1) img").attr("src","images/images-2.0/"+img);//公交搜索改为当前的
	jQuery("#mainSearchTip li:not(:eq(1)) img").each(function(){//同辈的图片改成非当前
		var img2 = this.src.split("/");
		var imgSrc = img2[img2.length-1].split(".")[0];
		if(imgSrc.indexOf("_sel")>-1){
			imgSrc = imgSrc.split("_")[0]+".gif";
		}else{
			imgSrc = imgSrc+".gif";
		}
		jQuery(this).attr("src","images/images-2.0/"+imgSrc);
	});
	jQuery("#searchTab>div[id='searchCont1']>table:eq(0)").show().siblings(":not(:last)").hide();//公交换乘方案的3个div切换,默认显示第一个
	jQuery("#busHref a:eq(0)").attr("class","cura").siblings().attr("class","");//默认（换乘方案）第一个选中
	jQuery("#resultList").show().children("#resultBusDefault").show().siblings().hide();//显示默认的本地搜索内容
	resizeApp();//自适应的缩放
	jQuery("#busStart").trigger("focusin").focus();
	mapAllInfo.setCurrentShowPage("1");//默认公交搜索页面
	addMenuItem_map();//设置从这里走，到这里去可以用
}


// 显示默认驾车搜索
function showDriverDefaultResultPage(){
	mapAllInfo.setMapSearchType("");//默认页面打印空地图
	jQuery("#print").html("<a href=\"print.html?x="+printObj.print_mapX+"&y="+printObj.print_mapY+"&z="+printObj.print_mapZoom+"&n="+encodeURI(printObj.print_mapCityName)+"\" target=\"_blank\">打印</a>");
	removeAllOverlays();//删除地图上的所有点和线
	
	jQuery("#left").show().children("div").hide();//隐藏左边列表下全部
	jQuery("#searchTab").show().children(":not(.cc)").hide();//隐藏地图搜索框下面所有子元素（排除布局控制的div）
	jQuery("#searchTab>ul,#searchCont2").show();//显示驾车搜索框
	var img = jQuery("#mainSearchTip li:eq(2) img")[0].src.split("/");//判断驾车搜索是否为当前
	if(img[img.length-1].split(".")[0].indexOf("_sel")>-1){
		img = img[img.length-1].split(".")[0]+".gif";
	}else{
		img = img[img.length-1].split(".")[0]+"_sel.gif";
	}
		
	jQuery("#mainSearchTip li:eq(2) img").attr("src","images/images-2.0/"+img);//驾车搜索改为当前的
	jQuery("#mainSearchTip li:not(:eq(2)) img").each(function(){//同辈的图片改成非当前
		var img2 = this.src.split("/");
		var imgSrc = img2[img2.length-1].split(".")[0];
		if(imgSrc.indexOf("_sel")>-1){
			imgSrc = imgSrc.split("_")[0]+".gif";
		}else{
			imgSrc = imgSrc+".gif";
		}
		jQuery(this).attr("src","images/images-2.0/"+imgSrc);
	});
	jQuery("#resultList").show().children("#resultDriverDefault").show().siblings().hide();//显示默认的驾车搜索内容
	resizeApp();//自适应的缩放
	jQuery("#driverStart").trigger("focusin").focus();
	mapAllInfo.setCurrentShowPage("4");//默认驾车搜索页面
	addMenuItem_map();//设置从这里走，到这里去可以用
}

// 显示地图搜索的结果页面
function showAroundResultPage(){
	if(jQuery("#hideResult").attr("class")!="hideResult2"){//当前是最大化的地图
		
		jQuery("#left").show().children("div").hide();//隐藏左边列表下全部
		jQuery("#searchTab").show().children(":not(.cc)").hide();//隐藏地图搜索框下面所有子元素（排除布局控制的div）
		jQuery("#searchTab>ul,#searchCont0").show();//显示地图搜索框
		var img = jQuery("#mainSearchTip li:eq(0) img")[0].src.split("/");//判断地图搜索是否为当前
		if(img[img.length-1].split(".")[0].indexOf("_sel")>-1){
			img = img[img.length-1].split(".")[0]+".gif";
		}else{
			img = img[img.length-1].split(".")[0]+"_sel.gif";
		}
			
		jQuery("#mainSearchTip li:eq(0) img").attr("src","images/images-2.0/"+img);//地图搜索改为当前的
		jQuery("#mainSearchTip li:not(:eq(0)) img").each(function(){//同辈的图片改成非当前
			var img2 = this.src.split("/");
			var imgSrc = img2[img2.length-1].split(".")[0];
			if(imgSrc.indexOf("_sel")>-1){
				imgSrc = imgSrc.split("_")[0]+".gif";
			}else{
				imgSrc = imgSrc+".gif";
			}
			jQuery(this).attr("src","images/images-2.0/"+imgSrc);
		});
		jQuery("#resultList").show().children("#resultAround,#returnResultNumber").show().siblings(":not(#resultAround,#returnResultNumber)").hide();//显示默认的本地搜索内容
	}
}

// 显示公交搜索的起始点选择页面
function showBusStartEndSelectPage(){
	mapAllInfo.setMapSearchType("bus");//打印空地图
	removeAllOverlays();//删除地图上的所有点和线
	if(jQuery("#hideResult").attr("class")=="hideResult2"){//当前是最大化的地图
		jQuery("#hideResult").click();
		
	}
	jQuery("#left").show().children("div").hide();//隐藏左边列表下全部
	jQuery("#searchTab").show().children(":not(.cc)").hide();//隐藏地图搜索框下面所有子元素（排除布局控制的div）
	jQuery("#searchTab>ul,#startEndOption").show();//显示公交搜索框和起始点选择框
	var img = jQuery("#mainSearchTip li:eq(1) img")[0].src.split("/");//判断公交搜索是否为当前
	if(img[img.length-1].split(".")[0].indexOf("_sel")>-1){
		img = img[img.length-1].split(".")[0]+".gif";
	}else{
		img = img[img.length-1].split(".")[0]+"_sel.gif";
	}
		
	jQuery("#mainSearchTip li:eq(1) img").attr("src","images/images-2.0/"+img);//公交搜索改为当前的
	jQuery("#mainSearchTip li:not(:eq(1)) img").each(function(){//同辈的图片改成非当前
		var img2 = this.src.split("/");
		var imgSrc = img2[img2.length-1].split(".")[0];
		if(imgSrc.indexOf("_sel")>-1){
			imgSrc = imgSrc.split("_")[0]+".gif";
		}else{
			imgSrc = imgSrc+".gif";
		}
		jQuery(this).attr("src","images/images-2.0/"+imgSrc);
	});
	resizeApp();//自适应的缩放
	mapAllInfo.setCurrentShowPage("2");//公交起始点页面
	addMenuItem_map2();//从这里走，到这里去禁用
}

// 显示驾车搜索的起始点选择页面
function showDriverStartEndSelectPage(){
	mapAllInfo.setMapSearchType("driver");//打印空地图
	if(jQuery("#hideResult").attr("class")=="hideResult2"){//当前是最大化的地图
		jQuery("#hideResult").click();
		
	}
	jQuery("#left").show().children("div").hide();//隐藏左边列表下全部
	jQuery("#searchTab").show().children(":not(.cc)").hide();//隐藏地图搜索框下面所有子元素（排除布局控制的div）
	jQuery("#searchTab>ul,#startEndOption2").show();//显示驾车搜索框和起始点选择框
	var img = jQuery("#mainSearchTip li:eq(2) img")[0].src.split("/");//判断驾车搜索是否为当前
	if(img[img.length-1].split(".")[0].indexOf("_sel")>-1){
		img = img[img.length-1].split(".")[0]+".gif";
	}else{
		img = img[img.length-1].split(".")[0]+"_sel.gif";
	}
		
	jQuery("#mainSearchTip li:eq(2) img").attr("src","images/images-2.0/"+img);//驾车搜索改为当前的
	jQuery("#mainSearchTip li:not(:eq(2)) img").each(function(){//同辈的图片改成非当前
		var img2 = this.src.split("/");
		var imgSrc = img2[img2.length-1].split(".")[0];
		if(imgSrc.indexOf("_sel")>-1){
			imgSrc = imgSrc.split("_")[0]+".gif";
		}else{
			imgSrc = imgSrc+".gif";
		}
		jQuery(this).attr("src","images/images-2.0/"+imgSrc);
	});
	//jQuery("#driverSC>strong").html(city.ctc2n(mapAllInfo.getCurrentCityCode()));
	//jQuery("#driverEC>strong").html(city.ctc2n(mapAllInfo.getCurrentCityCode()));
	resizeApp();//自适应的缩放
	mapAllInfo.setCurrentShowPage("5");//驾车起始点页面
	addMenuItem_map2();//从这里走，到这里去禁用
}
// 显示驾车搜索结果页面
function showDriverResultPage(){
	
	jQuery("#left").show().children("div").hide();//隐藏左边列表下全部
	jQuery("#searchTab").show().children(":not(.cc)").hide();//隐藏地图搜索框下面所有子元素（排除布局控制的div）
	jQuery("#searchTab>ul,#searchCont2").show();//显示驾车搜索框
	var img = jQuery("#mainSearchTip li:eq(2) img")[0].src.split("/");//判断驾车搜索是否为当前
	if(img[img.length-1].split(".")[0].indexOf("_sel")>-1){
		img = img[img.length-1].split(".")[0]+".gif";
	}else{
		img = img[img.length-1].split(".")[0]+"_sel.gif";
	}
		
	jQuery("#mainSearchTip li:eq(2) img").attr("src","images/images-2.0/"+img);//驾车搜索改为当前的
	jQuery("#mainSearchTip li:not(:eq(2)) img").each(function(){//同辈的图片改成非当前
		var img2 = this.src.split("/");
		var imgSrc = img2[img2.length-1].split(".")[0];
		if(imgSrc.indexOf("_sel")>-1){
			imgSrc = imgSrc.split("_")[0]+".gif";
		}else{
			imgSrc = imgSrc+".gif";
		}
		jQuery(this).attr("src","images/images-2.0/"+imgSrc);
	});
	
	jQuery("#resultList").show().children("#resultDriver").show().siblings(":not(#resultDriver)").hide();//显示搜索结果页面
	resizeApp();//自适应的缩放
	mapAllInfo.setCurrentShowPage("6");//驾车结果页面
	addMenuItem_map();//设置从这里走，到这里去可以用
}
// 显示默认公交换乘搜索结果页面
function showBusRouteSearchResultPage(){

	removeAllOverlays();//删除地图上的所有点和线
	
	jQuery("#left").show().children("div").hide();//隐藏左边列表下全部
	jQuery("#searchTab").show().children(":not(.cc)").hide();//隐藏地图搜索框下面所有子元素（排除布局控制的div）
	jQuery("#searchTab>ul,#searchCont1").show();//显示公交搜索框
	var img = jQuery("#mainSearchTip li:eq(1) img")[0].src.split("/");//判断公交搜索是否为当前
	if(img[img.length-1].split(".")[0].indexOf("_sel")>-1){
		img = img[img.length-1].split(".")[0]+".gif";
	}else{
		img = img[img.length-1].split(".")[0]+"_sel.gif";
	}
		
	jQuery("#mainSearchTip li:eq(1) img").attr("src","images/images-2.0/"+img);//公交搜索改为当前的
	jQuery("#mainSearchTip li:not(:eq(1)) img").each(function(){//同辈的图片改成非当前
		var img2 = this.src.split("/");
		var imgSrc = img2[img2.length-1].split(".")[0];
		if(imgSrc.indexOf("_sel")>-1){
			imgSrc = imgSrc.split("_")[0]+".gif";
		}else{
			imgSrc = imgSrc+".gif";
		}
		jQuery(this).attr("src","images/images-2.0/"+imgSrc);
	});
	jQuery("#searchTab>div[id='searchCont1']>table:eq(0)").show().siblings(":not(:last)").hide();//公交换乘方案的3个div切换,默认显示第一个
	jQuery("#busHref a:eq(0)").attr("class","cura").siblings().attr("class","");//默认（换乘方案）第一个选中
	jQuery("#resultList").show().children("#resultBus,#returnResultNumber").show().siblings(":not(#resultBus,#returnResultNumber)").hide()
	.end().children("#busRouteResult").show().siblings(":not(.cc)").hide();//显示换乘搜索结果页面,隐藏线路和站点搜索结果
	mapAllInfo.setCurrentShowPage("3");//公交换乘结果页面
	addMenuItem_map();//设置从这里走，到这里去可以用
}


// 显示公交换乘->公交线路或者公交车站搜索页面，"L"//公交线路搜索；"S"公交车站搜索
function showBusLineOrStationSearchPage(type){
	removeAllOverlays();//删除地图上的所有点和线
	
	jQuery("#left").show().children("div").hide();//隐藏左边列表下全部
	jQuery("#searchTab").show().children(":not(.cc)").hide();//隐藏地图搜索框下面所有子元素（排除布局控制的div）
	jQuery("#searchTab>ul,#searchCont1").show();//显示公交搜索框
	var img = jQuery("#mainSearchTip li:eq(1) img")[0].src.split("/");//判断公交搜索是否为当前
	if(img[img.length-1].split(".")[0].indexOf("_sel")>-1){
		img = img[img.length-1].split(".")[0]+".gif";
	}else{
		img = img[img.length-1].split(".")[0]+"_sel.gif";
	}
		
	jQuery("#mainSearchTip li:eq(1) img").attr("src","images/images-2.0/"+img);//公交搜索改为当前的
	jQuery("#mainSearchTip li:not(:eq(1)) img").each(function(){//同辈的图片改成非当前
		var img2 = this.src.split("/");
		var imgSrc = img2[img2.length-1].split(".")[0];
		if(imgSrc.indexOf("_sel")>-1){
			imgSrc = imgSrc.split("_")[0]+".gif";
		}else{
			imgSrc = imgSrc+".gif";
		}
		jQuery(this).attr("src","images/images-2.0/"+imgSrc);
	});
	if(type=="L"){//公交线路搜索
		jQuery("#searchTab>div[id='searchCont1']>table:eq(1)").show().siblings(":not(:last)").hide();//公交换乘方案的3个div切换,默认显示第2个
		jQuery("#busHref a:eq(1)").attr("class","cura").siblings().attr("class","");//公交线路选中
		
	}else{//公交车站搜索“S”
		jQuery("#searchTab>div[id='searchCont1']>table:eq(2)").show().siblings(":not(:last)").hide();//公交换乘方案的3个div切换,默认显示第3个
		jQuery("#busHref a:eq(2)").attr("class","cura").siblings().attr("class","");//公交车站选中
		
	}
	jQuery("#resultList").show().children("#resultBusDefault").show().siblings(":not(#resultBusDefault)").hide();//显示默认的公交搜索内容
	resizeApp();//自适应的缩放
	if(type=="L"){//公交线路搜索
		jQuery("#busRouteInput").trigger("focusin").focus();
	}else{//公交车站搜索“S”
		jQuery("#busStationInput").trigger("focusin").focus();
	}
	
	mapAllInfo.setCurrentShowPage("0");//默认地图搜索页面
	addMenuItem_map();//设置从这里走，到这里去可以用
}

// 显示公交换乘->公交线路或者公交车站搜索结果页面，"L"//公交线路搜索；"S"公交车站搜索
function showBusLineOrStationSearchResultPage(type){
	jQuery("#left").show().children("div").hide();//隐藏左边列表下全部
	jQuery("#searchTab").show().children(":not(.cc)").hide();//隐藏地图搜索框下面所有子元素（排除布局控制的div）
	jQuery("#searchTab>ul,#searchCont1").show();//显示公交搜索框
	var img = jQuery("#mainSearchTip li:eq(1) img")[0].src.split("/");//判断公交搜索是否为当前
	if(img[img.length-1].split(".")[0].indexOf("_sel")>-1){
		img = img[img.length-1].split(".")[0]+".gif";
	}else{
		img = img[img.length-1].split(".")[0]+"_sel.gif";
	}
		
	jQuery("#mainSearchTip li:eq(1) img").attr("src","images/images-2.0/"+img);//公交搜索改为当前的
	jQuery("#mainSearchTip li:not(:eq(1)) img").each(function(){//同辈的图片改成非当前
		var img2 = this.src.split("/");
		var imgSrc = img2[img2.length-1].split(".")[0];
		if(imgSrc.indexOf("_sel")>-1){
			imgSrc = imgSrc.split("_")[0]+".gif";
		}else{
			imgSrc = imgSrc+".gif";
		}
		jQuery(this).attr("src","images/images-2.0/"+imgSrc);
	});
	if(type=="L"){//公交线路搜索
		jQuery("#searchTab>div[id='searchCont1']>table:eq(1)").show().siblings(":not(:last)").hide();//公交换乘方案的3个div切换,显示第2个
		jQuery("#busHref a:eq(1)").attr("class","cura").siblings().attr("class","");//公交线路选中
		jQuery("#resultList").show().children("#resultBus").show().siblings().hide()
		.end().children("#busLineResult").show().siblings(":not(.cc)").hide();//显示公交线路搜索结果页面,隐藏线路和站点搜索结果
		
	}else{//公交车站搜索“S”
		jQuery("#searchTab>div[id='searchCont1']>table:eq(2)").show().siblings(":not(:last)").hide();//公交换乘方案的3个div切换,显示第3个
		jQuery("#busHref a:eq(2)").attr("class","cura").siblings().attr("class","");//公交车站选中
		jQuery("#resultList").show().children("#resultBus").show().siblings().hide()
		.end().children("#busStationResult").show().siblings(":not(.cc)").hide();//显示公交车站搜索结果页面,隐藏线路和公交换乘搜索结果
	}
	
	resizeApp();//自适应的缩放
	mapAllInfo.setCurrentShowPage("0");//默认地图搜索页面
	addMenuItem_map();//设置从这里走，到这里去可以用
}
//***************************页面的显示隐藏控制结束 ***************************