var carauthToken;
var web;
var toke;
var userId;
var tel;
var reset_pwd_method;
$(function(){
	var params = ns.getRequestParam();
	tel=params.tel;
	userId=params.userId;
	token=params.token;
	$(".telTxt").val(params.tel);
	$(".eye").click(function(){
		if ($(".serTxt").attr("type") == "password") {
		$(".serTxt").attr("type", "text");
		$(".eyeImage").attr("src","../images/open.png")
		}
		else {
		$(".serTxt").attr("type", "password")
			$(".eyeImage").attr("src","../images/close.png")
		}
	})
		var data={
			  "cmd": "getBaseInfoToOperator",    //获取运营商认证所需基本资料
			   "token": token,
			   "userId":userId,
			   "version": "1",
			   "params": {         //用户id
				"userId":userId, 
			    "newPhone": tel  //修改后手机号  (如果没有修改则为空)
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
        	   hideLoading();
               var res=JSON.parse(Decrypt(dd.key));
               console.log(res);
               if(res.result==0){
            	   carauthToken=res.detail.token;
            	   web=res.detail.website;
            	   reset_pwd_method=res.detail.reset_pwd_method;
            	   if(res.detail.reset_pwd_method=="1"){
                  	 $(".serviceP").addClass("hide");
                   }
                   else{
                  	 $(".serviceP").removeClass("hide"); 
                   }
            	   
               }else if(res.result==-1){
            	   window.location = encodeURI('*'+res.resultNote);
               }else{
            	   alert(res.resultNote);   
               }
           },
     });
})



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
        $('.getCode').text("重新发送" + countdown + "s");
        countdown--;
    }
    setTimeout(function() {
        fnSetBtnTime();
    }, 1000);
}

function getCode(){//获取验证码
		var data1={//首次获取验证码
				  "cmd": "operatorResetServiceNumber",     //运营商认证获取验证码和采集报告
				  "token": token,
				  "userId":userId,
				  "version": "1",
				  "params": {
				   "userId" :userId,
					"phone": tel,      //手机号码
				    "token": carauthToken,       //运营商认证令牌
				    "password": "",    //服务密码
				    "website": web,     //网站英文名称
				    "captcha": $(".codeTxt").val(),    //验证码
				    "reset_pwd_method":reset_pwd_method,
				    "type": "RESEND_RESET_PWD_CAPTCHA"      
				  }
				}
		var dataString=JSON.stringify(data1);

		showLoading();
	$.ajax({
       url:commonUrl+"/operatorResetServiceNumber",
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
              			location.reload();   
           	   }else{
           		   alert(res.resultNote);  
           	   }   
           }
       },
 });
	
}
function test(){
	var number = '^[0-9]*$' ;
	var params = ns.getRequestParam();
	var codeTxt=$(".codeTxt").val();
	var serTxt=$(".serTxt").val();
	if(codeTxt==""){
		alert("验证码不能为空")
	}else if( !$(".serviceP").hasClass("hide") && serTxt==""){
		alert("服务密码不能为空")
	}else if(!codeTxt.match(number)){
		alert("请输入正确的验证码！")
	} else if( codeTxt.indexOf(">") !==-1||serTxt.indexOf(">") !==-1) {
        alert('请勿输入非法字符');
    }else{
       	var data3={//提交数据
    			  "cmd": "operatorResetServiceNumber",     //运营商认证获取验证码和采集报告
    			  "token": token,
        		  "userId":userId,
        		  "version": "1",
        		  "params": {
        			  "userId" :userId,
        		    "phone": tel,      //手机号码
        		    "token": carauthToken,       //运营商认证令牌
        		    "password":serTxt ,    //服务密码
        		    "website": web,     //网站英文名称
        		    "captcha": $(".codeTxt").val(), 
        		    "reset_pwd_method":reset_pwd_method,//验证码
        		    "type": "SUBMIT_RESET_PWD"      //空 获取验证码   RESEND_CAPTCHA 重发短信验证码  SUBMIT_CAPTCHA  提交短信验证码
        		  }
        		}
       	var dataString=JSON.stringify(data3);
       	showLoading();
    	$.ajax({
            url:commonUrl+"/operatorResetServiceNumber",
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
                		$(".codeTxt").val("");
                		$(".serTxt").val("");
                		alert("重新输入新的验证码！")
               	 }else if(res.detail.process_code=="11000"){
               		 window.location = "yunyingSecond.html?token="+token +"&userId=" + userId +"&tel=" + tel;
               	 }
                }else if(res.result==-1){
             	   window.location = encodeURI('*'+res.resultNote);
                }else{
                	 if(res.detail.process_code=="30000"){
          
                   			location.reload();
               		
                	   }else{
                		   alert(res.resultNote);  
                	   }  
                }
            },
      });
    	
    }
	
}