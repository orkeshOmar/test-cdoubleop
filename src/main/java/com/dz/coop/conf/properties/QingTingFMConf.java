package com.dz.coop.conf.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @project: coop-client
 * @description: 蜻蜓FM参数配置
 * @author: songwj
 * @date: 2019-11-14 17:52
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Component
@ConfigurationProperties(prefix = "qingTingFM")
public class QingTingFMConf {

    /**
     * 获取访问令牌Url
     */
    private String url;

    /**
     * 授权模式
     */
    private String grantType;

    /**
     * 应用ID
     */
    private String clientId;

    /**
     * 应用密码
     */
    private String clientSecret;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

}
