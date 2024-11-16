package com.dz.coop.module.service.impl;

import com.dz.coop.module.service.MailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author panqz 2018-12-11 4:09 PM
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ConsumeServiceImplTest {

    @Resource
    private MailService mailService;

    @Test
    public void accept() {

        mailService.sendEmail("handsome");
    }
}