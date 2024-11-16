package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;


/**
 * @author panqz 2017-12-06 下午3:27
 */
@Service
public class DouDuUrlServiceImpl implements UrlService{
    @Override
    public String getBookListUrl(Partner owchPartner) {
        return MessageFormat.format(owchPartner.getBookListUrl(), DigestUtils.md5Hex(owchPartner.getAliasId() + owchPartner.getApiKey()).toLowerCase());
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getBookInfoUrl(), bookId, DigestUtils.md5Hex(owchPartner.getAliasId() + owchPartner.getApiKey() + bookId).toLowerCase());
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getChapterListUrl(), bookId, DigestUtils.md5Hex(owchPartner.getAliasId() + owchPartner.getApiKey() + bookId).toLowerCase());
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), cpBookId, chapterId, DigestUtils.md5Hex(owchPartner.getAliasId() + owchPartner.getApiKey() + cpBookId + chapterId).toLowerCase());
    }

//    @Override
//    public Long getClientId() {
//        return CommonPartnerEnum.CLIENT_141;
//    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.DOU_DU};
    }
}
