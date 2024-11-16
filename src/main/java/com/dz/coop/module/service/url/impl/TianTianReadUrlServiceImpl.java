package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author panqz
 * @create 2017-08-28 下午5:08
 */
@Service
public class TianTianReadUrlServiceImpl implements UrlService{
    @Override
    public String getBookListUrl(Partner owchPartner) {
        return owchPartner.getBookListUrl();
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getBookInfoUrl(), bookId);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getChapterListUrl(), bookId);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.XIAO_SHUO_WANG};
    }

}
