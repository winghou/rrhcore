   var nonceStr, timestamp, signature,linkurl,imgUrl,appId;
		linkurl=window.location.href;
	   $(document).ready(function() {       
		   var data={
				   "cmd": "wxShare",
				   "token": "",
				   "userId": "",
				   "version": "1",
				   "params": {
					   'url': location.href.split('#')[0]
					
				   }
				 }
			var dataString=JSON.stringify(data);
			$.ajax({
			    url:commonUrl+"/wxShare",
			    type: 'post',
			    dataType:'json',
			    contentType:'application/json', //很重要，没有这个就走不了fastjson解析器
			    timeout: 100000,
			    data: "{\"key\":\""+Encrypt(dataString)+"\"}",
			    success: function(dd){
			 	   hideLoading();
			        var res=JSON.parse(Decrypt(dd.key));
//			        console.log(res);
			        if(res.result==0){
			        	    nonceStr = res.detail.nonceStr;
			                signature = res.detail.signature;
			                timestamp = res.detail.timestamp;
			                appId=res.detail.appId;
			                wx.config({
			   		         debug: false,
			   		         appId: appId,
			   		         timestamp: timestamp,
			   		         nonceStr: nonceStr,
			   		         signature: signature,
			   		         jsApiList: ['onMenuShareQZone', 'onMenuShareWeibo','onMenuShareQQ','onMenuShareAppMessage','onMenuShareTimeline']
			   		     });
			        }else{
			     	   alert(res.resultNote);   
			        }
			    },
			});
//	        $.ajax({
//	            type: "POST",
//	            async: false,
//	            url: "/wxShare",
//	            dataType: "json",
//	            timeout: 2000,
//	            data: {
//	                'url': location.href.split('#')[0]
//	            },
//	            success: function(res) {
//	                nonceStr = res.nonceStr;
//	                signature = res.signature;
//	                timestamp = res.timestamp;
//	                //$("#iswxllq").html(signature);
//	            },
//	            error: function(msg) {}
//	        });
		
	   }); 
		   wx.ready(function () { 
// 				linkImg=window.location.host;
// 		        imgUrl= 'http://' +linkImg + '/'+ $('[shareImg]').attr('src');			    
			     var curWwwPath=window.document.location.href;			     
			     var pathName=window.document.location.pathname;
			     var pos=curWwwPath.indexOf(pathName);			     
			     var localhostPaht=curWwwPath.substring(0,pos);			    
			     var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);			    
			    
			     var realPath=localhostPaht+projectName;
			 
		        imgUrl= realPath+"/html/images/loverActivityShare.png";
			   wx.onMenuShareTimeline({
				    title: '@送你一份羞羞de情人节礼物', // 分享标题
				    desc: '想送她更好的？上随心花，极速放款！还有甜蜜好礼+提额相送~', // 分享标题
				    link: linkurl, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
				    imgUrl: imgUrl, // 分享图标
				    success: function () { 
				        // 用户确认分享后执行的回调函数
				    },
				    cancel: function () { 
				        // 用户取消分享后执行的回调函数
				    }
				});
			   wx.onMenuShareAppMessage({
				    title: '@送你一份羞羞de情人节礼物', // 分享标题
				    desc: '想送她更好的？上随心花，极速放款！还有甜蜜好礼+提额相送~', // 分享描述
				    link: linkurl, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
				    imgUrl: imgUrl, // 分享图标
				    type: '', // 分享类型,music、video或link，不填默认为link
				    dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
				    success: function () { 
				        // 用户确认分享后执行的回调函数
				    },
				    cancel: function () { 
				        // 用户取消分享后执行的回调函数
				    }
				});
			   wx.onMenuShareQQ({
				    title: '@送你一份羞羞de情人节礼物', // 分享标题
				    desc: '想送她更好的？上随心花，极速放款！还有甜蜜好礼+提额相送~', // 分享描述
				    link: linkurl, // 分享链接
				    imgUrl: imgUrl, // 分享图标
				    success: function () { 
				       alert("谢谢分享");
				    },
				    cancel: function () { 
				       // 用户取消分享后执行的回调函数
				    }
				});   
			   wx.onMenuShareQZone({
				    title: '@送你一份羞羞de情人节礼物', // 分享标题
				    desc: '想送她更好的？上随心花，极速放款！还有甜蜜好礼+提额相送~', // 分享描述
				    link: linkurl, // 分享链接
				    imgUrl: imgUrl, // 分享图标
				    success: function () { 
				       // 用户确认分享后执行的回调函数
				    },
				    cancel: function () { 
				        // 用户取消分享后执行的回调函数
				    }
				});
			   wx.onMenuShareWeibo({
				    title: '@送你一份羞羞de情人节礼物', // 分享标题
				    desc: '想送她更好的？上随心花，极速放款！还有甜蜜好礼+提额相送~', // 分享描述
				    link: linkurl, // 分享链接
				    imgUrl: imgUrl, // 分享图标
				    success: function () { 
				       // 用户确认分享后执行的回调函数
				    },
				    cancel: function () { 
				        // 用户取消分享后执行的回调函数
				    }
				});   
			        
			   });  