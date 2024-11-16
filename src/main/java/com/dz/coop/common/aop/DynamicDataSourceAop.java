package com.dz.coop.common.aop;

import com.dz.coop.common.annotation.DynamicDataSource;
import com.dz.dydaso.DataSourceContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @project: coop-client
 * @description: 动态数据源AOP
 * @author: songwj
 * @date: 2020-08-07 11:42
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@Component
@Aspect
public class DynamicDataSourceAop {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAop.class);

    @Pointcut("execution(* com.dz.coop.module.service.*.*(..))")
    public void cutPoint() {
    }

    @Before("cutPoint()")
    public void before(JoinPoint joinPoint) {
        try {
            Object target = joinPoint.getTarget();
            if(target == null){
                return;
            }

            Class<?>[] clazz = target.getClass().getInterfaces();
            if(clazz == null || clazz.length < 1){
                return;
            }

            Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
            if(parameterTypes == null || parameterTypes.length < 1){
                return;
            }

            String methodName = joinPoint.getSignature().getName();
            if(StringUtils.isBlank(methodName)){
                return;
            }
            for(Class<?> c:clazz){
                try {
                    Method method = c.getMethod(methodName, parameterTypes);
                    if (method == null) {
                        return;
                    }

                    DynamicDataSource dynamicDataSource = method.getAnnotation(DynamicDataSource.class);
                    if (dynamicDataSource != null) {
                        DataSourceContextHolder.setName(dynamicDataSource.value());
                    }
                }catch(NoSuchMethodException e){
                    logger.error(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @After("cutPoint()")
    public void after() {
        DataSourceContextHolder.clearName();
    }

}
