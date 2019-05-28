var platList;
var userId;
var token;
var content="";
var isiPhoneX;
$(function(){
	var params = ns.getRequestParam();
	token=params.token;
	userId=params.userId;
	isiPhoneX=params.isiPhoneX;
	 if(isiPhoneX==1){
	    	$(".head").css("padding-top","44px")
	    }else{
	    	$(".head").css("padding-top","24px")
	    }
	$(".searchPlatTxta").bind('input propertychange',function(){
		content=$.trim($(".searchPlatTxta").val());
		searchPlat(content);
		});	
	loadPlat();
	
	//清空搜索框的值
	$(".plat-cancel").click(function(){
		$(".searchPlatTxta").val("");
		searchPlat("");
	})
   //返回app提额页面
    $(".returnBtn").click(function(){
		 window.location = encodeURI("mentionTheAmountApp");
	})
})

//载入所有平台信息
function loadPlat(){
	var data={
			  "cmd": "platformQry",    //获取运营商认证所需基本资料
			  "token": token,
			  "userId": userId,
			   "version": "1",
			   "params": {         //用户id
			  }
			}
	var dataString=JSON.stringify(data);
	showLoading();
	   $.ajax({
         url:commonUrl+"/platformQry",
         type: 'post',
         dataType:'json',
         contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
         timeout: 100000,
         data: "{\"key\":\""+Encrypt(dataString)+"\"}",
         success: function(dd){
      	   hideLoading();
             var res=JSON.parse(Decrypt(dd.key));
             if(res.result==0){
               $(".mainContent").html("");
          	    platList= res.detail.platformList;
          	    $("<div class='listTitle'> 平台列表</div>").appendTo($(".mainContent"));
          	$(platList).each(function(i,obj){
          		var list=$("<div class='list'></div>").appendTo($(".mainContent")).attr("id",obj.id);
          		var listlogo=$("<span class='list-logo'></span>").appendTo(list);
          		var listlogoImages=$("<img class='list-logoImages' onerror='this.src='../../images/morenLogo.png''>").attr("src",obj.logoimageUrl).appendTo(listlogo);
          		var listName=$("<span class='listName'>"+obj.platformName+"</span>").appendTo(list);
          	     }) 
          	     //单个平台跳转
        $(".list").click(function(){
		var platId=$(this).attr("id");
		window.location="platLogin.html?token="+token +"&userId=" + userId +"&platId=" + platId;  
	})
             }else if(res.result==-1){
          	   window.location = encodeURI('*'+res.resultNote);
             }else{
          	   alert(res.resultNote);   
             }
         },
   });
}
function searchPlat(content){
	if($.trim(content)==""){
	    $(".mainContent").html("");
   	    $("<div class='listTitle'> 平台列表</div>").appendTo($(".mainContent"));
   	$(platList).each(function(i,obj){
   		var list=$("<div class='list'></div>").appendTo($(".mainContent")).attr("id",obj.id);
   		var listlogo=$("<span class='list-logo'></span>").appendTo(list);
   		var listlogoImages=$("<img class='list-logoImages'>").attr("src",obj.logoimageUrl).appendTo(listlogo);
   		var listName=$("<span class='listName'>"+obj.platformName+"</span>").appendTo(list);
   	  $(".list").click(function(){
  		var platId=$(this).attr("id");
  		window.location="platLogin.html?token="+token +"&userId=" + userId +"&platId=" + platId;  
  	})
   	     }) 
	}else{
		   $(".mainContent").html("");
		  	$(platList).each(function(i,obj){
		  		if((obj.platformName).indexOf(content) >= 0){
		  			var list=$("<div class='list'></div>").appendTo($(".mainContent")).attr("id",obj.id);
	         		var listlogo=$("<span class='list-logo'></span>").appendTo(list);
	         		var listlogoImages=$("<img class='list-logoImages'>").attr("src",obj.logoimageUrl).appendTo(listlogo);
	         		var listName=$("<span class='listName'>"+obj.platformName+"</span>").appendTo(list);
		  		}
		  	  $(".list").click(function(){
		  		var platId=$(this).attr("id");
		  		window.location="platLogin.html?token="+token +"&userId=" + userId +"&platId=" + platId;  
		  	})
	     	     }) 
	}
}