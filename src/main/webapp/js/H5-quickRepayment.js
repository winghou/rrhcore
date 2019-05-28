var userId;
var token;
$(function(){
	var params = ns.getRequestParam();
	userId=params.userId;
	token=params.token;
	var data={
			  "cmd": " fastRepayment ",            //请求的命令字
			  "token": token,                           //用来表示是否允许访问服务器资源
			  "userId": userId,                          //用户ID
			  "version": "1",                        //版本号：默认为1
			  "params": {
			            "userId": userId,           //用户ID
			  }
			}
	var dataString=JSON.stringify(data);
	showLoading();
	   $.ajax({
	        url:commonUrl+"/fastRepayment",
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
	             var notRepayOrder=res.detail.notRepayOrder;
	             $(notRepayOrder).each(function(i,obj){//审核过的
	            	//var orderList=$("<div class='orderList'></div>").appendTo($(".orderLists"));
	            	   if(obj.orderStatus==1){
	                	   var orderList=$("<div class='orderList'></div>").appendTo($(".orderLists")).css("background","url(../images/H5-yuqi.png) ");
		            	 }else if(obj.orderStatus==2){
		            		 var orderList=$("<div class='orderList'></div>").appendTo($(".orderLists")).css("background","url(../images/H5-haikuan.png) ");
		            	 }else if(obj.orderStatus==3){
		            		 var orderList=$("<div class='orderList'></div>").appendTo($(".orderLists")).css("background","url(../images/H5-daikuan.png) ");
		            	 }
	            	   $(".orderList").css("background-size","100% 100%");
	            	var infoTable=$("<table class='infoTable'></table>").appendTo(orderList);
	            	var tr=$("<tr></tr>").appendTo(infoTable);
	            	var moneyInfo=$("<td class='moneyInfo'></td>").appendTo(tr);
	            	var moneyNum=$("<div class='moneyNum'>"+obj.shouldPayAmt+"</div>").appendTo(moneyInfo);
	            	var moneyTip=$("<div class='moneyTip'>"+"RMB"+"</div>").appendTo(moneyInfo);
	            	var td=$("<td style='font-size: 16px;color: #666666;float: right;    text-align: left;'></td>").appendTo(tr);
	            	var orderNum=$("<div class='orderNum'>"+"订单号："+obj.orderNum +"</div>").appendTo(td);
	                var dateOfReturn=$("<div class='dateOfReturn'>"+"还款日："+obj.shouldRepayTime+"</div>").appendTo(td);
                   var orderStatus=$("<table class='orderStatus'></table>").appendTo(orderList);
                   var trSecond=$("<tr></tr>").appendTo(orderStatus);
                   if(obj.orderStatus==1){
                	   var dateOfStatus=$("<td class='dateOfStatus'>"+obj.borrowTips+"</td>").appendTo(trSecond).css("color","red");   
                   }else if(obj.orderStatus==2){
                	   var dateOfStatus=$("<td class='dateOfStatus'>"+obj.borrowTips+"</td>").appendTo(trSecond).css("color","#fbc38f");  
                   }else if(obj.orderStatus==3){
                	   var dateOfStatus=$("<td class='dateOfStatus'>"+obj.borrowTips+"</td>").appendTo(trSecond).css("color","#e38bd4"); 
                   }
                   var returnMoney=$("<td class='returnMoney'>"+"去还款"+"</td>").appendTo(trSecond).attr("repayId",obj.repayId);
	             })
	             var underReviewOrder=res.detail.underReviewOrder;
	             $(".orderNumber").text("订单编号："+underReviewOrder[0].orderNum);
	             $(".loanMoney").text("借款金额："+underReviewOrder[0].borrowAmt+"元");
	             $(".dateOfLaon").text("借款日期："+underReviewOrder[0].borrowTime);
	             
	             $(".returnMoney").click(function(){
	               var repayId= $(this).attr("repayId");
	               util.cookie.set("repayId", repayId);
	              location.href ="H5-payment.html?token="+token+"&userId="+userId+"&repayId="+repayId;
	             })
	            }else if(res.result==-1){
	         	   window.location = encodeURI('*'+res.resultNote);
	            }else{
	         	   alert(res.resultNote);   
	            }
	        },
	  });
})