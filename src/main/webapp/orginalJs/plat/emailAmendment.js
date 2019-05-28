var userId,valiCode,token;
$(function(){
	showLoading();
	token='';
	userId=ns.getRequestParam().userId;
	valiCode=ns.getRequestParam().valiCode;
	console.log(valiCode);
	var data={
	        "cmd": "emailDetailByCode",           //请求的命令字
	        "token": token,                        //用来表示是否允许访问服务器资源
	        "userId": userId,                      //用户ID
	        "version": "1",                        //版本号：默认为1
	        "params": {
	        	"userId": userId,
	            "valiCode":valiCode ,   //這邊是用戶自己填的，暫且寫死
	        }
	    }
	var dataString=JSON.stringify(data);
	$.ajax({
		url:"http://192.168.1.64:8080/rrhcore/emailDetailByCode",
		type: 'post',
        dataType:'json',
        contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
        timeout: 100000,
        data: "{\"key\":\""+Encrypt(dataString)+"\"}",
        success:function(res){
        	var res=JSON.parse(Decrypt(res.key));
        	console.log(res);
        	$(".email").val(res.detail.cntCommt);
        	$(".emailPassword").val(res.detail.cntPass);
        	hideLoading();
        }
	})

	
	 $('.btn').click(function(){
	    	email=$('.email').val();
	    		checkTxt2();
	    		if(isHasErr2==false){
	    			var emailPassword = $(".emailPassword").val();
	    			var data={
	    			        "cmd": "emailPwdChange",           //请求的命令字
	    			        "token": token,                        //用来表示是否允许访问服务器资源
	    			        "userId": userId,                      //用户ID
	    			        "version": "1",                        //版本号：默认为1
	    			        "params": {
	    			        	"userId": userId,
	    			            "valiCode":valiCode ,   //這邊是用戶自己填的，暫且寫死
	    			            "cntPass": emailPassword				//這邊是用戶自己填的，暫且寫死
	    			        }
	    			    }
	    			var dataString=JSON.stringify(data);
	    			showLoading();
	    			$.ajax({
	    				url:"http://192.168.1.64:8080/rrhcore/emailPwdChange",
	    				type: 'post',
	    		        dataType:'json',
	    		        contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
	    		        timeout: 100000,
	    		        data: "{\"key\":\""+Encrypt(dataString)+"\"}",
	    		        success:function(res){
	    		        	hideLoading();
	    		        	var res=JSON.parse(Decrypt(res.key));
	    		        	console.log(res);
	    		        	if(res.detail.newPwdChange=1){
	    		        		console.log(22222);
//	    		        		location.href="emailDisplay.html?userId="+userId;
	    		        	}else{
	    		        		alert(res.resultNote);
	    		        	}
	    		        }
	    			})
	    		}	    		        
	    })
})