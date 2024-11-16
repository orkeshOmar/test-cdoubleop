package com.dz.coop.module.mapper;

import com.dz.coop.module.model.BreakBookContent;

/**
 * @project: coop-client
 * @description: 未拆完章节内容mapper
 * @author: songwj
 * @date: 2022-01-13 15:21
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2022 DIANZHONG TECH. All Rights Reserved.
 */
public interface BreakBookContentMapper {

    void insertOrUpdate(BreakBookContent breakBookContent);

    BreakBookContent getByBookId(String bookId);

    void deleteByBookId(String bookId);

}
