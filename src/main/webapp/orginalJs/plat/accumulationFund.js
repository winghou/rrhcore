var isHasErr1,accountNumbe,isHasErr2,userId,fundId,token;
$(function(){	
	userId = ns.getRequestParam().userId;	
	token = ns.getRequestParam().token;
	fundId = ns.getRequestParam().fundId;
	if(fundId != undefined){
		console.log(fundId);
	}else{
		fundId =0;
	}
	console.log(fundId);
	showLoading();
	var data={
		   "cmd": "housingFundDetail",           //请求的命令字
		   "token": token,                        //用来表示是否允许访问服务器资源
		   "userId": userId,                      //用户ID
		   "version": "1",                        //版本号：默认为1
		   "params": {
			   "userId": userId,				//這邊是用戶自己填的，暫且寫死
			   "fundId": fundId
		   }
	   };
    var dataString=JSON.stringify(data);
    $.ajax({
    	url:"http://192.168.1.64:8080/rrhcore/housingFundDetail",
    	type: 'post',
    	dataType:'json',
    	contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
    	timeout: 100000,
    	data: "{\"key\":\""+Encrypt(dataString)+"\"}",
    	success:function(res){ 
    		hideLoading();
    		var res=JSON.parse(Decrypt(res.key));
    		if(res.result==0){    			
    			if(fundId != undefined && fundId != 0){
    				var loginFundNo = res.detail.loginFundNo;
    	    		var city = res.detail.city;
    	    		var province = res.detail.province;
    	    		var loginPassword = res.detail.loginPassword;
    	    		$(".select-value").val(city+"-"+province);
    	    		$(".accountNumbe").val(loginFundNo);
    	    		$(".bankPassword").val(loginPassword);
    			}	    		
	    		$(".btn").click(function(){
	    			var selectValue = $(".select-value").val();
	    	        if(selectValue==""){
	    	        	alert("请选择省市");
	    	        }else{
	    	        	checkTxt1();
	    	        	if(isHasErr1==false){
		    	        	if($(".bankPassword").val()==""){
		    	        		alert("请输入公积金密码");
		    	        	}else{ 
	    	        		   var all = $(".select-value").val().split("-");
	    	        		   var data={
	    	        		        "cmd": "housingFundSaveOrUpdate",           //请求的命令字
	    	        		        "token": token,                        //用来表示是否允许访问服务器资源
	    	        		        "userId": userId,                      //用户ID
	    	        		        "version": "1",                        //版本号：默认为1
	    	        		        "params": {
	    	        		        "userId": userId,				//這邊是用戶自己填的，暫且寫死
	    	        		        "fundId": fundId,
	    	        		        "loginFundNo": $(".accountNumbe").val(),
	    	        		        "loginPassword": $(".bankPassword").val(),
	    	        		        "province": all[1],
	    	        		        "city": all[0]
	    	        		         }
	    	        		   	};
	    	        		 var dataString=JSON.stringify(data);	        		 
	    	        		 $.ajax({
	    	        		     url:"http://192.168.1.64:8080/rrhcore/housingFundSaveOrUpdate",
	    	        		     type: 'post',
	    	        		     dataType:'json',
	    	        		     contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
	    	        		     timeout: 100000,
	    	        		     data: "{\"key\":\""+Encrypt(dataString)+"\"}",
	    	        		     success:function(res){    	        		        			    		
	    	        		    	 var res=JSON.parse(Decrypt(res.key));
	    	        		    	 if(res.result==0){
	    	        		    		 location.href = "accumulationDisplay.html?userId="+userId+"&token="+token;
	    	        		    	 }else{
	    	        		    		 hideLoading();
	    	        		    		 alert(res.resultNote);
	    	        		    	 }
	    	        		     }
	    	        		})
	    	        	 }
	    	         }
	    	      }
	    	    })	    			    		
    		}else{
    			 hideLoading();
    			 alert(res.resultNote)
    		}
    	}
    })  
}) 
// 验证公积金
function checkTxt1(){
    isHasErr1=false;
    accountNumbe = $(".accountNumbe").val();
    var bankExp = /^(\d){6,14}$/; 
    var getErrMsg = function() {
        if ($.trim(accountNumbe) == "") {
            isHasErr1 = true;
            return "请输入公积金账号";
        }
        if(!bankExp.test(accountNumbe)){
            isHasErr1 = true;
            return "请输入正确的公积金账号";
        }
        if ($.trim(accountNumbe).indexOf("<") >= 0) {
            isHasErr1 = true;
            return "公积金账号格式有问题";
        }
    };
    var errMsg = getErrMsg();
    // 如果出现错误显示错误提示动画,否则隐藏错误提示
    return isHasErr1 ? alert(errMsg) : hideErrTip();
}
/*去除密码的空格、禁止输入*/
function Trim(str,is_global){
    var result;
    result = str.replace(/(^\s+)|(\s+$)/g,"");
    if(is_global.toLowerCase()=="g")
    {
        result = result.replace(/\s/g,"");
    }
    return result;
}
function inputSapceTrim(e,this_temp){
    this_temp.value = Trim(this_temp.value,"g");
    var keynum;
    if(window.event){
        keynum = e.keyCode
    }
    else if(e.which){
        keynum = e.which
    }
    if(keynum == 32){
        return false;
    }
    return true;
}
function banInputSapce(e){
    var keynum;
    if(window.event){
        keynum = e.keyCode
    }
    else if(e.which){
        keynum = e.which
    }
    if(keynum == 32){
        return false;
    }
    return true;
}
// 点击密码，是否显示
function showps(){
    if (this.forms.password.type="password") {
        document.getElementById("box").innerHTML="<input class=\"bankPassword\" type=\"text\" name=\"password\" placeholder=\"请输入登录密码\"  onkeydown='return banInputSapce(event);' onKeyup='return inputSapceTrim(event,this);' value="+this.forms.password.value+">";
        document.getElementById("click").innerHTML="<img src='../../images/eye-o@2x.png' class='eye' onclick='hideps()'>";
    }
}
function hideps(){
    if (this.forms.password.type="text") {
        document.getElementById("box").innerHTML="<input class=\"bankPassword\" type=\"password\" name=\"password\" placeholder=\"请输入登录密码\"  onkeydown='return banInputSapce(event);' onKeyup='return inputSapceTrim(event,this);' value="+this.forms.password.value+">";
        document.getElementById("click").innerHTML="<img src='../../images/eye-c@2x.png' class='eye' onclick='showps()'>";
    }
}

