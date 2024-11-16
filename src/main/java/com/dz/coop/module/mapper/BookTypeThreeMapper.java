package com.dz.coop.module.mapper;


import com.dz.coop.module.model.BookTypeThree;

import java.util.List;

/**
 * @Author: sunxm
 * @Description:
 * @Date: Created in 下午9:58 2018/1/23
 */
public interface BookTypeThreeMapper {
    List<BookTypeThree> getListPage(BookTypeThree bookTypeThree);

    void delBookTypeThree(List<Long> idList);

    void updateStatus(BookTypeThree bookTypeThree);

    BookTypeThree getBookTypeThree(Integer id);

    void addBookTypeThree(BookTypeThree bookTypeThree);

    void updateBookTypeThree(BookTypeThree bookTypeThree);

    BookTypeThree selectName(BookTypeThree bookTypeThree);

    BookTypeThree getByName(BookTypeThree bookTypeThree);

    List<BookTypeThree> getAllTypeThree();
}
