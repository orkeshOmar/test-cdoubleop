package com.dz.coop.module.support;

import com.dz.coop.module.constant.Constant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: 书籍处理工具类测试
 * @author: songwj
 * @date: 2019-08-02 15:18
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("hot")
public class BookSupportTest {

    static {
        System.setProperty(Constant.DISK_SWITCH, "on");
    }

    @Test
    public void testDownImg() throws Exception {
        //String url = "http://www.cmread.com/wenxue/api/images/book/14696934413496?w=200&h=260";// http 302跳转至 https
        //String url = "https://www.cmread.com/wenxue/api/images/book/14642321787917?w=200&h=260";// https不跳转
        //String url = "https://opsys.anmaa.com/uploadimg/20200609145831_4207.jpg";// https不跳转
        //String url = "https://static.fengxingss.com/book/cover/e16d5374e41e9b1a15d3f4583198a57e.jpg";// https不跳转
        //String url = "http://www.oneceng.com/files/article/image/0/101/101s.jpg";// http不跳转
        //String url = "http://file.lingyun5.com/cover/500/201101010252303842/n8rTK0yv.jpg";// http 301跳转至 https
        //String url = "https://opsys.anmaa.com/uploadimg/20190409113352_9935.jpg";// https不跳转，有证书校验
        //String url = "https://admin.lanmaoyd.com/api/uploads/file/07a7ecb0129a4124546b329a2cbc84b2_20220718095941.jpg";
        String url = "https://www.zuok.cn/down/upload/book/16002929903004/20221020102600/m.jpg"; // 304，南风无归期，悦蓝泛娱
        boolean res = BookSupport.downImg("11000102235", url);
        //boolean res = BookImageSupport.downloadImage("11000102237", url);
        System.out.println("图片下载" + (res ? "成功" : "失败"));
    }

}