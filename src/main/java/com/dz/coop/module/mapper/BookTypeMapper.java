package com.dz.coop.module.mapper;

import com.dz.coop.module.model.BaseBookType;
import org.apache.ibatis.annotations.Param;

/**
 * @author panqz 2018-10-31 10:56 AM
 */

public interface BookTypeMapper {
    void save(@Param("bookId") String bookId, @Param("typeId") Long typeId);

    BaseBookType getBookType(@Param("bookId") String bookId, @Param("baseTypeId") Long baseTypeId);
}
