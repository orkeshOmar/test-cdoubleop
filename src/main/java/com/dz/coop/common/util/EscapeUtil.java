package com.dz.coop.common.util;


import com.dz.coop.common.annotation.Escape;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author panqz 2018-01-24 下午2:17
 */

public class EscapeUtil {
    private static final Logger logger = LoggerFactory.getLogger(EscapeUtil.class);

    private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String SUSPENSION_POINTS = "......";

    public static void escape(Object object) {
        List<Field> fieldList = new ArrayList<>();
        Class tempClass = object.getClass();
        while (tempClass != null && !tempClass.getName().toLowerCase().equals("java.lang.object")) {
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass();
        }
        for (Field field : fieldList) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Escape.class) && field.getType() == String.class) {
                String value;
                try {
                    value = (String) field.get(object);
                    if (StringUtils.isNotBlank(value)) {
                        field.set(object, escape(value));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public static String escape(String htmlStr) {
        if (htmlStr == null) {
            return null;
        }

        htmlStr = StringEscapeUtils.unescapeHtml4(htmlStr);
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");

        htmlStr = htmlStr.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "")
                .replaceAll("<p>", StringUtils.EMPTY)
                .replaceAll("</p>", LINE_SEPARATOR)
                .replaceAll("<br>", LINE_SEPARATOR)
                .replaceAll("<br/>", LINE_SEPARATOR)
                .replaceAll("<br />", LINE_SEPARATOR)
                .replaceAll("<em>", StringUtils.EMPTY)
                .replaceAll("</em>", StringUtils.EMPTY)
                .replaceAll("<b>", StringUtils.EMPTY)
                .replaceAll("</b>", StringUtils.EMPTY)
                .replaceAll("<span>", StringUtils.EMPTY)
                .replaceAll("</span>", StringUtils.EMPTY)
                .replaceAll("<strong>", StringUtils.EMPTY)
                .replaceAll("</strong>", StringUtils.EMPTY)
                .replaceAll("——内容来自【咪咕阅读】", StringUtils.EMPTY)
                .replace("……", SUSPENSION_POINTS)
                .replace("å", StringUtils.EMPTY);

        return htmlStr.trim();
    }

}
