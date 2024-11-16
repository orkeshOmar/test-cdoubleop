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
 * @create 2017-06-27 上午11:26
 */
@Service
public class QuYueUrlServiceImpl implements UrlService{
    private static final Logger logger = LoggerFactory.getLogger(CommonUrlServiceImpl.class);


    @Override
    public String getBookListUrl(Partner owchPartner) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey());
        String bookListParam = String.format("client_id=%s&sign=%s", clientId, sign);
        return owchPartner.getBookListUrl() + "?" + bookListParam;
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey());
        String bookInfoParam = String.format("client_id=%s&sign=%s&book_id=%s", clientId, sign, bookId);
        return owchPartner.getBookInfoUrl() + "?" + bookInfoParam;
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey());
        String volumeStrParam = String.format("client_id=%s&sign=%s&book_id=%s", clientId, sign, bookId);
        return owchPartner.getChapterListUrl() + "?" + volumeStrParam;
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey());
        String chapterInfoParam = String.format("client_id=%s&sign=%s&book_id=%s&chapter_id=%s", clientId, sign, cpBookId, chapterId);
        return owchPartner.getChapterInfoUrl() + "?" + chapterInfoParam;
    }

//

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.QU_YUE};
    }
}
