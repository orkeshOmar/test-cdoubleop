package com.dz.coop.module.mapper;

import com.dz.coop.common.annotation.Check;
import com.dz.coop.module.model.Chapter;
import com.dz.coop.module.model.ChapterExtend;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author panqz 2018-10-29 4:13 PM
 */

public interface ChapterMapper {
    @Check
    void save(Chapter chapter);

    Integer countChapters(String bookId);

    void delete(Long id);

    void deleteChapterExtend(@Param("bookId") String bookId, @Param("chapterId") String chapterId);

    void updateChapter(@Param("id") Long id, @Param("isExist") Integer isExist, @Param("wordNum") Integer wordNum);

    Map<String, Object> countBookWordNumAndChapterNum(String bookId);

    Chapter getLastChapter(String bookId);

    Chapter getChapterByCpChapterId(@Param("bookId") String bookId, @Param("cpChapterId") String cpChapterId);

    List<Chapter> listChapters(String bookId);

    void updateMoreOfChapter(Chapter chapte);

    void deleteMoreChapter(Chapter chapte);

    void modifyCpChapterId(String bookId);

    void modifyCpChapterIdByRange(@Param("bookId") String bookId, @Param("startChapterId") Long startChapterId, @Param("endChapterId") Long endChapterId, @Param("randomMark") String randomMark);

    void updateChapterInfo(Chapter chapter);

    List<Chapter> listMoreChapter(String bookId);

    void saveChapterExtend(ChapterExtend chapterExtend);

    void updateChapterExtend(ChapterExtend chapterExtend);

    ChapterExtend getChapterExtendByChapterId(String chapterId);

    void deleteChapterByBookChapterIdRange(@Param("bookId") String bookId, @Param("startChapterId") Long startChapterId, @Param("endChapterId") Long endChapterId);

    /**
     * 更新书籍指定章节范围的卷id
     * @param bookId
     * @param startChapterId
     * @param endChapterId
     * @param volumeId
     */
    void updateVolumeIdByBookIdAndChapterRange(@Param("bookId") String bookId, @Param("startChapterId") Long startChapterId, @Param("endChapterId") Long endChapterId, @Param("volumeId") String volumeId);
}
