var userId;
var token;
var repayId;
$(function(){
	var params = ns.getRequestParam();
	userId=params.userId;
	token=params.token;
	if(params.repayId){
		repayId=[{"repayId":params.repayId}];
	}else{
		repayIds= util.cookie.get("repayId").split(',');
		var arr=[];
		$(repayIds).each(function(i,obj){
			arr.push({
				repayId : obj
			});
		})
		repayId=arr;
		console.log(repayId);
	}
	//用户提示框展示
	$(".idea").click(function(){
		$(".mask").removeClass('hide');
	})
	//用户提示框关闭
	$(".shpwPanelsureBtn").click(function(){
		$(".mask").addClass('hide');
	})
	var data={
			  "cmd": "payWayChoosonPage",           //请求的命令字
			  "token": token,                        //用来表示是否允许访问服务器资源
			  "userId": userId,                      //用户ID
			  "version": "1",                        //版本号：默认为1
			  "params": {
			            "userId": userId,                //用户ID 
			            "repayIds":repayId
			  }	
			}
	var dataString=JSON.stringify(data);
	showLoading();
	   $.ajax({
	        url:commonUrl+"/payWayChoosonPage",
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
	            $(".moneyNum").text("￥"+res.detail.payAmt);
	         	$(".orderNum").text("订单编号："+res.detail.orderNo);
	         	$(".bankName").text("银联在线支付"+res.detail.bankInfo);
	         	$(".telNumber").text(res.detail.customerServiceTelephone);
	         	$(".wx").click(function(){
	         		 window.location=res.detail.weChatUrl;
	         	})
	         	$(".contentline2").text("随心花本期还款"+res.detail.payAmt+"元,及时还款还可享受提额哦 ！")
	            }else if(res.result==-1){
	         	   window.location = encodeURI('*'+res.resultNote);
	            }else{
	         	   alert(res.resultNote);   
	            }
	        },
	  });
	   $(".zfb").click(function(){
		   window.location=encodeURI('zfbRepay');
	   })
	   $(".card").click(function(){
		   $(".mask2").removeClass("hide");
	   })
	   $(".cancelBtn").click(function(){
		   $(".mask2").addClass("hide");  
	   })
	   $(".surePay").click(function(){
		   $(".mask2").addClass("hide");
		   var data2={
				   "cmd": "fastWithholding",           //请求的命令字
				   "token":token,                        //用来表示是否允许访问服务器资源
				   "userId": userId,                      //用户ID
				   "version": "1",                        //版本号：默认为1
				   "params": {
				             "userId": userId,                //用户ID 
				             "repayIds":repayId
				   }	
				 }
		   var dataString=JSON.stringify(data2);
			showLoading();
			   $.ajax({
			        url:commonUrl+"/fastWithholding",
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
			         //代扣成功
			            window.location="H5-waitting.html?token="+token+"&userId="+userId+"&withholdTradeNo="+res.detail.withholdTradeNo;
			            }else if(res.result==-1){
			         	   window.location = encodeURI('*'+res.resultNote);
			            }else{
			         	   alert(res.resultNote);   
			            }
			        },
			  });
	   })
})