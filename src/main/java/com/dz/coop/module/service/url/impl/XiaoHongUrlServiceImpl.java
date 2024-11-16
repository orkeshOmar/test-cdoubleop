package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

/**
 * 小红阅读
 *
 * @author luozukai
 */
@Service
public class XiaoHongUrlServiceImpl implements UrlService{


    private final String appkeyUrl = "?appkey=" + "8aada63965a2a1100165a3a00a240004";

    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl() + appkeyUrl;
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return owchPartner.getBookInfoUrl() + "/" + bookId + appkeyUrl;
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return owchPartner.getChapterListUrl() + "/" + bookId + appkeyUrl;
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return owchPartner.getChapterInfoUrl() + "/" + chapterId + appkeyUrl;
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.HONG_HUA};
    }



}
