package com.bestsign.model;

import java.math.BigDecimal;

public class PdfData {
/*    "pageNum": "新添加元素所在的页码", 
    "x": "新添加元素x坐标, 用百分比表示，取值0.0~1.0", 
    "y": "新添加元素y坐标, 用百分比表示，取值0.0~1.0", 
    "type": "新添加元素的类型. 目前支持：text, image两种", 
    "value": "新添加元素的内容. 如果是text类型，为文本;如果是image类型，为base64编码后的图片内容", 
    "fontSize": "如果新添加元素是text类型，可以用来指定新添加元素的字体大小。默认14"*/
	private String pageNum;
	private String x;
	private String y;
	private String type;
	private String value;
	private String fontSize;

	public PdfData(String pageNum1, String x1, String y1, String type1, String value1, String fontSize1) {
		BigDecimal dx = new BigDecimal(x1);
		BigDecimal dy = new BigDecimal(y1);
		BigDecimal d21 = new BigDecimal("21.0");
		BigDecimal d29 = new BigDecimal("29.7");
		pageNum = pageNum1;
		x = dx.divide(d21, 3, BigDecimal.ROUND_HALF_UP).toString();
		y = dy.divide(d29, 3, BigDecimal.ROUND_HALF_UP).toString();
		type = type1;
		value = value1;
		fontSize = fontSize1;
	}
	public String getPageNum() {
		return pageNum;
	}
	public void setPageNum(String pageNum) {
		this.pageNum = pageNum;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getFontSize() {
		return fontSize;
	}
	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}
}
