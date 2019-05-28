var province;
var city;
var userId;
var token;
var SecurityId;
var isiPhoneX;
$(function() {
	//获取链接拼接参数
    var params = ns.getRequestParam();
    userId = params.userId;
    token = params.token;
    SecurityId=params.SecurityId;
    isiPhoneX=params.isiPhoneX;
    if(isiPhoneX==1){
    	$(".head").css("padding-top","44px")
    }else{
    	$(".head").css("padding-top","24px")
    }
    //密码框可视区分
    $(".eyeImage").click(function() {
        if ($(".passwordContent").attr("type") == "password") {
            $(".passwordContent").attr("type", "text");
            $(".eyeImage").attr("src", "../../images/open.png")
        } else {
            $(".passwordContent").attr("type", "password")
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
    //-----省市选择-----
    $('.selectCityImage').mPicker({
        //级别
        level: 2,
        //需要渲染的json，二级联动的需要嵌套子元素，有一定的json格式要求
        dataJson: dataJson,
        //true:联动
        Linkage: true,
        //显示行数
        rows: 6,
        //默认值填充
        idDefault: true,
        //分割符号
        splitStr: '-',
        //	    //头部代码
        //	    header:'<\div class="mPicker-header">两级联动选择插件<\/div>',
        confirm: function() {
            //更新json
            // this.container.data('mPicker').updateData(level3);
            // console.info($('.select-value').data('value1')+'-'+$('.select-value').data('value2'));
            //	    	console.log(province);
            //	    	console.log(city);
        },
        cancel: function() {
            //console.info($('.select-value').data('value1')+'-'+$('.select-value').data('value2'));
        }
    })
    //载入用户个人信息
    var data={
    		  "cmd": "socialSecurityDetail",
    		  "token": token,
    		  "userId": userId,
    		  "version": "1",
    		  "params": {
    		    "userId": userId,
    		    "SecurityId": SecurityId
    		  }
    		} 
    var dataString=JSON.stringify(data);
    showLoading();
	  $.ajax({
	      url:commonUrl+"/socialSecurityDetail",
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
	           province=res.detail.province;
	           city=res.detail.city;
	           if(res.detail.province+res.detail.city){
	        	   $(".addressContent").val(res.detail.province+res.detail.city);	          
	        	   }
	         
	           $('.accountContent').val(res.detail.loginSecurityNo);
	           $(".passwordContent").val(res.detail.loginPassword);
	          }else if(res.result==-1){
	       	   window.location = encodeURI('*'+res.resultNote);
	          }else{
	       	   alert(res.resultNote);   
	          }
	      },
	});
    
  //用户提交个人信息  
  $(".sureBtn").click(function(){
	  var address=$(".addressContent").val();
	  var account=$(".accountContent").val();
	  var password=$(".passwordContent").val();
	  var number = /^[0-9]*$/;
	  var reg = new RegExp("[\\u4E00-\\u9FFF]+","g");
	  if(address==""){
		  alert("地址不能为空")
	  }else if(account==""){
		  alert("社保账号不能为空")
	  }else if(!number.test(account)){
		  alert("请输入正确的社保账号")
	  }else if(password==""){
		  alert("登录密码不能为空")
	  }else if(reg.test(password)){
		  alert("请输入正确的账号密码")
	  }else{
		  //接后台接口
		  var data1={
				  "cmd": "socialSecuritySaveOrUpdate",
				  "token": token,
				  "userId": userId,
				  "version": "1",
				  "params": {
				    "userId": userId,
				    "loginSecurityNo": account,
				    "loginPassword": password,
				    "province": province,
				    "city": city,
				    "SecurityId": SecurityId
				  }
				}
		  var dataString=JSON.stringify(data1);
		  showLoading();
		  $.ajax({
		      url:commonUrl+"/socialSecuritySaveOrUpdate",
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
		        	  window.location="showSocialSecurity.html?token="+token +"&userId=" + userId; 
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