var carauthToken;
var web;
var token;
var userId;
$(function(){
	var params = ns.getRequestParam();
	var isget=params.isget;
	$(".telTxt").val(params.tel);
	$(".serTxt").val(params.password);
	token=params.token;
	userId=params.userId;
	web=params.web;
	carauthToken=params.carauthToken;
	if(params.isget==1){
		$(".getCode").addClass("hide");
	}
//	var data={
//				  "cmd": "getBaseInfoToOperator",    //获取运营商认证所需基本资料
//				   "token": params.token,
//				   "userId":params.userId,
//				   "version": "1",
//				   "params": {         //用户id
//					"userId":params.userId, 
//				    "newPhone": $(".telTxt").val()  //修改后手机号  (如果没有修改则为空)
//				  }
//				}
//	var dataString=JSON.stringify(data); 
//	showLoading();
//	   $.ajax({
//           url:commonUrl+"/getBaseInfoToOperator",
//           type: 'post',
//           dataType:'json',
//           contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
//           timeout: 100000,
//           data: "{\"key\":\""+Encrypt(dataString)+"\"}",
//           success: function(dd){
//        	   hideLoading();
//               var res=JSON.parse(Decrypt(dd.key));
//               if(res.result==0){
//            	   carauthToken=res.detail.token;
//            	   web=res.detail.website;
//            	   fnSetBtnTime();
//               }else if(res.result==-1){
//            	   window.location = encodeURI('*'+res.resultNote);
//               }else{
//            	   alert(res.resultNote);   
//               }
//           },
//     });
	 fnSetBtnTime();
})
	   function getCode(){//获取验证码
	       if($(".getCode").text()=="获取验证码"){
	        var params = ns.getRequestParam();
		    var telTxt =$(".telTxt").val();
		    var serTxt =$(".serTxt").val();
			if(telTxt=="" ){
				alert("请输入手机号");
				return;
			}else if(serTxt=="" ){
				alert("请输入服务密码");
			}else if(!ns.isMobel(telTxt)){
				alert("请输入正确的手机号码");
				return;
			}else{
				var data1={//首次获取验证码
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
						    "captcha": $(".codeTxt").val(),    //验证码
						    "type": "RESEND_CAPTCHA"      //空 获取验证码   RESEND_CAPTCHA 重发短信验证码  SUBMIT_CAPTCHA  提交短信验证码
						  }
						}
				var dataString=JSON.stringify(data1);
//				 	var data2={//再次获取验证码
//							  "cmd": "operator",     //运营商认证获取验证码和采集报告
//				  		  "token": params.token,
//				  		  "userId":params.userId,
//				  		  "version": "1",
//				  		  "params": {
//				  			 "userId" :params.userId,
//				  		    "phone": $(".telTxt").val(),      //手机号码
//				  		    "token": carauthToken,       //运营商认证令牌
//				  		    "password": $(".serTxt").val(),    //服务密码
//				  		    "website": web,     //网站英文名称
//				  		    "captcha": $(".codeTxt").val(),    //验证码
//						    "type": "RESEND_CAPTCHA"      //空 获取验证码   RESEND_CAPTCHA 重发短信验证码  SUBMIT_CAPTCHA  提交短信验证码
//						  }
//						}
				showLoading();
			$.ajax({
		        url:commonUrl+"/operator",
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
		            	fnSetBtnTime();	
		            }else if(res.result==-1){
		         	   window.location = encodeURI('*'+res.resultNote);
		            }else{
		            	 if(res.detail.process_code=="30000"){
		            		 showPanel(res.resultNote);
	                		 $(".cancel").click(function(){
	                   			 hidePanel();
	                   		 })
	                   		  $(".sure").click(function(){
	               			 var tel=$(".telTxt").val();
	               			 var password=$(".serTxt").val();
	               			 var tel=$(".telTxt").val();
	              		   window.location="yunyingSecond.html?token="+token +"&userId=" + userId +"&tel=" + tel+"&password="+password;  
	               		 })
	                	   }else{
	                		   alert(res.resultNote);  
	                	   }   
		            }
		        },
		  });
			}
}else{
	return;
}
		}

function test(){
	var params = ns.getRequestParam();
	var number = '^[0-9]*$' ; 
	var telTxt =$(".telTxt").val();
	var serTxt =$(".serTxt").val();
	var codeTxt=$(".codeTxt").val();
	if(telTxt==""){
		alert("请输入11位的手机号码")
	}else if(serTxt==""){
		alert("服务密码不能为空")
	}else if(codeTxt==""){
		alert("验证码不能为空")
	}else if(!ns.isMobel(telTxt) ){
		alert("请输入正确的手机号码")
	}else if(!codeTxt.match(number)){
		alert("请输入正确的验证码！")
	} else if(telTxt.indexOf("<") !== -1 || serTxt.indexOf(">") !== -1 || codeTxt.indexOf(">") !==-1) {
        alert('请勿输入非法字符');
    }else{
    	
       	var data3={//提交数据
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
        		    "captcha": $(".codeTxt").val(),    //验证码
        		    "type": "SUBMIT_CAPTCHA"      //空 获取验证码   RESEND_CAPTCHA 重发短信验证码  SUBMIT_CAPTCHA  提交短信验证码
        		  }
        		}
       	var dataString=JSON.stringify(data3);
       	showLoading();
    	$.ajax({
            url:commonUrl+"/operator",
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
                	if(res.detail.process_code=="10002"||res.detail.process_code=="10001"){
               		 var password=$(".serTxt").val();
               		 var tel=$(".telTxt").val();
               		 var isget=0;
               		 window.location="validate.html?token="+token +"&userId=" + userId +"&tel=" + tel+"&password="+password+"&isget="+isget+"&web="+web+"&carauthToken="+carauthToken;
               	 }else if(res.detail.process_code=="10008"){
               		window.CreditXAgent('onSubmit','submit_operator', {
        		       	  msg: "成功",
        		       	  rslt: 0,
        		       	  // 其他信息
        		       	});
               		 window.location = encodeURI("success") 
               	 }else if(res.detail.process_code=="10017"||res.detail.process_code=="10018"){
               		 showPanel(res.detail.content);
               		 $(".cancel").click(function(){
               			 hidePanel();
               		 })
               		 $(".sure").click(function(){
               			 var tel=$(".telTxt").val();
               			 var password=$(".serTxt").val();
               			 var isget=1;
               			 window.location="yunying.html?token="+token +"&userId=" + userId +"&tel=" + tel+"&password="+password+"&isget="+isget+"&web="+web+"&carauthToken="+carauthToken;
               			
               		 })
               	 }
                }else if(res.result==-1){
             	   window.location = encodeURI('*'+res.resultNote);
                }else{
                	 if(res.detail.process_code=="30000"){
                		 showPanel(res.resultNote);
                		 $(".cancel").click(function(){
                   			 hidePanel();
                   		 })
                   		  $(".sure").click(function(){
               			 var tel=$(".telTxt").val();
               			 var password=$(".serTxt").val();
               			 var tel=$(".telTxt").val();
               			window.CreditXAgent('onSubmit','submit_operator', {
                	       	  msg: "失败",
                	       	  rslt: 1,
                	       	  // 其他信息
                	       	});
              		   window.location="yunyingSecond.html?token="+token +"&userId=" + userId +"&tel=" + tel+"&password="+password;
                   			
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
    	
    }
	
}

var countdown = 60;
function fnSetBtnTime() {
    if (countdown === 0) {
        $('.getCode').removeAttr('disabled');
        $('.getCode').css({
            'color': '#7F8CFF'
        });
        $('.getCode').text("获取验证码");
        countdown = 60;
        return;
    } else {
        $('.getCode').attr('disabled', 'disabled');
        $('.getCode').css({
            'color': '#999'
        });
        $('.getCode').text(  countdown+"s" );
        countdown--;
    }
    setTimeout(function() {
        fnSetBtnTime();
    }, 1000);
}