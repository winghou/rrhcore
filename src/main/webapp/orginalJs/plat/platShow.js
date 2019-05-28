var userId;
var token;
var totalPlat;
var isiPhoneX;
$(function(){
	//获取链接拼接参数
    var params = ns.getRequestParam();
    userId = params.userId;
    token = params.token;
    isiPhoneX=params.isiPhoneX;
    if(isiPhoneX==1){
    	$(".head").css("padding-top","44px")
    }else{
    	$(".head").css("padding-top","24px")
    }
    //返回app提额页面
    $(".returnBtn").click(function(){
		 window.location = encodeURI("mentionTheAmountApp");
	})
    var data={
  		  "cmd": "userPlatforms",
  		  "token": token,
  		  "userId": userId,
  		  "version": "1",
  		  "params": {
  		    "userId": userId
  		  }
  		} 
    var dataString=JSON.stringify(data);
    showLoading();  
	   $.ajax({
	         url:commonUrl+"/userPlatforms",
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
	            	 totalPlat=res.detail.platformSize;
	            	 var platList=res.detail.userPlatformList;
	            	 $(platList).each(function(i,obj){
	            		 var record=$("<div class='record'></div>").attr('platId',obj.platformId).appendTo($(".list"));
	            		 var divImg=$("<div></div>").appendTo(record);
	            		 var socialSecuritylogo=$("<img class='socialSecurity-logo'>").attr("src",obj.logoimageUrl).appendTo(divImg);
	            		 var account=$("<div class='account'>"+obj.cntCommt +"</div>").appendTo(record);
	            		 var divImg1=$("<div></div>").appendTo(record);
	            		 var turnNext=$("<img class='turnNext' src='../../images/turnNext.png'>").appendTo(divImg1);
	            		  $(".record").click(function(){
	            			   var platId=$(this).attr("platId");
	            			   window.location="platLogin.html?token="+token +"&userId=" + userId+"&platId="+platId; 
	            		   })
	            	 
	            	 })
	            	 
	             }else if(res.result==-1){
	          	   window.location = encodeURI('*'+res.resultNote);
	             }else{
	          	   alert(res.resultNote);   
	             }
	         },
	   });
	   $(".addAccount").click(function(){
			   window.location="platformBinding.html?token="+token +"&userId=" + userId;  
	   })
	 
})