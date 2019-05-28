var userId;
var token;
var withDrawId;
ids = [];
$(function(){
	var params = ns.getRequestParam();
	userId=params.userId;
	token=params.token;
	withDrawId=params.withDrawId;
	var data={
			  "cmd": "queryRepayDetail",           //请求的命令字
			  "token": token,                        //用来表示是否允许访问服务器资源
			  "userId": userId,                      //用户ID
			  "version": "1",                        //版本号：默认为1
			  "params": {
			            "userId": userId,           //用户ID
			          "withDrawId": withDrawId,           //订单ID
			  }
			}
	var dataString=JSON.stringify(data);
	showLoading();
	  $.ajax({
	        url:commonUrl+"/queryRepayDetail",
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
	            var repayLists=res.detail.repayLists;
	            $(repayLists).each(function(i,obj){
	            	if(obj.orderStatus==1){
	            	var orderList=$("<div class='orderList selected'></div>").appendTo($(".orderLists")).attr("repayId",obj.repayId).css("background","#fff");
	            	var partOne=$("<div class='partOne'></div>").appendTo(orderList);
	            	 var selectImg=$("<img src='../images/H5-orderSelected.png' class='selectImg'>").appendTo(partOne)
	            	}else{
	            		var orderList=$("<div class='orderList'></div>").appendTo($(".orderLists")).attr("repayId",obj.repayId).css("background","#f0f0f0");;
	            		var partOne=$("<div class='partOne'></div>").appendTo(orderList);
	            		 var selectImg=$("<img src='../images/H5-orderSelect.png' class='selectImg'>").appendTo(partOne)
	            	}
	            	
	            	
	               
	                var partTwo=$("<div class='partTwo'></div>").appendTo(orderList);
	                var date=$("<div class='date'>"+obj.currentPeriod+"/"+"</div>").appendTo(partTwo);
	                var allDate=$("<span class='allDate'>"+obj.totalPeriods+"</span>").appendTo(date);
	                var money=$("<div class='money'>"+obj.repayAmt+"</div>").appendTo(partTwo);
	                var partThree=$("<div class='partThree'></div>").appendTo(orderList);
	                var dateOfReturnMoney=$("<div class='dateOfReturnMoney'>"+obj.repayDate+"</div>").appendTo(partThree);
	                var dateOfReturnMoneyTip=$("<div class='dateOfReturnMoneyTip'>"+"还款日期"+"</div>").appendTo(partThree);
	          
	            })
	                
	            $(".orderList").click(function(){
	            	if($(this).hasClass("selected")){
	            		$(this).nextAll().find(".selectImg").attr("src","../images/H5-orderSelect.png");
						$(this).nextAll().removeClass("selected");
					  	$(this).nextAll().css("background","#f0f0f0");
	                	$(this).removeClass("selected");
	                	$(this).css("background","#f0f0f0");
	                	$(this).find(".selectImg").attr("src","../images/H5-orderSelect.png");
	                	total();
	            	}else{
	            		$(this).prevAll().find(".selectImg").attr("src","../images/H5-orderSelected.png");
						$(this).prevAll().addClass("selected");
						$(this).addClass("selected");
					  	$(this).css("background","#fff");
					  	$(this).prevAll().css("background","#fff");
	                	$(this).find(".selectImg").attr("src","../images/H5-orderSelected.png");
	                	total();
	            	}
	            })
	            }else if(res.result==-1){
	         	   window.location = encodeURI('*'+res.resultNote);
	            }else{
	         	   alert(res.resultNote);   
	            }
	        },
	  });
	  $(".returnBtn").click(function(){
		  if(ids==""){
			  return;
		  }else{
			 util.cookie.set("repayId", ids);
			 //console.log(util.cookie.get("repayId"));
			 location.href ="H5-payment.html?token="+token+"&userId="+userId; 
		  }
	  })
})
	function total(){
	    ids = [];
	    moneyInAll=0;
	    $(".moneyNum").text("0.00元")
		$('.orderList').each(function(i,obj){
			if($('.orderList').eq(i).hasClass("selected")){
				moneyInAll+=parseFloat($('.orderList').eq(i).find(".money").text()); 
				$(".moneyNum").text(moneyInAll+"元")
				ids.push($(this).attr('repayId'));
			}
		})
	}