package com.dz.coop.module.service.impl;

import com.dz.coop.module.service.MailService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * @author panqz 2018-12-10 10:56 AM
 */
@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    @Resource
    public JavaMailSender mailSender;

    @Value("${email.userName}")
    private String from;

    @Value("${email.to}")
    private String to;

    @Override
    public void sendEmail(String text) {
        sendEmail(to, "抓书异常警报", text, false, false);
    }

    @Override
    public void sendEmail(String text, boolean isLocationOpen) {
        sendEmail(to, "抓书异常警报", text, false, isLocationOpen);
    }

    @Override
    public void sendEmail(String to, String subject, String text, boolean useHtml, boolean isLocationOpen) {
        if (StringUtils.equals(System.getProperty("email.switch"), "on") && isLocationOpen) {
            MimeMessage message = mailSender.createMimeMessage();
            try {
                MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
                messageHelper.setFrom(from);
                messageHelper.setTo(StringUtils.split(to, ";"));
                messageHelper.setSubject(subject);
                messageHelper.setText(text, useHtml);
                messageHelper.setSentDate(new Date());
                mailSender.send(message);
                logger.info("邮件发送成功，主题为：{}，收件人：{}", subject, to);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
