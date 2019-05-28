var token;
var userId;
var reqId="";
$(function(){
	//文本框赋初值
	var params = ns.getRequestParam();
	if($(".telTxt").val()==""){
		$(".telTxt").val(params.tel);
	}
	if($(".serTxt").val()==""){
		$(".serTxt").val(params.password);
	}
	token=params.token;
	userId=params.userId;
	//密码可见与否
	   $(".eyeImage").click(function() {
	        if ($(".serTxt").attr("type") == "password") {
	            $(".serTxt").attr("type", "text");
	            $(".eyeImage").attr("src", "../../images/open.png")
	        } else {
	            $(".serTxt").attr("type", "password")
	            $(".eyeImage").attr("src", "../../images/close.png")
	        }
	    })
	  $(".link").click(function(){
		 locationChanged("../attorney.html");
	 })   
})
//用户提交信息按钮
function submitInformation(){
	var params = ns.getRequestParam();
	var number = '^[0-9]*$' ;
	var telTxt =$(".telTxt").val();
	var serTxt =$(".serTxt").val();
	var codeTxt=$(".codeTxt").val();
	if(telTxt==""){
		alert("请输入11位手机号码")
	}else if(serTxt==""){
		alert("服务密码不能为空")
	}else if(serTxt.length<6){
		alert("服务密码不能少于6位")
	}else if(!ns.isMobel(telTxt) ){
		alert("请输入正确的手机号码")
	}else if(codeTxt=="" && !$(".code").hasClass("hide")){
		alert("请输入短信验证码")
    }else if(telTxt.indexOf("<") !== -1 || serTxt.indexOf(">") !== -1 || codeTxt.indexOf(">")!==-1 ) {
        alert('请勿输入非法字符');
    }else if(!codeTxt.match(number)){
    	alert("请输入正确的短信验证码")
    }else{
    	var data={
				  "cmd": "whiteKnightOperatorLogin",           //请求的命令字
				  "token": token,                        //用来表示是否允许访问服务器资源
				  "userId": userId,                      //根据业务需要 加的（这里是中介id）
				  "version": "1",                        //版本号：默认为1
				  "params": {
				    "userId": userId,  //用户ID
					"phone":telTxt,      //认证手机号
					"password":serTxt,     //服务密码
					"reqId":reqId,        //任务id
					"smsCode":codeTxt     //短信验证码
				  }
    	}
         var dataString=JSON.stringify(data);
    	showLoading1();
     	$.ajax({
            url:commonUrl+"/whiteKnightOperatorLogin",
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
                	$(".telTxt").attr("readonly","true");
                   if(res.detail.process_code=="CCOM1000"){
                		window.CreditXAgent('onSubmit','submit_operator', {
            		       	  msg: "成功",
            		       	  rslt: 0,
            		       	  // 其他信息
            		       	});
              		 locationChanged(encodeURI("success"));
                   }else if(res.detail.process_code=="CCOM3069"){
                	  $(".code").removeClass("hide");
                	  fnSetBtnTime();
                	  reqId=res.detail.reqId;
                   }else if(res.detail.process_code=="CCOM3014"){
                	   $(".code").removeClass("hide");
                 	  fnSetBtnTime();
                 	  reqId=res.detail.reqId;
                 	  $(".getCode").attr("onclick","getCode1()");
                 	  $(".surebtn").attr("onclick","submitInformation1()");
                   }else if(res.detail.process_code=="CCOM4025"||res.detail.process_code=="CCOM4207"||res.detail.process_code=="CCOM4208"){
                	   alert(res.detail.content);
                   }
                }else if(res.result==-1){
             	   window.location = encodeURI('*'+res.resultNote);
                }else if(res.result==1){
                		 showPanel(res.resultNote);
                		 $(".cancel").click(function(){
                   			 hidePanel();
                   		 })
                   		  $(".sure").click(function(){
                   			  location.reload(); 
               			window.CreditXAgent('onSubmit','submit_operator', {
                	       	  msg: "失败",
                	       	  rslt: 1,
                	       	  // 其他信息
                	       	});
              	
                   			
               		 })
                  		
      
                
            }else{
            	 alert(res.resultNote);  
            }
            }
      });
    	}	
    
}
//二次鉴权用户提交信息按钮
function submitInformation1(){
	var params = ns.getRequestParam();
	var number = '^[0-9]*$' ;
	var telTxt =$(".telTxt").val();
	var serTxt =$(".serTxt").val();
	var codeTxt=$(".codeTxt").val();
	if(telTxt==""){
		alert("请输入11位手机号码")
	}else if(serTxt==""){
		alert("服务密码不能为空")
	}else if(serTxt.length<6){
		alert("服务密码不能少于6位")
	}else if(!ns.isMobel(telTxt) ){
		alert("请输入正确的手机号码")
	}else if(codeTxt=="" && !$(".code").hasClass("hide")){
		alert("请输入短信验证码")
    }else if(telTxt.indexOf("<") !== -1 || serTxt.indexOf(">") !== -1 || codeTxt.indexOf(">")!==-1 ) {
        alert('请勿输入非法字符');
    }else if(!codeTxt.match(number)){
    	alert("请输入正确的短信验证码")
    }else{
    	var data={
    			  "cmd": "whiteKnightOperatorVerifyAuthSms",           //请求的命令字
    			  "token": token,                        //用来表示是否允许访问服务器资源
    			  "userId": userId,                      //根据业务需要 加的（这里是中介id）
    			  "version": "1",                        //版本号：默认为1
    			  "params": {
    			    "userId": userId,  //用户ID
    				"phone":telTxt,      //认证手机号
    				"password":serTxt,     //服务密码
    				"reqId":reqId,        //任务id
    				"smsCode":codeTxt     //短信验证码
    			  }
    			}
         var dataString=JSON.stringify(data);
    	showLoading1();
     	$.ajax({
            url:commonUrl+"/whiteKnightOperatorVerifyAuthSms",
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
                	$(".telTxt").attr("readonly","true");
                   if(res.detail.process_code=="CCOM1000"){
                		window.CreditXAgent('onSubmit','submit_operator', {
            		       	  msg: "成功",
            		       	  rslt: 0,
            		       	  // 其他信息
            		       	});
              		 locationChanged(encodeURI("success"));
                   }else if(res.detail.process_code=="CCOM4206"){
                	   alert(res.detail.content);
                   }
                }else if(res.result==-1){
             	   window.location = encodeURI('*'+res.resultNote);
                }else{
                		 showPanel(res.resultNote);
                		 $(".cancel").click(function(){
                   			 hidePanel();
                   		 })
                   		  $(".sure").click(function(){
                   			location.reload();

               			window.CreditXAgent('onSubmit','submit_operator', {
                	       	  msg: "失败",
                	       	  rslt: 1,
                	       	  // 其他信息
                	       	});
  
                   			
               		 })
                  		
                	   
                }
            },
      });
    	}	
    
}
//用户获取验证码
  function getCode(){
  var data1= {
    		  "cmd": "whiteKnightOperatorSendLoginSms",           //请求的命令字
    		  "token": token,                        //用来表示是否允许访问服务器资源
    		  "userId":userId ,                      //根据业务需要 加的（这里是中介id）
    		  "version": "1",                        //版本号：默认为1
    		  "params": {
    		    "userId": userId,  //用户ID
    			"reqId":reqId,        //任务id
    		  }
    		}
    var dataString=JSON.stringify(data1);
	showLoading1();
	$.ajax({
        url:commonUrl+"/whiteKnightOperatorSendLoginSms",
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
               alert(res.resultNote);  
            }
        },
  });
    }
//二次鉴权用户获取验证码
  function getCode1(){
	 var data1= {
		  "cmd": "whiteKnightOperatorSendAuthSms",           //请求的命令字
		  "token": token,                        //用来表示是否允许访问服务器资源
		  "userId": userId,                      //根据业务需要 加的（这里是中介id）
		  "version": "1",                        //版本号：默认为1
		  "params": {
		    "userId": userId,  //用户ID
			"reqId":reqId,        //任务id
		  }
		}
    var dataString=JSON.stringify(data1);
	showLoading1();
	$.ajax({
        url:commonUrl+"/whiteKnightOperatorSendAuthSms",
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
               alert(res.resultNote);  
            }
        },
  });
    }
//获取验证码倒计时
var countdown = 60;
function fnSetBtnTime() {
    if (countdown === 0) {
        $('.getCode').removeAttr('disabled');
        $('.getCode').css({
            'color': '#E284D2'
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
function locationChanged(str){
	window.location=str
}
function forget(){
	var tel=$(".telTxt").val()
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
		showLoading1();
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
	            	
	            	window.location="carrieroperatorForget.html?token="+token+"&userId="+userId+"&reqId1="+reqId1+"&pwdFlag="+pwdFlag+"&smsCodeFlag="+smsCodeFlag+"&tel="+tel;
   	            }else if(res.result==-1){
	         	   window.location = encodeURI('*'+res.resultNote);
	            }else{
	               alert(res.resultNote);  
	            }
	        },
	  });
	}                                                                                         

}