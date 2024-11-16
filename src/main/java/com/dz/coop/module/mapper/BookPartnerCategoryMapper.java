package com.dz.coop.module.mapper;

import com.dz.coop.module.model.PartnerCategoryModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @project: coop-client
 * @description: 书籍合作伙伴分类映射
 * @author: songwj
 * @date: 2019-05-17 16:48
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public interface BookPartnerCategoryMapper {

    PartnerCategoryModel getCategory(@Param("cpId") Long cpId, @Param("cpCategoryId") String cpCategoryId);

}
