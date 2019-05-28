﻿﻿var ns = {
	'version': '1.0',
	'base': '',
	'apiPath':'api.do'
};

/**
 * @基础组件封装
 */
(function() {
	ns.nameSpace = function(str) {
		var arr = str.split('.');
        var o = ns;
		for (var i = 0; i < arr.length; i++) {
            o[arr[i]] = o[arr[i]] || {};
            o = o[arr[i]];
		}
	};

    ns.getModule = function(str) {
		var arr = str.split('.');
		for (var i = 0; i < arr.length; i++) {
			if (ns['module'][arr[i]] != undefined) {
                return ns['module'][arr[i]];
			}
		}
        return null;
    };

    ns.ajaxRequest = function(cmdName, params, callBack) {
		var jsonString = JSON.stringify(new Cmd(cmdName, params));
		console.log(jsonString);
		$.ajax({
			url: ns.apiPath,
			timeout: 30000,
			type: "POST",
			dataType: "json",
			contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
			data: jsonString,
			success: callBack
		});
	};

	ns.ajaxSyncRequest = function(cmdName, parment, callBack) {
		var jsonString = JSON.stringify(new Cmd(cmdName, parment));
		console.log(jsonString);
		$.ajax({
			url: ns.apiPath,
			timeout: 10000,
			type: "POST",
			dataType: "json",
			contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
			data: jsonString,
			async: false,
			success: callBack
		});
	};

	ns.isEmpty = function(str) {
		if (str == undefined || str == "" || str == null) {
			return true;
		}
		return false;
	};

    /**
     * 生成noty提示
     * @param {string} type 可选值有 infomation warning error success
     * @param {string} text
     * @param {string} layout 类似topCenter
     */
    ns.genNoty = function(type, text, layout,callFunc){
    	var title = '';
    	switch(type){
    	case 'success':
    		title = '';
    		break;
    	case 'error':
    		title = '';
    		break;
    	case 'warning':
    		title = '';
    		break;
    	default:
    		title = '';
    	}
    	
        var n = noty({
            text        : '<strong>'+title+'   </strong>'+text,
            type        : type,
            dismissQueue: true,
            timeout     : 3000,
            layout      : layout == null ? 'topCenter' : layout,
            theme       : 'bootstrapTheme',
            closeWith   : ['button', 'click'],
            maxVisible  : 20,
            maxVisible: 1, // you can set max visible notification for dismissQueue true option,
            killer: true,
            callback:{
            	afterShow:callFunc
            }
        });
    };
    
    /**
     * 生成noty confirm提示
     * @param {string} type 可选值有 infomation warning error success
     * @param {string} text
     * @param {string} layout 类似topCenter
     */
    ns.genNotyConfirm = function(text, layout,callBack,cancelBack){
        var n = noty({
            text        : text,
            type        : "confirm",
            dismissQueue: true,
            buttons     : [
                             {addClass: 'btn btn-primary', text: '确认', onClick: function ($noty) {
                            	 callBack();
                            	 $noty.close();
                             }
                             },
                             {addClass: 'btn btn-danger', text: '取消', onClick: function ($noty) {
                            	 if(typeof cancelBack != "undefined"){
                            		 cancelBack();
                            	 }
                                 $noty.close();}
                             }
                          ],
        
            timeout     : 2500,
            layout      : layout == null ? 'topCenter' : layout,
            theme       : 'bootstrapTheme',
            closeWith   : ['button', 'click'],
            maxVisible  : 20,
            maxVisible: 1, // you can set max visible notification for dismissQueue true option,
            killer: true,
            modal:true
        });
    };

	/**
     * 验证非法字符
     * @param {string}
     * @return {boolean}, 返回false表示含有非法字符
     */
	ns.checkStrValid = function(checkStr) {
		if(typeof checkStr == "undefined"){
			return true;
		}
		var str = ["!", "#", "$", "%", "/", "\\", "'", "*", "&", "|"];
		for (var i = 0; i < str.length; i++) {
			if (checkStr.indexOf(str[i]) >= 0) {
				return false;
			}
		}
		return true;
	};

	/**
     * 验证车牌号是否正确
     * @param {string}
     * @returns {boolean}
     */
	ns.isCarNo = function(str) {
		var no = $.trim(str);
		if (no == "") {
			return true;
		}
		var patrn = /^[(\u4e00-\u9fa5)|(a-zA-Z)]{1}[a-zA-Z]{1}[a-zA-Z_0-9]{4,6}[a-zA-Z_0-9_\u4e00-\u9fa5]$/;
		if (!patrn.exec(no)) {
			return false;
		}
		return true;
	};
	
	/**
	 * 验证Email是否正确
	 * 
	 * @param strEmail
	 * @returns {Boolean}
	 */
	ns.isEmail = function (strEmail) {
		var pattern = /^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
		if (strEmail == ''){
			return true;
		}
		return pattern.test(strEmail);
	};
	/**
	 * 验证 0 或 正整数
	 */
	ns.isPositiveInteger = function(value){
		var re=/^(([1-9]{1}\d*)|([0]{1}))$/;
		return re.test(value);
	};
	
    
	/***
	 * 正整数验证
	 ***/
	ns.checkIntegerValid = function (checkStr){
		var reg = /^[0-9]*[1-9][0-9]*$/;
		return reg.test(checkStr);
	};
	
    /** 
     * 生成格式化的排量显示字符串
     * @param {string} str
     * @return {string}
     */ 
    ns.formatDisplacement = function(str) 
    {
        var reg =  /^([1-9]\d|\d).\dT$/ ;
        if (!reg.test(str)) {
            return str + "L";
        }
        return str;
    };

    /**
     * 判断日期区间的合法性，若提交间隔限制，可进行限制判断
     * @param {string} startTime
     * @param {string} endTime
     * @param {string} dateLimit
     * @returns {boolean}   
     */
    ns.checkPeriodDateValid = function(startTime,endTime,dateLimit) {
    	var startTimeArray = startTime.split("-");
    	var endTimeArray = endTime.split("-");
    	startTime = startTimeArray.join("/");
    	endTime = endTimeArray.join("/");
    	
    	var startDate = new Date(startTime);
    	var endDate = new Date(endTime);
    	
    	if (endDate.getTime() - startDate.getTime() < 0) {
    		ns.alert("结束时间不能小于开始时间", "提示");
    		return false;
    	}

    	if(typeof dateLimit != "undefined" ){
    		if ((endDate.getTime() - startDate.getTime() +1) / (24 * 60 * 60 * 1000) > dateLimit) {
        		ns.alert("查询时间段不能超过"+dateLimit+"天", "提示");
        		return false;
        	}
    	}
    	
    	
    	return true;
    };
    
    ns.checkPeriodDateValidV2 = function(startTime,endTime,dateLimit) {
    	var startTimeArray = startTime.split("-");
    	var endTimeArray = endTime.split("-");
    	startTime = startTimeArray.join("/");
    	endTime = endTimeArray.join("/");
    	
    	var startDate = new Date(startTime);
    	var endDate = new Date(endTime);
    	

    	if (endDate.getTime() - startDate.getTime() < 0) {
    		return false;
    	}
    	
    	if(typeof dateLimit != "undefined" ){
    		if ((endDate.getTime() - startDate.getTime() +1) / (24 * 60 * 60 * 1000) > dateLimit) {
        		return false;
        	}
    	}
    	
    	
    	return true;
    };
    
    /**
     * 比较日期大小
     * @param {string} startTime
     * @param {string} endTime
     * @returns {boolean}   
     */
    ns.compareDate = function(startTime,endTime) {
    	var startTimeArray = startTime.split("-");
    	var endTimeArray = endTime.split("-");
    	startTime = startTimeArray.join("/");
    	endTime = endTimeArray.join("/");
    	
    	var startDate = new Date(startTime);
    	var endDate = new Date(endTime);
    	
    	if (endDate.getTime() - startDate.getTime() < 0) {
    		return false;
    	}else{
    		return true;
    	}
    };
    
    /**
     * 获取指定日期距当前日期天数 （指定日期大于当前日期）
     * @param {string} startTime
     * @param {string} endTime
     * @returns {boolean}   
     */
    ns.getDaysFromToday = function(dateStr) {
    	var dateStrArray = dateStr.split("-");
    	dateStr = dateStrArray.join("/");
    	
    	var dateStrMilli = new Date(dateStr).getTime();
    	var todayMilli = new Date().getTime();
    	
    	return Math.floor((dateStrMilli-todayMilli)/(3600*1000*24));
    };
    
    /**
     * 获取指定日期
     * @param {string} date
     * @param {int} step
     * @returns {string}   
     */
    ns.getAssignDate = function(date,step) {
    	var dateArray = date.split("-");
    	dateTime = dateArray.join("/");
    	
    	var newDate = new Date(dateTime);
    	
    	//获取step天后的日期，若为负数，可获取过去的日期
    	newDate.setDate(newDate.getDate()+step);
    	
    	var y = newDate.getFullYear();
    	var m = newDate.getMonth()+1;//获取当前月份的日期
    	var d = newDate.getDate();
    	
    	if(parseInt(m) < 10){
    		 m = "0"+m;
    	}
    	
    	if(parseInt(d) < 10){
    		d = "0"+d;
    	}
    	
    	return y+"-"+m+"-"+d;
    };
    
    /**
     * 根据月份步长获取指定日期
     * @param {string} date
     * @param {int} step
     * @returns {string}   
     */
    ns.getAssignDateByMonth = function(date,step) {
    	var dateArray = date.split("-");
    	dateTime = dateArray.join("/");
    	
    	var newDate = new Date(dateTime);
    	
    	//获取step天后的日期，若为负数，可获取过去的日期
    	newDate.setMonth(newDate.getMonth()+step);
    	
    	var y = newDate.getFullYear();
    	var m = newDate.getMonth()+1;//获取当前月份的日期
    	var d = newDate.getDate();
    	
    	if(parseInt(m) < 10){
    		 m = "0"+m;
    	}
    	
    	if(parseInt(d) < 10){
    		d = "0"+d;
    	}
    	
    	return y+"-"+m+"-"+d;
    };
    
    /**
     * 根据月份步长获取指定日期
     * @param {string} date
     * @param {int} step
     * @returns {string}   
     */
    ns.getAssignTime = function(datetime,seconds) {
    	var dateArray = datetime.split("-");
    	dateTime = dateArray.join("/");
    	
    	var newDateTime = new Date(dateTime).getTime()+seconds*1000;
    	
    	return ns.dateFormat(new Date(newDateTime),"yyyy-MM-dd hh:mm:ss");
    };
    
    /**
     * 获取今天日期
     * @returns {string}   
     */
    ns.getToday = function(){
    	var nowDate = new Date();
    	var y = nowDate.getFullYear();
    	var m = nowDate.getMonth()+1;
    	var d = nowDate.getDate();
    	
    	if(parseInt(m) < 10){
   		 	m = "0"+m;
    	}
   	
	   	if(parseInt(d) < 10){
	   		d = "0"+d;
	   	}
   	
	   	return y+"-"+m+"-"+d;
    };
    
    /**
     * 获取当前月份
     * @returns {string}   
     */
    ns.getMonth = function(){
    	var nowDate = new Date();
    	var y = nowDate.getFullYear();
    	var m = nowDate.getMonth()+1;
    	if(parseInt(m) < 10){
   		 	m = "0"+m;
    	}
   	
	   	
	   	return y+"-"+m;
    };
    
    /**
     * 获取时间数组
     * @returns {Array}
     */
    ns.getTimeArray = function(time) {
    	var timeArray = time.split(" ");
    	var dateArray = timeArray[0].split("-");
    	var hmsArray = timeArray[1].split(":");
    	var detailArray = [];
    	
    	detailArray["year"] = dateArray[0];
    	detailArray["month"] = dateArray[1];
    	detailArray["day"] = dateArray[2];
    	detailArray["hour"] = hmsArray[0];
    	detailArray["minutes"] = hmsArray[1];
    	detailArray["seconds"] = hmsArray[2];
    	
    	return detailArray;
    	
    };
    
    /**
     * 获取该月份的天数
     * @returns {num}   
     */
    ns.getMonthDays = function(myMonth){
    	var now = new Date(); 
    	var nowYear = now.getYear(); 
        var monthStartDate = new Date(nowYear, myMonth, 1);      
        var monthEndDate = new Date(nowYear, myMonth + 1, 1);      
        var   days   =   (monthEndDate   -   monthStartDate)/(1000   *   60   *   60   *   24);      
        return   days;      
    };
    
    /**
     * 获取该月份的开始时间
     * @returns {String}   
     */
    ns.getLastMonthStartDate = function(){  
    	var now = new Date(); 
    	var nowYear = now.getYear();             //当前年     
    	nowYear += (nowYear < 2000) ? 1900 : 0;  //    
    	      
    	var lastMonthDate = new Date();  //上月日期  
    	lastMonthDate.setDate(1);  
    	lastMonthDate.setMonth(lastMonthDate.getMonth()-1);  
    	var lastMonth = lastMonthDate.getMonth();  
    	 
        var lastMonthStartDate = new Date(nowYear, lastMonth, 1);  
        return ns.dateFormat(lastMonthStartDate,"yyyy-MM-dd");    
    };  
      
    /**
     * 获取该月份的结束时间
     * @returns {String}   
     */
    ns.getLastMonthEndDate = function(){ 
    	var now = new Date(); 
    	var nowYear = now.getYear();             //当前年     
    	nowYear += (nowYear < 2000) ? 1900 : 0;  //    
    	      
    	var lastMonthDate = new Date();  //上月日期  
    	lastMonthDate.setDate(1);  
    	lastMonthDate.setMonth(lastMonthDate.getMonth()-1);  
    	var lastMonth = lastMonthDate.getMonth();  
    	 
        var lastMonthEndDate = new Date(nowYear, lastMonth, ns.getMonthDays(lastMonth));  
        return ns.dateFormat(lastMonthEndDate,"yyyy-MM-dd");    
    };
    
    
    /**
     * 检查是否未定义，如未定义则返回空
     * @param {string} obj
     * @returns {string}   
     */
    ns.checkUndefined = function (obj){
    	return (typeof obj == "undefined" ? "" : obj);
    };
    
    /**
     * 回车搜索函数
     * @param {string} selectStr
     * @param {string} callback
     * @returns {void}   
     */
    ns.enterSubmit = function (selectStr, callBack){
    	$(selectStr).bind('keyup',function(e){
    		if(e.keyCode == 13){
    			callBack();
    			return false;
    		}
    	 });
    };
    
    /**
     * @验证手机号是否正确
     * @param {String} str
     * @returns {Boolean}
     */
    ns.isMobel = function (str) {
    	if ($.trim(str) == '')
    		return;
    	var myreg = /^1\d{10}$/;
    	if (!myreg.test(str)) {
    		return false;
    	} else {
    		return true;
    	}
    };
    
    /**
     * @验证电话号码
     * @param {String} checkStr
     * @returns {Boolean}
     */
    ns.checkLinkPhoneValid = function(checkStr){
    	var tel=/^(\(\d{3,4}\)|\d{3,4}\-{0,1})?\d{7,8}(\-\d{1,5})?$/;
    	var mobile = /^1[3|4|5|8][0-9]\d{4,8}$/;
    	
    	var test1 = tel.test(checkStr);
    	var test2 = mobile.test(checkStr);

    	return (test1 || test2);
    };
    
    /**
    * 电话验证(包含400及95555等特殊电话号码)
    * @param String checkStr
    * @return Boolean
    * @author fancy
    */
    ns.checkPhoneValid = function(checkStr) {
        var reg = /^(\d{3,4}\-?\d{7,8}|1\d{10}|9\d{4}|\d{3}\-?\d{3}\-?\d{4})$/;
        if(reg.test(checkStr))
        {
            return true;
        }
        return false;
    }; 
    
    /***
     * @价格验证
     * @param {string} price
     * @returns {BOOLEAN}
     * @author *** 
     */
    ns.checkPriceValid = function(price){
    	var reg = /(^[1-9]{1}\d{0,6}$)|(^[1-9]{1}\d{0,6}\.{1}\d{1,2}[0]*$)|(^[0]{1}\.{1}\d{0,1}[1-9]{1}[0]*$)/;
		if(!reg.test(price)){
			return false;
		}else{
			return true;
		}
    };
    
    ns.checkPriceValidV2 = function(price){
    	var reg = /(^[1-9]{1}\d{0,6}$)|(^[1-9]{1}\d{0,6}\.{1}\d{0,1}[0]*$)|(^[0]{1}\.{1}\d{0,1}[1-9]{1}[0]*$)/;
		if(!reg.test(price)){
			return false;
		}else{
			return true;
		}
    };
    
    /**
     * 克隆一个对象
     */
    ns.clone = function(obj){
    	var objClone;
        if (obj.constructor == Object){
            objClone = new obj.constructor(); 
        }else{
            objClone = new obj.constructor(obj.valueOf()); 
        }
        for(var key in obj){
            if ( objClone[key] != obj[key] ){ 
                if ( typeof(obj[key]) == 'object' ){ 
                    objClone[key] = ns.clone(obj[key]);
                }else{
                    objClone[key] = obj[key];
                }
            }
        }
        objClone.toString = obj.toString;
        objClone.valueOf = obj.valueOf;
        return objClone; 
    };
  
	/**
     * 数组去重
     * author: wengq
     * 
     */
    ns.arrUnique = function(arr){
    	var n = {},r=[]; //n为hash表，r为临时数组
    	for(var i = 0; i < arr.length; i++) //遍历当前数组
    	{
    		if (!n[arr[i]]) //如果hash表中没有当前项
    		{
    			n[arr[i]] = true; //存入hash表
    			r.push(arr[i]); //把当前数组的当前项push到临时数组里面
    		}
    	}
    	return r;
    };
    
    /**
     * 格式化日期
     */
    ns.dateFormat = function(date, fmt){
    	// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
    	// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
    	// 例子： 
    	// ns.dateFormat(new Date(), "yyyy-MM-dd hh:mm:ss.S") ==> 2014-01-06 08:09:04.423 
    	// ns.dateFormat(new Date(), "yyyy/M/d") ==> 2014/1/6 
    	 var o = {
 	        "M+": date.getMonth() + 1, //月份 
 	        "d+": date.getDate(), //日 
 	        "h+": date.getHours(), //小时 
 	        "m+": date.getMinutes(), //分 
 	        "s+": date.getSeconds(), //秒 
 	        "q+": Math.floor((date.getMonth() + 3) / 3), //季度 
 	        "S": date.getMilliseconds() //毫秒 
 	    };
 	    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
 	    for (var k in o)
 	    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
 	    return fmt;
    };
	
	/**
	 * 省份选取通用UI
	 * @param {jqueryObj} province 省份select
	 */
	ns.genProvinceList = function(province){
		var provinceList = ns.dict.getProvinceList();
		if(ns.isEmpty(provinceList)){
			return;
		}
		var optionStr = '<option value="">-请选择-</option>';
		for(var i=0; i< provinceList.length; i++){
			optionStr += '<option value="' + provinceList[i].provinceId+'">' + provinceList[i].provinceName+'</option>';
		}
		$(province).html(optionStr);
	};
	
	/**
	 * 根据选择的省份，联动获取城市数据
	 * @param {jqueryObj} province 省份select
	 * @param {jqueryObj} city 城市select
	 */
	ns.changeProvince = function(province, city){
		var provinceId = $(province).val();
		var selectHtml = '';
		if(ns.isEmpty(provinceId)){
			selectHtml = '<option value="">-请选择-</option>';
			$(city).html(selectHtml);
			return;
		}
		var cityList = ns.dict.getCityList(provinceId);
		if(ns.isEmpty(cityList)){
			selectHtml = '<option value="">-请选择-</option>';
			$(city).html(selectHtml);
			return;
		}
		for(var i = 0; i < cityList.length; i++){
			selectHtml += '<option value="' + cityList[i].cityId + '">' + cityList[i].cityName + '</option>';
		}
		$(city).html(selectHtml);
	};
	
	
	/**
	 * checkbox全选和取消全选操作
	 */
	ns.checkAll = function(wrap1, wrap2){
		$(document).on('click',wrap1,function(){
			if($(wrap1).length == $(wrap1 + ":checked").length){
				$(wrap2).attr("checked","checked");
			}else{
				$(wrap2).removeAttr("checked");
			}
		});
		$(document).on('click',wrap2,function(){
			if($(wrap2).length != 0 && $(wrap2).length == $(wrap2 + ":checked").length){
				$(wrap1).attr("checked","checked");
			}else{
				$(wrap1).removeAttr("checked");
			}
		});
	};
	
	/**
	 * @获取url参数方法
	 * @returns {jsonObj}
	 */
	ns.getRequestParam = function() {
		var url = location.search;//获取url中"?"符后的字串
		var theRequest = new Object();

		if (url.indexOf("?") != -1) {
		    var str = url.substr(1);
		    strs = str.split("&");
		    for(var i = 0; i < strs.length; i ++) {
		       theRequest[strs[i].split("=")[0]]=unescape(strs[i].split("=")[1]);
		    }
		}
		return theRequest;
	};
	function Cmd(cmd, params) {
		this.cmd = cmd;
		this.token = ns.token;
		this.version = 1;
		if(params != null){
			this.pageNo = params.pageNo;
			this.onePageNum = params.onePageNum;
			this.params = params;
		}
	}
	
})();
