package com.dz.coop.module.listener;

import com.dz.coop.common.Consume;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.service.ConsumeService;
import com.dz.tools.TraceKeyHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @project: coop-client
 * @description: 听书热门书籍消费
 * @author: songwj
 * @date: 2019-08-26 16:30
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Component
public class HotAudioListener implements Consume {

    @Resource
    private ConsumeService consumeService;

    private static final String PREFIX = "听书热门书籍";

    @Override
    public String getTopic() {
        return Constant.TOPIC_AUDIO_POPULAR;
    }

    @Override
    public void execute(String s) {
        TraceKeyHolder.setUserKey("prefix", PREFIX);
        consumeService.accept(s);
    }

}
