var carauthToken;
var web;
var token;
var userId;
$(function(){
	var params = ns.getRequestParam();
	if($(".telTxt").val()==""){
		$(".telTxt").val(params.tel);
	}
	if($(".serTxt").val()==""){
		$(".serTxt").val(params.password);
	}
	token=params.token;
	userId=params.userId;
	checktel();
	$(".telTxt").bind('input propertychange',function(){telchange();});
	 $(".link").click(function(){
		 locationChanged("attorney.html");
	 })
//	$(".forget").click(function(){
//		if($(".telTxt").val()==""){
//			alert("请输入手机号")
//		}else{
//			locationChanged("forget.html?token="+token +"&userId=" + userId +"&tel=" + $(".telTxt").val());
//		}
//	})
})
function locationChanged(str){
//	window.CreditXAgent('onLeavePage');
	window.location=str
}
function test(){
	var params = ns.getRequestParam();
	var number = '^[0-9]*$' ;
	var telTxt =$(".telTxt").val();
	var serTxt =$(".serTxt").val();
	var number = '^[0-9]*$' ;
	if(telTxt==""){
		alert("请输入11位手机号码")
	}else if(serTxt==""){
		alert("服务密码不能为空")
	}else if(serTxt.length<6){
		alert("服务密码不能少于6位")
	}else if(!ns.isMobel(telTxt) ){
		alert("请输入正确的手机号码")
	} else if(telTxt.indexOf("<") !== -1 || serTxt.indexOf(">") !== -1 ) {
        alert('请勿输入非法字符');
    }else{
    	var data={
    			  "cmd": "getBaseInfoToOperator",    //获取运营商认证所需基本资料
    			   "token": params.token,
    			   "userId":params.userId,
    			   "version": "1",
    			   "params": {         //用户id
    				"userId":params.userId,
    			    "newPhone": $(".telTxt").val()  //修改后手机号  (如果没有修改则为空)
    			  }
    			}
    	var dataString=JSON.stringify(data);
    	showLoading();
    	   $.ajax({
               url:commonUrl+"/getBaseInfoToOperator",
               type: 'post',
               dataType:'json',
               contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
               timeout: 100000,
               data: "{\"key\":\""+Encrypt(dataString)+"\"}",
               success: function(dd){
                   var res=JSON.parse(Decrypt(dd.key));
                   //console.log(res);
                   if(res.result==0){
                	   carauthToken=res.detail.token;
                	   web=res.detail.website;
                     if(res.detail.reset_pwd_method=="0"){
                    	 $(".forget").addClass("hide");
                     }
                     else{
                    	 $(".forget").removeClass("hide");
                     }
                	var data1={//提交数据
       			  "cmd": "operator",     //运营商认证获取验证码和采集报告
       			  "token": params.token,
           		  "userId":params.userId,
           		  "version": "1",
           		  "params": {
           		   "userId" :params.userId,
           		    "phone": $(".telTxt").val(),      //手机号码
           		    "token": carauthToken,       //运营商认证令牌
           		    "password": $(".serTxt").val(),    //服务密码
           		    "website": web,     //网站英文名称
           		    "captcha": "",    //验证码
           		    "type": ""      //空 获取验证码   RESEND_CAPTCHA 重发短信验证码  SUBMIT_CAPTCHA  提交短信验证码
           		  }
           		}
          	var dataString1=JSON.stringify(data1);

       	$.ajax({
               url:commonUrl+"/operator",
               type: 'post',
               dataType:'json',
               contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
               timeout: 100000,
               data: "{\"key\":\""+Encrypt(dataString1)+"\"}",
               success: function(dd){
            	   hideLoading();
                   var res=JSON.parse(Decrypt(dd.key));
                   //console.log(res);
                   if(res.result==0){
                	 if(res.detail.process_code=="10002"||res.detail.process_code=="10001"){
                		 var password=$(".serTxt").val();
                		 var tel=$(".telTxt").val();
                		 var isget=0;
                		 locationChanged("yunying.html?token="+token +"&userId=" + userId +"&tel=" + tel+"&password="+password+"&isget="+isget+"&web="+web+"&carauthToken="+carauthToken);
                	 }else if(res.detail.process_code=="10008"){
                			window.CreditXAgent('onSubmit','submit_operator', {
              		       	  msg: "成功",
              		       	  rslt: 0,
              		       	  // 其他信息
              		       	});
                		 locationChanged(encodeURI("success"))
                	 }else if(res.detail.process_code=="10017"||res.detail.process_code=="10018"){
                		 showPanel(res.detail.content);
                		 $(".cancel").click(function(){
                			 hidePanel();
                		 })
                		 $(".sure").click(function(){
                			 var tel=$(".telTxt").val();
                			 var password=$(".serTxt").val();
                			 var isget=1;
                			 locationChanged("yunying.html?token="+token +"&userId=" + userId +"&tel=" + tel+"&password="+password+"&isget="+isget+"&web="+web+"&carauthToken="+carauthToken);
                		 })
                	 }
                   }else if(res.result==-1){
                	   locationChanged(encodeURI('*'+res.resultNote));
                   }else{
                	   if(res.detail.process_code=="30000"){
                			window.CreditXAgent('onSubmit','submit_operator', {
                  	       	  msg: "失败",
                  	       	  rslt: 1,
                  	       	  // 其他信息
                  	       	});
                			 showPanel(res.resultNote);
                    		 $(".cancel").click(function(){
                       			 hidePanel();
                       		 })
                       		  $(".sure").click(function(){
                   			 var tel=$(".telTxt").val();
                   			 var password=$(".serTxt").val();
                   			 var tel=$(".telTxt").val();
                  		   locationChanged("yunyingSecond.html?token="+token +"&userId=" + userId +"&tel=" + tel+"&password="+password);
                   		 })
                	   }else{
                			window.CreditXAgent('onSubmit','submit_operator', {
                  	       	  msg: "失败",
                  	       	  rslt: 1,
                  	       	  // 其他信息
                  	       	});
                		   alert(res.resultNote);
                	   }

                   }
               },
         });

                   }else if(res.result==-1){

                	   hideLoading()
                	   locationChanged(encodeURI('*'+res.resultNote));
                   }else{
                	   hideLoading()
                	   alert(res.resultNote);
                   }
               },
         });

    }

}
function telchange(){
	var myreg = /^1\d{10}$/;
	var tel=$(".telTxt").val();
	if(ns.isMobel(tel)){
		setTimeout(function(){checktel();},200)

	}
}
function checktel(){
	var params = ns.getRequestParam();
	token=params.token;
	userId=params.userId;
	var data3={
			  "cmd": "getBaseInfoToOperator",    //获取运营商认证所需基本资料
			   "token": params.token,
			   "userId":params.userId,
			   "version": "1",
			   "params": {         //用户id
				"userId":params.userId,
			    "newPhone": $(".telTxt").val()  //修改后手机号  (如果没有修改则为空)
			  }
			}
	var dataString=JSON.stringify(data3);
	showLoading();
	   $.ajax({
         url:commonUrl+"/getBaseInfoToOperator",
         type: 'post',
         dataType:'json',
         contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
         timeout: 100000,
         data: "{\"key\":\""+Encrypt(dataString)+"\"}",
         success: function(dd){
      	   hideLoading();
             var res=JSON.parse(Decrypt(dd.key));
             //console.log(res);
             if(res.result==0){
          	   carauthToken=res.detail.token;
          	   web=res.detail.website;
               if(res.detail.reset_pwd_method=="0"){
              	 $(".forget").addClass("hide");
               }
               else{
              	 $(".forget").removeClass("hide");
               }
             }else if(res.result==-1){
          	   hideLoading()
          	   locationChanged(encodeURI('*'+res.resultNote));
             }else{
          	   hideLoading()
          	   alert(res.resultNote);
          	 $(".forget").addClass("hide");
             }
         },
   });
}
function forget(){
	var tel=$(".telTxt").val();
	if(tel==""){
		alert("请输入11位手机号码")
	}else if(!ns.isMobel(tel) ){
		alert("请输入正确的手机号码")
	}else{
		var data2={
				  "cmd": "whiteKnightOperatorResetpwd",           //请求的命令字
				  "token": token,                        //用来表示是否允许访问服务器资源
				  "userId": userId,                      //根据业务需要 加的（这里是中介id）
				  "version": "1",                        //版本号：默认为1
				  "params": {
				    "userId": userId,  //用户ID
					"phone":tel,      //认证手机号
					"password":"",     //服务密码
					"reqId":"",        //任务id
					"smsCode":"",     //短信验证码
				  }
				}
		 var dataString=JSON.stringify(data2);
		showLoading();
		$.ajax({
	        url:commonUrl+"/whiteKnightOperatorResetpwd",
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
	            	var reqId1=res.detail.reqId;
	            	var pwdFlag=res.detail.pwdFlag;
	            	var smsCodeFlag=res.detail.smsCodeFlag;
	            	
	            	window.location="carrieroperator/carrieroperatorForget.html?token="+token+"&userId="+userId+"&reqId1="+reqId1+"&pwdFlag="+pwdFlag+"&smsCodeFlag="+smsCodeFlag+"&tel="+tel;
   	            }else if(res.result==-1){
	         	   window.location = encodeURI('*'+res.resultNote);
	            }else{
	               alert(res.resultNote);  
	            }
	        },
	  });
	}                                                                                         

}