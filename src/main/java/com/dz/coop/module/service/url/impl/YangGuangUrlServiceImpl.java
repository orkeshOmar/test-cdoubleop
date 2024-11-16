package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author panqz 2018-01-18 上午10:57
 */
@Service
public class YangGuangUrlServiceImpl implements UrlService{
    @Override
    public String getBookListUrl(Partner owchPartner) {
        return MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId() + "", owchPartner.getApiKey());
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId() + "", owchPartner.getApiKey(), bookId);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId() + "", owchPartner.getApiKey(), bookId);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId() + "", owchPartner.getApiKey(), cpBookId, chapterId);

    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.YANG_GUANG_YUE_DU};
    }
}
