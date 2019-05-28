var userId;
var token;
var source;
$(function(){
	 var params = ns.getRequestParam();
	    userId = params.userId;
	    token = params.token;
	    source=params.source;
		updateList(); 
	//板块切换
	    $(".firstActive").click(function(){
	    	if($(".middle").hasClass("hide")){
	    		$(".secondPanelActive").addClass("hide");
	    		$(".middle").removeClass("hide");
	    	    $(".firstActive").css("color","#f9ca00");
	    	    $(".secondActive").css("color","#965428");
	    	    $("html").css("background"," #714a39")
	    	}
	    })
	        $(".secondActive").click(function(){
	    	if($(".secondPanelActive").hasClass("hide")){
	    		$(".secondPanelActive").removeClass("hide");
	    		$(".middle").addClass("hide");
	    	    $(".firstActive").css("color","#965428");
	    	    $(".secondActive").css("color","#f9ca00");
	    	    $("html").css("background"," #f7f7f7")
	    	}
	    })
	    //活动规则
	    $(".ruleImages,.secondActiverules").click(function(){
	    	if($(window).height()==$(document).height()){
	    		$(".mask").css("height", "");	
	    	}else{
	        $(".mask").css("height", $(document).height());
	    	}
	    	$(".mask").removeClass("hide");
	    })
	    //取消遮罩
	    $(".maskClose").click(function(){
	    	$(".mask").addClass("hide");
	    })
	    //交互
	    $(".sureBtnTitleImage").click(function(){
	     location.href ="http://api.xiaoyuedai.com/register1.html?HD-180201";
	    })
})

function updateList(){
	$(".datatable").html("");
	var data={
			  "cmd": "loverActivity",
			  "token": token,
			  "userId": userId,
			  "version": "1",
			  "params": {
			    "userId": userId,
			    "start_time": "",
			    "end_time": ""
			  }
			}
	var dataString=JSON.stringify(data);
	showLoading();
	$.ajax({
	    url:commonUrl+"/loverActivity",
	    type: 'post',
	    dataType:'json',
	    contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
	    timeout: 100000,
	    data: "{\"key\":\""+Encrypt(dataString)+"\"}",
	    success: function(dd){
	 	   hideLoading();
	        var res=JSON.parse(Decrypt(dd.key));
//	        console.log(res);
	        if(res.result==0){
	          var dataLists=res.detail.list;
	        var tr=  $("<tr></tr>").appendTo(".datatable");
	        $("<td class='headName'>"+"名次"+"<td>").appendTo(tr);
	        $("<td class='headName'>"+"用户手机号"+"<td>").appendTo(tr);
	        $("<td class='headName'>"+"借款总额"+"<td>").appendTo(tr);
	          $(dataLists).each(function(i,obj){
	        	  var account=(obj.item_code).substring(0,3)+"*****"+(obj.item_code).substring((obj.item_code).length-4);
	        	  var tr=  $("<tr></tr>").appendTo(".datatable");
	        	  $("<td>"+(i+1)+"<td>").appendTo(tr);
	  	        $("<td>"+account+"<td>").appendTo(tr);
	  	        $("<td>"+obj.borrow_amt+"<td>").appendTo(tr);
	          })
	          if(res.detail.borrow_amt){
	        	  $(".accountLine1").text("您目前的借款总额："+res.detail.borrow_amt+"元")  
	          }
	          if(res.detail.rank){
	        	  $(".accountLine2").text("您的即时排名：第"+res.detail.rank+"名")  
	          }
	        }else if(res.result==-1){
	        	  var dataLists=res.detail.list;
	  	        var tr=  $("<tr></tr>").appendTo(".datatable");
	  	        $("<td class='headName'>"+"名次"+"<td>").appendTo(tr);
	  	        $("<td class='headName'>"+"用户手机号"+"<td>").appendTo(tr);
	  	        $("<td class='headName'>"+"借款总额"+"<td>").appendTo(tr);
	  	          $(dataLists).each(function(i,obj){
	  	        	  var account=(obj.item_code).substring(0,3)+"*****"+(obj.item_code).substring((obj.item_code).length-4);
	  	        	  var tr=  $("<tr></tr>").appendTo(".datatable");
	  	        	  $("<td>"+(i+1)+"<td>").appendTo(tr);
	  	  	        $("<td>"+account+"<td>").appendTo(tr);
	  	  	        $("<td>"+obj.borrow_amt+"<td>").appendTo(tr);
	  	          })
	        }else{
	     	   alert(res.resultNote);   
	        }
	    },
	});
	  setTimeout('updateList()', 600000);

}