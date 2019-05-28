var isHasErr1,isHasErr2,bankPassword,email,userId,token;

$(function(){
	showLoading();
	var userId = ns.getRequestParam().userId;
	token="";
	var flat = ns.getRequestParam().flat;
	console.log(flat);
	/*var token = ns.getRequestParam().token;
	console.log(userId);
	console.log(token);*/
	var data={
	        "cmd": "userEmails",           //请求的命令字
	        "token": token,                        //用来表示是否允许访问服务器资源
	        "userId": userId,                      //用户ID
	        "version": "1",                        //版本号：默认为1
	        "params": {
	        	"userId": userId				//這邊是用戶自己填的，暫且寫死
	        }
	    };
	var dataString=JSON.stringify(data);
	$.ajax({
    	url:"http://192.168.1.64:8080/rrhcore/userEmails",
		type: 'post',
        dataType:'json',
        contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
        timeout: 100000,
        data: "{\"key\":\""+Encrypt(dataString)+"\"}",
    	success:function(res){  		
    		var res=JSON.parse(Decrypt(res.key));
    		var lisContent = res.detail.userEmailList;
    		console.log(res);
    		if((lisContent.length>0 && flat!=1) || (lisContent.length>0 && flat=="undefind")){
    			hideLoading();
    			location.href="emailDisplay.html?userId="+userId+"&token="+token;
    			return;
    		}else{
    			hideLoading();
    			$('.btn').click(function(){
    		    	email=$('.email').val();
    		    	checkTxt1();
    		    	if(isHasErr1==false){
    		    		checkTxt2();
    		    		if(isHasErr2==false){
    		    			var emailPassword = $(".emailPassword").val();
    		    			var data={
    		    			        "cmd": "emailLogin",           //请求的命令字
    		    			        "token": token,                        //用来表示是否允许访问服务器资源
    		    			        "userId": userId,                      //用户ID
    		    			        "version": "1",                        //版本号：默认为1
    		    			        "params": {
    		    			        	"userId": userId,
    		    			            "cntCommt": email,   
    		    			            "cntPass": emailPassword			
    		    			        }
    		    			    }
    		    			var dataString=JSON.stringify(data);
    		    			showLoading();
    		    			$.ajax({
    		    				url:"http://192.168.1.64:8080/rrhcore/emailLogin",
    		    				type: 'post',
    		    		        dataType:'json',
    		    		        contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
    		    		        timeout: 100000,
    		    		        data: "{\"key\":\""+Encrypt(dataString)+"\"}",
    		    		        success:function(res){
    		    		        	 hideLoading();
    		    		        	var res=JSON.parse(Decrypt(res.key));
    		    		        	console.log(res);
    		    		        	if(res.detail.code==1){
    		    		        		location.href="emailDisplay.html?userId="+userId+"&token="+token; 
    		    		        	}else if(res.detail.code!="1"){
    		    		        		alert(res.resultNote);
    		    		        	}
    		    		        }
    		    			})
    		    		}
    		    	}
    		        
    		    })  			
    		}   	
    	}
	});
	
	
	
	
	/*点击 删除邮箱内容*/
    $('.delect').click(function(){
        $('.email').val("");
    })
   
    
})

function fnShowDeleteIcon() {
    $('.clicks').show();
}
function fnHideDeleteIcon() {
    $('.clicks').hide();
}
// 验证邮箱账号
function checkTxt1(){
    isHasErr1=false;
    email = $(".email").val();
    var hz = email.indexOf("@");
    var hzContent=email.substring(hz,email.length);
    var yz = ["@qq.com","@126.com","@163.com","@sina.com"];  
    var result= $.inArray(hzContent, yz);
    var getErrMsg = function() {
        if ($.trim(email) == "") {
            isHasErr1 = true;
            return "请输入邮箱账号";
        };
        if(result === -1){
        	isHasErr1 = true;
            return "您的当前账号我们不支持哦！";
        }
        if ($.trim(email).indexOf("<") >= 0) {
            isHasErr1 = true;
            return "邮箱格式有问题";
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
        document.getElementById("box").innerHTML="<input class=\"emailPassword\" type=\"text\" name=\"password\" maxlength=\"30\" minlength=\"6\"  placeholder=\"请输入您的邮箱密码\"  onkeydown='return banInputSapce(event);' onKeyup='return inputSapceTrim(event,this);' value="+this.forms.password.value+">";
        document.getElementById("click").innerHTML="<img src='../../images/eye-o@2x.png' class='eye' onclick='hideps()'>";
    }
}
function hideps(){
    if (this.forms.password.type="text") {
        document.getElementById("box").innerHTML="<input class=\"emailPassword\" type=\"password\" name=\"password\" maxlength=\"30\" minlength=\"6\"  placeholder=\"请输入您的邮箱密码\"  onkeydown='return banInputSapce(event);' onKeyup='return inputSapceTrim(event,this);' value="+this.forms.password.value+">";
        document.getElementById("click").innerHTML="<img src='../../images/eye-c@2x.png' class='eye' onclick='showps()'>";
    }
}
// 验证密码
function checkTxt2(){
    isHasErr2=false;
    bankPassword = $(".emailPassword").val();
    var getErrMsg = function() {
        if ($.trim(bankPassword) == "") {
            isHasErr2 = true;
            return "请输入邮箱密码";
        }
        if ($.trim(bankPassword).indexOf("<") >= 0) {
            isHasErr2 = true;
            return "邮箱密码格式有问题";
        }
    };
    var errMsg = getErrMsg();
    // 如果出现错误显示错误提示动画,否则隐藏错误提示
    return isHasErr2 ? alert(errMsg) : hideErrTip();
}