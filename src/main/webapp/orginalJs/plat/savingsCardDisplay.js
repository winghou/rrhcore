var userId,token;
$(function(){
	showLoading();
	userId = ns.getRequestParam().userId;
	token = ns.getRequestParam().token;	
	var data={
	        "cmd": "depositCardList",           //请求的命令字
	        "token": token,                        //用来表示是否允许访问服务器资源
	        "userId": userId,                      //用户ID
	        "version": "1",                        //版本号：默认为1
	        "params": {
	        	"userId": userId				//這邊是用戶自己填的，暫且寫死
	        }
	    };
	var dataString=JSON.stringify(data);
	$.ajax({
    	url:"http://192.168.1.64:8080/rrhcore/depositCardList",
		type: 'post',
        dataType:'json',
        contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
        timeout: 100000,
        data: "{\"key\":\""+Encrypt(dataString)+"\"}",
    	success:function(res){    		
    		var res=JSON.parse(Decrypt(res.key));
    		if(res.result==0){
    			var lisContent = res.detail.depositCardList;    			
        		for(var i=0;i<lisContent.length;i++){
        			var lisContents = lisContent[i];
        			var savingsCars = lisContents.cmtCommt;
        			var lengths = savingsCars.length;
        			var bankCars = new Array(lengths-3).join('*') + savingsCars.slice(lengths-4);
        			bankCars=bankCars.replace(/\s/g, '').replace(/(.{4})/g, "$1 ");
        			var lis = "<li class='reviseCar' id="+lisContents.depositCardId+">"+
        			    "<i><img src=\'../../images/card@2x.png\' alt=\'\'></i>"+
        			    "<span>"+bankCars+"</span>"+
        			    "</li>";
        			 $(".newSaveCar").before(lis);
        			 hideLoading();
        			 $(".reviseCar").click(function(){
        				 var depositCardId = $(this).attr('id');
        				 location.href ='savingsDepositCard.html?userId='+userId+"&depositCardId="+depositCardId+"&token="+token;
        			 })   
        			 $(".newSaveCar").click(function(){
        			    	if($(".reviseCar").length>=3){			
        						$('#mask').show();
        					}else{
        						location.href="savingsDepositCard.html?userId="+userId+"&token="+token+"&depositCardId=";
        					}        
        			    });
        		}
    		}else{
    			hideLoading();
    			alert(res.resultNote);
    		}	
    	}  	
    })  
   
    
    // 隐藏遮罩
    $(".maskBtn").click(function(){
    	$("#mask").css("display","none");
    })

})
