package com.dz.coop.module.service;

import com.dz.content.api.book.vo.DealTypeParam;
import com.dz.coop.common.annotation.DynamicDataSource;

/**
 * @author supf@dianzhong.com
 * @date 2024/06/25 14:55
 */
public interface ShortStoryService {

    @DynamicDataSource("dataSourceReadOnly")
    void updateContentByPartner(String bookId, String cpBookId, Long partnerId);

    @DynamicDataSource("dataSourceReadOnly")
    DealTypeParam checkNeedAddType(String bookId);
}
