package com.dz.coop.conf;

import com.dz.coop.common.Consume;
import com.dz.rocketmq.consumer.IConsumer;
import com.dz.rocketmq.consumer.RocketMQConsumer;
import com.dz.rocketmq.producer.RocketMQProducer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author panqz 2018-10-30 11:03 AM
 */
@Deprecated
//@Configuration
public class RocketMQConf implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQConf.class);

    @Value("${rocket.address}")
    private String rocketAddress;

    @Value("${rocket.projectName}")
    private String projectName;

    @Value("${rocket.threads}")
    private Integer threads;

    private Map<String, IConsumer> consumers = new HashMap<>();

    @Bean
    public RocketMQConsumer rocketMQConsumer() {
        String pro = System.getProperty("consume.threads");
        threads = StringUtils.isNotBlank(pro) && StringUtils.isNumeric(pro) ? Integer.parseInt(pro) : threads;
        return new RocketMQConsumer(rocketAddress, projectName, consumers);
        //return new RocketMQConsumer(rocketAddress, projectName, consumers, threads);
    }

    @Bean
    public RocketMQProducer rocketMQProducer() {
        return new RocketMQProducer(rocketAddress);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, Consume> beansOfType = context.getBeansOfType(Consume.class);
        if (MapUtils.isNotEmpty(beansOfType)) {
            for (Consume consume : beansOfType.values()) {
                consumers.put(consume.getTopic(), consume);
                logger.info("消费者初始化：topic:{}", consume.getTopic());
            }
        }
    }
}
