package com.dz.coop.conf.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author panqz 2018-10-30 7:06 PM
 */
@Component
@ConfigurationProperties(prefix = BookProperties.PREFIX)
public class BookProperties {

    public static final String PREFIX = "book";

    public String path;

    public static String getPREFIX() {
        return PREFIX;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
