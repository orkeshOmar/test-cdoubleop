package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;


/**
 * @author panqz
 * @create 2017-06-24 下午10:28
 */
@Service
public class CommonUrlServiceImpl implements UrlService{


    @Override
    public String getBookListUrl(Partner owchPartner) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey());
        String bookListParam = String.format("client_id=%s&sign=%s", clientId, sign);
        return handleUrl(owchPartner.getBookListUrl(), bookListParam);
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + bookId);
        String bookInfoParam = String.format("client_id=%s&sign=%s&book_id=%s", clientId, sign, bookId);
        return handleUrl(owchPartner.getBookInfoUrl(), bookInfoParam);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + bookId);
        String volumeStrParam = String.format("client_id=%s&sign=%s&book_id=%s", clientId, sign, bookId);
        return handleUrl(owchPartner.getChapterListUrl(), volumeStrParam);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + cpBookId + chapterId);
        String chapterInfoParam = String.format("client_id=%s&sign=%s&book_id=%s&chapter_id=%s", clientId, sign, cpBookId, chapterId);
        return handleUrl(owchPartner.getChapterInfoUrl(), chapterInfoParam);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.URL_COMMON};
    }

    private String handleUrl(String url, String suffix) {
        if (url.indexOf("?") == -1) {
            url += "?";
        } else {
            url = url + "&";
        }
        return url + suffix;
    }

    @Override
    public String getCategoryListUrl(Partner owchPartner) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey());
        String param = String.format("client_id=%s&sign=%s", clientId, sign);
        String url = owchPartner.getCategoryListUrl();
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        return handleUrl(url, param);
    }
}
