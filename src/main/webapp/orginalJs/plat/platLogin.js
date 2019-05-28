var userId;
var token;
var platId;
var isiPhoneX;
$(function(){
	//获取链接拼接参数
    var params = ns.getRequestParam();
    userId = params.userId;
    token = params.token;
   platId=params.platId;
   isiPhoneX=params.isiPhoneX;
   if(isiPhoneX==1){
   	$(".head").css("padding-top","44px")
   }else{
   	$(".head").css("padding-top","24px")
   }
   //初始化页面载入用户账户信息 
    var data={
    		  "cmd": "platformLogin",
    		  "token": token,
    		  "userId": userId,
    		  "version": "1",
    		  "params": {
    		    "cntLx": platId,
    		    "userId": userId
    		  }
    		} 
    var dataString=JSON.stringify(data);
    showLoading();
	   $.ajax({
	         url:commonUrl+"/platformLogin",
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
	                $(".platLogo").attr("src",res.detail.logoimageUrl);
	                $(".platName").text(res.detail.platformName);
	                if(res.detail.cntCommt!==""){
	                	 $(".platAccountTxt").val(res.detail.cntCommt);
	                   }
	               if(res.detail.cntPass!==""){
	            	   $(".platPwdTxt").val(res.detail.cntPass);  
	               }
	             }else if(res.result==-1){
	          	   window.location = encodeURI('*'+res.resultNote);
	             }else{
	          	   alert(res.resultNote);   
	             }
	         },
	   });
    
    //密码框可视区分
    $(".eyeImage").click(function() {
        if ($(".platPwdTxt").attr("type") == "password") {
            $(".platPwdTxt").attr("type", "text");
            $(".eyeImage").attr("src", "../../images/open.png")
        } else {
            $(".platPwdTxt").attr("type", "password")
            $(".eyeImage").attr("src", "../../images/close.png")
        }
    })
    //用户授权协议书
    $(".attory").click(function(){
    	 window.location = "dataAcquisitionService.html"
    })
    //返回app提额页面
    $(".returnBtn").click(function(){
		 window.location = encodeURI("mentionTheAmountApp");
	})
    //用户提交信息授权
   $(".sureBtn").click(function(){
	   var platAccount=$(".platAccountTxt").val();
	   var platPwdTxt=$(".platPwdTxt").val();
	   var number = /^[0-9]*$/;
	   var reg = new RegExp("[\\u4E00-\\u9FFF]+","g");
	   if(platAccount==""){
		   alert("平台账号不能为空");
	   }else if(!number.test(platAccount)){
		   alert("请输入正确的平台账号");
	   }else if(platPwdTxt==""){
		   alert("登录密码不能为空");
	   }else if(reg.test(platPwdTxt)){
		   alert("请输入正确的密码格式");
	   }else{
		   //接后台接口
		   var data1={
				   "cmd": "platformLoanCtmCntSave",
				   "token": token,
				   "userId": userId,
				   "version": "1",
				   "params": {
				     "cntLx": platId,
				     "userId": userId,
				     "cntCommt": platAccount,
				     "cntPass": platPwdTxt
				   }
				 } 
		   var dataString=JSON.stringify(data1);
		    showLoading();
			   $.ajax({
			         url:commonUrl+"/platformLoanCtmCntSave",
			         type: 'post',
			         dataType:'json',
			         contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
			         timeout: 100000,
			         data: "{\"key\":\""+Encrypt(dataString)+"\"}",
			         success: function(dd){
			      	   hideLoading();
			             var res=JSON.parse(Decrypt(dd.key));
			            // console.log(res);
			             if(res.result==0){
		window.location="platShow.html?token="+token +"&userId=" + userId;  
			             }else if(res.result==-1){
			          	   window.location = encodeURI('*'+res.resultNote);
			             }else{
			          	   alert(res.resultNote);   
			             }
			         },
			   });
	   }
   }) 
    
    
})