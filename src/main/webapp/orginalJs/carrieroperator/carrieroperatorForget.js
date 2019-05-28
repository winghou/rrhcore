var token;
var userId;
var reqId;
var pwdFlag;
var smsCodeFlag;
var tel;
$(function(){
	//赋初值
	var params = ns.getRequestParam();
	token=params.token;
	userId=params.userId;
	reqId=params.reqId1;
	pwdFlag=params.pwdFlag;
	smsCodeFlag=params.smsCodeFlag;
	tel=params.tel;
	if(smsCodeFlag=="true"){
		$(".code").removeClass("hide");
		fnSetBtnTime();
	}else if(smsCodeFlag=="false"){
		$(".code").addClass("hide");
	}

	//密码框可见与不可见
	 $(".eyeImage").click(function() {
	        if ($(".serTxt").attr("type") == "password") {
	            $(".serTxt").attr("type", "text");
	            $(".eyeImage").attr("src", "../../images/open.png")
	        } else {
	            $(".serTxt").attr("type", "password")
	            $(".eyeImage").attr("src", "../../images/close.png")
	        }
	    })
	    
})
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
//获取验证码
    function getForgetCode(){
	var data={
			  "cmd": "whiteKnightOperatorSendResetpwdSms",           //请求的命令字
			  "token": token,                        //用来表示是否允许访问服务器资源
			  "userId": userId,                      //根据业务需要 加的（这里是中介id）
			  "version": "1",                        //版本号：默认为1
			  "params": {
			    "userId":userId,  //用户ID
				"reqId":reqId        //任务id
			  }
			}
	 var dataString=JSON.stringify(data);
	showLoading1();
	$.ajax({
        url:commonUrl+"/whiteKnightOperatorSendResetpwdSms",
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
//            	window.location="carrieroperatorForget.html?token="+token+"&userId="+userId+"&reqId1="+reqId1+"&pwdFlag="+pwdFlag+"&smsCodeFlag="+smsCodeFlag;
	            }else if(res.result==-1){
         	   window.location = encodeURI('*'+res.resultNote);
            }else{
               alert(res.resultNote);  
            }
        },
  });
   }
    
    //重置运营商密密码
    function forgetBtn(){
    	var password=$(".serTxt").val();
    	var smsCode=$(".codeTxt").val();
    	var number = '^[0-9]*$' ;
    	if(smsCode=="" && !$(".code").hasClass("hide")){
         alert("请填写短信验证码");    	
         }else if(!smsCode.match(number)){
         alert("请输入正确的短信验证码");	 
         }else if(password==""){
         alert("请填写服务密码"); 	 
         }else if(password.length<6){
     	alert("服务密码不能少于6位")
     	}else if(!password.match(number)){
         alert("请输入正确的服务密码");
         }else{
        	 	var data1={
        	      		  "cmd": "whiteKnightOperatorResetpwd",           //请求的命令字
        	      		  "token": token,                        //用来表示是否允许访问服务器资源
        	      		  "userId": userId,                      //根据业务需要 加的（这里是中介id）
        	      		  "version": "1",                        //版本号：默认为1
        	      		  "params": {
        	      		    "userId": userId,  //用户ID
        	      			"phone":tel,      //认证手机号
        	      			"password":password,     //服务密码
        	      			"reqId":reqId,        //任务id
        	      			"smsCode":smsCode     //短信验证码
        	      		  }
        	      		}
        	 	  var dataString=JSON.stringify(data1);
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
        	            	if(res.detail.process_code=="CCOM1000"){
        	            		alert(res.detail.content);
        	            		setTimeout(function(){
        	            			   window.location="../yunyingSecond.html?token="+token+"&userId="+userId+"&tel="+tel;	
        	            		},2000);
        	         
        	            	}else if(res.detail.process_code=="CCOM4214" || res.detail.process_code=="CCOM4216"){
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
                       			  forget(); 
                   		 })
        	             
        	            }
        	        },
        	  });
         }
    }
    function forget(){
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