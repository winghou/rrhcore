
$(function(){
	var params = ns.getRequestParam();
	var userId=params.userId;
	var phoneId=params.phoneId;
	var mesId=params.mesId;
	var token=params.token;
	var data={
			   "cmd": "queryOneMessageByMesId",
			   "token": token,
			   "version": "1",
			   "userId":userId,
			    "params": {
			    "userId": userId,
			    "phoneId": phoneId,
			    "mesId": mesId
			  }
			}
	var dataString=JSON.stringify(data); 
	   $.ajax({
           url:commonUrl+"/queryOneMessageByMesId",
           type: 'post',
           async: false,
           dataType:'json',
           contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
           timeout: 100000,
           data: "{\"key\":\""+Encrypt(dataString)+"\"}",
           success: function(res){
               var res=JSON.parse(Decrypt(res.key));
               if(res.result==0){
            	  $(".content").text(res.detail.comtent);
            	  $(".time").text(res.detail.publishTime);
            	  $(".title").text(res.detail.title);
               }else if(res.result==-1){
               	   window.location = encodeURI('*'+res.resultNote);
               }else{
            	   alert(res.resultNote);   
               }
           },
     });
})