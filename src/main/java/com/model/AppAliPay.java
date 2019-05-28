package com.model;

public class AppAliPay {
    private Integer payId;

    private String partner;

    private String sellerId;

    private String privateKey;

    private String publicKey;

    private String notifyUrl;

    private String returnUrl;

    private String signType;

    private String inputCharset;

    private String paymentType;

    private String service;

    private String antiPhishingKey;

    private String exterInvokeIp;

    private String timeoutExpress;

    private String productCode;
    
    public String WIDout_trade_no;
	public String WIDsubject;
	public String WIDtotal_fee;
	public String WIDbody;
	
	private String privateKey2;	//支付宝新版本私钥
	private String publicKey2;		//支付宝新版本公钥
	private String notifyUrl2;		//支付宝新版本异步回调地址
	private String gateWayUrl;	//支付宝网关地址
	
	


	public String getPrivateKey2() {
		return privateKey2;
	}

	public void setPrivateKey2(String privateKey2) {
		this.privateKey2 = privateKey2;
	}

	public String getPublicKey2() {
		return publicKey2;
	}

	public void setPublicKey2(String publicKey2) {
		this.publicKey2 = publicKey2;
	}

	public String getNotifyUrl2() {
		return notifyUrl2;
	}

	public void setNotifyUrl2(String notifyUrl2) {
		this.notifyUrl2 = notifyUrl2;
	}

	public String getGateWayUrl() {
		return gateWayUrl;
	}

	public void setGateWayUrl(String gateWayUrl) {
		this.gateWayUrl = gateWayUrl;
	}

	public String getWIDout_trade_no() {
		return WIDout_trade_no;
	}

	public void setWIDout_trade_no(String wIDout_trade_no) {
		WIDout_trade_no = wIDout_trade_no;
	}

	public String getWIDsubject() {
		return WIDsubject;
	}

	public void setWIDsubject(String wIDsubject) {
		WIDsubject = wIDsubject;
	}

	public String getWIDtotal_fee() {
		return WIDtotal_fee;
	}

	public void setWIDtotal_fee(String wIDtotal_fee) {
		WIDtotal_fee = wIDtotal_fee;
	}

	public String getWIDbody() {
		return WIDbody;
	}

	public void setWIDbody(String wIDbody) {
		WIDbody = wIDbody;
	}

	public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAntiPhishingKey() {
        return antiPhishingKey;
    }

    public void setAntiPhishingKey(String antiPhishingKey) {
        this.antiPhishingKey = antiPhishingKey;
    }

    public String getExterInvokeIp() {
        return exterInvokeIp;
    }

    public void setExterInvokeIp(String exterInvokeIp) {
        this.exterInvokeIp = exterInvokeIp;
    }

    public String getTimeoutExpress() {
        return timeoutExpress;
    }

    public void setTimeoutExpress(String timeoutExpress) {
        this.timeoutExpress = timeoutExpress;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}