package com.dz.coop.common.util;

import org.junit.Test;

/**
 * @project: coop-client
 * @description: 公共测试
 * @author: songwj
 * @date: 2020-01-15 15:10
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public class CommonTest {

    @Test
    public void testUpdateQingTingFMBookInfo() {
        String[] bookIds = {"30000000387", "30000001450"};

        for (int i = 0; i < bookIds.length; i++) {
            String bookId = bookIds[i];

            HttpUtil.sendGet("http://api.wwread.cn/coop-client/qingtingfm/callback/updateQingTingFMBookInfo/" + bookId);

            System.out.println("第[" + (i + 1) + "]本bookId=" + bookId + "书籍更新成功");
        }
    }

}
