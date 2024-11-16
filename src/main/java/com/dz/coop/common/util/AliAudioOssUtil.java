package com.dz.coop.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.dz.coop.common.SpringContextHolder;
import com.dz.coop.conf.properties.AliAudioOssConf;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.support.BookSupport;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;

/**
 * @project: coop-client
 * @description: 阿里音频OSS工具类
 * @author: songwj
 * @date: 2020-12-07 21:37
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class AliAudioOssUtil {

    private static Logger logger = LoggerFactory.getLogger(AliAudioOssUtil.class);

    private static AliAudioOssConf aliOssConf = SpringContextHolder.getBean(AliAudioOssConf.class);
    private static String endpoint = aliOssConf.getEndpoint();
    private static String accessKeyId = aliOssConf.getAccessKeyId();
    private static String accessKeySecret = aliOssConf.getAccessKeySecret();
    private static String bucketName = aliOssConf.getBucketName();

    private static OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

    /**
     * 保存二进制文件
     * @param path 存储路径
     * @param stream
     */
    public static boolean setFile(String path, InputStream stream) {
        try {
            logger.info("写文件内容到OSS[{}]", path);
            ossClient.putObject(bucketName, path, stream);
            return true;
        } catch (Exception e) {
            logger.error("AliAudioOssUtil setFile error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 删除文件
     * @param path 存储路径
     * @return
     */
    public static boolean deleteFile(String path) {
        try {
            ossClient.deleteObject(bucketName, path);
            logger.info("删除OSS[{}]文件内容", path);

            if (StringUtils.equals(System.getProperty(Constant.DISK_SWITCH), Constant.SWITCH_ON)) {
                String localPath = BookSupport.getAudioLocalPath(path);
                FileUtils.deleteQuietly(new File(localPath));
                logger.info("删除本地磁盘[{}]文件内容", localPath);
            }

            return true;
        } catch (Exception e) {
            logger.error("AliOssUtil deleteFile error: {}", e.getMessage(), e);
        }
        return false;
    }

}
