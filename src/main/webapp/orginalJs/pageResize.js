
var resizeHeight = 220;//自适应减去的左半边的上部分高度
/******************************页面自适应******************************/
 
function getMapHeight(h){
	return h-50;//剪掉头部的高度
	
}
function getMapWidth(w){
	if(jQuery("#hideResult").attr("class")=="hideResult2"){//最大化状态，只要剪掉收缩框的宽度
		return w-8;
	}else{//剪掉收缩框的宽度+左侧的宽度
		return w-398;
	}
	
}

function resizeApp(){//用于自适应高度处理
	try{
		var bodyHeight  = getWindowHeight();
		var bodyWidth  = getWindowWidth();
		var LeftWidth  = getLeftWidth()+12;
		if( bodyHeight - resizeHeight > 0){
			jQuery("#left").height(bodyHeight - 72);
			jQuery("#resultList").height(bodyHeight - resizeHeight);
		}
		if( bodyHeight - 71 > 0){
			document.getElementById("hideResult").style.height = bodyHeight - 71 + "px" ;
		}
		if( bodyHeight - 99 > 0){
			document.getElementById("mapObjDIV").style.height = bodyHeight - 99 + "px" ;
			$("#mapObj").height(bodyHeight - 119);
			$("#right").height(bodyHeight - 119);
		}
		
		if( bodyWidth - LeftWidth > 0){
			document.getElementById("mapObjDIV").style.width = bodyWidth - LeftWidth + "px" ;
		}//地图宽度10/408
	
		if( bodyWidth - LeftWidth > 0){
			document.getElementById("mapTitle").style.width = bodyWidth - LeftWidth + "px" ;
		}//标题宽度10/408
		//if(window.addEventListener && mapObj){ // FF
			mapObj.updateSize();//火狐浏览器更新地图尺寸，否则地图加载不全
		//}
	}catch(ex){}
}//end-2007-1-27-LP-自适应高度处理end


function getLeftWidth(){
	var left = 0;

	if(jQuery("#left").is(":visible")){
		left = 252;
	}
	return left;
}

function getWindowHeight() {//获取页面显示区域高度
	if (window.self && self.innerHeight) {
		return self.innerHeight;
	}
	if (document.documentElement && document.documentElement.clientHeight) {
		return document.documentElement.clientHeight;
	}
	return 0;
}

function getWindowWidth() {//获取页面显示区域宽度
	if (window.self && self.innerWidth) {
	return self.innerWidth;
	}
	if (document.documentElement && document.documentElement.clientWidth) {
	return document.documentElement.clientWidth;
	}
	return 0;
	}
/******************************页面自适应结束******************************/