package com.dz.coop.conf.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author panqz 2018-12-16 6:59 PM
 */
@Component
@ConfigurationProperties(prefix = "qiniu")
@Deprecated
public class QiNiuConf {

    private String accesskey;
    private String secretkey;
    private String img;
    private String bucket;
    private String prefixkey;

    public String getAccesskey() {
        return accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPrefixkey() {
        return prefixkey;
    }

    public void setPrefixkey(String prefixkey) {
        this.prefixkey = prefixkey;
    }
}
