package com.dz.coop.common.aop;

import com.dz.glory.common.support.SpringMVCInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @project: coop-client
 * @description: 请求拦截器
 * @author: songwj
 * @date: 2021-06-03 20:36
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
@Configuration
public class RequestInterceptor extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SpringMVCInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

}
