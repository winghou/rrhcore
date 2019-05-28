var page_now = 1;
var page_allnum = 1;
var page_number = 10;
var DivId;
var ChangePageNumFunction;
function ShowoPage(PrevFont, NextFont, PageNumFont1, PageNumFont2,
		PageNumFont3, PageNumFont4, RecCount, RecPerPage, PageNum, CurrPage,
		DivId, ChangePageNumFunction) {

	/***************************************************************************
	 * 调用 eg: ShowoPage("上一页","下一页","<font color='red'>","</font>","[<font
	 * color='red'>","</font>]",allcount,10,4,page_now,"page_num");
	 * 
	 * PrevFont 上一页标志 NextFont 下一页标志
	 * 
	 * PageNumFont1,PageNumFont2 非当前页数字的样式 PageNumFont3,PageNumFont4 当前页数字的样式
	 * 
	 * RecCount 记录总数 RecPerPage 每页记录个数 PageNum 当前页前后延伸显示4个 CurrPage 当前的页数 DivId
	 * 显示页数信息DOM的Id ChangePageNumFunction
	 * 
	 * //PageCount 一共有几页
	 **************************************************************************/
	this.DivId = DivId;
	this.ChangePageNumFunction = ChangePageNumFunction;
	if (RecCount % RecPerPage == 0) {// 判断一共有几页,如果是0,可以取整.如果不为0,不能取整.
		PageCount = RecCount / RecPerPage;
	} else {
		PageCount = (parseInt(RecCount / RecPerPage) + 1);
	}

	if (PageCount <= 1) {// 一共的页数小于等于1时,一共只有一页.PageCount=1;总页数为1
		PageCount = 1;
	}

	prevpage = parseInt(CurrPage - 1);
	if (prevpage < 1) {
		prevpage = 1;
	}
	nextpage = parseInt(CurrPage + 1);
	if (nextpage > PageCount) {
		nextpage = PageCount;
	}

	if (CurrPage <= 1 && PageCount == 1) {

		CurrPage = 1;
		PrevPageUrl = "&nbsp;" + PrevFont + "&nbsp;";
		NextPageUrl = "&nbsp;" + NextFont + "&nbsp;";
	} else if (CurrPage <= 1) {
		CurrPage = 1;
		PrevPageUrl = "&nbsp;" + PrevFont + "&nbsp;";
		NextPageUrl = "&nbsp;<A href=\"javascript:nextPageNum()\">"
				+ NextFont + "</A>&nbsp;";
	} else if (CurrPage >= PageCount) {
		CurrPage = PageCount;
		PrevPageUrl = "&nbsp;<A href=\"javascript:lastPageNum()\">"
				+ PrevFont + "</A>&nbsp;";
		NextPageUrl = "&nbsp;" + NextFont + "&nbsp;";
	} else {
		PrevPageUrl = "&nbsp;<A href=\"javascript:lastPageNum()\">"
				+ PrevFont + "</A>&nbsp;";
		NextPageUrl = "&nbsp;<A href=\"javascript:nextPageNum()\">"
				+ NextFont + "</A>&nbsp;";
	}

	PageStart = CurrPage - PageNum <= 1 ? 1
			: (CurrPage - PageNum - (CurrPage > PageCount - PageNum ? PageNum
					+ CurrPage - PageCount : 0));
	PageEnd = CurrPage + PageNum >= PageCount ? PageCount
			: (CurrPage + PageNum + (CurrPage - 1 < PageNum ? PageNum
					- CurrPage + 1 : 0));
	var html = new Array();
	html.push("<div  id=\"page\" style=\" text-indent:10px;\">");

	if (PageStart < 1)
		PageStart = 1;
	if (PageNum * 2 + 1 > PageCount)
		PageEnd = PageCount;

	// document.write (PrevPageUrl);
	html.push(PrevPageUrl);
	for ( var i = PageStart; i <= PageEnd; i++) {
		if (i == CurrPage) {
			// document.write ("&nbsp;"+PageNumFont3+i+PageNumFont4+"&nbsp;");
			html.push("&nbsp;" + PageNumFont3 + i + PageNumFont4 + "&nbsp;");
		} else {
			// document.write ("&nbsp;<A
			// href=\""+url+"Page="+i+"\">"+PageNumFont1+i+PageNumFont2+"</A>&nbsp;");
			html.push("&nbsp;<A href=\"javascript:pageNum(" + i
					+ ")\">" + PageNumFont1 + i
					+ PageNumFont2 + "</A>&nbsp;");
		}
	}
	// document.write (NextPageUrl);
	html.push(NextPageUrl);
	html.push("</div><div class=\"cc\"></div>");

	return html.join("");
}
function pageNum(num) {// 分页
	page_now = num;
	jQuery("#" + DivId + ">table").hide();
	eval(ChangePageNumFunction + "(" + (num - 1) + ")");
}

function nextPageNum() {
	if (page_now < 2) {
		pageNum(2);
	} else {
		if (page_now < PageCount) {
			pageNum(page_now + 1);
		}
	}
}

function lastPageNum() {
	if (page_now > 0) {
		pageNum(page_now - 1);
	}
}
