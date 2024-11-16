package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;


/**
 * @author panqz
 * @create 2017-08-21 下午7:11
 */
@Service
public class WuTongUrlServiceImpl implements UrlService{
    @Override
    public String getBookListUrl(Partner owchPartner) {
        return MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), DigestUtils.md5Hex(owchPartner.getAliasId() + owchPartner.getApiKey()));
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), DigestUtils.md5Hex(owchPartner.getAliasId() + owchPartner.getApiKey() + bookId), bookId);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), DigestUtils.md5Hex(owchPartner.getAliasId() + owchPartner.getApiKey() + bookId), bookId);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), DigestUtils.md5Hex(owchPartner.getAliasId() + owchPartner.getApiKey() + cpBookId + chapterId), cpBookId, chapterId);
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.WU_TONG};
    }

}
