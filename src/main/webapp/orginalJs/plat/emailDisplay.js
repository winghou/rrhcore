var userId,token;

$(function(){
	showLoading();
	userId = ns.getRequestParam().userId;
	/*token = ns.getRequestParam().token;
	console.log(token);
	console.log(userId); */  
	token="";
    var emailPassword = $(".emailPassword").val();
	var data={
	        "cmd": "userEmails",           //请求的命令字
	        "token": token,                        //用来表示是否允许访问服务器资源
	        "userId": userId,                      //用户ID
	        "version": "1",                        //版本号：默认为1
	        "params": {
	        	"userId": userId				//這邊是用戶自己填的，暫且寫死
	        }
	    };
	var dataString=JSON.stringify(data);
	$.ajax({
    	url:"http://192.168.1.64:8080/rrhcore/userEmails",
		type: 'post',
        dataType:'json',
        contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
        timeout: 100000,
        data: "{\"key\":\""+Encrypt(dataString)+"\"}",
    	success:function(res){  		
    		var res=JSON.parse(Decrypt(res.key));
    		var lisContent = res.detail.userEmailList;
    		for(var i=0;i<lisContent.length;i++){
    			var lisContents = lisContent[i];
    			console.log(lisContents);
    			var savingsCars = lisContents.cntCommt;
    			var valiCode = lisContents.valiCode;
    			var lis = "<li class='reviseCar'>"+
    			    "<i><img src=\'../../images/qqyouxiang@2x.png\' alt=\'\' title="+valiCode+"></i>"+
    			    "<span>"+savingsCars+"</span>"+
    			    "<b></b>"+
    			    "</li>";
    			 $(".newSaveCar").before(lis);
    			 
    			 hideLoading();
    			 
    			 $(".reviseCar").click(function(){
    				 console.log($(this).find("img").attr('title'));
    				 var valiCode = $(this).find("img").attr('title');
    				 location.href ='emailAmendment.html?userId='+userId+"&valiCode="+valiCode+"&token="+token+"&flat=1";
    			 })    			 
    		}
    	}
    	
    })  
    
    $(".newSaveCar").click(function(){
        location.href="emailLogin.html?userId="+userId+"&token="+token+"&flat=1";
    });
 // 隐藏遮罩
    $(".maskBtn").click(function(){
    	$("#mask").css("display","none");
    })
})