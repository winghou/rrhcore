$(function(){
    var params = ns.getRequestParam();
    var token=params.token;
    var userId=params.userId;
	var data1={
	  		  "cmd": "adviceOrbackType",
	  		  "token": token,
	  		  "version": "1",
	  		  "userId":userId,
	  		  "params": {
	  			  
	  		  }
	  }
	var dataString=JSON.stringify(data1);
	showLoading();
	  $.ajax({
          url:commonUrl+"/adviceOrbackType",
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
           	var kind =res.detail.type;  	   
           	$(kind).each(function(i,obj){
           		var li=$("<li></li>").appendTo($(".typesofadvice"));
           		$("<a  href='javascript:;' selectid>"+obj.typeName+"</a>").appendTo(li).attr("selectid",obj.typeCode);
           	});
            $.divselect("#divselect","#inputselect");
              }else if(res.result==-1){
           	   window.location = encodeURI('*'+res.resultNote);
              }else{
           	   alert(res.resultNote);   
              }
          },
    });
});
function sub(){
	
	 var params = ns.getRequestParam();
	    var number = '^[0-9]*$' ;
	    var token=params.token;
	    var userId=params.userId;
	    var select =$(".kind").attr("kindId");
	    var content=$(".txtarea").val();
	    var qqtxt=$(".qqTxt").val();
	    if(select=="0" ){
	    alert("请选择意见类型");	
	    }else if(content==""){
	      alert("请输入您的反馈意见");
	    }else if(qqtxt!=="" && !qqtxt.match(number)){
	    	alert("请输入正确的qq号");
	    }else if(content.indexOf(">") !==-1||qqtxt.indexOf(">") !==-1){
	    	alert("请勿输入非法字符！")
	    }else{
	    	var data={
	    	  		  "cmd": "adviceOrback",
	    	  		  "token": token,
	    	  		  "version": "1",
	    	  		  "userId":userId,
	    	  		  "params": {
	    	  		    "userId": userId,     //用户id
	    	  		    "advice": content,    //意见内容
	    	  		    "type": select,      //意见类型
	    	  		    "contactWay": qqtxt   //联系方式
	    	  		  }
	    	  }
	    		var dataString=JSON.stringify(data);
	    		showLoading();
	    		  $.ajax({
	    	          url:commonUrl+"/adviceOrback",
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
	    	            	  $(".kind").attr("kindId","0");
	    	          	   $(".txtarea").val("");
	    	          	   $(".qqTxt").val("");
	    	          	    $(".kind").html("意见类型");
	    	           	   alert("提交成功！")
	    	              }else if(res.result==-1){
	    	           	   window.location = encodeURI('*'+res.resultNote);
	    	              }else{
	    	           	   alert(res.resultNote);   
	    	              }
	    	          },
	    	    });
	    }

}