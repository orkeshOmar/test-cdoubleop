package com.dz.coop.module.mapper;


import com.dz.coop.module.model.BookTypeCenter;

import java.util.List;

/**
 * @author panqz 2018-12-10 6:51 PM
 */

public interface BookTypeCenterMapper {
    void save(BookTypeCenter bookTypeCenter);

    List<BookTypeCenter> getBookTypeCenter(BookTypeCenter bookTypeCenter);

}
