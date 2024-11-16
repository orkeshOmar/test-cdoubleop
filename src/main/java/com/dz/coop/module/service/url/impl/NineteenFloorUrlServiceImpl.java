package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author panqz
 * @create 2017-06-24 下午6:31
 */
@Service
public class NineteenFloorUrlServiceImpl implements UrlService{


    @Override
    public String getBookListUrl(Partner owchPartner) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey());
        return MessageFormat.format(owchPartner.getBookListUrl(), clientId, sign);
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + bookId);
        return MessageFormat.format(owchPartner.getBookInfoUrl(), bookId, clientId, sign);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + bookId);
        return MessageFormat.format(owchPartner.getChapterListUrl(), bookId, clientId, sign);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey() + cpBookId + chapterId);
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId, chapterId, clientId, sign);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.NINTEEN_LOU};
    }
}
