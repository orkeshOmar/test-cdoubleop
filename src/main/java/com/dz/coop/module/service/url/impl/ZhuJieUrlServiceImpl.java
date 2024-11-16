package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

@Service
public class ZhuJieUrlServiceImpl implements UrlService{

    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl();
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return owchPartner.getBookInfoUrl();
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return null;
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return owchPartner.getChapterInfoUrl();
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.ZHU_JIE_DUAN_PIAN};
    }

    @Override
    public String getCategoryListUrl(Partner owchPartner) {
        return owchPartner.getCategoryListUrl();
    }

}
