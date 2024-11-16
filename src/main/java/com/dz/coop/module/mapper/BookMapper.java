package com.dz.coop.module.mapper;

import com.dz.coop.module.model.Book;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author panqz 2018-10-26 7:32 PM
 */

public interface BookMapper {

    void save(Book book);

    List<String> listAllPopularBooks();

    Book getBookByBookId(String bookId);

    Book getBookByCpBookIdAndPartnerId(@Param("cpBookId") String cpBookId, @Param("partnerId") Long partnerId);

    void update(Book book);

    List<String> listRecentUpdateBooks(int bookType);

    /**
     * 获取所有需要更新的音频书籍（不含南京萌鹿和蜻蜓FM的）
     * @return
     */
    List<String> getAllNeedUpdateAudioBookId();

    List<String> getAllLostBookStatusBookId();

    void updateStatus(Book book);

    /**
     * 获取指定创建时间范围的书籍id
     * @param timeStart 起始时间，例；2019-03-27 00:00:00
     * @param timeEnd 终止时间，例；2019-03-27 23:59:59
     * @return
     */
    List<String> getBookIdByTimeRange(@Param("timeStart") String timeStart, @Param("timeEnd") String timeEnd);

    void updateCpBookId(@Param("bookId") String bookId, @Param("cpBookId") String cpBookId);

    /**
     * 更新书籍信息
     */
    void updateBookInfo(Book book);

    List<String> getAllBookIdByPartner(Long partnerId);

}
