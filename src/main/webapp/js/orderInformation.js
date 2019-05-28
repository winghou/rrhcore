var userId;
var token;
var withDrawId;
var mesId="";
$(function(){
	var params = ns.getRequestParam();
	userId=params.userId;
	token=params.token;
	mesId=params.mesId;
	withDrawId=params.withId;
	var data={
			  "cmd": "queryOrderDetail",
			  "token": token,
			  "userId": userId,
			  "version": "1",
			  "params": {
			    "userId":userId,
			    "withDrawId":withDrawId,
			    "mesId": mesId,
			    "isJump": "yes",
			  }
			}
	var dataString=JSON.stringify(data);
	showLoading();
	   $.ajax({
	        url:commonUrl+"/queryOrderDetail",
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
	            	$(".shouldPayMoney").text(res.detail.shouldPayAmt)
	            	$(".loanDate").text(res.detail.borrowTime);
	            	$(".repayDate").text(res.detail.repayTime);
	            	$(".orderAccountContent").text(res.detail.orderNum);
	            	$(".borrowAmt").text(res.detail.borrowAmt);
	            	$(".curperiod").text(res.detail.curperiod);
	            	$(".waysOfUse").text(res.detail.purposeName);
	            	$(".splitShouldPayAmt").text(res.detail.splitShouldPayAmt);
	            	$(".insurancesName").text(res.detail.insurancesName);
	            	$(".insuranceShouldPayAmt").text(res.detail.insuranceShouldPayAmt);
	            	$(".commissionAmt").text(res.detail.commissionAmt);
	            	$(".curperiod").text(res.detail.curperiod);
	            	$(".couponNumber").text("共计"+res.detail.couponAmt);
	            	$(".thePageText1").text(res.detail.thePageText1);
	            	$(".thePageText2").text(res.detail.thePageText2);
	            	$(".thePageText3").text(res.detail.thePageText3);
	            	$(".thePageText4").text(res.detail.thePageText4);
	            	$(".thePageText5").text(res.detail.thePageText5);
	            	$(".thePageText6").text(res.detail.thePageText6);
	            	$(".consumeNumber").text(res.detail.consumeNumber);
	            	var protocolList=res.detail.protocolList;
	            	$(protocolList).each(function(i,obj){
	            		$("<span class='protocolDetail'>"+obj.protocolName+"</span>").appendTo($(".protocol")).attr("protocolCode",obj.protocolCode);
	            	})
	            	$(".protocolDetail").click(function(){
	            		var protocolCode=$(this).attr("protocolCode"); 
	            		var data2={
	            				  "cmd": "getProtocolUrl",
	            				  "token":token,
	            				  "userId": userId,
	            				  "version": "1",
	            				  "params": {
	            				    "userId": userId,
	            				    "protocolCode": protocolCode,
	            				    "withDrawId":withDrawId,
	            				    "borrowAmt": res.detail.borrowAmt,
	            				    "borrowPerion": res.detail.curperiod,
	            				    "purposeCode": res.detail.purposeName,
	            				    "token": token,
	            				    "insuranceType": ""
	            				  }
	            				}
	            		 var dataString = JSON.stringify(data2);
	        		    showLoading();
	        			 $.ajax({
	        			        url: commonUrl + "/getProtocolUrl",
	        			        type: 'post',
	        			        dataType: 'json',
	        			        contentType: 'application/json', //很重要，没有这个就走不了fastjson解析器
	        			        timeout: 100000,
	        			        data: "{\"key\":\"" + Encrypt(dataString) + "\"}",
	        			        success: function(dd) {
	        			            hideLoading();
	        			            var res = JSON.parse(Decrypt(dd.key));
	        			            console.log(res);
	        			            if (res.result == 0) {
	        			            	 location.href =res.detail.protocolUrl;
	        			            } else if (res.result == -1) {
	        			                window.location = encodeURI('*' + res.resultNote);
	        			            } else {
	        			                alert(res.resultNote);
	        			            }
	        			        },
	        			    });
	            	})
	            	
	            	$(".inall").text(res.detail.shouldPayAmt+"元");
	            	$(".loanBtn").attr("curperiodStatus",res.detail.curperiodStatus);
	            	$(".loanBtn").click(function(){
	            		if($(this).attr("curperiodStatus")==0){
	            			var repayId=res.detail.repayId;
	            			 location.href ="H5-payment.html?token="+token+"&userId="+userId+"&repayId="+repayId;	
	            		}else if($(this).attr("curperiodStatus")==1){
	            			location.href="H5-periodizationAccount?token="+token+"&userId="+userId+"&withDrawId="+withDrawId;
	            		}
	            	})
	            }else if(res.result==-1){
	         	   window.location = encodeURI('*'+res.resultNote);
	            }else{
	         	   alert(res.resultNote);   
	            }
	        },
	  });
})