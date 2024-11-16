package com.dz.coop.module.support;

import com.dz.coop.common.SpringContextHolder;
import com.dz.coop.common.util.AliOssUtil;
import com.dz.coop.common.util.MyX509TrustManager;
import com.dz.coop.conf.properties.AliAudioOssConf;
import com.dz.coop.conf.properties.AliOssConf;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.service.BookService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author panqz 2018-10-26 7:25 PM
 */
@Component
public class BookSupport {
    private static final Logger logger = LoggerFactory.getLogger(BookSupport.class);

    public static final String IMG_SUFFIX = ".jpg";

    private static final String BOOK_PATH_PREFIX;

    private static final String CND_PREFIX;

    private static final String CND_DOMAIN;

    private static final String LOCAL_PREFIX = "/";

    private static final String AUDIO_BOOK_PATH_PREFIX;

    private static final String AUDIO_LOCAL_BOOK_PATH_PREFIX;

    @Resource
    private BookService bookService;

    private static BookSupport bookSupport;

    @PostConstruct
    public void init() {
        bookSupport = this;
        bookSupport.bookService = this.bookService;
    }

    static {
        AliOssConf bean = SpringContextHolder.getBean(AliOssConf.class);
        BOOK_PATH_PREFIX = bean.getPrefix();
        CND_PREFIX = bean.getCdnPrefix();
        CND_DOMAIN = bean.getCdnDomain();

        AliAudioOssConf audioOssConf = SpringContextHolder.getBean(AliAudioOssConf.class);
        AUDIO_BOOK_PATH_PREFIX = audioOssConf.getPrefix();
        AUDIO_LOCAL_BOOK_PATH_PREFIX = audioOssConf.getLocalPrefix();
    }

    public static String getBookPath(String bookId, String prefix) {
        if (StringUtils.isBlank(bookId) || bookId.length() < 11) {
            return StringUtils.EMPTY;
        }

        String one = StringUtils.substring(bookId, 0, 1);
        String two = StringUtils.substring(bookId, 1, 2);
        String three = StringUtils.substring(bookId, 2, 3);
        String four = StringUtils.substring(bookId, 3, 4);

        StringBuilder sb = new StringBuilder(prefix);
        sb.append(one).append("x").append(two).append("/").append(one).append(two).append("x").append(three).append("/")
                .append(one).append(two).append(three).append("x").append(four).append("/").append(bookId).append("/");

        return sb.toString();

    }

    public static String getBookPath(String bookId) {
        return getBookPath(bookId, BOOK_PATH_PREFIX);
    }

    public static String getBookImg(String bookId) {
        return getBookPath(bookId) + bookId + IMG_SUFFIX;
    }

    public static String getAliCdnBookImgPath(String bookId) {
        return CND_DOMAIN + getBookPath(bookId, CND_PREFIX) + bookId + IMG_SUFFIX;
    }

    /**
     * 获取音频书籍路径
     * @param bookId
     * @return
     */
    public static String getAudioBookPath(String bookId) {
        return new StringBuilder(AUDIO_BOOK_PATH_PREFIX).append(bookId).append("/").toString();
    }

    public static boolean existCover(String bookId) {
        return AliOssUtil.doesObjectExist(getBookImg(bookId));
    }

    public static boolean downImg(String bookId, String imgUrl) {
        if (StringUtils.isBlank(imgUrl)) {
            logger.info("bookId={},书籍的封面地址为空", bookId);
            return false;
        }

        Book book = bookSupport.bookService.getBookByBookId(bookId);
        String cpId = book.getPartnerId();
        String prefix = imgUrl.split(":")[0];

        try {
            String bookImgPath = getBookImg(bookId);
            logger.info("cpId_{}, bookId={}, imgUrl={}, bookImgPath={}", cpId, bookId, imgUrl, bookImgPath);

            if (StringUtils.equals(prefix, "http")) {
                URL url = new URL(imgUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                    logger.info("cpId_{},bookId={},responseCode_{},imgUrl={},书籍封面地址有跳转，跳转后地址：newImgUrl={}", cpId, bookId, responseCode, imgUrl, conn.getHeaderField("Location"));
                    imgUrl = conn.getHeaderField("Location");
                    prefix = imgUrl.split(":")[0];
                }
            }

            if (ThirdPart.YUE_LAN_FAN_YU.getCpId().equals(Long.parseLong(cpId))) {
                return BookImageSupport.downloadImage(bookId, imgUrl);
            } else {
                if (StringUtils.equals(prefix, "http")) {
                    MyX509TrustManager.downloadHttpBookCover(imgUrl, bookId);
                } else {
                    MyX509TrustManager.downloadHttpsBookCover(imgUrl, bookId);
                }
            }

            return true;
        } catch (Exception e) {
            logger.warn("cpId_{},bookId={},imgUrl={},封面下载失败", cpId, bookId, imgUrl);
            logger.error(e.getMessage(), e);
            if (StringUtils.startsWith(prefix, "http")) {
                return BookImageSupport.downloadImage(bookId, imgUrl);
            } else {
                return false;
            }
        }
    }

    public static String getCoverWap(String bookId) {
        return bookId + IMG_SUFFIX;
    }

    public static String getLocalPath(String filePath) {
        return LOCAL_PREFIX + filePath;
    }

    public static String getAudioLocalPath(String filePath) {
        return AUDIO_LOCAL_BOOK_PATH_PREFIX + filePath;
    }

}
