package com.dz.coop.common.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.dz.coop.common.SpringContextHolder;
import com.dz.coop.conf.properties.AliOssConf;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.support.BookSupport;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @project: coop-client
 * @description: 阿里OSS处理工具
 * @author: songwj
 * @date: 2020-04-21 21:01
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class AliOssUtil {

    private static Logger log = LoggerFactory.getLogger(AliOssUtil.class);

    private static AliOssConf aliOssConf = SpringContextHolder.getBean(AliOssConf.class);
    private static String endpoint = aliOssConf.getEndpoint();
    private static String accessKeyId = aliOssConf.getAccessKeyId();
    private static String accessKeySecret = aliOssConf.getAccessKeySecret();
    private static String bucketName = aliOssConf.getBucketName();
    private static String LOCAL_PREFIX = "/";

    private static OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

    /**
     * 获取章节内容
     * @param path 路径
     * @return
     */
    public static String getContent(String path) {
        try {
            if (doesObjectExist(path)) {
                log.info("从OSS[{}]读取文本内容", path);
                OSSObject ossObject = ossClient.getObject(bucketName, path);
                // 读取文件内容
                BufferedReader reader = new BufferedReader(new InputStreamReader(ossObject.getObjectContent()));

                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                // 数据读取完成后，获取的流必须关闭，否则会造成连接泄漏，导致请求无连接可用，程序无法正常工作
                reader.close();
                return sb.toString();
            } else if (StringUtils.equals(System.getProperty(Constant.DISK_SWITCH), Constant.SWITCH_ON)) {
                log.info("从本地磁盘[{}]读取文本内容", LOCAL_PREFIX + path);
                String content = FileUtils.readFileToString(new File(LOCAL_PREFIX + path), "UTF-8");
                return content;
            }
        } catch (Exception e) {
            log.error("AliOssUtil getContent error: {}", e.getMessage(), e);
        }

        return StringUtils.EMPTY;
    }

    /**
     * 保存书籍内容
     * @param path    路径
     * @param content 章节内容
     * @return
     */
    public static boolean setContent(String path, String content) {
        try {
            log.info("写文本内容到OSS[{}]", path);
            ossClient.putObject(bucketName, path, new ByteArrayInputStream(content.getBytes("utf-8")));

            if (StringUtils.equals(System.getProperty(Constant.DISK_SWITCH), Constant.SWITCH_ON)) {
                log.info("写文本内容到本地磁盘[{}]", LOCAL_PREFIX + path);
                FileUtils.writeStringToFile(new File(LOCAL_PREFIX + path), content, "UTF-8");
            }

            return true;
        } catch (Exception e) {
            log.error("AliOssUtil setContent error: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 保存二进制文件
     * @param path 存储路径
     * @param stream
     */
    public static boolean setFile(String path, InputStream stream) {
        try {
            if (StringUtils.equals(System.getProperty(Constant.DISK_SWITCH), Constant.SWITCH_ON)) {
                String localPath = LOCAL_PREFIX + path;
                log.info("写文件内容到本地磁盘[{}]", localPath);

                File localFile = new File(localPath);
                if (!localFile.exists()) {
                    FileOutputStream output = FileUtils.openOutputStream(localFile);
                    IOUtils.copy(stream, output);
                    IOUtils.closeQuietly(output);
                }
            }

            log.info("写文件内容到OSS[{}]", path);
            ossClient.putObject(bucketName, path, stream);

            return true;
        } catch (Exception e) {
            log.error("AliOssUtil setFile error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 保存二进制文件
     * @param path 存储路径
     * @param stream
     */
    public static boolean setCover(String path, InputStream stream) {
        try {
            if (StringUtils.equals(System.getProperty(Constant.DISK_SWITCH), Constant.SWITCH_ON)) {
                log.info("写文件内容到本地磁盘[{}]", LOCAL_PREFIX + path);
                FileOutputStream output = FileUtils.openOutputStream(new File(LOCAL_PREFIX + path));
                IOUtils.copy(stream, output);
                IOUtils.closeQuietly(output);
            }

            // 如果流已经关闭则重新读取
            if (stream.available() == 0) {
                stream = FileUtils.openInputStream(new File(LOCAL_PREFIX + path));
            }

            log.info("写文件内容到OSS[{}]", path);
            ossClient.putObject(bucketName, path, stream);

            return true;
        } catch (Exception e) {
            log.error("AliOssUtil setCover error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取文件
     * @param path 路径
     * @return
     */
    public static InputStream getFile(String path) {
        try {
            if (doesObjectExist(path)) {
                log.info("从OSS[{}]读取文件内容", path);
                OSSObject ossObject = ossClient.getObject(bucketName, path);
                InputStream objectContent = ossObject.getObjectContent();
                return objectContent;
            } else if (StringUtils.equals(System.getProperty(Constant.DISK_SWITCH), Constant.SWITCH_ON)) {
                log.info("从本地磁盘[{}]读取文件内容", LOCAL_PREFIX + path);
                return new FileInputStream(new File(LOCAL_PREFIX + path));
            }
        } catch (Exception e) {
            log.error("AliOssUtil getFile error: {}", e.getMessage(), e);
        }

        return null;
    }

    /**
     * copy文件
     * @param sourceObjectName      文件源地址
     * @param destinationObjectName 目标地址
     * @return
     */
    public static boolean copyFile(String sourceObjectName, String destinationObjectName) {
        try {
            ossClient.copyObject(bucketName, sourceObjectName, bucketName, destinationObjectName);
            return true;
        } catch (Exception e) {
            log.error("AliOssUtil setFile error: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 删除文件
     * @param path 存储路径
     * @return
     */
    public static boolean deleteFile(String path) {
        try {
            ossClient.deleteObject(bucketName, path);
            log.info("删除OSS[{}]文件内容", path);

            if (StringUtils.equals(System.getProperty(Constant.DISK_SWITCH), Constant.SWITCH_ON)) {
                String localPath = BookSupport.getLocalPath(path);
                FileUtils.deleteQuietly(new File(localPath));
                log.info("删除本地磁盘[{}]文件内容", localPath);
            }

            return true;
        } catch (Exception e) {
            log.error("AliOssUtil deleteFile error: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 获取文件大小
     * @param path 路径
     * @return
     */
    public static Long getFileLength(String path) {
        try {
            OSSObject ossObject = ossClient.getObject(bucketName, path);
            // 读取文件内容
            long length = ossObject.getObjectMetadata().getContentLength();
            return length;
        } catch (Exception e) {
            log.error("AliOssUtil getFileLength error: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 删除文件夹
     * @param path 路径
     * @return
     */
    public static boolean deleteDirectory(String path) {
        try {
            boolean existFile = true;
            ObjectListing objectListing = ossClient.listObjects(bucketName, path);

            while (existFile) {
                List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
                if (CollectionUtils.isNotEmpty(objectSummaries)) {
                    List<String> keys = new ArrayList<>();
                    for (OSSObjectSummary summary : objectSummaries) {
                        keys.add(summary.getKey());
                    }
                    ossClient.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(keys));
                } else {
                    ossClient.deleteObject(bucketName, path);
                    existFile = false;
                }
            }

            return true;
        } catch (Exception e) {
            log.error("AliOssUtil deleteDirectory error: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 列出文件夹下的所有文件
     * @param path 路径
     * @return
     */
    public static List<String> listFiles(String path) {
        List<String> fileList = new ArrayList<>();
        try {
            boolean existFile = true;
            ObjectListing objectListing = ossClient.listObjects(bucketName, path);

            while (existFile) {
                List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
                if (CollectionUtils.isNotEmpty(objectSummaries)) {
                    for (OSSObjectSummary summary : objectSummaries) {
                        fileList.add(summary.getKey());
                    }
                } else {
                    existFile = false;
                }
            }

            return fileList;
        } catch (Exception e) {
            log.error("AliOssUtil listFiles error: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 判断文件是否存在
     * @param path 路径
     * @return
     */
    public static boolean doesObjectExist(String path) {
        return ossClient.doesObjectExist(bucketName, path);
    }

}
