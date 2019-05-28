var userId;
var token;
var creditId;
var isiPhoneX;
$(function() {
    //获取链接拼接参数
    var params = ns.getRequestParam();
    userId = params.userId;
    token = params.token;
    creditId = params.creditId;
    isiPhoneX=params.isiPhoneX;
    if(isiPhoneX==1){
    	$(".head").css("padding-top","44px")
    }else{
    	$(".head").css("padding-top","24px")
    }
    //用户授权协议书
    $(".attory").click(function(){
    	 window.location = "dataAcquisitionService.html"
    })
     //返回app提额页面
    $(".returnBtn").click(function(){
		 window.location = encodeURI("mentionTheAmountApp");
	})
    //载入用户信息
    	  var data1=  {
    	     "cmd": "creditCardDetail",
    	     "token":token,
    	     "userId": userId,
    	     "version": "1",
    	     "params": {
    	     "userId":userId,
    	     "creditCardId": creditId
    	  }
        	    	}
    var dataString=JSON.stringify(data1);
	  showLoading();
	  $.ajax({
	      url:commonUrl+"/creditCardDetail",
	      type: 'post',
	      dataType:'json',
	      contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
	      timeout: 100000,
	      data: "{\"key\":\""+Encrypt(dataString)+"\"}",
	      success: function(dd){
	   	   hideLoading();
	          var res=JSON.parse(Decrypt(dd.key));
//	          console.log(res);
	          if(res.result==0){
	        	$(".userNameTxt").val(res.detail.customName); 
	        	 $(".creditAccountTxt").val(res.detail.cntCommt);
	        	 $(".creditTypeTxt").val(res.detail.bankName);
	        	 $(".creditPawTxt").val(res.detail.cntPass);
	        	 $(".userTelTxt").val(res.detail.mobilePhone);
	          }else if(res.result==-1){
	       	   window.location = encodeURI('*'+res.resultNote);
	          }else{
	       	   alert(res.resultNote);   
	          }
	      },
	});
	  
    //用户授权提交个人信息
    $(".sureBtn").click(function() {
        var myReg = /^[\u4e00-\u9fa5]+$/; //汉字正则
        var regExp = /^0?1[3|4|5|7|8][0-9]\d{8}$/;
        var reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,30}$/;
        var number = /^[0-9]*$/;
        var creditAccount = $(".creditAccountTxt").val();
        var creditType = $(".creditTypeTxt").val();
        var creditPaw = $(".creditPawTxt").val();
        var userTel = $(".userTelTxt").val();
        var userName = $(".userNameTxt").val();
        if (creditAccount == "") {
        	 alert("信用卡号不能为空")
        } else if (!number.test(creditAccount)) {
            alert("请输入正确的信用卡号")
        } else if (creditType == "") {
            alert("银行类型不能为空")
        } else if (!myReg.test(creditType)) {
            alert("请输入正确的银行类型")
        } else if (creditPaw == "") {
            alert("网银登录密码不能为空")
        } else if (!reg.test(creditPaw)) {
            alert("请输入正确的网银登录密码")
        } else if (userTel == "") {
            alert("开卡绑定的手机号不能为空")
        } else if (!regExp.test(userTel)) {
            alert("请输入正确的手机号")
        } else {
            //接后台接口
        	  var data=  {
        	    	  "cmd": "creditCardSaveOrUpdate",
        	    	  "token":token ,
        	    	  "userId": userId,
        	    	  "version": "1",
        	    	  "params": {
        	    	    "userId": userId,
        	    	    "bankCardNo":creditAccount,
        	    	    "bankCardPwd":creditPaw,
        	    	    "bankName": creditType,
        	    	    "mobilePhone": userTel,
                        "creditCardId":creditId
        	    	  }
        	    	}
        	  var dataString=JSON.stringify(data);
        	  showLoading();
        	  $.ajax({
        	      url:commonUrl+"/creditCardSaveOrUpdate",
        	      type: 'post',
        	      dataType:'json',
        	      contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
        	      timeout: 100000,
        	      data: "{\"key\":\""+Encrypt(dataString)+"\"}",
        	      success: function(dd){
        	   	   hideLoading();
        	          var res=JSON.parse(Decrypt(dd.key));
//        	          console.log(res);
        	          if(res.result==0){
        	           window.location="creditShow.html?token="+token +"&userId=" + userId;  
        	          }else if(res.result==-1){
        	       	   window.location = encodeURI('*'+res.resultNote);
        	          }else{
        	       	   alert(res.resultNote);   
        	          }
        	      },
        	});
        	
        }
    })
      //密码框可视区分
    $(".eyeImage").click(function() {
        if ($(".creditPawTxt").attr("type") == "password") {
            $(".creditPawTxt").attr("type", "text");
            $(".eyeImage").attr("src", "../../images/open.png")
        } else {
            $(".creditPawTxt").attr("type", "password")
            $(".eyeImage").attr("src", "../../images/close.png")
        }
    })
})