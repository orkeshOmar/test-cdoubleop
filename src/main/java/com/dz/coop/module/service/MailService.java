package com.dz.coop.module.service;

/**
 * @author panqz 2018-12-10 10:55 AM
 */

public interface MailService {

    void sendEmail(String text);

    /**
     * 发送邮件
     * @param text 邮件内容
     * @param isLocationOpen 指定位置开关是否开启，true为是
     */
    void sendEmail(String text, boolean isLocationOpen);

    /**
     * 发送邮件
     * @param to 收件人
     * @param subject 主题
     * @param text 邮件内容
     * @param useHtml 是否启用html，true为是
     * @param isLocationOpen 指定位置开关是否开启，true为是
     */
    void sendEmail(String to, String subject, String text, boolean useHtml, boolean isLocationOpen);

}
