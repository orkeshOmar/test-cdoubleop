package com.dz.coop.module.service.url.impl;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * @author panqz 2018-05-07 下午3:53
 */
@Service
public class XinHuaUrlServiceImpl implements UrlService {
    @Override
    public String getBookListUrl(Partner owchPartner) {
        return MessageFormat.format(owchPartner.getBookListUrl(), owchPartner.getAliasId(), sign(owchPartner.getAliasId() + "", owchPartner.getApiKey()));
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getBookInfoUrl(), owchPartner.getAliasId(), sign(owchPartner.getAliasId() + "", owchPartner.getApiKey(), bookId), bookId);
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        return MessageFormat.format(owchPartner.getChapterListUrl(), owchPartner.getAliasId(), sign(owchPartner.getAliasId() + "", owchPartner.getApiKey(), bookId), bookId);
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        return MessageFormat.format(owchPartner.getChapterInfoUrl(), owchPartner.getAliasId(), sign(owchPartner.getAliasId() + "", owchPartner.getApiKey(), cpBookId, chapterId), cpBookId, chapterId);
    }


    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.XIN_HUA_YUE_DU};
    }

    private String sign(String... paras) {
        StringBuilder sb = new StringBuilder();
        for (String param : paras) {
            sb.append(param);
        }
        return DigestUtils.md5Hex(sb.toString()).toLowerCase();
    }
}
