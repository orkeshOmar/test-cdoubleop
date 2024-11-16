package com.dz.coop.common.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author panqz 2017-09-05 下午5:18
 */

public class Utils {

    public static String replaceNullContent(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        Pattern pattern = Pattern.compile("null", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            content = matcher.replaceAll("");
        }
        return content;
    }

    public static boolean hasNullContent(String content) {
        if (StringUtils.isBlank(content)) {
            return true;
        }
        Pattern pattern = Pattern.compile("null", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    public static String createBookListSign(String clientId, String apiKey) {
        return DigestUtils.md5Hex(clientId + apiKey);
    }

    public static String createBookInfoSign(String clientId, String apiKey, String bookId) {
        return DigestUtils.md5Hex(clientId + apiKey + bookId);
    }

    public static String createChapterListSign(String clientId, String apiKey, String bookId) {
        return createBookInfoSign(clientId, apiKey, bookId);
    }

    public static String createChatperInfoSign(String clientId, String apiKey, String bookId, String chapterId) {
        return DigestUtils.md5Hex(clientId + apiKey + bookId + chapterId);
    }
}
