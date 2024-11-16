package com.dz.coop.common;

import com.dz.rocketmq.consumer.IConsumer;

/**
 * @author panqz 2018-10-31 1:58 PM
 */

public interface Consume extends IConsumer {
    String getTopic();
}
