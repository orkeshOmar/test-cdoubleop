package com.dz.coop.module.mapper;


import com.dz.coop.module.model.PartnerUrl;

/**
 * @author panqz 2018-10-27 10:46 AM
 */

public interface PartnerUrlMapper {
    PartnerUrl getPartnerUrlByPartnerId(Long id);
}
