package com.dz.coop.common.annotation;

import java.lang.annotation.*;

/**
 * @project: coop-client
 * @description: 动态数据源注释
 * @author: songwj
 * @date: 2020-08-07 11:00
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DynamicDataSource {
    String value() default "";
}
