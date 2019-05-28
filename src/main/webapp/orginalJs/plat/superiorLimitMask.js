function showSuperiorLimitMask() {
    var superiorLimitMask = $("<div class='superiorLimitMask'></div>");
    var superiorLimitMaskPanel = $("<div class='superiorLimitMaskPanel'></div>").appendTo(superiorLimitMask);
    $("<img src='../../images/superiorLimit-logo.png' class='superiorLimit-logo'>").appendTo(superiorLimitMaskPanel);
    $("<div class='superiorLimitMaskTip1'>Sorry</div>").appendTo(superiorLimitMaskPanel);
    $("<div class='superiorLimitMaskTip2'>您的绑定已超过上限，</div>").appendTo(superiorLimitMaskPanel);
    $("<div class='superiorLimitMaskTip3'>最多3个哦！</div>").appendTo(superiorLimitMaskPanel);
    $("<div class='superiorLimitMask-btn click'>我知道啦</div>").appendTo(superiorLimitMaskPanel);
    $("body").append(superiorLimitMask);
    $(".superiorLimitMask-btn").click(function(){
    	hideSuperiorLimitMask(); 
    })
}


function hideSuperiorLimitMask() {
    $(".superiorLimitMask").remove();
}
function showLoading1(){
	   var loadingCSS = $("<style type='text/css'>.current a {font-size: 20px;}.over {background-color: #f5f5f5;display: none;height: 100%;left: 0;opacity: 0.5;position: fixed;top: 0;width: 100%;z-index: 1000;}.layout {display: none;height: 20%;left: 40%;position: fixed;text-align: center;top: 40%;width: 20%;z-index: 10013;}</style>");
	    $("head")[0].appendChild(loadingCSS[0]);
	    var loadingDOM = $("<div id='over' class='over'></div><div id='layout' class='layout'><img src='http://api.mochoupay.com/html/images/loading.gif'/></div>");
	    $("body").append(loadingDOM);
	    $("#over").show();
	    $("#layout").show();
	    
}
