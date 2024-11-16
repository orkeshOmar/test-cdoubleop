package com.dz.coop.module.service;

import com.dz.content.api.book.vo.AdminCpPartnerQueryRequest;
import com.dz.coop.common.annotation.DynamicDataSource;
import com.dz.coop.module.model.Partner;

import java.util.List;

/**
 * @project: coop-client
 * @description: CP合作伙伴服务层
 * @author: songwj
 * @date: 2021-01-12 20:06
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
public interface PartnerService {

    @DynamicDataSource("dataSourceReadOnly")
    Partner getPartnerById(Long id);

    /**
     * 查询指定条件的Partner列表
     * @param request
     * @return
     */
    List<Partner> listAllPartners(AdminCpPartnerQueryRequest request);

}
