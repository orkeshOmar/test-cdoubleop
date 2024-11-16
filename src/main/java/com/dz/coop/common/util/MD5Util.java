package com.dz.coop.common.util;

import java.security.MessageDigest;

/*
 * MD5 算法
*/
public class MD5Util {
    

	/**
     * 对字符串进行md5加密
     * @param s 要加密的字符串
     * @return md5加密后的字符串
     */
    public final static String MD5(String s) {
        char hexDigits[] = {
                           '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                           'a', 'b', 'c', 'd',
                           'e', 'f'};
        try {
//            byte[] strTemp = s.getBytes();
            byte[] strTemp = s.getBytes("utf-8");
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

}