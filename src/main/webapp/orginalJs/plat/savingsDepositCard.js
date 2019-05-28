var isHasErr1,isHasErr2,isHasErr3,isHasErr4,bankCar,bankType,bankPassword,phone,userId,token,depositCardId;
$(function(){
	showLoading();
	userId = ns.getRequestParam().userId;
	token = ns.getRequestParam().token;
	depositCardId = ns.getRequestParam().depositCardId;
	if(depositCardId != undefined) {		
		console.log(depositCardId);
	}else{
		depositCardId=0;
	}
	console.log(depositCardId);
	var data={
		"cmd": "depositCardDetail",           //请求的命令字
		"token": token,                        //用来表示是否允许访问服务器资源
		"userId": userId,                      //用户ID
		"version": "1",                        //版本号：默认为1
		"params": {
			"userId": userId,				//這邊是用戶自己填的，暫且寫死
			"depositCardId": depositCardId
		}
	};
	var dataString=JSON.stringify(data);
	$.ajax({
		url:"http://192.168.1.64:8080/rrhcore/depositCardDetail",
		type: 'post',
		dataType:'json',
		contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
		timeout: 100000,
		data: "{\"key\":\""+Encrypt(dataString)+"\"}",
		success:function(res){			
			hideLoading();
			var res=JSON.parse(Decrypt(res.key));
			$(".customName").val(res.detail.customName);
			if(res.result==0){
				if(depositCardId != undefined) {
					var bankCar = res.detail.cntCommt;
					var bankType = res.detail.bankName;
					var bankPassword = res.detail.cntPass;
					var phone = res.detail.mobilePhone;
					$(".bankCar").val(bankCar);
					$(".bankType").val(bankType);
					$(".bankPassword").val(bankPassword);
					$(".phone").val(phone);
				}
				$(".btn").click(function(){
					checkTxt1();
					if(isHasErr1==false){
						checkTxt3();
						if(isHasErr3==false){
							checkTxt4();
							if(isHasErr4==false){
								checkTxt2();
								if(isHasErr2 == false){
									var data={
										"cmd": "depositCardSaveOrUpdate",           //请求的命令字
										"token": token,                        //用来表示是否允许访问服务器资源
										"userId": userId,                      //用户ID
										"version": "1",                        //版本号：默认为1
										"params": {
											"userId": userId,				//這邊是用戶自己填的，暫且寫死
											"depositCardId": depositCardId,
											"bankCardNo": $(".bankCar").val(),
											"bankName": $(".bankType").val(),
											"bankCardPwd": $(".bankPassword").val(),
											"mobilePhone": $(".phone").val(),
										}
									};
									var dataString=JSON.stringify(data);
									$.ajax({
										url:"http://192.168.1.64:8080/rrhcore/depositCardSaveOrUpdate",
										type: 'post',
										dataType:'json',
										contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
										timeout: 100000,
										data: "{\"key\":\""+Encrypt(dataString)+"\"}",
										success:function(res){
											var res=JSON.parse(Decrypt(res.key));
											if(res.result==0){
												location.href = "savingsCardDisplay.html?userId="+userId+"&token="+token+"&depositCardId="+depositCardId;
											}else{
												hideLoading();
												alert(res.resultNote);
											}
										}
									})
								}
							}
						}
					}
				})
			}else{
				hideLoading();
				alert(res.resultNote);
			}
		}
	})
})


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
        document.getElementById("box").innerHTML="<input class=\"bankPassword\" type=\"text\" name=\"password\" maxlength=\"30\" minlength=\"6\" placeholder=\"请输入您的网银登录密码\"  onkeydown='return banInputSapce(event);' onKeyup='return inputSapceTrim(event,this);' value="+this.forms.password.value+">";
        document.getElementById("click").innerHTML="<img src='../../images/eye-o@2x.png' class='eye' onclick='hideps()'>";
    }
}
function hideps(){
    if (this.forms.password.type="text") {
        document.getElementById("box").innerHTML="<input class=\"bankPassword\" type=\"password\" name=\"password\" maxlength=\"30\" minlength=\"6\"  placeholder=\"请输入您的网银登录密码\"  onkeydown='return banInputSapce(event);' onKeyup='return inputSapceTrim(event,this);' value="+this.forms.password.value+">";
        document.getElementById("click").innerHTML="<img src='../../images/eye-c@2x.png' class='eye' onclick='showps()'>";
    }
}
// 验证银行卡号
function checkTxt1(){
    isHasErr1=false;
    bankCar = $(".bankCar").val();
    var bankExp = /\d{15}|\d{19}/;
    var getErrMsg = function() {
        if ($.trim(bankCar) == "") {
            isHasErr1 = true;
            return "请输入储蓄卡号";
        }
        if(!bankExp.test(bankCar)){
            isHasErr1 = true;
            return "请输入正确的储蓄卡号";
        }
        if ($.trim(bankCar).indexOf("<") >= 0) {
            isHasErr1 = true;
            return "储蓄卡格式有问题";
        }
    };
    var errMsg = getErrMsg();
    // 如果出现错误显示错误提示动画,否则隐藏错误提示
    return isHasErr1 ? alert(errMsg) : hideErrTip();
}
// 验证储存卡类型
function checkTxt3(){
    isHasErr3=false;
    bankType = $(".bankType").val();
    var banksExp = /^[\u4E00-\u9FA5]+$/g;
    var getErrMsg = function() {
        if ($.trim(bankType) == "") {
            isHasErr3 = true;
            return "请输入储存卡类型";
        }
        if(!banksExp.test(bankType)){
            isHasErr3 = true;
            return "请输入正确的储存卡类型";
        }
        if ($.trim(bankType).indexOf("<") >= 0) {
            isHasErr3 = true;
            return "储存卡类型格式有问题";
        }
    };
    var errMsg = getErrMsg();
    // 如果出现错误显示错误提示动画,否则隐藏错误提示
    return isHasErr3 ? alert(errMsg) : hideErrTip();
}
// 验证密码
function checkTxt4(){
    isHasErr4=false;
    bankPassword = $(".bankPassword").val();
    var banksExp = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,30}$/;
    var getErrMsg = function() {
        if ($.trim(bankPassword) == "") {
            isHasErr4 = true;
            return "请输入储存卡密码";
        }
        if(!banksExp.test(bankPassword)){
            isHasErr4 = true;
            return "密码由字母和数字组合";
        }
        if ($.trim(bankPassword).indexOf("<") >= 0) {
            isHasErr4 = true;
            return "储存卡密码格式有问题";
        }
    };
    var errMsg = getErrMsg();
    // 如果出现错误显示错误提示动画,否则隐藏错误提示
    return isHasErr4 ? alert(errMsg) : hideErrTip();
}
// 验证手机号码
function checkTxt2(txtObj) {
    isHasErr2=false;
    phone=$(".phone").val();
    var regExp = /^0?1[3|4|5|7|8][0-9]\d{8}$/;
    var getErrMsg = function() {
        if ($.trim(phone) == "") {
            isHasErr2 = true;
            return "请输入手机号码";
        }
        if(!regExp.test(phone)){
            isHasErr2 = true;
            return "请输入正确的手机号码";
        }
        if ($.trim(phone).indexOf("<") >= 0) {
            isHasErr2 = true;
            return "手机格式有问题";
        }
    };
    var errMsg = getErrMsg();
    // 如果出现错误显示错误提示动画,否则隐藏错误提示
    return isHasErr2 ? alert(errMsg) : hideErrTip();
}