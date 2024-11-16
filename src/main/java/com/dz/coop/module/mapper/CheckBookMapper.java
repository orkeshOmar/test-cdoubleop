package com.dz.coop.module.mapper;

import com.dz.coop.module.model.CheckBook;

/**
 * @project: coop-client
 * @description: TODO
 * @author: songwj
 * @date: 2023-05-19 7:59 下午
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2023 DIANZHONG TECH. All Rights Reserved.
 */
public interface CheckBookMapper {

    CheckBook getCheckBook(String bookId);

}