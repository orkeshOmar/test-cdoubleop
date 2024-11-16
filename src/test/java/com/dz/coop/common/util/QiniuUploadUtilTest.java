package com.dz.coop.common.util;

import com.dz.coop.module.support.BookSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: TODO
 * @author: songwj
 * @date: 2020-06-17 10:45
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class QiniuUploadUtilTest {

    static {
        System.setProperty("disk.switch", "on");
    }

    @Test
    public void testUploadFileToQiNie() throws Exception {
        String bookId = "30000000008";
        String bookImg = BookSupport.getBookImg(bookId);
        String bucket = "bookimgtest";
        String targetFile = BookSupport.getBookPath(bookId, "cppartner/") + BookSupport.getCoverWap(bookId);
        QiniuUploadUtil.uploadFileToQiNie(bookImg, bucket, targetFile);
    }

}