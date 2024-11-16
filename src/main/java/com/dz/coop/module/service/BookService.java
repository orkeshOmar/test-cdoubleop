package com.dz.coop.module.service;


import com.dz.coop.common.annotation.DynamicDataSource;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;

import java.io.InputStream;
import java.util.List;

/**
 * @author panqz 2018-10-29 10:45 AM
 */

public interface BookService {

    String generateBookId();

    /**
     * 生成有声读物书籍id
     * @return
     */
    String generateAudioBookId();

    /**
     * 获取书籍类型（1：文本，2：有声读物）
     * @param partner
     * @return
     */
    Integer getBookType(Partner partner);

    /**
     * 是否是有声读物
     * @param bookType
     * @return
     */
    boolean isAudio(int bookType);

    /**
     * 指定cp是否是有声读物
     * @param partner
     * @return
     */
    boolean isAudio(Partner partner);

    String saveBook(CPBook book);

    Book getBookByBookId(String bookId);

    void updateBookAfterSaveChapter(String bookId);

    /**
     * 更新指定创建时间范围的书籍封面
     * @param timeStart 起始时间，例；2019-03-27 00:00:00
     * @param timeEnd 终止时间，例；2019-03-27 23:59:59
     * @return
     */
    boolean updateBookCoverByTimeRange(String timeStart, String timeEnd);

    void updateCpBookId(String bookId, String cpBookId);

    @DynamicDataSource("dataSourceReadOnly")
    Book getBookByCpBookIdAndPartnerId(String cpBookId, Long partnerId);

    /**
     * 查询最近更新书籍
     * @return
     */
    List<String> listRecentUpdateBooks();

    /**
     * 获取所有拆分书籍ID
     * @return
     */
    List<String> getAllBreakSourceBookIds();

    /**
     * 获取需要更新的听书书籍ID
     * @return
     */
    List<String> getAllNeedUpdateAudioBookId();

    /**
     * 上传封面
     * @param bookId
     * @param in
     * @return
     */
    boolean uploadCover(String bookId, InputStream in);

}
