jQuery.divselect = function(divselectid,inputselectid) {
    var inputselect = $(inputselectid);
    $(divselectid+" cite").click(function(){
        var ul = $(divselectid+" ul");
        if(ul.css("display")=="none"){
            ul.slideDown("fast");
        }else{
            ul.slideUp("fast");
        }
    });
    $(".content, .QQ, .surebtn").click(function(){
    	 var ul = $(divselectid+" ul");
    	 ul.slideUp("fast");
    })
    $(divselectid+" ul li a").click(function(){
        var txt = $(this).text();
        $(divselectid+" cite").html(txt);
        var value = $(this).attr("selectid");
        $(".kind").attr("kindId",value);
        inputselect.val(value);
        $(divselectid+" ul").hide();
        
    });
};