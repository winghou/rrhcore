var userId;
var token;
var remainingLotteryNum;
var increasedCredit;
var orginally=0;
$(function(){
	var params = ns.getRequestParam();
	userId=params.userId;
	token=params.token;
	orginal();
	 var lottery = {
             index: 0, //当前转动到哪个位置，起点位置
             count: 0, //总共有多少个位置
             timer: 0, //setTimeout的ID，用clearTimeout清除
             speed: 20, //初始转动速度
             times: 0, //转动次数
             cycle: 50, //转动基本次数：即至少需要转动多少次再进入抽奖环节
             prize: 0, //中奖位置
             init: function(id) {
                 if ($("#" + id).find(".lottery-unit").length > 0) {
                     $lottery = $("#" + id);
                     $units = $lottery.find(".lottery-unit");
                     this.obj = $lottery;
                     this.count = $units.length;
                     $lottery.find(".lottery-unit-" + this.index).children().addClass("active");
                 }
             },
             roll: function() {
                 var index = this.index;
                 var count = this.count;
                 var lottery = this.obj;
                 $(lottery).find(".lottery-unit-" + index).children().removeClass("active");
                 index += 1;
                 if (index > count - 1) {
                     index = 0;
                 }
                 $(lottery).find(".lottery-unit-" + index).children().addClass("active");
                 this.index = index;
                 return false;
             },
             stop: function(index) {
                 this.prize = index;
                 return false;
             }
         };

         function roll() {
             lottery.times += 1;
             lottery.roll();
             var prize_site = $("#lottery").attr("prize_site");
             if (lottery.times > lottery.cycle + 10 && lottery.index == prize_site) {
                 var prize_id = $("#lottery").attr("prize_id");
                 var prize_name = $("#lottery").attr("prize_name");
                 if(prize_id==0){
                	 if(remainingLotteryNum==0){
                		 setTimeout(function () { 
                			 $(".sadmask").removeClass("hide");
                		    }, 500);
                		 orginal();
                	 }else{
                		 setTimeout(function () { 
                			 $(".sadmask").removeClass("hide");
                    		 $(".closesub").removeClass("hide");
                		    }, 500);
                		 orginal();
                	 }
                	
                 }else if(prize_id==1){
                	 setTimeout(function () { 
                		 $(".happyImage").attr("src","../images/10yuan.png"); 
                     	$(".maskhappy").removeClass("hide");
            		    }, 500);
                	 orginal();
                 }else if(prize_id==2){
                	 setTimeout(function () { 
                		 $(".happyImage").attr("src","../images/20yuan.png"); 
                      	$(".maskhappy").removeClass("hide");
            		    }, 500);
                	 orginal();
                 }
                // alert("前端中奖位置："+prize_site+"\n"+"中奖名称："+prize_name+"\n中奖id："+prize_id)
                 clearTimeout(lottery.timer);
                 lottery.prize = -1;
                 lottery.times = 0;
                 click = false;

             } else {
                 if (lottery.times < lottery.cycle) {
                     lottery.speed -= 10;
                 } else if (lottery.times == lottery.cycle) {
                     var index = Math.random() * (lottery.count) | 0;
                     lottery.prize = index;
                 } else {
                     if (lottery.times > lottery.cycle + 10 && ((lottery.prize == 0 && lottery.index == 7) || lottery.prize == lottery.index + 1)) {
                         lottery.speed += 110;
                     } else {
                         lottery.speed += 20;
                     }
                 }
                 if (lottery.speed < 40) {
                     lottery.speed = 40;
                 }
                 lottery.timer = setTimeout(roll, lottery.speed);
             }
             return false;
         }

         var click = false;

         $(function() {
             lottery.init('lottery');
             $("#lottery a").click(function() {
                  if (click) {
                     return false;
                 }else if(orginally==0){
                	 return;
                 } else if(remainingLotteryNum==0){
                	 alert("很遗憾您已没有抽奖机会！")
                 }else{
                     lottery.speed = 200;
                   var data={
                		   "cmd": "lotteryActivity",           //请求的命令字
                		   "token": token,                        //用来表示是否允许访问服务器资源
                		   "userId": userId,                      //用户ID
                		   "version": "1",                        //版本号：默认为1
                		   "params": {
                		             "userId": userId           //用户ID
                		   }
                   }
                   var dataString=JSON.stringify(data); 
              	 showLoading();       
            	 $.ajax({
                     url:commonUrl+"/lotteryActivity",
                     type: 'post',
                     dataType:'json',
                     contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
                     timeout: 100000,
                     data: "{\"key\":\""+Encrypt(dataString)+"\"}",
                     success: function(dd){
                    	  var res=JSON.parse(Decrypt(dd.key));
                          hideLoading();
                          console.log(res);
                          if(res.result==0){
                        	  var creditResult=res.detail.creditResult;
                        	  if(creditResult==0){
                        		  $("#lottery").attr("prize_site", "7");
                                  $("#lottery").attr("prize_id", "0");
                                  $("#lottery").attr("prize_name", "谢谢参与");
                                  roll();
                                  click = true;
                                  return false; 
                                  
                        	  }else if(creditResult==1){
                        		  $("#lottery").attr("prize_site", "3");
                                  $("#lottery").attr("prize_id", "1");
                                  $("#lottery").attr("prize_name", "提额10元");
                                  roll();
                                  click = true;
                                  return false;
                                 
                        	  }else if(creditResult==2){
                        		    var x = 2;     
                        		    var y = 0;     
                        		   var rand = parseInt(Math.random() * (x - y + 1) + y);
                        		   if(rand==0){
                        			   $("#lottery").attr("prize_site", "1");
                                       $("#lottery").attr("prize_id", "2");
                                       $("#lottery").attr("prize_name", "提额20元");
                                       roll();
                                       click = true;
                                       return false;
                                       
                        		   }else if(rand==1){
                        			   $("#lottery").attr("prize_site", "4");
                                       $("#lottery").attr("prize_id", "2");
                                       $("#lottery").attr("prize_name", "提额20元");
                                       roll();
                                       click = true;
                                       return false;
                                       
                        		   }else if(rand==2){
                        			   $("#lottery").attr("prize_site", "6");
                                       $("#lottery").attr("prize_id", "2");
                                       $("#lottery").attr("prize_name", "提额20元");
                                       roll();
                                       click = true;
                                       return false;
                                    
                        		   }else{
                        			   $("#lottery").attr("prize_site", "7");
                                       $("#lottery").attr("prize_id", "0");
                                       $("#lottery").attr("prize_name", "谢谢参与");
                                       roll();
                                       click = true;
                                       return false; 
                        		   }
                        		 
                        	  }
                          }
                          else if(res.result==-1){
                         	   window.location = encodeURI('*'+res.resultNote);
                          }else{
                       	   alert(res.resultNote);   
                          }
                    	 
                    	 }
                     });
                 }
             });
         })
         $(".closeImage, .sureBtn").click(function(){
        	 $(".maskhappy").addClass("hide");
         })
         $(".closeImage1, .closesub").click(function(){
        	 $(".sadmask").addClass("hide");
         })	
})
function orginal(){
	var data={
			"cmd": "showLotteryTable",           //请求的命令字
			  "token": token,                        //用来表示是否允许访问服务器资源
			  "userId": userId,                      //用户ID
			  "version": "1",                        //版本号：默认为1
			  "params": {
			            "userId": userId           //用户ID
			  }
	}
	var dataString=JSON.stringify(data); 
	
	 $.ajax({
         url:commonUrl+"/showLotteryTable",
         type: 'post',
         dataType:'json',
         contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
         timeout: 100000,
         data: "{\"key\":\""+Encrypt(dataString)+"\"}",
         success: function(dd){
        	  var res=JSON.parse(Decrypt(dd.key));
             
              console.log(res);
              if(res.result==0){
            	  remainingLotteryNum=res.detail.remainingLotteryNum;
            	  increasedCredit=res.detail.increasedCredit;
            	  $(".number").text(remainingLotteryNum);
            	  $(".money").text(increasedCredit);
            	  orginally=1;
              }
              else if(res.result==-1){
             	   window.location = encodeURI('*'+res.resultNote);
              }else{
           	   alert(res.resultNote);   
              }
        	 
        	 }
         });
}
