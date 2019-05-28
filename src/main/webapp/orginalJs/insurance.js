var userId;
var token;
var couponList;
var couponType;
var couponId;
var insuranceType;
var source;
var moneyOfInsurance;
var loanPur
var data1Val; 
var data2Val;
var isHomePage;
//设备信息
var idfa;
var imei;
var ip;
var longitude;
var latitude;
var isIPhoneX;
$(function() {
    var params = ns.getRequestParam();
    userId = params.userId;
    token = params.token;
    couponList = params.couponList;
    couponType = params.couponType;
    couponId = params.couponId;
    loanPur=params.loanPur;
    moneyOfInsurance=params.moneyOfInsurance;
    insuranceType = params.insuranceType;
    idfa = params.idfa;
    imei = params.imei;
    data1Val = params.data1Val;
    data2Val = params.data2Val;
    isHomePage=params.isHomePage;
    ip = params.ip;
    longitude=params.longitude;
    latitude = params.latitude;
    isIPhoneX = params.isIPhoneX;
    source = params.source; //0:原生ios 1：H5 2:安卓
    var data = {
        "cmd": "getInsurance",
        "token": token,
        "userId": userId,
        "version": "1",
        "params": {
            "userId": userId
        }
    }
    var dataString = JSON.stringify(data);
    showLoading();
    $.ajax({
        url: commonUrl + "/getInsurance",
        type: 'post',
        dataType: 'json',
        contentType: 'application/json', //很重要，没有这个就走不了fastjson解析器
        timeout: 100000,
        data: "{\"key\":\"" + Encrypt(dataString) + "\"}",
        success: function(dd) {
            hideLoading();
            var res = JSON.parse(Decrypt(dd.key));
            console.log(res);
            if (res.result == 0) {
                $(".personName").text(res.detail.customName);
                var insurance = res.detail.appInsurancesList;
                $(insurance).each(function(i, obj) {
                    if (insuranceType == "" && i == 0) {
                        var insuranceList = $("<div class='insuranceList'></div>").attr("select", true).attr('insuranceType', obj.insuranceType).appendTo($(".insuranceLists"));
                        var personInf = $("<div class='personInf'></div>").appendTo(insuranceList);
                        $("<img src='../images/insurance-Checked.png'  class='insuranceImage'>").appendTo(personInf);
                    } else {
                        if (obj.insuranceType == insuranceType) {
                            var insuranceList = $("<div class='insuranceList'></div>").attr("select", true).attr('insuranceType', obj.insuranceType).appendTo($(".insuranceLists"));
                            var personInf = $("<div class='personInf'></div>").appendTo(insuranceList);
                            $("<img src='../images/insurance-Checked.png'  class='insuranceImage'>").appendTo(personInf);
                        } else {
                            var insuranceList = $("<div class='insuranceList'></div>").attr("select", false).attr('insuranceType', obj.insuranceType).appendTo($(".insuranceLists"));
                            var personInf = $("<div class='personInf'></div>").appendTo(insuranceList);
                            $("<img src='../images/insurance-No Checked.png'  class='insuranceImage'>").appendTo(personInf);
                        }
                    }
                    var insuranceName = $("<div class='insuranceName'>" + obj.insuranceName + "</div>").appendTo(personInf);
                    $("<img src='../images/insurance-question@2x.png' class='insurance-question'>").appendTo(insuranceName);
                    $("<div class='payfor'>" + "0元" + "</div>").appendTo(personInf);
                    var insuranceAccount = $("<div class='insuranceAccount'>" + obj.insuranceType + "</div>").appendTo(insuranceList);
                })
                if (isHomePage == 1) {
                	  $(".insuranceList").click(function() {
                          $(".insuranceImage").attr("src", '../images/insurance-No Checked.png');
                          $(".insuranceList").attr("select", false);
                          $(this).attr("select", true);
                          insuranceType = $(this).attr('insuranceType');
                          $(this).find("img.insuranceImage").attr("src", '../images/insurance-Checked.png');
                          couponList = '&couponType='+couponType+'&couponId='+couponId;
                          location.href = 'loanPage.html?userId=' + userId + "&token=" + token + "&insuranceType=" + insuranceType+"&idfa="+idfa+"&imei="+imei+"&ip="+ip+"&longitude="+longitude+"&latitude="+latitude+"&loanPur="+loanPur+"&data1Val="+data1Val+"&data2Val="+data2Val+"&isIPhoneX="+isIPhoneX+"&source="+source;   
                      })
                } else if (source == 0) {
                      $(".insuranceList").click(function() {
                        $(".insuranceImage").attr("src", '../images/insurance-No Checked.png');
                        $(".insuranceList").attr("select", false);
                        $(this).attr("select", true);
                        insuranceType = $(this).attr('insuranceType');
                        $(this).find("img.insuranceImage").attr("src", '../images/insurance-Checked.png');
                        window.webkit.messageHandlers.getInsuranceType.postMessage(insuranceType);
                    })
                }else if(source == 2){
                	  $(".insuranceList").click(function() {
                          $(".insuranceImage").attr("src", '../images/insurance-No Checked.png');
                          $(".insuranceList").attr("select", false);
                          $(this).attr("select", true);
                          insuranceType = $(this).attr('insuranceType');
                          $(this).find("img.insuranceImage").attr("src", '../images/insurance-Checked.png');
                          window.mochouhua.getInsuranceType(insuranceType);
                      })
                }
                  $(".payfor").text(moneyOfInsurance+"元")
            } else if (res.result == -1) {
                window.location = encodeURI('*' + res.resultNote);
            } else {
                alert(res.resultNote);
            }
        },
    });
})