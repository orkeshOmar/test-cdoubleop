package com.dz.coop.common.util;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;


public class EncodeUtil {


	// 将 s 进行 BASE64 编码
	public static String encodeBASE64(String s) throws UnsupportedEncodingException {
		if (s == null)
			return null;
		return new String(Base64.encodeBase64(s.getBytes("UTF-8")), "UTF-8");
	}

	// 将 BASE64 编码的字符串 s 进行解码
	public static String decodeBase64(String s) throws UnsupportedEncodingException {
		if (StringUtils.isEmpty(s))
			return null;
		return new String(Base64.decodeBase64(s.getBytes("UTF-8")), "UTF-8");
	}



}
