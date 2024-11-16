package com.dz.coop.module.service.url;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Objects;

/**
 * @author panqz
 * @create 2017-06-24 下午4:56
 */

public interface UrlService {
    String getBookListUrl(Partner owchPartner);

    String getBooKInfoUrl(Partner owchPartner, String bookId);

    String getChapterListUrl(Partner owchPartner, String bookId);

    String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId);

    ThirdPart[] getClient();

    default String getCategoryListUrl(Partner owchPartner) {
        // 分类接口一般参数相对固定, 故增加一个默认接口直接返回原始值
        String clientId = owchPartner.getAliasId();
        String sign = DigestUtils.md5Hex(clientId + owchPartner.getApiKey());
        String url = owchPartner.getCategoryListUrl();
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        // 防止key变化后没有人知道
        if (!url.contains(sign)) {
            Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
            logger.error("clientId clientId_{} sign不包含在url中", clientId);
        }
        return url;
    }

}
