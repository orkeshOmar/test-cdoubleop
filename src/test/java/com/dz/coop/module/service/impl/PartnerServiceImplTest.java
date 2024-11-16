package com.dz.coop.module.service.impl;

import com.dz.content.api.book.support.OrderByItem;
import com.dz.content.api.book.vo.AdminCpPartnerQueryRequest;
import com.dz.coop.TestBase;
import com.dz.coop.module.constant.BookTypeEnum;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.PartnerService;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @project: coop-client
 * @description: TODO
 * @author: songwj
 * @date: 2021-04-25 16:21
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
public class PartnerServiceImplTest extends TestBase {

    @Resource
    private PartnerService partnerService;

    @Test
    public void testListAllPartners() throws Exception {
        AdminCpPartnerQueryRequest request = new AdminCpPartnerQueryRequest();

        request.setLimit(Constant.LIMIT);
        request.setIsSync(1);
        request.setBookType(BookTypeEnum.AUDIO.getType() + "");

        List<OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new OrderByItem("id", true));

        request.setOrders(orderByItems);

        List<Partner> partners = partnerService.listAllPartners(request);
        System.out.println(partners.size());
    }

}