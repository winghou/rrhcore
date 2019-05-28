var userId;
var token;
var repayId;
var withholdTradeNo;
$(function(){
	var params = ns.getRequestParam();
	userId=params.userId;
	token=params.token;
	withholdTradeNo=params.withholdTradeNo;
	repayIds= util.cookie.get("repayId").split(',');
	var arr=[];
	$(repayIds).each(function(i,obj){
		arr.push({
			repayId : obj
		});
	})
	repayId=arr;
	console.log(repayId);
	getResult();
})
i = 0;
j = 0;
count = 0;
MM = 0;
SS = 20;  // 秒 90s
MS = 0;
totle = (MM+1)*600;
d = 180*(MM+1);
MM = "0" + MM;
var gameTime = 20;
//count down
var showTime = function(){
    totle = totle - 1;
    if (totle == 0) {
    	history.go(-1);
        clearInterval(s);
        clearInterval(t1);
        clearInterval(t2);
       
        $(".pie2").css("-o-transform", "rotate(" + d + "deg)");
        $(".pie2").css("-moz-transform", "rotate(" + d + "deg)");
        $(".pie2").css("-webkit-transform", "rotate(" + d + "deg)");
    } else {
        if (totle > 0 && MS > 0) {
            MS = MS - 1;
            if (MS < 10) {
                MS = "0" + MS
            }
            ;
        }
        ;
        if (MS == 0 && SS > 0) {
            MS = 10;
            SS = SS - 1;
            if (SS < 10) {
                SS = "0" + SS
            }
            ;
        }
        ;
        if (SS == 0 && MM > 0) {
            SS = 20;
            MM = MM - 1;
            if (MM < 10) {
                MM = "0" + MM
            
            }
            ;
        }
        ;
    }
    ;
    $(".time").html(SS + "秒");
	
};

var start1 = function(){
	//i = i + 0.6;
	i = i + 360/((gameTime)*10);  //旋转的角度  90s 为 0.4  60s为0.6
	count = count + 1;
	if(count <= (gameTime/2*10)){  // 一半的角度  90s 为 450
		$(".pie1").css("-o-transform","rotate(" + i + "deg)");
		$(".pie1").css("-moz-transform","rotate(" + i + "deg)");
		$(".pie1").css("-webkit-transform","rotate(" + i + "deg)");
	}else{
		$(".pie2").css("backgroundColor", "#8894FF");
		$(".pie2").css("-o-transform","rotate(" + i + "deg)");
		$(".pie2").css("-moz-transform","rotate(" + i + "deg)");
		$(".pie2").css("-webkit-transform","rotate(" + i + "deg)");
	}
};

var start2 = function(){
    j = j + 0.6;
    count = count + 1;
    if (count == 300) {
        count = 0;
        clearInterval(t2);
        t1 = setInterval("start1()", 100);
    }
	$(".pie2").css("-o-transform","rotate(" + j + "deg)");
	$(".pie2").css("-moz-transform","rotate(" + j + "deg)");
	$(".pie2").css("-webkit-transform","rotate(" + j + "deg)");
}

var countDown = function() {
    //80*80px 时间进度条
    i = 0;
    j = 0;
    count = 0;
    MM = 0;
    SS = gameTime;
    MS = 0;
    totle = (MM + 1) * gameTime * 10;
    d = 180 * (MM + 1);
    MM = "0" + MM;

    showTime();

    s = setInterval("showTime()", 100);
    start1();
    //start2();
    t1 = setInterval("start1()", 100);
}
function getResult(){
	var data=
			{
				  "cmd": "qryPayResultFrequently",           //请求的命令字
				  "token":token,                        //用来表示是否允许访问服务器资源
				  "userId": userId,                      //用户ID
				  "version": "1",                        //版本号：默认为1
				  "params": {
				            "userId": userId,                //用户ID 
				"repayIds": repayId   ,            
				  }	
				}
	var dataString=JSON.stringify(data);
	   $.ajax({
	        url:commonUrl+"/qryPayResultFrequently",
	        type: 'post',
	        dataType:'json',
	        contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
	        timeout: 100000,
	        data: "{\"key\":\""+Encrypt(dataString)+"\"}",
	        success: function(dd){
	     	   hideLoading();
	            var res=JSON.parse(Decrypt(dd.key));
	            console.log(res);
	            if(res.result==0){
	            	window.location="H5-resultOfReturnBackMoney.html?token="+token+"&userId="+userId+"&withholdTradeNo="+withholdTradeNo;
	            }else if(res.result==-1){
	         	   window.location = encodeURI('*'+res.resultNote);
	            }else{
	            	getResult();    
	            }
	        },
	  });
}