package com.dz.coop.module.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dz.content.api.book.rpc.AdminCpPartnerRpc;
import com.dz.content.api.book.vo.AdminCpPartnerQueryRequest;
import com.dz.content.api.book.vo.AdminCpPartnerVO;
import com.dz.coop.common.util.BeanUtil;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.PartnerService;
import com.dz.glory.common.vo.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @project: coop-client
 * @description: CP合作伙伴服务层
 * @author: songwj
 * @date: 2021-01-12 20:07
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2021 DIANZHONG TECH. All Rights Reserved.
 */
@Service
public class PartnerServiceImpl implements PartnerService {

    @Reference
    private AdminCpPartnerRpc adminCpPartnerRpc;

    @Override
    public Partner getPartnerById(Long id) {
        AdminCpPartnerVO adminCpPartnerVO = adminCpPartnerRpc.getCpInfo(id).getData();
        return BeanUtil.convert2Partner(adminCpPartnerVO);
    }

    @Override
    public List<Partner> listAllPartners(AdminCpPartnerQueryRequest request) {
        Page<AdminCpPartnerVO> adminCpPartnerVOPage = adminCpPartnerRpc.getList(request).getData();
        List<AdminCpPartnerVO> adminCpPartnerVOs = adminCpPartnerVOPage.getData();
        long totalPage = adminCpPartnerVOPage.getTotalPage();

        if (totalPage > 1) {
            for (int i = 2; i <= totalPage; i++) {
                request.setPageNo(i);
                List<AdminCpPartnerVO> data = adminCpPartnerRpc.getList(request).getData().getData();
                adminCpPartnerVOs.addAll(data);
            }
        }

        return BeanUtil.convert2PartnerList(adminCpPartnerVOs);
    }

}
