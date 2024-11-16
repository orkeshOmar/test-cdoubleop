package com.dz.coop.module.service;

import com.dz.coop.module.model.PartnerCategoryModel;
/**
 * @project: coop-client
 * @description: 书籍分类服务
 * @author: songwj
 * @date: 2019-05-17 16:27
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public interface BookCategoryService {

    /**
     * 添加书籍分类
     * @param bookId
     * @param cpId
     * @param cpCategoryId
     */
    boolean addCategory(String bookId, Long cpId, String cpCategoryId);

    /**
     * 获取书籍分类
     * @param cpId
     * @param cpCategoryId
     */
    PartnerCategoryModel getCategory(Long cpId, String cpCategoryId);

}
