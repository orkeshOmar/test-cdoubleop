package com.dz.coop.module.service;


import com.dz.coop.common.annotation.DynamicDataSource;
import com.dz.coop.module.model.Partner;

import java.util.List;

/**
 * @author panqz 2018-10-27 10:51 AM
 */
public interface MsgService {

    @DynamicDataSource("dataSourceReadOnly")
    void produceByPartner(List<Partner> partners);

    @DynamicDataSource("dataSourceReadOnly")
    void produceByPartner(Long partnerId);

    @DynamicDataSource("dataSourceReadOnly")
    void produceByPartner(Partner partner);

    @DynamicDataSource("dataSourceReadOnly")
    void produceByBook(List<String> books);

    @DynamicDataSource("dataSourceReadOnly")
    void produceByBook(String bookId);

    @DynamicDataSource("dataSourceReadOnly")
    void produce(String cpBookId, Long partnerId);

    @DynamicDataSource("dataSourceReadOnly")
    void produce(String cpBookId, Partner partner);

    @DynamicDataSource("dataSourceReadOnly")
    void produce(String bookId, String cpBookId, String lastChapterId, Integer lastChapterIndex, Long partnerId);

    @DynamicDataSource("dataSourceReadOnly")
    void produce(String bookId, String cpBookId, String lastChapterId, Integer lastChapterIndex, Partner partner);

    @DynamicDataSource("dataSourceReadOnly")
    void produceNewBookByPartner(Long partnerId);
    
}
