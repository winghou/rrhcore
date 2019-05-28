
       /**
        * 根据转盘旋转角度判断获得什么奖品
        * @param deg
        * @returns {*}
        */
       var whichAward = function(deg) {
           if ((deg >=3 && deg <= 20) || (deg >=345 && deg <= 359)) { //iphonex
               return "1";
           } else if ((deg >= 25 && deg <= 68)) { //30元现金券
               return "2";
           } else if ((deg >=73 && deg <= 115) || (deg >=255 && deg <= 290)) { //谢谢参与
               return "3";
           } else if (deg > 120 && deg <= 156) { //定制靠枕
               return "4";
           } else if (deg > 165 && deg <= 200) { //5元现金券
               return "5";
           } else if (deg > 210 && deg <= 245) { //定制鼠标垫
               return "6";
           }else if (deg > 300 && deg <= 335) { //定制水杯
               return "7";
           }
           //iphonex:1 ,30元现金券：2，谢谢参与：3，靠枕：4，5元现金券：5，鼠标垫：5，水杯：7
       }
       var KinerLottery = new KinerLottery({
           rotateNum: 8, //转盘转动圈数
           body: "#box", //大转盘整体的选择符或zepto对象
           direction: 0, //0为顺时针转动,1为逆时针转动
           disabledHandler: function(key) {
               switch (key) {
                   case "noStart":
                       pop("活动尚未开始");
                       break;
                   case "completed":
                       pop("活动已结束");
                       break;
               }
           }, //禁止抽奖时回调
           clickCallback: function() {
        	   var warddeg;
               //此处访问接口获取奖品
               if(prizeNum==1){
            	   warddeg=createRandom2(1 , 300 , 335)   
               }else if(prizeNum==2){
            	   warddeg=createRandom2(1 , 210 , 245)   
               }else if(prizeNum==3){
            	   warddeg=createRandom2(1 , 120 , 156)
               }else if(prizeNum==4){
            	   warddeg=createRandom2(1 , 25 , 68) 
               }else if(prizeNum==5){
            	   warddeg=createRandom2(1 , 165 , 200) 
            	  
               }else if(prizeNum==6){
            	  var arr;
            	  var num1=createRandom2(1 , 3 , 20);
            	  var num2=createRandom2(1 , 345 , 359);
            	  arr=[num1,num2];
            	  index = Math.floor((Math.random()*arr.length));
            	  warddeg=arr[index].toString();
               }else if(prizeNum==7){
            	   var arr;
             	  var num1=createRandom2(1 , 78 , 110);
             	  var num2=createRandom2(1 , 260 , 285);
             	  arr=[num1,num2];
             	  index = Math.floor((Math.random()*arr.length));
             	  warddeg=arr[index].toString();
               }
               //console.log(warddeg);
               this.goKinerLottery(Number(warddeg));
           }, //点击抽奖按钮,再次回调中实现访问后台获取抽奖结果,拿到抽奖结果后显示抽奖画面
           KinerLotteryHandler: function(deg) {
        	   //iphonex:1 ,30元现金券：2，谢谢参与：3，靠枕：4，5元现金券：5，鼠标垫：6，水杯：7
        	   setTimeout(function() {
        	   if(prizeNum==6){
        		   maskIpohe(isFullInfo);
        	   }else if(prizeNum==4){
        		   mask("30元红包",isFullInfo);
        	   }else if(prizeNum==7){
        		   $(".mask1").removeClass("hide");
        	   }else if(prizeNum==3){
        		   mask("定制靠枕",isFullInfo);  
        	   }else if(prizeNum==5){
        		   mask("5元红包",isFullInfo);  
        	   }else if(prizeNum==2){
        		   mask("鼠标垫",isFullInfo);   
        	   }else if(prizeNum==1){
        		   mask("定制水杯",isFullInfo); 
        	   }else{
        		   $(".mask1").removeClass("hide"); 
        	   }
                   //pop("恭喜您获得:" + whichAward(deg));
        	   }, 1000);
               } //抽奖结束回调
       });
 
