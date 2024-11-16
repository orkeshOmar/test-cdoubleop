package com.dz.coop.common.util;

import org.apache.commons.collections.MapUtils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author panqz 2017-09-01 下午1:53
 */

public class SignUtil {

    private static final String DEFAULT_KEY = "";

    public static String sign(Map<String,String> params) {
        return sign(createLinkString(params),DEFAULT_KEY);
    }

    public static String sign(Map<String,String> params, String privateKe) {
        return sign(createLinkString(params),privateKe);
    }

    public static String sign(String content, String privateKey) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64
                    .decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance("SHA1WithRSA");//签名算法SHA1WithRSA

            signature.initSign(priKey);
            signature.update(content.getBytes("UTF-8"));

            byte[] signed = signature.sign();

            return Base64.encode(signed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private static String createLinkString(Map<String, String> params) {

        if (MapUtils.isEmpty(params)) {
            return null;
        }

        TreeMap<String, String> keys = new TreeMap<>(params);
        Set<Map.Entry<String, String>> entries = keys.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();

        StringBuilder sb = new StringBuilder();

        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String result = sb.toString();

        return result.substring(0, result.lastIndexOf("&"));
    }


}
