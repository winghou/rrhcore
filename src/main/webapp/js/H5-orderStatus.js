var userId;
var token;
var withDrawId;
var mesId="";
$(function(){
	var params = ns.getRequestParam();
	userId=params.userId;
	token=params.token;
	mesId=params.mesId;
	withDrawId=params.withId;
	var data={
			  "cmd": "queryOrderDetail",
			  "token": token,
			  "userId": userId,
			  "version": "1",
			  "params": {
			    "userId":userId,
			    "withDrawId":withDrawId,
			    "mesId": mesId,
			    "isJump": "yes",
			  }
			}
	var dataString=JSON.stringify(data);
	showLoading();
	   $.ajax({
	        url:commonUrl+"/queryOrderDetail",
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
	            	$(".orderAccount").text("订单编号："+res.detail.orderNum);
	            	$(".loanMoney").text("借款金额："+res.detail.borrowAmt);
	            	if(res.detail.loanStatus==0){
	            	 $(".step2").attr("src","../images/H5-step2-2.png");
	            	 $(".step3").attr("src","../images/H5-step1-1.png");
	            	}else if(res.detail.loanStatus==1){
		            $(".step2").attr("src","../images/H5-step2-2.png");
		            $(".step3").attr("src","../images/H5-step1-2.png");	
	            	}
	            }else if(res.result==-1){
	         	   window.location = encodeURI('*'+res.resultNote);
	            }else{
	         	   alert(res.resultNote);   
	            }
	        },
	  });
	   $(".sureBtn").click(function(){
		   window.location = encodeURI('returnMainPage');  
	   })
})