package com.dz.coop.module.listener;

import com.dz.coop.common.Consume;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.service.ConsumeService;
import com.dz.tools.TraceKeyHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author panqz 2018-10-31 1:55 PM
 */
@Component
public class CommonBookListener implements Consume {

    @Resource
    private ConsumeService consumeService;

    private static final String PREFIX = "常规书籍";

    @Override
    public String getTopic() {
        return Constant.TOPIC_BOOK_COMMON;
    }

    @Override
    public void execute(String s) {
        TraceKeyHolder.setUserKey("prefix", PREFIX);
        consumeService.accept(s);
    }
}
