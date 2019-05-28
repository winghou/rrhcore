package com.bestsign.config;

/**
 * 上上签预览合同清晰度
 *
 * @author geosmart
 * @date 2016-12-27
 */
public enum EnumDpi {
	
	DPI_96("96","低清"),
	DPI_120("120","普清"),
	DPI_160("160","高清"),
	DPI_240("240","超清");
	
    private String code;
    private String desc;

    EnumDpi(String code,String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
