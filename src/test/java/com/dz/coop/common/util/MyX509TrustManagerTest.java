package com.dz.coop.common.util;

import com.dz.coop.TestBase;
import com.dz.coop.module.constant.Constant;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: Http和Https文件下载测试
 * @author: songwj
 * @date: 2019-08-02 14:23
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class MyX509TrustManagerTest extends TestBase {

    static {
        System.setProperty(Constant.DISK_SWITCH, "on");
    }

    @Test
    public void testDownLoadFromUrlHttps() throws Exception {
        String url = "https://opsys.anmaa.com/uploadimg/20200609145831_4207.jpg";
        // String url = "https://static.fengxingss.com/book/cover/e16d5374e41e9b1a15d3f4583198a57e.jpg";
        String path = "D:/123.jpg";
        MyX509TrustManager.downLoadFromUrlHttps(url, path);
    }

    @Test
    public void testDownLoadFromUrlHttp() throws Exception {
        //String url = "http://www.qczww.cn/Upload/cover/5480.jpg";
        String url = "http://www.oneceng.com/files/article/image/0/101/101s.jpg";
        String path = "D:/newImg/1234.jpg";
        MyX509TrustManager.downLoadFromUrlHttp(url, path);
    }

    @Test
    public void testDownloadHttpBookCover() throws Exception {
        String url = "http://www.oneceng.com/files/article/image/0/101/101s.jpg";
        MyX509TrustManager.downloadHttpBookCover(url, "30000000008");
    }

    @Test
    public void testDownloadHttpsBookCover() throws Exception {
        String url = "https://opsys.anmaa.com/uploadimg/20200609145831_4207.jpg";
        MyX509TrustManager.downloadHttpsBookCover(url, "30000000008");
    }

}