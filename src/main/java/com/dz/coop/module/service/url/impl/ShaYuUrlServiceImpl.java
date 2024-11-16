package com.dz.coop.module.service.url.impl;


import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

/**
 * @author xiaoluoluo
 * 傻鱼
 */
@Service
public class ShaYuUrlServiceImpl implements UrlService{
    private static final String KEY = "1de2bfccc3abc4d7d49c4c58aa15594e";
    private static final String SECRET = "b82ab1fec00e27e437e9e5f704819823";

    @Override
    public String getBookListUrl(Partner owchPartner) {
        String sign = DigestUtils.md5Hex(DigestUtils.md5Hex(KEY + "getbooklist") + SECRET);
        return owchPartner.getBookListUrl() + "?key=" + KEY + "&token=" + sign;
    }

    @Override
    public String getBooKInfoUrl(Partner owchPartner, String bookId) {
        String sign = DigestUtils.md5Hex(DigestUtils.md5Hex(KEY + "getbookinfo" + bookId) + SECRET);
        return owchPartner.getBookInfoUrl() + "&bookid=" + bookId + "&key=" + KEY;
    }

    @Override
    public String getChapterListUrl(Partner owchPartner, String bookId) {
        String sign = DigestUtils.md5Hex(DigestUtils.md5Hex(KEY + "getbookchapterlist" + bookId) + SECRET);
        return owchPartner.getChapterListUrl() + "?bookid=" + bookId + "&key=" + KEY + "&token=" + sign;
    }

    @Override
    public String getChpaterContentUrl(Partner owchPartner, String cpBookId, String chapterId) {
        String sign = DigestUtils.md5Hex(DigestUtils.md5Hex(KEY + "getbookchapter" + cpBookId + chapterId) + SECRET);
        return owchPartner.getChapterInfoUrl() + "?bookid=" + cpBookId + "&chapterid=" + chapterId + "&key=" + KEY + "&token=" + sign;
    }

    @Override
    public ThirdPart[] getClient() {
        return new ThirdPart[]{ThirdPart.SHA_YU_ZHONG_WEN};
    }

    @Override
    public String getCategoryListUrl(Partner owchPartner) {
        String sign = DigestUtils.md5Hex(DigestUtils.md5Hex(KEY + "categories") + SECRET);
        return owchPartner.getCategoryListUrl() + "&key=" + KEY;
    }
}
