package com.dz.coop.common.util;

import com.coremedia.iso.IsoFile;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @project: coop-client
 * @description: 音频工具类
 * @author: songwj
 * @date: 2019-07-26 18:09
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class AudioUtil {

    private static Logger logger = LoggerFactory.getLogger(AudioUtil.class);

    /**
     * 下载文件
     * @param remoteSourceUrl 远程资源Url
     * @param destinationPath 文件存放路径
     * @return
     */
    public static boolean download(String remoteSourceUrl, String destinationPath) {
        DataInputStream dis = null;
        OutputStream out = null;

        try {
            long start = System.currentTimeMillis();
            URL url = new URL(remoteSourceUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Mobile Safari/537.36");
            conn.setConnectTimeout(60 * 1000);
            conn.setReadTimeout(60 * 1000);
            dis = new DataInputStream(conn.getInputStream());
            File destinationFile = new File(destinationPath);
            destinationFile.getParentFile().mkdirs();
            out = new FileOutputStream(destinationFile);

            byte[] bt = new byte[10240];
            int len = 0;

            logger.info("开始下载文件：{}", remoteSourceUrl);
            while ((len = dis.read(bt)) != -1) {
                out.write(bt, 0, len);
            }

            logger.info("文件[{}]下载完毕，耗时：{}秒，文件存放路径：{}", remoteSourceUrl, (System.currentTimeMillis() - start) / 1000.0, destinationPath);
            return true;
        } catch (Exception e) {
            logger.error("{}音频文件下载失败：{}", remoteSourceUrl, e.getMessage(), e);
            return false;
        } finally {
            try {
                if (dis != null) {
                    dis.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取文件的播放时长
     * @param audioFilePath 原始音频文件的全路径
     * @return
     */
    public static long getDuration(String audioFilePath) {
        long duration = 0L;

        try {
            if (StringUtils.isBlank(audioFilePath)) {
                logger.error("传入的音频文件路径为空");
                return duration;
            }

            InputStream is = new FileInputStream(audioFilePath);
            byte[] bt = new byte[16];
            is.read(bt, 0, bt.length);
            String audioPrefix = new String(bt);

            // M4A、mp4格式文件
            if (StringUtils.contains(audioPrefix, "ftypM4A") || StringUtils.contains(audioPrefix, "ftypmp42")) {
                IsoFile file = new IsoFile(audioFilePath);
                duration = file.getMovieBox().getMovieHeaderBox().getDuration() / file.getMovieBox().getMovieHeaderBox().getTimescale();
            } else {
                MP3File file = new MP3File(audioFilePath);
                MP3AudioHeader mp3AudioHeader = file.getMP3AudioHeader();
                duration = mp3AudioHeader.getTrackLength();
            }
        } catch (Exception e) {
            logger.error("文件[{}]获取播放时长失败：{}", audioFilePath, e.getMessage(), e);
        }

        return duration;
    }

}
