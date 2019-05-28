package com.frame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.util.AESOperator;
import com.util.StringUtil;

/**
 * 得到fastjson序列化和反序列化json
 * 
 * @author bill
 *
 */
public class FastJsonHttpMessageConverter extends
		AbstractHttpMessageConverter<Object> {
    private Logger logger = Logger.getLogger(FastJsonHttpMessageConverter.class);

	public final static Charset UTF8 = Charset.forName("UTF-8");

	private Charset charset = UTF8;

	private SerializerFeature[] serializerFeature = new SerializerFeature[0];

	public FastJsonHttpMessageConverter() {
		super(new MediaType("application", "json", UTF8), new MediaType(
				"application", "*+json", UTF8));
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	public Charset getCharset() {
		return this.charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public SerializerFeature[] getSerializerFeature() {
		return serializerFeature;
	}

	public void setSerializerFeature(SerializerFeature[] serializerFeature) {
		this.serializerFeature = serializerFeature;
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz,
			HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		InputStream in = inputMessage.getBody();

		byte[] buf = new byte[1024];
		for (;;) {
			int len = in.read(buf);
			if (len == -1) {
				break;
			}

			if (len > 0) {
				baos.write(buf, 0, len);
			}
		}

		byte[] bytes = baos.toByteArray();
        String s = new String(bytes, charset);
        JSONObject object = (JSONObject) JSON.parse(s);
        String value =  StringUtil.nvl(object.get("key"));
        String param = null;
        JSONObject object2 = null;
        String result = "";
        if(null == value || "".equals(value)){
        	param = StringUtil.nvl(object.get("params"));
        	object2 = (JSONObject) JSON.parse(param);
        	object2.put("isCode", "false");
        	object.put("params", object2);
        	result = object.toString();
        }else{
        	try {
        		result = AESOperator.decrypt(value);
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        logger.debug("request rawStr==" + s);
//        String ss = URLDecoder.decode(s, charset.displayName());
        // return JSON.parseObject(bytes, 0, bytes.length, charset.newDecoder(),
        // clazz);
        logger.debug("request urldecodeStr==" + s);
        return JSON.parseObject(result, clazz);

	}

	@Override
	protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		OutputStream out = outputMessage.getBody();
		String text = JSON.toJSONString(obj, serializerFeature);
		JSONObject object = (JSONObject) JSON.parse(text);
		String detail = StringUtil.nvl(object.get("detail"));
		JSONObject jsonDetail = (JSONObject) JSON.parse(detail);
		String result = "";
		String isCode = StringUtil.nvl(jsonDetail.get("isCode"));
		if(null != isCode && !"".equals(isCode) && "false".equals(isCode)){
			jsonDetail.remove("isCode");
			object.put("detail", jsonDetail);
			result = object.toString();
		}else{
			try {
				text = JSON.toJSONString(AESOperator.encrypt(object.toString())).replace("\\r\\n", "").replace("\"", "").replace("\\r", "").replace("\\n", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("key", text);
			result = JSON.toJSONString(jsonObject, serializerFeature);
		}
		byte[] bytes = result.getBytes(charset);
		out.write(bytes);
	}

}
