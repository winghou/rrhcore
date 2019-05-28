var toke;
var userId;
var gangSign;
$(function(){
	var params = ns.getRequestParam();
	token=params.token;
	userId=params.userId;
	var withdrawId=params.withdrawId;
	var borrowPerion=params.borrowPerion;
	var borrowAmt=params.borrowAmt;
	var purposeCode=params.purposeCode;
    gangSign=params.gangSign;
	var data;
	if(withdrawId==""){
		 data={
				  "cmd": "queryServiceProrocol",           //请求的命令字
				  "token": token,                        //用来表示是否允许访问服务器资源
				  "userId": userId,                      //用户ID
				  "version": "1",                        //版本号：默认为1
				  "params": {
				            "userId": userId,           //用户ID
				            "withdrawId":withdrawId,
				            "borrowPerion":borrowPerion,//用户订单ID
				            "borrowAmt":borrowAmt,                   //借款额度
							"purposeCode":purposeCode,
							"gangSign":gangSign
							
				  }
				}
		
	}else{
	 data={
				  "cmd": "queryServiceProrocol",           //请求的命令字
				  "token": token,                        //用来表示是否允许访问服务器资源
				  "userId": userId,                      //用户ID
				  "version": "1",                        //版本号：默认为1
				  "params": {
				            "userId": userId,           //用户ID
				            "withdrawId":withdrawId,            //用户订单ID
				            "gangSign":gangSign
				  }
				}
	}
	var dataString=JSON.stringify(data); 
	//showLoading();
	   $.ajax({
           url:commonUrl+"/queryServiceProrocol",
           type: 'post',
           dataType:'json',
           contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
           timeout: 100000,
           data: "{\"key\":\""+Encrypt(dataString)+"\"}",
           success: function(dd){
        	  // hideLoading();
               var res=JSON.parse(Decrypt(dd.key));
              console.log(res);
               if(res.result==0){
            	  $(".contractNo").text("编号："+res.detail.contractNo);
            	  $(".loanName").text(res.detail.customName);
            	  $(".identityNum").text(res.detail.idetityCard);
            	  $(".loanTel").text(res.detail.phone);
            	  $(".loanPlace").text(res.detail.initiatedAddress);
            	  if(res.detail.actualAmt==""){
            		  $(".money").text("") 
            	  }else{
            		  $(".money").text(digitUppercase(res.detail.actualAmt)); 
            	  }
            	
            	  
            	  $(".day").text(res.detail.borrowPerion);
//            	  if(res.detail.serveRate==""){
//            		  $(".serveRate").text(res.detail.serveRate);  
//            	  }else{
//            		  $(".serveRate").text(res.detail.serveRate+"%");  
//            	  }
            	  $(".loanDate").text(res.detail.loanDate);
            	  $(".repayDate").text(res.detail.repayDate);
            	  if(res.detail.shouldPayAmt==""){
            		  $(".shouldPayAmt").text("");
            	  }else{
            		  $(".shouldPayAmt").text(digitUppercase(res.detail.shouldPayAmt));  
            	  }
            	  if(res.detail.purposeName==""){
            		  $(".usageOfLoan").text("");
            	  }else{
            		  $(".usageOfLoan").text(res.detail.purposeName);  
            	  }
            	  $(".bankCardNo").text(res.detail.bankCardNo);
            	  if(res.detail.dayRate==""){
            		  $("#value1").text(res.detail.dayRate);  
            	  }else{
            		  $("#value1").text(res.detail.dayRate);  
            	  }
            	  if(res.detail.overdule3DayRate==""){
            		  $("#value2").text(res.detail.overdule3DayRate);  
            	  }else{
            		  $("#value2").text(res.detail.overdule3DayRate+"%");  
            	  }
            	  if(res.detail.overduleOver3DayRate==""){
            		  $("#value3").text(res.detail.overduleOver3DayRate);  
            	  }else{
            		  $("#value3").text(res.detail.overduleOver3DayRate+"%");  
            	  }
            	  if(res.detail.overdueManageFee ==""){
            		  $("#value4").text(res.detail.overdueManageFee );  
            	  }else{
            		  $("#value4").text(res.detail.overdueManageFee );  
            	  }
            	  if(res.detail.borrowDate ==""){
            		  $("#value8").text("");  
            	  }else{
            		  $("#value8").text(res.detail.borrowDate);  
            	  }
            	  if(res.detail.commissionAmt==""){
            		  $(".servicePay").text("") 
            	  }else{
            		  $(".servicePay").text(digitUppercase(res.detail.commissionAmt)); 
            	  }
               }else if(res.result==-1){
            	   window.location = encodeURI('*'+res.resultNote);
               }else{
            	   alert(res.resultNote);   
               }
           },
     });
})
 var digitUppercase = function(n) {  
        var fraction = ['角', '分'];  
        var digit = [  
            '零', '壹', '贰', '叁', '肆',  
            '伍', '陆', '柒', '捌', '玖'  
        ];  
        var unit = [  
            ['元', '万', '亿'],  
            ['', '拾', '佰', '仟']  
        ];  
        var head = n < 0 ? '欠' : '';  
        n = Math.abs(n);  
        var s = '';  
        for (var i = 0; i < fraction.length; i++) {  
      	  if(i === fraction.length - 1) {
              s += (digit[Math.round(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '');
            } else {
              s += (digit[Math.floor(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '');
            }
        }  
        s = s || '整';  
        n = Math.floor(n);  
        for (var i = 0; i < unit[0].length && n > 0; i++) {  
            var p = '';  
            for (var j = 0; j < unit[1].length && n > 0; j++) {  
                p = digit[n % 10] + unit[1][j] + p;  
                n = Math.floor(n / 10);  
            }  
            s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s;  
        }  
        return head + s.replace(/(零.)*零元/, '元')  
            .replace(/(零.)+/g, '零')  
            .replace(/^整$/, '零元整');  
    }; 