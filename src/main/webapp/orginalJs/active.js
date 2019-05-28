var toke;
var userId;
var chance=0;
$(function(){
	var params = ns.getRequestParam();
	userId=params.userId;
	token=params.token;
	$(".btn2Images").click(function(){
	$(".myActive").addClass("hide");
	$(".myinvite").removeClass("hide");
	})
	$(".btn1Images").click(function(){
		$(".myinvite").addClass("hide");
		$(".myActive").removeClass("hide");
	})
  
	 var clipboard = new Clipboard('.copyBtn');   
	 clipboard.on('success', function(e) {
	 alert("复制成功");
	 e.clearSelection();  
	 });   
	 clipboard.on('error', function(e) {   
	alert("复制失败请手动输入")  
	 }); 
	 
	 var data={
			 "cmd": "queryUserRewardAmt",           //请求的命令字
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
         url:commonUrl+"/queryUserRewardAmt",
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
            	 $(".copycontent").text(res.detail.inviteCode);
            	 $(".invitePanelImages").click(function(){
            		 window.location = encodeURI('@'+res.detail.inviteCode);
            	   })
            	 if(res.detail.inviteFriends.length==0){
            		 $(".juage").html("");
            		 $(".Panel2").addClass("hide");
            		 $("<div class='invitenone'>暂无邀请！</div>").appendTo($(".juage"));
            		 $("<div class='invitenonetip'>亲，快来邀请好友吧</div>").appendTo($(".juage"));
            	 }else{
                 	$(".firstNum").text("邀请成功"+res.detail.loanUserCount+"位");
                     $(".rewardAmt").text(res.detail.rewardAmt);
                     $(".overplus").text("红包剩余"+res.detail.canWithdrawAmt+"元");
                     $(".unknowmomey").text(res.detail.remainingIncreasedCredit)
                     $(".word2").text("已获得"+res.detail.totalIncreasedCredit+"元额度");
                     $(".drawbtn").text("提额抽奖（"+res.detail.lotteryNum+"次）");
                     chance=res.detail.lotteryNum;
                     var list=res.detail.inviteFriends;
                     $(list).each(function(i,obj){
                     	var tr=$("<tr></tr>").appendTo($(".list"));
                     	$("<td>"+ obj.invitedUserPhone+"</td>").appendTo(tr);
                     	$("<td>"+ obj.invitedUserStatus+"</td>").appendTo(tr);
                     }) 
                     
            	 }
          
             }else if(res.result==-1){
          	   window.location = encodeURI('*'+res.resultNote);
             }else{
          	   alert(res.resultNote);   
             }
        	 }
         })
         	$(".drawbtn").click(function(){
		if(chance==0){
			alert("您还没有抽奖机会！")
		}else{
			window.location="Luckdraw.html?token="+token +"&userId=" + userId
		}
	})
	  	$(".getReal").click(function(){
            window.location = encodeURI("getReal");	
           })
})