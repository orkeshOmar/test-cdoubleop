package com.dz.coop.common.util;

import com.dz.coop.module.model.Book;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: 发送MQ消息测试
 * @author: songwj
 * @date: 2019-07-27 16:44
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class SendMQMsgUtilTest {

    @Test
    public void testSend() throws Exception {
        String bookId = "30000000008";
        SendMQMsgUtil.send(bookId, 3);
    }

}