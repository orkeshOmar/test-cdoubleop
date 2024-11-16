package com.dz.coop.common.util;

import com.dz.coop.common.SpringContextHolder;
import com.dz.coop.conf.properties.QiNiuConf;
import com.dz.coop.module.constant.Constant;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class QiniuUploadUtil {

    public static final Auth auth;

    private static final String BUCKET;

    private static final String IMG_URL;

    private static final String LOCAL = "/";

    static {
        QiNiuConf bean = SpringContextHolder.getBean(QiNiuConf.class);
        auth = Auth.create(bean.getAccesskey(), bean.getSecretkey());

        BUCKET = bean.getBucket();

        IMG_URL = bean.getImg();
    }

    public static final Logger logger = LoggerFactory.getLogger(QiniuUploadUtil.class);

    /**
     * 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
     */
    public static UploadManager uploadManager = new UploadManager();

    /**
     * 上传数据到CND
     * @param data
     * @param key
     * @param upToken
     * @return
     */
    public static boolean upload(byte[] data, String key, String upToken) {
        boolean flag = false;
        try {
            Response res = uploadManager.put(data, key, upToken);
            if (res.isOK()) {
                //success
                flag = true;
            }
        } catch (QiniuException e) {
            logger.info(e.getMessage(), e);
        }
        return flag;
    }

    public static boolean uploadFileToQiNie(String srcFile, String bucket, String targetFile) {
        boolean flag = false;
        InputStream in = null;

        try {
            if (AliOssUtil.doesObjectExist(srcFile)) {
                in = AliOssUtil.getFile(srcFile);
            } else if (StringUtils.equals(System.getProperty(Constant.DISK_SWITCH), Constant.SWITCH_ON)) {
                in = FileUtils.openInputStream(new File(LOCAL + srcFile));
            }

            String upToken = auth.uploadToken(bucket, targetFile);
            byte[] byteArray;
            try {
                byteArray = IOUtils.toByteArray(in);
                logger.info("开始上传CND");
                flag = upload(byteArray, targetFile, upToken);
                logger.info("上传CDN结束");
                if (flag) {
                    String refreshUrl = IMG_URL + targetFile;
                    flag = RefreshUtil.fresh(getJsonOfUrls(refreshUrl));
                    logger.info("刷新CND结束, imgUrl={}", refreshUrl);
                }
            } catch (IOException e) {
                logger.error("文件转换字节流发生异常！" + srcFile, e);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(in);
        }
        return flag;
    }

    private static String getJsonOfUrls(String str) {
        Map<String, Object> map = new HashMap<>(1);
        String[] arru = new String[]{str};
        map.put("urls", arru);
        return JsonUtils.toJSON(map);
    }

}
