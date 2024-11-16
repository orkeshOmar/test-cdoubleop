package com.dz.coop.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @project: coop-client
 * @description: 字符串处理工具
 * @author: songwj
 * @date: 2019-12-02 17:58
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class StringUtil {

    /**
     * 正则预编译
     */
    private static Pattern INTRODUCTION_PATTERN = Pattern.compile("(\r?\n(\\s*\r?\n)+)");
    //private static Pattern INTRODUCTION_PATTERN = Pattern.compile("(\r?\n(\\s*)?(\r?\n)?)|(\r?\n(\\s+)?(\r?\n)+)");

    private static Pattern HTML_PATTERN = Pattern.compile("<[a-z]+.*>");

    /**
     * 移除空行
     * @param srcStr 原始字符串
     * @return
     */
    public static String removeEmptyLines(String srcStr) {
        Pattern pattern = INTRODUCTION_PATTERN;
        Matcher matcher = pattern.matcher(srcStr);
        return matcher.replaceAll("\r\n");
    }

    /**
     * 获取字符串的真实长度（去除换行符和空格，省略号和数字算一个字符）
     * @param srcStr
     * @return
     */
    public static int getRealLength(String srcStr) {
        return StringUtils.isNotBlank(srcStr) ? srcStr.replaceAll("\r\n|\r|\n|\t| |　| ", "").replaceAll("\\.+", ".").replaceAll("\\d+(\\.\\d+)?", "1").length() : 0;
    }

    /**
     * 判断字符串内容是否包含HTML标签
     * @param content
     * @return true为包含
     */
    public static boolean containsHtml(String content) {
        return StringUtils.isNotBlank(content) ? HTML_PATTERN.matcher(content).find() : false;
    }

}
