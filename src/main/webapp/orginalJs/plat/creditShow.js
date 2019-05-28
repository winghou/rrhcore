var userId;
var token;
var creditCardSize;
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
  var data=  {
    	  "cmd": "creditCardList",
    	  "token":token ,
    	  "userId": userId,
    	  "version": "1",
    	  "params": {
    	    "userId": userId
    	  }
    	}
  var dataString=JSON.stringify(data);
  showLoading();
  $.ajax({
      url:commonUrl+"/creditCardList",
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
        	  creditCardSize=res.detail.creditCardSize;
              var creditList=res.detail.creditCardList;
            $(creditList).each(function(i,obj){
            	var record=$("<div class='record'></div>").appendTo($(".list")).attr("creditId",obj.creditCardId);
            	var divImg=$("<div></div>").appendTo(record);
            	$("<img class='socialSecurity-logo'>").attr('src',"../../images/credit-logo.png").appendTo(divImg);
            	var savingsCars = obj.cmtCommt;
    			var lengths = savingsCars.length;
    			var bankCars = new Array(lengths-3).join('*') + savingsCars.slice(lengths-4);
    			bankCars=bankCars.replace(/\s/g, '').replace(/(.{4})/g, "$1 ");
            	$("<div class='account'>"+bankCars+"</div>").appendTo(record);
            })
            $(".record").click(function(){
            	var creditId=$(this).attr("creditId");
            	 window.location="credit.html?token="+token +"&userId=" + userId+"&creditId="+creditId;
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
  $(".addAccountName").click(function(){
	  if(creditCardSize>=3){
		  showSuperiorLimitMask(); 
	  }else{
		  window.location="credit.html?token="+token +"&userId=" + userId;  
	  }
  })
})