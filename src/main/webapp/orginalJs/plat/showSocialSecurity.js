var userId;
var token;
var socialSecurityTotal;
var SecurityId;
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
var data={
		  "cmd": "socialSecurityList",
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
      url:commonUrl+"/socialSecurityList",
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
        	  socialSecurityTotal=res.detail.socialSecuritySize;
         	 var socialSecurityList=res.detail.socialSecurityList;
         	 $(socialSecurityList).each(function(i,obj){
         	    var record=$("<div class='record'></div>").appendTo($(".list")).attr("socialSecurityId",obj.SecurityId);
         	    var divImg=$("<div></div>").appendTo(record);
         	    $("<img class='socialSecurity-logo' src='../../images/socialSecurity-logo.png'>").appendTo(divImg);
         	    $("<div class='account'>"+obj.cmtCommt+"</div>").appendTo(record);
         	 })
         	     $(".record").click(function(){
                 var socialSecurityId=$(this).attr("socialSecurityId");
    	         window.location="socialSecurity.html?token="+token +"&userId=" + userId+"&SecurityId="+socialSecurityId;   
              })
          }else if(res.result==-1){
       	   window.location = encodeURI('*'+res.resultNote);
          }else{
       	   alert(res.resultNote);   
          }
      },
});
  //返回app提额页面
  $(".returnBtn").click(function(){
		 window.location = encodeURI("mentionTheAmountApp");
	})
 $(".addAccount").click(function(){
	 if(socialSecurityTotal>=3){
		  showSuperiorLimitMask(); 
	 }else{
		 window.location="socialSecurity.html?token="+token +"&userId=" + userId;   
	 }
 })   
})