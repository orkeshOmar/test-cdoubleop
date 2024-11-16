package com.dz.coop.module.mapper;


import com.dz.coop.module.model.Partner;

import java.util.List;

/**
 * @author panqz 2018-10-26 7:53 PM
 */
public interface PartnerMapper {

    List<Partner> listAllPartners(int bookType);

    Partner getPartnerById(Long id);

    /**
     * 获取所有我方标准的CP
     * @return
     */
    List<Partner> listAllOurStandardPartners();

    /**
     * 获取所有自有的CP
     * @return
     */
    List<Partner> listAllOwnPartners();

}
