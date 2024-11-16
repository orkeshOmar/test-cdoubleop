package com.dz.coop.module.support;

/**
 * @author panqz 2018-10-26 7:26 PM
 */
public class ChapterSupport {

    private static final String CHAPTER_SUFFIX = ".txt";

    public static final String AUDIO_CHAPTER_SUFFIX = ".m4a";

    public static final String M4A = "m4a";

    public static String getChapterPath(String bookId, Long chapterId) {
        return BookSupport.getBookPath(bookId) + chapterId + CHAPTER_SUFFIX;
    }

    public static String getAudioChapterPath(String bookId, Long chapterId, String suffixFormat) {
        return BookSupport.getAudioBookPath(bookId) + chapterId + suffixFormat;
    }

}
