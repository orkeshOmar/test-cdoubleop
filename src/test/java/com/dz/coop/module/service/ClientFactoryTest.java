package com.dz.coop.module.service;

import com.dz.coop.module.service.cp.ClientService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @project: coop-client
 * @description: TODO
 * @author: songwj
 * @date: 2020-03-13 20:54
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class ClientFactoryTest {

    @Test
    public void testGetOwnPartner() throws Exception {
        Long cpId = 297L;
        ClientService instance = ClientFactory.getInstance(cpId);
        System.out.println(instance);
    }

    @Test
    public void testGetOurStandardPartner() throws Exception {
        Long cpId = 301L;
        ClientService instance = ClientFactory.getInstance(cpId);
        System.out.println(instance);
    }

}