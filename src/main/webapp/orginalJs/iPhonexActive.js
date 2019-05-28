var token;
var userId;
var userChance;
var prizeNum;
var isFullInfo;
var isMoving=1;
var currentPage=0;
var devicetype;
$(function(){
	//查看设备类型
    var u = navigator.userAgent;
    var isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1;
    var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
    if (isAndroid) {
   	 devicetype="android";
    } else if (isiOS) {
   	 devicetype="ios";
    } else {
   	 devicetype="other";
    }
	var params = ns.getRequestParam();
	token=params.token;
	userId=params.userId;
	updatePage();//加载页面信息
if(devicetype!=="ios"){
	$(".appleTip").remove();
}
	//页面切换
	$('.pageOneTitle').addClass('animated bounceInRight');
	  $('.pages').parallax({
  		direction: 'vertical', 	// horizontal (水平翻页)
  		swipeAnim: 'default', 	// cover (切换效果)
  		drag:      true,		// 是否允许拖拽 (若 false 则只有在 touchend 之后才会翻页)
  		loading:   false,		// 有无加载页
  		indicator: false,		// 有无指示点
  		arrow:     true,		// 有无指示箭头
  		/*
  		 * 翻页后立即执行
  		 * {int} 		index: 第几页
  		 * {DOMElement} element: 当前页节点
  		 * {String}		directation: forward(前翻)、backward(后翻)、stay(保持原页)
  		 */
  		onchange: function(index, element, direction) {
//  			if(index==2 && direction=="forward"){
//  	  			$('.pageThreeTitle').addClass('animated flipInY');
//  	  		$('.box').addClass('animated bounceInDown');
//  	  	$('.record').addClass('animated tada');
//  	  		
//  	  		}
  			
  			if(index==0 && direction=="stay"){
  				if(devicetype=="ios"){
  					window.location = encodeURI("ActiveHomePage") 
  				}else if(devicetype=="android"){
  					window.mochouhua.getName("ActiveHomePage");
  				}
  				
 			}else if(index==0 && currentPage!==0){
 			         currentPage=0;
 			    	if(devicetype=="ios"){
 			    	   window.location = encodeURI("ActiveHomePage") 
 	  				}else if(devicetype=="android"){
 	  				window.mochouhua.getName("ActiveHomePage");
 	  				}
  			}else if (index==1 && currentPage!==1){
  			       currentPage=1;
  			     if(devicetype=="ios"){
			    	 window.location = encodeURI("ActivityRulesPage") 
	  				}else if(devicetype=="android"){
	  					window.mochouhua.getName("ActivityRulesPage");
	  				}
 			}else if(index==2 && currentPage!==2){
  			      currentPage=2;
  			    if(devicetype=="ios"){
			    	  window.location = encodeURI("LuckyWheelPage") 
	  				}else if(devicetype=="android"){
	  					window.mochouhua.getName("LuckyWheelPage");
	  				}
			    
  			} 
  		 //console.log(index, element, direction);
  			
  		},
  		/*
  		 * 横竖屏检测
  		 * {String}		orientation: landscape / protrait
  		 */
  		orientationchange: function(orientation) {
  			
  		}

  	});
	  //获得奖品填写资料页面
	  $(".imagesBtn").click(function(){
		  
		  if($(this).hasClass("disabled")){
			  return
		  }else{
			  checkInformation();//用户获奖信息;
			  if(devicetype=="ios"){
		    	  window.location = encodeURI("WinningRecord"); 
  				}else if(devicetype=="android"){
  					window.mochouhua.getName("WinningRecord");
  				}
			  $(".gift").removeClass("hide");
			  $(".wrapper").addClass("hide");
		  }
	  })
	  //关闭资料填写页面
	  $(".gift-close").click(function(){
		  $(".gift").addClass("hide");
		  $(".wrapper").removeClass("hide");
	  })
	  $(".writeInfoBtn0").click(function(){
		  $(".mask,.wrapper").addClass("hide");
		  $(".gift").removeClass("hide");
		  isMoving=1;
		  checkInformation();
		   if(devicetype=="ios"){
		    	  window.location = encodeURI("WinningRecord"); 
				}else if(devicetype=="android"){
					window.mochouhua.getName("WinningRecord");
				}
	  })
	   $(".writeInfoBtn1").click(function(){
		   checkInformation();
		  $(".mask2,.wrapper").addClass("hide");
		  $(".gift").removeClass("hide");
		  isMoving=1;
		  if(devicetype=="ios"){
	    	  window.location = encodeURI("WinningRecord"); 
				}else if(devicetype=="android"){
					window.mochouhua.getName("WinningRecord");
				}
		  
	  })
	  $(".maskClose").click(function(){
		  $(".mask,.mask2,.mask1,.mask3").addClass("hide");
		   isMoving=1;
		  updatePage();
//		  if(prizeNum!==7){
//			  checkInformation();
//		  }
		 
	  })
	   $(".mask3").click(function(){
		   $(".mask3").addClass("hide");
	   })
	  $(".gift-submitImages").click(function(){
		  if($(".gift-submitImages").hasClass("readonly")){
			    $(".gift-submitImages").attr("src","../images/gift-save.png")
				   $(".nameTxt,.telTxt ,.addrArea ").removeAttr("readonly");
				   $(".nameTxt,.telTxt ,.addrArea ").removeClass("change");
				   $(".gift-submitImages").removeClass("readonly");
				   return;
		  }
		  var name=$(".nameTxt").val();
		  var tel=$(".telTxt").val();
		  var regExp = /^0?1[3|4|5|7|8][0-9]\d{8}$/;
		  var address=$(".addrArea").val();
		  if(name==""){
			  pop("姓名不能为空");
		  }else if(tel==""){
			  pop("联系方式不能为空")
		  }else if(address==""){
			  pop("个人地址不能为空") 
		  }else if(!regExp.test(tel)){
			  pop("请输入正确的手机号")
		  }else if(name.indexOf("<") !== -1 || name.indexOf(">") !== -1){
			  pop("请输入正确的姓名")
		  }else if(tel.indexOf("<") !== -1 || tel.indexOf(">") !== -1){
			  pop("请输入正确的联系方式")
		  }else if(address.indexOf("<") !== -1 || address.indexOf(">") !== -1){
			  pop("请输入正确的联系地址");
		  }else{
				var data3={
						  "cmd": "updatePrizePersonInfo",           //请求的命令字
						  "token": token,                        //用来表示是否允许访问服务器资源
						  "userId": userId,                      //用户ID
						  "version": "1",                        //版本号：默认为1
						  "params": {
						            "userId": userId,                //用户ID 
						            "customName":name,
						            "customPhone":tel,
						            "customAddress":address
						  }	
						}
				var dataString=JSON.stringify(data3);
				showLoading();
				   $.ajax({
					   url:commonUrl+"/updatePrizePersonInfo",
					   type: 'post',
					  dataType:'json',
					   contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
					   timeout: 100000,
					   data: "{\"key\":\""+Encrypt(dataString)+"\"}",
					   success: function(dd){
					 	   hideLoading();
					       var res=JSON.parse(Decrypt(dd.key));
					       //console.log(res);
					       if(res.result==0){
					      $(".mask3").removeClass("hide");
					      setTimeout(function() {
					    	  $(".mask3").addClass("hide");
						}, 3000);
					      checkInformation();//用户获奖信息；
					      }else if(res.result==-1){
					    	   window.location = encodeURI('*'+res.resultNote);
					    	   
					       }else{
					        
					    	   pop(res.resultNote);   
					      }
					   },
					 });
			
		  }
			 
		  
	  })
})

function updatePage(){
	//调取接口获取用户信息
	var data={
			  "cmd": "prizeLotteryTable",           //请求的命令字
			  "token": token,                        //用来表示是否允许访问服务器资源
			  "userId": userId,                      //用户ID
			  "version": "1",                        //版本号：默认为1
			  "params": {
			            "userId": userId,                //用户ID 
			  }	
			}
	var dataString=JSON.stringify(data); 
	//showLoading();
	   $.ajax({
  url:commonUrl+"/prizeLotteryTable",
  type: 'post',
 dataType:'json',
  contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
  timeout: 100000,
  data: "{\"key\":\""+Encrypt(dataString)+"\"}",
  success: function(dd){
	   //hideLoading();
      var res=JSON.parse(Decrypt(dd.key));
      //console.log(res);
      if(res.result==0){
     $(".chanceNumber").text(res.detail.lotteryNum);
     userChance=res.detail.lotteryNum
       var luckyuser=res.detail.lotteryRecord;
     $(".recordul").html("");
     $(luckyuser).each(function(i,obj){
    	 var li=$("<li>"+obj+"</li>").appendTo($(".recordul"));
     })
     if(res.detail.isLottery==0){
    	 $(".imagesBtn").attr("src","../images/pageThreeDisabled.png")
    	 $(".imagesBtn").addClass("disabled");
    	 
     }else if(res.detail.isLottery==1){
      	 $(".imagesBtn").attr("src","../images/pageThreeAbled.png")
    	 $(".imagesBtn").removeClass("disabled");
     }
     }else if(res.result==-1){
   	   window.location = encodeURI('*'+res.resultNote);
   	   
      }else{
       
   	   pop(res.resultNote);   
     }
  },
});
}
function createRandom2(num , from , to){
    var arr=[];
    var json={};
    while(arr.length<num){
        //产生单个随机数
        var ranNum=Math.ceil(Math.random()*(to-from))+from;
        //通过判断json对象的索引值是否存在 来标记 是否重复
        if(!json[ranNum]){
            json[ranNum]=1;
            arr.push(ranNum);
        }   
    }
    return arr;
    
}
function mask(name,isSubmit){
	$(".giftName").text(name);
	$(".mask").removeClass("hide")
	if(isSubmit==0){
	$(".writeInfoBtn0").removeClass("hide");	
	}else{
		$(".writeInfoBtn0").addClass("hide");	
	}
}
function maskIpohe(isSubmit){
	$(".mask2").removeClass("hide");
	if(isSubmit==0){
		$(".writeInfoBtn1").removeClass("hide");	
		}else{
			$(".writeInfoBtn1").addClass("hide");	
		}
	
	
}
function checkInformation(){
	var data2={
			  "cmd": "showPrizeRecord",           //请求的命令字
			  "token": token,                        //用来表示是否允许访问服务器资源
			  "userId": userId,                      //用户ID
			  "version": "1",                        //版本号：默认为1
			  "params": {
			            "userId": userId,                //用户ID 
			  }	
			}
	var dataString=JSON.stringify(data2);
	   $.ajax({
		   url:commonUrl+"/showPrizeRecord",
		   type: 'post',
		  dataType:'json',
		   contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
		   timeout: 100000,
		   data: "{\"key\":\""+Encrypt(dataString)+"\"}",
		   success: function(dd){
		 	   //hideLoading();
		       var res=JSON.parse(Decrypt(dd.key));
		       //console.log(res);
		       if(res.result==0){
		   if(res.detail.isFullInfo==1){
			   
			   $(".gift-submitImages").attr("src","../images/gift-rev.png")
			   $(".nameTxt,.telTxt ,.addrArea ").attr("readonly","readonly");
			   $(".nameTxt,.telTxt ,.addrArea ").addClass("change");
			   $(".gift-submitImages").addClass("readonly");
		   }else if(res.detail.isFullInfo==0){
			   $(".gift-submitImages").attr("src","../images/gift-sub.png")
		   }
		      $(".nameTxt").val(res.detail.customName);
		      $(".telTxt").val(res.detail.customPhone);
		      $(".addrArea").val(res.detail.customAddress);
		      var luckyrecord=res.detail.prizeArray;
		      $(".displayTable").html("");
		      var tr=$("<tr></tr>").appendTo($(".displayTable"));
		      var td1=$("<td></td>").appendTo(tr);
		      $("<img src='../images/gift-tabletitle1.png' class='gift-tabletitle'>").appendTo(td1);
		      var td2=$("<td></td>").appendTo(tr);
		      $(  "<img src='../images/gift-tabletitle2.png' class='gift-tabletitle'>").appendTo(td2);
		      var td3=$("<td></td>").appendTo(tr);
		      $("<img src='../images/gift-tabletitle3.png' class='gift-tabletitle'>").appendTo(td3);
		      $(luckyrecord).each(function(i,obj){
		    	 var tr=$("<tr></tr>").appendTo($(".displayTable"));
		     	 $("<td>"+ obj.prizeDate +"</td>").appendTo(tr);
		     	 $("<td>"+ obj.prizeName +"</td>").appendTo(tr);
		     	 $("<td>"+ obj.prizeNum +"</td>").appendTo(tr);
		      })
		      }else if(res.result==-1){
		    	   window.location = encodeURI('*'+res.resultNote);
		       }else{ 
		    	   pop(res.resultNote);   
		      }
		   },
		 });
}

function pop(errMsg){
	if($('.poperrorTip').length >0){
		return
	}else{
	   var errorTip = $("<div class='poperrorTip'></div>");
	   var errorwords = $("<div class='poperrorwords'></div>").appendTo(errorTip);
	   $("body").append(errorTip);
 // 显示灰色错误色块提示
 $(".poperrorwords").text(errMsg);
 var eleWidth = $('.poperrorTip').width()+10;
//console.log(eleWidth);
 $(".poperrorTip").css({  'left': '50%', 'margin-left': '-' + eleWidth / 2 + 'px' });
 setTimeout(function() { hidePop();},4000)
}
}
function hidePop(){
	$(".poperrorTip").remove();
}