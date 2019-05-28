package com.creditx.xbehavior;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RiskInvoker {

    private static final String UTF8 = "utf8";
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final Random rnd = new Random(System.currentTimeMillis());

    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String apiScheme = "https";
    private String apiHost = "blp.creditx.com";
    private int apiPort = 443;
    private String apiPath = "/xbehavior_risk_score";

    private String appKey = null;
    private String accessKey = null;

    SecretKeySpec keySpec = null;

    public RiskInvoker(String appKey, String accessKey)
            throws UnsupportedEncodingException {
        setAppKey(appKey);
        setAccessKey(accessKey);
    }

    public String getAppKey() {
        return appKey;
    }
    public void setAppKey(final String value) {
        appKey = value;
    }

    public String getAccessKey() {
        return accessKey;
    }
    public void setAccessKey(final String value) throws UnsupportedEncodingException {
        accessKey = value;
        keySpec = new SecretKeySpec(accessKey.getBytes(UTF8), HMAC_SHA1);
    }

    public RiskResponse query(RiskRequest riskReq)
            throws IOException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String r = Integer.toString(rnd.nextInt());
        String payload = gson.toJson(riskReq);
        // System.out.println(payload);
        String signString = "POST|" + apiPath + "|" + r + "|" + payload;

		Mac mac = Mac.getInstance(HMAC_SHA1);
        mac.init(keySpec);
        byte[] sigBytes = mac.doFinal(signString.getBytes(UTF8));
        String sig = DatatypeConverter.printBase64Binary(sigBytes);

        HttpUrl url = new HttpUrl.Builder()
            .scheme(apiScheme)
            .host(apiHost)
            .port(apiPort)
            .addPathSegments(apiPath)
            .addQueryParameter("r", r)
            .addQueryParameter("sig", sig)
            .build();

        RequestBody reqBody = RequestBody.create(JSON, payload);
        Request request = new Request.Builder()
            .url(url)
            .post(reqBody)
            .build();
        Response response = client.newCall(request).execute();
        String respBody = response.body().string();
        // System.out.println(respBody);
        RiskResponse riskResp = gson.fromJson(respBody, RiskResponse.class);
        riskResp.setStatusCode(response.code());
        return riskResp;
    }
}
