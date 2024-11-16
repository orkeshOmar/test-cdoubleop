package com.dz.coop.common.util;

import org.junit.Test;

/**
 * @project: coop-client
 * @description: 音频工具类测试
 * @author: songwj
 * @date: 2019-07-26 19:36
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class AudioUtilTest {

    @Test
    public void testDownload() throws Exception {
        String remoteSourceUrl = "http://yousheng-shayu.oss-cn-beijing.aliyuncs.com/1/9902a47ebcd0bc8f0c3ad07e711f2f96.mp3?OSSAccessKeyId=LTAI4G2KdAs8wMRRHXT49WiX&Expires=1607158985&Signature=4g8seNxLDKoeLgsZSKuBelFutfc%3D";
        String destinationPath = "D:/1338469.mp3";
        AudioUtil.download(remoteSourceUrl, destinationPath);
    }

    @Test
    public void testGetDuration() throws Exception {
        //String audioFilePath = "D:/黄昏里.mp3";
        String audioFilePath = "D:/1338469.mp3";
        long duration = AudioUtil.getDuration(audioFilePath);
        System.out.println(duration);
    }

}