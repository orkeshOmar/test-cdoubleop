package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

/**
 * 享阅
 *
 * @author luozukai
 */
@Service
public class XiangYueUrlServiceImpl implements UrlService{

    private final String appkeyUrl = "?apikey=" + "482143bbc73c5652f57f32de03a3674e";

    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl() + appkeyUrl;
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return owchPartner.getBookInfoUrl() + appkeyUrl + "&bookid=" + bookId;
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return owchPartner.getChapterListUrl() + appkeyUrl + "&bookid=" + bookId;
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return owchPartner.getChapterInfoUrl() + appkeyUrl + "&bookid=" + cpBookId + "&chapterid=" + chapterId;
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.XIANG_YUE};
    }
}
