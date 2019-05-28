var token;
var userId;
var couponList;
var insuranceType;
var source;
var loanPur//借款用途
var cutRate; //砍头息
var mchRate; //利息
var loanDay; //借款天数
var loanAll; //借款金额
var loanInall;//借款总金额
var moneyOfInsurance; //购险金额
var preferentialAmt;//优惠金额
var returnMoneyInall;
var couponList;
var couponList1;
var contractNo;
var protocolList;
var data1Val;//借款金额
var data2Val;//借款天数
var data1Value;//借款金额
var data2Value;//借款天数
var consumeType //消费订单类型
 //设备信息
 var idfa;
 var imei;
 var ip;
 var longitude;
 var latitude;
 var source;
 var isIPhoneX;
$(function() {
    var params = ns.getRequestParam();
    userId = params.userId;
    token = params.token;
    insuranceType = params.insuranceType;
    couponList1 = params.couponList;
    couponList = params.couponList;
    loanPur=params.loanPur;
    couponType=params.couponType;
    couponId=params.couponId;
    if(params.couponType == undefined || params.couponId == undefined || params.couponType == '' || params.couponId == ''){
    	couponList1=[];
    	couponList=	couponList1;
    }else{
    	couponList1='[{'+
    	'"couponType":'+params.couponType+','+
    	'"couponId":'+params.couponId+'}]';
       
        couponList=	JSON.parse(couponList1);
    }   
   
    source=params.source;
    idfa = params.idfa;
    imei = params.imei;
    ip = params.ip;
    longitude = params.longitude;
    latitude = params.latitude;
    data1Value = params.data1Val;
    data2Value = params.data2Val;
    data1Val = params.data1Val;
    data2Val = params.data2Val;
    isIPhoneX=params.isIPhoneX;
    
    $('#demo-inline').mobiscroll().select({
        display: 'inline',
        showInput: false
    });
    $('#demo-inline1').mobiscroll().select({
        display: 'inline',
        showInput: false                    
    });
    var data = {
        "cmd": "queryAmtAndDaysAndCoupons", //请求的命令字
        "token": token, //用来表示是否允许访问服务器资源
        "userId": userId, //根据业务需要 加的（这里是中介id）
        "version": "1", //版本号：默认为1
        "params": {
            "userId": userId, //用户ID
            "couponIds": couponList,
            "insuranceType": insuranceType //保险类型
        }
    }
    var dataString = JSON.stringify(data);
    showLoading();
    $.ajax({
        url: commonUrl + "/queryAmtAndDaysAndCoupons",
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
            	if(insuranceType==""){
            		insuranceType=res.detail.insuranceType;	
            	}
                //借款金额
            	if(res.detail.borrowAmtLists==undefined){
            		
            	}else{
            		 var borrowAmtLists = (res.detail.borrowAmtLists).reverse();
              		  $(borrowAmtLists).each(function(i, obj) {
                            $("<option >" + obj + "</option>").attr("value", i + 1).appendTo("#demo-inline");
                        })	
            	}
            	
            	
                //借款期限
                var borrowdaysLists = res.detail.borrowdaysLists;
                $(borrowdaysLists).each(function(i, obj) {
                    $("<option >" + obj + "</option>").attr("value", i + 1).appendTo("#demo-inline1");
                })
                //砍头息
                cutRate = res.detail.cutRate;
                //利息
                mchRate = res.detail.mchRate;
                //商品消费类型
                consumeType=res.detail.consumeType;
                //优惠券可用
                $(".couponNumber").text(res.detail.couponNum);
                //遍历借款用途
                var loanpurpose = res.detail.purpose;
                $(loanpurpose).each(function(i, obj) {
                    $("<option >" + obj.purposeName + "</option>").attr("purposeCode", obj.purposeCode).attr("value", i + 1).appendTo("#demo-inline2");
                if(loanPur!==""&&loanPur==obj.purposeCode){
                   $(".waysOfUse").text(obj.purposeName);
                   $(".waysOfUse").attr("purposeCode",obj.purposeCode);
                }
                })
            
                  
                //借款用途确定
                $(".sureBtn").click(function() {
                
                    $("#demo-inline2").next().find('div.dw-li').each(function() {
                        if ($(this).attr("aria-selected") == "true") {
                            var purpName;
                            var purNumber = $(this).attr("data-val");
                            $("#demo-inline2 option").each(function() {
                                if ($(this).attr("value") == purNumber) {
                                    $(".waysOfUse").attr("purposecode", $(this).attr("purposecode"));
                                    loanPur= $(this).attr("purposecode");
                                }
                            })
                            $(".waysOfUse").text(($(this).find('div.dw-i').text()));
                            $(".mask").addClass("hide");
                        } else {
                            $(this).css("color", "#cecece")
                        }
                    })

                })
                $(".selectwaysOfUse").click(function() {
                	event.stopPropagation();
                    $(".mask").removeClass("hide");
                })
                //购买商品名称
                $(".thePageText6").text(res.detail.thePageText6);
                //购买商品类型
                $(".thePageText1").text(res.detail.thePageText1);
                //商品批号
                $(".thePageText2").text(res.detail.thePageText2);
                //商品批号值
                $(".consumeNumber").text(res.detail.consumeNumber);
                //会员费名称
                $(".thePageText3").text(res.detail.thePageText3);
                //会员期限
                $(".thePageText4").text(res.detail.thePageText4);
                //应还款金额
                $(".thePageText5").text(res.detail.thePageText5);
                //遍历所有协议
                protocolList = res.detail.protocolList;
                $(protocolList).each(function(i, obj) {
                    var protocolList = $("<span class='protocolList'></span>").attr("protocolCode",obj.protocolCode).appendTo($(".protocol"));
                    $("<span class='protocolName'>" + obj.protocolName + "</span>").appendTo(protocolList);
                })
                $(".protocolList").click(function(){
                	var protocolCode=$(this).attr("protocolCode");
                	var data3={
                			  "cmd": "getProtocolUrl",
                			  "token": token,
                			  "userId": userId,
                			  "version": "1",
                			  "params": {
                				    "userId": userId,
                				    "protocolCode": protocolCode,
                				 "withDrawId":"",
                				 "borrowAmt":loanInall,
                				 "borrowPerion":loanDay,
                				 "purposeCode":loanPur,
                				 "token":token,
                				 "insuranceType":insuranceType
                			  }
                			}
                	 var dataString = JSON.stringify(data3);
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
        			            //console.log(res);
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
                //协议选中状态
                $(".loanPagePro").click(function() {
                    if ($('.protocol').hasClass("selected")) {
                        $('.protocol').removeClass("selected");
                        $(this).attr("src", "../images/loanPageProtocolNo.png");
                        $(".loanBtn").addClass("disabled");
                    } else {
                        $('.protocol').addClass("selected");
                        $(this).attr("src", "../images/loanPageProtocolSlected.png");
                        $(".loanBtn").removeClass("disabled");
                    }
                })
                //保险页面跳转
                if(res.detail.consumeStatus==0){
                	$(".insuranceOrderselected").addClass("hide");
                }else if(res.detail.consumeStatus==1){
                	$(".insuranceOrderselected").removeClass("hide");
                $(".insuranceOrderTitle").click(function() {
                           location.href = 'insurance.html?userId=' + userId + "&token=" + token + "&couponId="+couponId+"&couponType="+couponType+"&insuranceType="+insuranceType + "&moneyOfInsurance="+moneyOfInsurance+"&source="+source+"&idfa="+idfa+"&imei="+imei+"&ip="+ip+"&longitude="+longitude+"&latitude="+latitude+"&loanPur="+loanPur+"&data1Val="+data1Val+"&data2Val="+data2Val+"&isIPhoneX="+isIPhoneX+"&isHomePage=1";
                       })
                }
             
                //优惠券选择
                $(".coupon").click(function(){
                	var cl=[];
                	if(res.detail.couponNum=="无"){
                		return;
                		}else{
                			 location.href = 'coupon.html?userId=' + userId + "&token=" + token + "&insuranceType="+insuranceType+"&source="+source+"&couponList="+cl+"&idfa="+idfa+"&imei="+imei+"&ip="+ip+"&longitude="+longitude+"&latitude="+latitude+"&loanPur="+loanPur+"&data1Val="+data1Val+"&data2Val="+data2Val+"&isIPhoneX="+isIPhoneX;
                		}
                })
                //优惠金额
                preferentialAmt=res.detail.preferentialAmt;
              
                //合同号
                contractNo=res.detail.contractNo;
                //滑动取值
                $('#demo-bottom').mobiscroll().select({
                    display: 'bottom'
                });
                $('#demo-inline').mobiscroll().select({
                    display: 'inline',
                    showInput: false
                }).scroller('setValue',data1Value, true);;
                $('#demo-inline1').mobiscroll().select({
                    display: 'inline',
                    showInput: false                    
                }).scroller('setValue',data2Value, true);
                $('#demo-inline2').mobiscroll().select({
                    display: 'inline',
                    showInput: false
                });
                $(".dw-li").each(function(i, obj) {
                    if ($(this).attr("aria-selected") == "true") {
                        $(this).css("color", "#333333");

                    } else {
                        $(this).css("color", "#cecece")
                    }
                })
                //芝麻信用
                if(res.detail.openidStatus==0){
                	if(source==0){
                		 window.webkit.messageHandlers.zhima.postMessage(res.resultNote);	
                	}else if(source==2)
                	window.mochouhua.zhima(res.resultNote); 
                }
                
                //借款金额初始化
                    $("#demo-inline").next().find('div.dw-li').each(function() {
                    	if( data1Value==$(this).attr("data-val")){
                      		 $("#demo-inline").next().find('div.dw-li').attr("aria-selected" , false);
                      		$(this).addClass("dw-sel");
                      		 $(this).attr("aria-selected",true);
                      		 //var first=-40*(data1Value-1);
                      		 //$("#demo-inline").next().find('div.dw-ul').css("transform" ,  'translate3d(0px,'+first+'px, 0px)');
                      		 $(this).css("color" ,  'rgb(51, 51, 51)');
                       	}
                    })
               
           
                
               $("#demo-inline").next().find('div.dw-li').each(function() {
               	   if ($(this).attr("aria-selected") == "true") {
                       $(".loanMoney").text((($(this).find('div.dw-i').text()) * (1 - cutRate)).toFixed(2) + "元");
                       $(".moneyOfInsurance").text((($(this).find('div.dw-i').text()) * cutRate).toFixed(2) + "元");
                       moneyOfInsurance = ($(this).find('div.dw-i').text()) * cutRate;
                       loanAll = parseFloat(($(this).find('div.dw-i').text()) * (1 - cutRate));
                       loanInall=parseFloat(($(this).find('div.dw-i').text()));
                       $(".moneyOfReturn").text((parseFloat(loanAll) + (parseFloat(loanAll) * mchRate * loanDay)).toFixed(2)+"元");
                       $(".moneyOfBuyInsurance").text((parseFloat(moneyOfInsurance) + (parseFloat(moneyOfInsurance) * mchRate * loanDay)).toFixed(2)+"元");
                       returnMoneyInall =(parseFloat(loanAll) + (parseFloat(loanAll) * mchRate * loanDay) + parseFloat(moneyOfInsurance) + (parseFloat(moneyOfInsurance) * mchRate * loanDay)).toFixed(2);
                       $(".inall").text((returnMoneyInall-parseFloat(preferentialAmt)).toFixed(2)+"元");
                   } else {
                       $(this).css("color", "#cecece")
                   }
               	
                 
                })
                //借款期限初始化
                     $("#demo-inline1").next().find('div.dw-li').each(function() {
                	if( data2Value==$(this).attr("data-val")){
                  		 $("#demo-inline1").next().find('div.dw-li').attr("aria-selected" , false);
                  		 $(this).addClass("dw-sel");
                  		 $(this).attr("aria-selected",true);
                  		// var first=-40*(data2Value-1);
                  		 //$("#demo-inline1").next().find('div.dw-ul').css("transform" ,  'translate3d(0px,'+first+'px, 0px)');
                  		 $(this).css("color" ,  'rgb(51, 51, 51)');
                  		 
                   	}
                })

                $("#demo-inline1").next().find('div.dw-li').each(function() {
                		 if ($(this).attr("aria-selected") == "true") {
                         	  data2Val=$(this).attr("data-val");
                             $(".loanDay").text($(this).find('div.dw-i').text() + "天");
                             $(".dayOfInsurance").text($(this).find('div.dw-i').text() + "天");
                             loanDay = $(this).find('div.dw-i').text();
                             $(".moneyOfReturn").text((parseFloat(loanAll) + (parseFloat(loanAll) * mchRate * loanDay)).toFixed(2)+"元");
                             $(".moneyOfBuyInsurance").text((parseFloat(moneyOfInsurance) + (parseFloat(moneyOfInsurance) * mchRate * loanDay)).toFixed(2)+"元");
                             returnMoneyInall = (parseFloat(loanAll) + (parseFloat(loanAll) * mchRate * loanDay) + parseFloat(moneyOfInsurance) + (parseFloat(moneyOfInsurance) * mchRate * loanDay)).toFixed(2);
                             
                 				$(".inall").text((returnMoneyInall-parseFloat(preferentialAmt)).toFixed(2)+"元");
                 			 
                         } else {
                             $(this).css("color", "#cecece")
                         }	
                })

            } else if (res.result == -1) {
                window.location = encodeURI('*' + res.resultNote);
            } else {
                alert(res.resultNote);
            }
        },
    });
	 //借款用途取消

    	$(".main").click(function(){
    		if(!$(".mask").hasClass("hide")){
    			 $(".mask").addClass("hide");
    		}
    		
    	})
    
    //iphonex适配
    if(isIPhoneX==1){
    $(".Settlement").css("height","91px");
    }
    //提交信息
    $(".sureLoanBtn").click(function(){
	 	var data1={
    			   "cmd": "IneedMoney ",           //请求的命令字
    			   "token": token,                        //用来表示是否允许访问服务器资源
    			   "userId": userId,                      //根据业务需要 加的（这里是中介id）
    			   "version": "1",                        //版本号：默认为1
    			   "params": {
    			             "userId": userId,           //用户ID
    			 "loanAmt": loanInall,           //借款金额
    			 "loanPerion": loanDay,           //借款天数
    			 "couponIds":couponList,
    			 "contractNo": contractNo,           //合同号
    			 "insuranceType":insuranceType,  //保险名称
    			 "protocolList":protocolList,
    			 "consumeType":consumeType,//商品消费类型
    			 "purposeCode":$(".waysOfUse").attr("purposecode"),   //借款用途
    			 "idfa":idfa,
    			 "imei":imei,
    			 "ip":ip,
    			 "longitude":longitude,
    			 "latitude":latitude
    			   }
    			 }
		 var dataString = JSON.stringify(data1);
		    showLoading();
		 $.ajax({
	        url: commonUrl + "/IneedMoney",
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
	            	 $(".showPanelmask").addClass("hide");
	            	if(source==0){
	            		 window.webkit.messageHandlers.getWithDrawId.postMessage({"withDrawId":res.detail.withDrawId,"loanDay":loanDay});
	            		 }else if(source==2){
	            			   window.mochouhua.getWithDrawId(res.detail.withDrawId,loanDay); 
	            		 }
	            } else if (res.result == -1) {
	            	 $(".showPanelmask").addClass("hide");
	                window.location = encodeURI('*' + res.resultNote);
	            } else {
	            	 $(".showPanelmask").addClass("hide");
	                alert(res.resultNote);
	            }
	        },
	    });
    })
    //提交取消
    $(".cancelBtn").click(function(){
    	$(".showPanelmask").addClass("hide");
    })
    //立即借款
    $(".loanBtn").click(function(){
   	if($(this).hasClass("disabled")){
   		return;
   }else if($(".waysOfUse").text()=="请选择"){
	   alert("请选择借款用途");
   }else{
	   $(".showPanelmask").removeClass("hide");
	   $(".panelContent2").text("金额"+loanInall+"元,期限"+loanDay+"天");
   			}
   

    })
   
});