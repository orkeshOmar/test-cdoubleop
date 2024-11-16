package com.dz.coop.module.service.url.impl;


import com.dz.coop.common.util.Utils;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author panqz 2017-12-22 下午4:02
 */
@Service
public class XingKongUrlServiceImpl implements UrlService{
    @Override
    public String getBookListUrl(Partner owchPartner) {
        String clientId = owchPartner.getAliasId();
        return MessageFormat.format(owchPartner.getBookListUrl(), clientId, Utils.createBookListSign(clientId,
                owchPartner.getApiKey()));
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        return MessageFormat.format(owchPartner.getBookInfoUrl(), clientId, bookId, Utils.createBookInfoSign(clientId,
                owchPartner.getApiKey(), bookId));
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        return MessageFormat.format(owchPartner.getChapterListUrl(), clientId, bookId, Utils.createChapterListSign(clientId,
                owchPartner.getApiKey(), bookId));
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        String clientId = owchPartner.getAliasId();
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), clientId, cpBookId, chapterId, Utils.createChatperInfoSign(clientId, owchPartner.getApiKey(),
                cpBookId, chapterId));
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.LONG_YUE_DU};
    }

}
