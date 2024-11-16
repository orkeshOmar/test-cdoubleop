package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author panqz
 * 创策
 * @create 2017-06-24 下午10:28
 */
@Service
public class ChuangCeUrlServiceImpl implements UrlService{
    private static final Logger logger = LoggerFactory.getLogger(ChuangCeUrlServiceImpl.class);


    @Override
    public String getBookListUrl(Partner owchPartner) {
        String clientId = owchPartner.getAliasId();
        //String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey());
        String bookListParam = String.format("appId=%s&appSecret=%s", clientId, owchPartner.getApiKey());
        return handleUrl(owchPartner.getBookListUrl(), bookListParam);
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        //String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + bookId);
        String bookInfoParam = String.format("appId=%s&appSecret=%s&book_id=%s", clientId, owchPartner.getApiKey(), bookId);
        return handleUrl(owchPartner.getBookInfoUrl(), bookInfoParam);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        //String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + bookId);
        String volumeStrParam = String.format("appId=%s&appSecret=%s&book_id=%s", clientId, owchPartner.getApiKey(), bookId);
        return handleUrl(owchPartner.getChapterListUrl(), volumeStrParam);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + cpBookId + chapterId);
        String chapterInfoParam = String.format("appId=%s&appSecret=%s&book_id=%s&chapter_id=%s&sign=%s", clientId, owchPartner.getApiKey(), cpBookId, chapterId, sign);
        return handleUrl(owchPartner.getChapterInfoUrl(), chapterInfoParam);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.CHUANG_CE};
    }

    private String handleUrl(String url, String suffix) {
        if (url.indexOf("?") == -1) {
            url += "?";
        } else {
            url = url + "&";
        }
        return url + suffix;
    }
}
