var userId;
var token;
var withholdTradeNo;
$(function(){
	var params = ns.getRequestParam();
	userId=params.userId;
	token=params.token;
	withholdTradeNo=params.withholdTradeNo;
	var data={
			  "cmd": "payResult",           //请求的命令字
			  "token": token,                        //用来表示是否允许访问服务器资源
			  "userId": userId,                      //用户ID
			  "version": "1",                        //版本号：默认为1
			  "params": {
		   "userId": userId,           //用户ID
			"outTradeNo": "",          //还款列表ID
			"totalFee": "",              //还款总金额
			"withholdTradeNo": withholdTradeNo,              //代扣成功返回唯一支付订单号
			  }
			}
	   var dataString=JSON.stringify(data);
	   showLoading();
	   $.ajax({
	        url:commonUrl+"/payResult",
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
	             $(".tip").text("成功还款"+res.detail.repayAmt+"元");
	             $(".money").text(res.detail.useAmt+"元");
	            }else if(res.result==-1){
	         	   window.location = encodeURI('*'+res.resultNote);
	            }else{
	         	   alert(res.resultNote);   
	            }
	        },
	  });
	   $(".sureBtn").click(function(){
		   window.location="returnApp";
	   })
})