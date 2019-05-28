var userId,token;
$(function(){
	showLoading();
	userId = ns.getRequestParam().userId;
	token = ns.getRequestParam().token;
	var data={
	        "cmd": "housingFundList",           //请求的命令字
	        "token": token,                        //用来表示是否允许访问服务器资源
	        "userId": userId,                      //用户ID
	        "version": "1",                        //版本号：默认为1
	        "params": {
	        	"userId": userId				//這邊是用戶自己填的，暫且寫死
	        }
	    };
	var dataString=JSON.stringify(data);
    $.ajax({
    	url:"http://192.168.1.64:8080/rrhcore/housingFundList",
		type: 'post',
        dataType:'json',
        contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
        timeout: 100000,
        data: "{\"key\":\""+Encrypt(dataString)+"\"}",
    	success:function(res){    		
    		var res=JSON.parse(Decrypt(res.key));
    		if(res.result==0){
	    		var lisContent = res.detail.housingFundList;
	    		for(var i=0;i<lisContent.length;i++){
	    			var lisContents = lisContent[i];    			
	    			var lis = "<li class='reviseCar' id="+lisContents.fundId+">"+
	    			    "<i><img src=\'../../images/gongjijin@2x.png\' alt=\'\'></i>"+
	    			    "<span>"+lisContents.cmtCommt+"</span>"+
	    			    "</li>";
	    			 $(".newSaveCar").before(lis);
	    			 hideLoading();
	    			 $(".reviseCar").click(function(){
	    				 var fundId = $(this).attr('id');
	    				 location.href ='accumulationFund.html?userId='+userId+"&fundId="+fundId+"&token="+token;
	    			 })    			 
	    		}
    		}else{
    			hideLoading();
    			alert(res.resultNote);
    		}
    	}    	
    })
    
    $(".newSaveCar").click(function(){
    	showLoading();
    	if($(".reviseCar").length>=3){	
    		hideLoading();
			$('#mask').show();
		}else{
			hideLoading();
			location.href="accumulationFund.html?userId="+userId+"&token="+token+"&fundId=";
		}        
    });
    
    // 隐藏遮罩
    $(".maskBtn").click(function(){
    	$("#mask").css("display","none");
    })
})