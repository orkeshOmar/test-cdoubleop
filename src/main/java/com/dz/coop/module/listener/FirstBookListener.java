package com.dz.coop.module.listener;

import com.dz.coop.common.Consume;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.service.ConsumeService;
import com.dz.tools.TraceKeyHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @project: coop-client
 * @description: 优先消费
 * @author: songwj
 * @date: 2019-04-19 20:40
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Component
public class FirstBookListener implements Consume {

    @Resource
    private ConsumeService consumeService;

    private static final String PREFIX = "优先书籍";

    @Override
    public String getTopic() {
        return Constant.TOPIC_BOOK_FIRST;
    }

    @Override
    public void execute(String content) {
        TraceKeyHolder.setUserKey("prefix", PREFIX);
        consumeService.accept(content);
    }
}
