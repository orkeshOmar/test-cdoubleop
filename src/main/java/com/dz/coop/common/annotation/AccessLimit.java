package com.dz.coop.common.annotation;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * @project: coop-client
 * @description: 访问限制注释
 * @author: songwj
 * @date: 2020-11-07 17:59
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Order(Ordered.HIGHEST_PRECEDENCE)
public @interface AccessLimit {

    /**
     * 需要做限制的CPID，多个用英文逗号分隔
     * @return
     */
    long[] cpPartnerIds();

    /**
     * 限制时间（单位：秒），多个用英文逗号分隔，与CPID一一对应
     * @return
     */
    int[] limitSecond();

    /**
     * 限制次数，多个用英文逗号分隔，与CPID一一对应
     * @return
     */
    int[] limitCount();

    /**
     * 休眠毫秒数，如果为0则不休眠，则走次数限制逻辑
     * @return
     */
    long[] sleepMillisecond();

}
