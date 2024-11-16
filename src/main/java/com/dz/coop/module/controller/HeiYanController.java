package com.dz.coop.module.controller;

import com.dz.coop.conf.HeiYanBookConf;
import com.dz.coop.module.mapper.ChapterMapper;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.model.Chapter;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.BookService;
import com.dz.coop.module.service.ClientFactory;
import com.dz.coop.module.service.PartnerService;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.vo.Ret;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @project: coop-client
 * @description: 黑岩书籍处理
 * @author: songwj
 * @date: 2019-06-19 17:04
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RestController
@RequestMapping("/portal/heiyan")
public class HeiYanController {

    private Logger logger = LoggerFactory.getLogger(HeiYanController.class);

    @Resource
    private BookService bookService;

    @Resource
    private ChapterMapper chapterMapper;

    @Resource
    private PartnerService partnerService;

    /**
     * 黑岩爬取书籍和本地书籍自动匹配
     * @return
     */
    @RequestMapping("/autoMatch/{partnerId}")
    public Ret autoMatch(@PathVariable("partnerId") Long partnerId) {
        try {
            Map<String, String> bookMaps = HeiYanBookConf.bookMap;
            Partner partner = partnerService.getPartnerById(partnerId);

            if (partner == null) {
                logger.error("指定的partnerId=[{}]不存在", partnerId.toString());
                return Ret.error(-1, "指定的partnerId=[" + partnerId + "]不存在");
            }

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            ClientService instance = ClientFactory.getInstance(partner.getId());
            List<CPBook> bookList = instance.getBookList(partner);

            if (CollectionUtils.isNotEmpty(bookList)) {
                Map<String, String> map = new HashMap<>();

                for (CPBook cpBook : bookList) {
                    map.put(cpBook.getName(), cpBook.getId());
                }

                for (Map.Entry<String, String> me : bookMaps.entrySet()) {
                    String bookId = me.getKey();
                    String srcBookName = me.getValue();// 原始书籍名称

                    Book book = bookService.getBookByBookId(bookId);

                    if (book != null) {
                        if (map.containsKey(srcBookName)) {
                            String cpBookId = map.get(srcBookName);
                            // 更新书籍的cpBookId
                            bookService.updateCpBookId(bookId, cpBookId);
                            // 将本地书籍cp章节id替换成接口所对应的章节id
                            List<Chapter> chapters = chapterMapper.listChapters(bookId);
                            List<CPVolume> volumeList = instance.getVolumeList(partner, cpBookId);

                            if (CollectionUtils.isNotEmpty(volumeList)) {
                                // 将接口中书籍章节列表按，key：章节名，value：章节id，存放
                                Map<String, String> chaptersMap = new HashMap<>();

                                for (CPVolume cpVolume : volumeList) {
                                    List<CPChapter> chapterList = cpVolume.getChapterList();

                                    if (CollectionUtils.isNotEmpty(chapterList)) {
                                        for (CPChapter cpChapter : chapterList) {
                                            chaptersMap.put(cpChapter.getName(), cpChapter.getId());
                                        }
                                    }
                                }

                                for (Chapter chapter : chapters) {
                                    String name = chapter.getName();

                                    if (chaptersMap.containsKey(name)) {
                                        chapter.setCpChapterId(chaptersMap.get(name));
                                        chapterMapper.updateChapterInfo(chapter);
                                        logger.info("bookId={},bookName={},chapterId={},chapterName={}, cp章节Id更新成功", bookId, book.getName(), chapter.getId(), name);
                                    } else {
                                        logger.info("bookId={},bookName={},chapterId={},chapterName={}接口中没有匹配到本章数据，cp章节Id不执行更新", bookId, book.getName(), chapter.getId(), name);
                                    }
                                }
                            }
                        } else {
                            logger.info("partnerId={}书籍列表接口中不存在书籍：{}", partnerId, srcBookName);
                        }
                    } else {
                        logger.info("bookId={}原始书库不存在", bookId);
                    }
                }
            }

            return Ret.success("success");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            e.printStackTrace();
            return Ret.error(-1, "黑岩书籍自动匹配失败");
        }
    }

}
