var token;
var userId;
$(function(){
	
	var params = ns.getRequestParam();
	userId=params.userId;
	token=params.token;
	var data={
			  "cmd": "queryOrderRecords",           //请求的命令字
			  "token": token,                        //用来表示是否允许访问服务器资源
			  "userId": userId,                      //用户ID
			  "version": "1",                        //版本号：默认为1
			  "params": {
			            "userId": userId,           //用户ID
			  }
			}
	var dataString=JSON.stringify(data);
	showLoading();
	   $.ajax({
        url:commonUrl+"/queryOrderRecords",
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
            	if(res.detail.orderRecordLists==""){
            		$(".orderLists").addClass("hide");
            		$(".noCount").removeClass("hide");
            	}else{
            		$(".noCount").addClass("hide");
            		$(".orderLists").removeClass("hide");
            		  var orderLists=res.detail.orderRecordLists;
            		  $(".orderLists").html("");
                 	  $(orderLists).each(function(i,obj){
                 		  var orderList=$("<div class='orderList'></div>").appendTo($(".orderLists")).attr("orderId",obj.withId).attr("orderStatus",obj.loanStatus);
                 		  var orderLine1=$("<div class='orderLine1'></div>").appendTo(orderList);
                 		  if(obj.loanStatus==0){//审核中
                 		$("<div class='orderNumber'>"+ "订单号："+obj.contractNo +"</div>").appendTo(orderLine1).css("border-left","solid 3px #3399FF");
                 	    $("<div class='orderStatus'>"+"借款审核中" +"</div>").appendTo(orderLine1).css("color","#3399FF");
                 	   
                   		 }else if(obj.loanStatus==1){//已放款
                   		 $("<div class='orderNumber'>"+ "订单号："+obj.contractNo +"</div>").appendTo(orderLine1).css("border-left","solid 3px #F6B373");
                   		 $("<div class='orderStatus'>"+"还款中" +"</div>").appendTo(orderLine1).css("color","#F6B373");
                   		
                   		 }else if(obj.loanStatus==2){//逾期
                   		$("<div class='orderNumber'>"+ "订单号："+obj.contractNo +"</div>").appendTo(orderLine1);
                   	   $("<div class='orderStatus'>"+"逾期" +"</div>").appendTo(orderLine1);
                   	   
                   		 }else if(obj.loanStatus==3){//结清
                   		  $("<div class='orderNumber'>"+ "订单号："+obj.contractNo +"</div>").appendTo(orderLine1).css("border-left","solid 3px #999999");
                   		$("<div class='orderStatus'>"+"已还款" +"</div>").appendTo(orderLine1).css("color","#999999");;
                   		
                   		 }
                 		  var orderLine2=$("<div class='orderLine2'></div>").appendTo(orderList);
                 		  var date=$("<div class='date'></div>").appendTo(orderLine2);
                 		  $("<div class='loanDate'>"+"借款日期:"+obj.borrowTime +"</div>").appendTo(date);
                 		  $("<div class='returnDate'>"+"还款日期:"+obj.repayTime +"</div>").appendTo(date);
                 		  $("<div class='returnAllMoney'>"+obj.borrowAmt+"元"+"</div>").appendTo(orderLine2);
                 	
                 	  })
                 	  $(".orderList").click(function(){
                 		  if($(this).attr("orderStatus")==0){
                 			 var withId=$(this).attr("orderid");
                 			  window.location="H5-orderStatus.html?token="+token+"&userId="+userId+"&withId="+withId; 
                 		  }else if($(this).attr("orderStatus")==1||$(this).attr("orderStatus")==2){
                 			 var withId=$(this).attr("orderid");
                 			 window.location="H5-orderInformation.html?token="+token+"&userId="+userId+"&withId="+withId;  
                 		  }
                 	  })
            	}
         	
         	   
            }else if(res.result==-1){
         	   window.location = encodeURI('*'+res.resultNote);
            }else{
         	   alert(res.resultNote);   
            }
        },
  });
})