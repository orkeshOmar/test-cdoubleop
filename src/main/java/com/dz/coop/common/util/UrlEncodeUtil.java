package com.dz.coop.common.util;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class UrlEncodeUtil {
	public static final String ENCODING = "UTF-8";

	public static String urlencodeRFC3986(String s) {
	    if (s == null) {
	        return "";
	    }
	    try {
	        return URLEncoder.encode(s, ENCODING)
	                .replace("+", "%20").replace("*", "%2A")
	                .replace("%7E", "~");
	    } catch (UnsupportedEncodingException wow) {
	        throw new RuntimeException(wow.getMessage(), wow);
	    }
	}


}
