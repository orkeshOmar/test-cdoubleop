package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

/**
 * @author panqz
 * @create 2017-06-25 下午8:26
 */
@Service
public class FengHuangUrlServiceImpl implements UrlService{
    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl();
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return owchPartner.getBookInfoUrl() + "?bookid=" + bookId;
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return owchPartner.getChapterListUrl() + "?bookid=" + bookId;
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return owchPartner.getChapterInfoUrl() + "?bookid=" + cpBookId + "&chapterid=" + chapterId;
    }

//    @Override
//    public Long getClientId() {
//        return CommonPartnerEnum.CLIENT_10;
//    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.FENG_HUANG};
    }
}
