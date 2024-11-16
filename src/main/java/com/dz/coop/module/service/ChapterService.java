package com.dz.coop.module.service;


import com.dz.content.api.book.vo.BookChapterVO;
import com.dz.coop.common.annotation.DynamicDataSource;
import com.dz.coop.module.model.Chapter;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.vo.SaveChapterResp;

import java.util.List;
import java.util.Map;

/**
 * @author panqz 2018-12-11 11:42 PM
 */

public interface ChapterService {

    SaveChapterResp saveChapter(CPChapter chapter, String bookId, Long volumeId, boolean isInnerDz, int bookType, Long cpId);

    SaveChapterResp updateChapter(String bookId, CPChapter cpChapter, Chapter chapter, boolean isInnerDz, Partner partner);

    /**
     * 更新章节扩展信息表中的音频格式
     * @param chapterId
     * @param audioFormat
     */
    void updateChapterExtend(String bookId, String chapterId, String audioFormat, BookChapterVO bookChapterVO);

    /**
     * 更新章节扩展信息表中的音频格式
     * @param bookId
     * @param chapterId
     * @param cpChapter
     */
    void updateChapterExtend(String bookId, String chapterId, CPChapter cpChapter, Long cpId, BookChapterVO bookChapterVO);

    String getChapterPathSuffix(CPChapter cpChapter, Long cpId);

    @DynamicDataSource("dataSourceReadOnly")
    Chapter getLastChapter(String bookId);

    @DynamicDataSource("dataSourceReadOnly")
    Integer countChapters(String bookId);

    @DynamicDataSource("dataSourceReadOnly")
    Map<String, Object> countBookWordNumAndChapterNum(String bookId);

    @DynamicDataSource("dataSourceReadOnly")
    List<Chapter> listMoreChapter(String bookId);

    List<BookChapterVO> getAllChapterByBookId(String bookId);

    BookChapterVO getBookChapter(Long chapterId);

    Integer updateBookChapter(BookChapterVO bookChapterVO);

}
