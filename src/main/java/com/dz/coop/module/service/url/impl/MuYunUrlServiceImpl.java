package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author panqz 2018-04-09 下午5:36
 */
@Service
public class MuYunUrlServiceImpl implements UrlService{
    @Override
    public String getBookListUrl(Partner owchPartner) {
        return MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getApiKey());
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getApiKey(), bookId);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getApiKey(), bookId);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getApiKey(), cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.MU_YUN_SHU_YUAN};
    }
}
