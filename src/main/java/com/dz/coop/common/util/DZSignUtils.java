package com.dz.coop.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class DZSignUtils {


	public static String sign(Map<String, String> parameterMap) {
		if(parameterMap == null){
			return null;
		}
		Map<String, String> filterParameterMap = filterParameter(parameterMap,"");
		String linkParameterStr = linkFileName(filterParameterMap);
		return DigestUtils.md5Hex(linkParameterStr);
	}

	/**remove key=sign 
	 * @param parameterMap
	 */
	public static Map<String, String> filterParameter(Map<String, String> parameterMap,String removeKey) {
		Map<String, String> resultMap = new HashMap<String, String>();
		if(parameterMap != null){
			for (String key : parameterMap.keySet()) {
				if(StringUtils.isNotBlank(removeKey) && removeKey.equals(key)){
					continue;
				}
				resultMap.put(key, parameterMap.get(key));
			}
		}
		return resultMap;
	}

	/**
	 * 将map的key与value排序用&拼接成字符串
	 * @param parameterMap
	 * @return key1=value1&key2=value2&key3=value3.....
	 */
	public static String linkFileName(Map<String, String> parameterMap) {
		if (parameterMap == null || parameterMap.size() < 1) {
			return null;
		}

		List<String> keyList = new ArrayList<String>(parameterMap.keySet());
		Collections.sort(keyList);

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < keyList.size(); i++) {
			String key = keyList.get(i);
			String keyValue = parameterMap.get(key);
			sb.append(key).append("=").append(keyValue);
			if (i < keyList.size() - 1) {
				sb.append("&");
			}
		}
		return sb.toString();
	}
	/**
	 * @param parameterMap
	 * @return key1value1key2value2key3value3.....
	 */
	public static String linkFileNameNoConnector(Map<String, String> parameterMap) {
		if (parameterMap == null || parameterMap.size() < 1) {
			return null;
		}
		
		List<String> keyList = new ArrayList<String>(parameterMap.keySet());
		Collections.sort(keyList);
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < keyList.size(); i++) {
			String key = keyList.get(i);
			String keyValue = parameterMap.get(key);
			sb.append(key).append(keyValue);
		}
		return sb.toString();
	}

	public static boolean verify(Map<String, String> parameterMap, String sign) {
		String localSign = sign(parameterMap);
		if (localSign != null) {
			return localSign.equals(sign);
		}
		return false;
	}

	/**
	 * @author kangyf
	 * @date 2016-03-31
	 * 将map的key与value排序用&拼接成字符串
	 * @param parameterMap
	 * @return &urlencode_rfc3986(key1)=urlencode_rfc3986(value1)&urlencode_rfc3986(key2)=urlencode_rfc3986(value2).....
	 */
	public static String oauth1BaseString(Map<String, String> parameterMap) {
		if (parameterMap == null || parameterMap.size() < 1) {
			return null;
		}

		List<String> keyList = new ArrayList<String>(parameterMap.keySet());
		Collections.sort(keyList);

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < keyList.size(); i++) {
			String key = keyList.get(i);
			String keyValue = parameterMap.get(key);
			sb.append(UrlEncodeUtil.urlencodeRFC3986((key))).append("=").append(UrlEncodeUtil.urlencodeRFC3986(keyValue));
//			if (i < keyList.size() - 1) {
//				sb.append("&");
//			}
		}
		return sb.toString();
	}

	public static String oauth1SignatureHMAC_SHA1(String baseString, String consumerSecret, String tokenSecret) throws UnsupportedEncodingException{
		String key = UrlEncodeUtil.urlencodeRFC3986(consumerSecret)+"&"+UrlEncodeUtil.urlencodeRFC3986(tokenSecret);
		return EncodeUtil.encodeBASE64(DigestUtils.sha1Hex(baseString+key));
	}

}
