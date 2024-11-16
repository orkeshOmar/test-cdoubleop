package com.dz.coop.common.util;

import com.aliyun.oss.common.utils.DateUtil;
import com.dz.coop.common.SpringContextHolder;
import com.dz.coop.conf.properties.AliOssConf;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @project: coop-client
 * @description: 阿里CDN工具
 * @author: songwj
 * @date: 2020-09-01 21:17
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class AliCdnUtil {

    private static Logger logger = LoggerFactory.getLogger(AliCdnUtil.class);

    private static AliOssConf aliOssConf = SpringContextHolder.getBean(AliOssConf.class);

    private static String accessKeyId = aliOssConf.getAccessKeyId();

    private static String accessKeySecret = aliOssConf.getAccessKeySecret();

    private static final String FORMAT = "JSON";

    private static final String VERSION = "2018-05-10";

    private static final String SIGNATURE_METHOD = "HMAC-SHA1";

    private static final String SIGNATURE_VERSION = "1.0";

    private static final String ACTION = "RefreshObjectCaches";

    private static final String OBJECT_TYPE = "File";

    private final static String CHARSET_UTF8 = "utf8";

    private final static String ALGORITHM = "UTF-8";

    private final static String SEPARATOR = "&";

    public static boolean refreshCdn(String path) {
        try {
            Map<String, String> param = new HashMap<>();

            param.put("AccessKeyId", accessKeyId);
            param.put("Format", FORMAT);
            param.put("Version", VERSION);
            param.put("SignatureMethod", SIGNATURE_METHOD);
            param.put("SignatureVersion", SIGNATURE_VERSION);
            param.put("SignatureNonce", System.currentTimeMillis()+"");
            param.put("Timestamp", DateUtil.formatAlternativeIso8601Date(new Date()));
            param.put("Action", ACTION);
            param.put("ObjectPath", path);
            param.put("ObjectType", OBJECT_TYPE);
            param.put("Signature", generate("GET", param, accessKeySecret));

            StringBuilder sb = new StringBuilder();
            param.forEach((key, value)->{
                sb.append(key).append("=").append(value).append("&");
            });
            String post = HttpUtil.sendGet("http://cdn.aliyuncs.com?" + sb.toString().substring(0, sb.length() - 1));
            logger.info("path: {}, ali cdn refresh result: {}", path, post);

            return true;
        } catch (Exception e) {
            logger.error("path: {}, refresh ali cdn fail: {}", path, e.getMessage(), e);
            return false;
        }
    }

    private static String generate(String method, Map<String, String> parameter,
                                  String accessKeySecret) throws Exception {
        String signString = generateSignString(method, parameter);
        byte[] signBytes = hmacSHA1Signature(accessKeySecret + "&", signString);
        String signature = newStringByBase64(signBytes);

        if ("POST".equals(method)) {
            return signature;
        }

        return URLEncoder.encode(signature, "UTF-8");
    }

    private static String generateSignString(String httpMethod, Map<String, String> parameter)
            throws IOException {
        TreeMap<String, String> sortParameter = new TreeMap<String, String>();
        sortParameter.putAll(parameter);

        String canonicalizedQueryString = generateQueryString(sortParameter, true);
        if (null == httpMethod) {
            throw new RuntimeException("httpMethod can not be empty");
        }

        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(httpMethod).append(SEPARATOR);
        stringToSign.append(percentEncode("/")).append(SEPARATOR);
        stringToSign.append(percentEncode(canonicalizedQueryString));

        return stringToSign.toString();
    }

    private static String percentEncode(String value) {
        try {
            return value == null ? null : URLEncoder.encode(value, CHARSET_UTF8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        } catch (Exception e) {
        }
        return "";
    }

    private static byte[] hmacSHA1Signature(String secret, String baseString)
            throws Exception {
        if (StringUtils.isEmpty(secret)) {
            throw new IOException("secret can not be empty");
        }
        if (StringUtils.isEmpty(baseString)) {
            return null;
        }
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(CHARSET_UTF8), ALGORITHM);
        mac.init(keySpec);
        return mac.doFinal(baseString.getBytes(CHARSET_UTF8));
    }

    private static String newStringByBase64(byte[] bytes)
            throws UnsupportedEncodingException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return new String(org.apache.commons.codec.binary.Base64.encodeBase64(bytes, false), CHARSET_UTF8);
    }

    private static String generateQueryString(Map<String, String> params, boolean isEncodeKV) {
        StringBuilder canonicalizedQueryString = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (isEncodeKV) {
                canonicalizedQueryString.append(percentEncode(entry.getKey())).append("=").append(percentEncode(entry.getValue())).append("&");
            } else {
                canonicalizedQueryString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        if (canonicalizedQueryString.length() > 1) {
            canonicalizedQueryString.setLength(canonicalizedQueryString.length() - 1);
        }

        return canonicalizedQueryString.toString();
    }

}
