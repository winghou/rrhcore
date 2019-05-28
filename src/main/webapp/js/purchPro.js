var toke;
var userId;
var withdrawId;
var protocolType;
$(function(){
	var params = ns.getRequestParam();
	token=params.token;
	userId=params.userId;
	withdrawId=params.withdrawId;
	protocolType=params.protocolType;
	var data={
			  "cmd": "getProtocolInfo",           //请求的命令字
			  "token": token,                        //用来表示是否允许访问服务器资源
			  "userId": userId,                      //用户ID
			  "version": "1",                        //版本号：默认为1
			  "params": {
			            "userId": userId,           //用户ID
			            "withdrawId":withdrawId,
			            "protocolType":protocolType,
			  }
			}
	var dataString=JSON.stringify(data);
	   $.ajax({
           url:commonUrl+"/getProtocolInfo",
           type: 'post',
           dataType:'json',
           contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
           timeout: 100000,
           data: "{\"key\":\""+Encrypt(dataString)+"\"}",
           success: function(dd){
               var res=JSON.parse(Decrypt(dd.key));
               console.log(res);
               if(res.result==0){
            	   $(".value1").text(res.detail.contractNo);
            	   $(".value2").text(res.detail.userName);
            	   $(".value3").text(res.detail.idCard);
            	   $(".value4").text(res.detail.mobile);
            	   $(".value9").text(res.detail.serverName);
            	   $(".value5").text(res.detail.signAddress);
            	   $(".value6").text(res.detail.signDate);
            	   $(".value7").text(res.detail.signAddress);
            	   $(".value8").text(res.detail.cutRate);
               }else if(res.result==-1){
            	   window.location = encodeURI('*'+res.resultNote);
               }else{
            	   alert(res.resultNote);   
               }
           },
     });
})