package com.dz.coop.module.mapper;


import com.dz.coop.module.model.BookId;

/**
 * @author panqz 2018-10-29 11:07 AM
 */

public interface BookIdMapper {
    void insertBookId(BookId bookId);

    /**
     * 生成有声读物书籍id
     * @param bookId
     */
    void insertAudioBookId(BookId bookId);
}
