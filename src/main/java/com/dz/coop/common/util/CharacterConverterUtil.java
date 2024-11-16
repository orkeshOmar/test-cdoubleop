package com.dz.coop.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @project: coop-client
 * @description: 字符转换工具
 * @author: songwj
 * @date: 2021-11-01 11:23
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
public class CharacterConverterUtil {

    private static final String CHAR_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 将传入的字符串转为数字，字符串中的数字不转换
     * @param str
     * @return
     */
    public static String convertchar2number(String str) {
        if (StringUtils.isNotBlank(str)) {
            StringBuilder sb = new StringBuilder();
            String[] split = str.split("");

            for (String charStr : split) {
                if (StringUtils.contains(CHAR_STR, charStr)) {
                    sb.append(StringUtils.indexOf(CHAR_STR, charStr));
                } else {
                    sb.append(charStr);
                }
            }

            return sb.toString();
        }

        return str;
    }

}
