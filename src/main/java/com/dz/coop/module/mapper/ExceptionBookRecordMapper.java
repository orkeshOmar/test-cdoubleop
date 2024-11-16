package com.dz.coop.module.mapper;

import com.dz.coop.module.model.vo.ExceptionBookRecordVO;

/**
 * @project: coop-client
 * @description: 异常书籍记录持久层
 * @author: songwj
 * @date: 2020-01-07 20:51
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public interface ExceptionBookRecordMapper {

    void save(ExceptionBookRecordVO exceptionBookRecordVO);

    void deleteByBookId(String bookId);

}
