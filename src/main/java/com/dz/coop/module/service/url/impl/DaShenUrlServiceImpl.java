package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author panqz 2018-01-18 上午10:57
 */
@Service
public class DaShenUrlServiceImpl implements UrlService{

    private static final String key = "e6ddc5478934e8120c587c44ebc03691";
    private static final String secret = "51ce5db87bf2a59f1624c54e553a95ab";

    private static final String ACTION_BOOK_LIST = "getbooklist";
    private static final String ACTION_BOOK_INFO = "getbookinfo";
    private static final String ACTION_CHAPTER_LIST = "getbookchapterlist";
    private static final String ACTION_CHAPTER_INFO = "getbookchapter";

    @Override
    public String getBookListUrl(Partner owchPartner) {
        return MessageFormat.format(owchPartner.getBookListUrl(), key, sign(secret, key, ACTION_BOOK_LIST));
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {

        return MessageFormat.format(owchPartner.getBookInfoUrl(), key, sign(secret, key, ACTION_BOOK_INFO, bookId), bookId);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getChapterListUrl(), key, sign(secret, key, ACTION_CHAPTER_LIST, bookId), bookId);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), key, sign(secret, key, ACTION_CHAPTER_INFO, cpBookId, chapterId), cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.DA_SHENG_ZHONG_WEN};
    }

//    @Override
//    public Long getClientId() {
//        return CommonPartnerEnum.CLIENT_152;
//    }


    private String sign(String secret, String... paras) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paras.length; i++) {
            sb.append(paras[i]);
        }
        return DigestUtils.md5Hex(DigestUtils.md5Hex(sb.toString()).toLowerCase() + secret).toLowerCase();

    }


}
