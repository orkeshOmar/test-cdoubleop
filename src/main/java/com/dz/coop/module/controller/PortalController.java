package com.dz.coop.module.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.dz.content.api.book.rpc.AdminBookRpc;
import com.dz.content.api.book.vo.AdminBookVO;
import com.dz.content.api.book.vo.AdminCpPartnerQueryRequest;
import com.dz.content.api.book.vo.BookChapterVO;
import com.dz.coop.common.exception.BookException;
import com.dz.coop.common.util.*;
import com.dz.coop.module.constant.BookTypeEnum;
import com.dz.coop.module.constant.ChapterModifySyncTypeEnum;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.constant.UpdateBookTypeEnum;
import com.dz.coop.module.mapper.BookMapper;
import com.dz.coop.module.mapper.ChapterMapper;
import com.dz.coop.module.mapper.VolumeMapper;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPCategory;
import com.dz.coop.module.service.*;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.coop.module.support.BookSupport;
import com.dz.coop.module.support.ChapterSupport;
import com.dz.glory.common.jedis.client.JedisClient;
import com.dz.glory.common.jedis.support.Key;
import com.dz.tools.JsonUtil;
import com.dz.tools.TraceKeyHolder;
import com.dz.vo.Ret;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author panqz 2018-10-26 7:34 PM
 */
@RestController
@RequestMapping("/portal/book/")
public class PortalController {
    private static final Logger logger = LoggerFactory.getLogger(PortalController.class);

    @Resource
    private MsgService MsgService;

    @Resource
    private SyncService syncService;

    @Resource
    private PartnerService partnerService;

    @Resource
    private BookMapper bookMapper;

    @Resource
    private VolumeMapper volumeMapper;

    @Resource
    private ChapterMapper chapterMapper;

    @Resource
    private ChapterService chapterService;

    @Resource
    private MailService mailService;

    @Resource
    private BookService bookService;

    @Resource
    private BookCategoryService bookCategoryService;

    @Resource
    private ExceptionBookRecordService exceptionBookRecordService;

    @Resource
    private JedisClient jedisClient;

    @Resource
    private BookBreakChapterSyncService bookBreakChapterSyncService;

    @Reference
    private AdminBookRpc adminBookRpc;

    @RequestMapping("/sync")
    @ResponseBody
    public Ret portal(@RequestBody CPBook cpBook) {
        try {

            CheckUtil.check(cpBook);

            syncService.sync(cpBook);

            return Ret.success("SUCCESS");

        } catch (BookException e) {

            mailService.sendEmail(e.getMessage());

            logger.error(e.getMessage(), e);
            return Ret.error(-1, "ERROR");

        } catch (Exception e) {

            mailService.sendEmail(e.getMessage());

            logger.error(e.getMessage(), e);
            return Ret.error(-1, "ERROR");
        }
    }

    /**
     * 单本书覆盖
     * @param bookId
     * @param isforceCover
     * @return
     */
    @RequestMapping("/cover")
    @ResponseBody
    public Ret portal(String bookId, boolean isforceCover) {
        try {
            TraceKeyHolder.setUserKey("topic", bookId.startsWith("3") ? Constant.TOPIC_AUDIO_FIRST : Constant.TOPIC_BOOK_FIRST);
            syncService.cover(bookId, isforceCover);

            return Ret.success("SUCCESS");

        } catch (BookException e) {

            mailService.sendEmail(e.getMessage());

            logger.error(e.getMessage(), e);
            return Ret.error(-1, "ERROR");

        } catch (Exception e) {

            mailService.sendEmail(e.getMessage());

            logger.error(e.getMessage(), e);
            return Ret.error(-1, "ERROR");
        }
    }

    /**
     * 批量覆盖书籍
     * @param bookIds 书籍id，多个id用“-”分隔，例如：11000000027-11000000028
     * @param isforceCover 是否强制覆盖，true为是
     * @return
     */
    @RequestMapping("/batchCover")
    @ResponseBody
    public Ret batchCover(String bookIds, boolean isforceCover) {
        try {
            String[] bookIdArr = bookIds.split("-");

            logger.info("开始批量覆盖书籍，批量书籍id=[{}]", bookIds);
            for (String bookId : bookIdArr) {
                try {
                    TraceKeyHolder.setUserKey("topic", bookId.startsWith("3") ? Constant.TOPIC_AUDIO_FIRST : Constant.TOPIC_BOOK_FIRST);
                    syncService.cover(bookId, isforceCover);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    continue;
                }
            }
            logger.info("批量覆盖书籍结束");

            return Ret.success("Batch cover bookIds = [" + bookIds + "] success");
        } catch (BookException e) {
            mailService.sendEmail(e.getMessage());
            logger.error(e.getMessage(), e);
            return Ret.error(-1, "batch cover error");
        } catch (Exception e) {
            mailService.sendEmail(e.getMessage());
            logger.error(e.getMessage(), e);
            return Ret.error(-1, "batch cover error");
        }
    }

    /**
     * 覆盖指定cp的所有书籍
     * @param partnerId
     * @param isforceCover
     * @return
     */
    @RequestMapping("/coverByPartner/{partnerId}")
    @ResponseBody
    public Ret coverByPartner(@PathVariable("partnerId") Long partnerId, boolean isforceCover) {
        try {
            Partner partner = partnerService.getPartnerById(partnerId);

            if (partner == null) {
                logger.error("指定的partnerId=[{}]不存在", partnerId.toString());
                return Ret.error(-1, "指定的partnerId=[" + partnerId + "]不存在");
            }

            TraceKeyHolder.setUserKey("topic", partner.getBookType() == 2 ? Constant.TOPIC_AUDIO_FIRST : Constant.TOPIC_BOOK_FIRST);

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            ClientService instance = ClientFactory.getInstance(partner.getId());
            List<CPBook> bookList = instance.getBookList(partner);
            String partnerName = partner.getName();

            if (CollectionUtils.isNotEmpty(bookList)) {
                logger.info("开始批量覆盖[partnerName={}][partnerId={}]书籍，覆盖书籍数量为{}本", partnerName, partnerId.toString(), bookList.size());
                for (CPBook cpBook : bookList) {
                    try {
                        Book book = bookService.getBookByCpBookIdAndPartnerId(cpBook.getId(), partnerId);
                        syncService.cover(book.getBookId(), isforceCover);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        continue;
                    }
                }
                logger.info("批量覆盖[partnerName={}]partnerId=[{}]书籍结束", partnerName, partnerId.toString());
            }

            return Ret.success("覆盖[" + partnerName + "]所有书籍成功");
        } catch (BookException e) {
            mailService.sendEmail(e.getMessage());
            logger.error(e.getMessage(), e);
            return Ret.error(-1, "ERROR");
        } catch (Exception e) {
            mailService.sendEmail(e.getMessage());
            logger.error(e.getMessage(), e);
            return Ret.error(-1, "ERROR");
        }
    }

    /**
     * 覆盖指定章节id范围的内容
     * @param bookId 书籍id
     * @param startChapterId 从指定章节id开始，0表示从头开始
     * @param endChapterId 从指定章节id结束，-1表示到最后一章
     * @param isforceCover 是否强制覆盖，true为是
     * @return
     */
    @RequestMapping("/coverByChapterRange")
    @ResponseBody
    public Ret coverByChapterRange(String bookId, Long startChapterId, Long endChapterId, boolean isforceCover) {
        try {
            TraceKeyHolder.setUserKey("topic", bookId.startsWith("3") ? Constant.TOPIC_AUDIO_FIRST : Constant.TOPIC_BOOK_FIRST);

            syncService.cover(bookId, startChapterId, endChapterId, isforceCover);

            return Ret.success("SUCCESS");
        } catch (BookException e) {
            mailService.sendEmail(e.getMessage());

            logger.error(e.getMessage(), e);

            return Ret.error(-1, "ERROR");
        } catch (Exception e) {
            String srcMsg = e.getMessage();
            mailService.sendEmail(srcMsg);
            logger.error(srcMsg, e);
            exceptionBookRecordService.saveExceptionBookRecord(srcMsg);
            return Ret.error(-1, "ERROR");
        }
    }

    /**
     * 同步所有cp
     * @return Ret
     */
    @RequestMapping("/common")
    @ResponseBody
    public Ret syncCommon(Integer bookType) {
        if (jedisClient.setnx(2, new Key("COM"), 1, 30 * 60) > 0) {
            bookType = bookType == null ? BookTypeEnum.TXT.getType() : bookType;
            TraceKeyHolder.setUserKey("topic", bookType == BookTypeEnum.TXT.getType() ? Constant.TOPIC_BOOK_COMMON : Constant.TOPIC_AUDIO_COMMON);

            AdminCpPartnerQueryRequest request = new AdminCpPartnerQueryRequest();
            request.setLimit(Constant.LIMIT);
            request.setIsSync(1);
            request.setBookType(bookType + "");

            MsgService.produceByPartner(partnerService.listAllPartners(request));
        }

        return Ret.success("success");
    }

    /**
     * 同步热门书籍
     * @return Ret
     */
    @RequestMapping("/hot")
    @ResponseBody
    public Ret syncHot(Integer bookType) {
        bookType = bookType == null ? BookTypeEnum.TXT.getType() : bookType;
        TraceKeyHolder.setUserKey("topic", bookType == BookTypeEnum.TXT.getType() ? Constant.TOPIC_BOOK_POPULAR : Constant.TOPIC_AUDIO_POPULAR);

        List<String> bookIds = bookService.listRecentUpdateBooks();

        // 拆分的原始书籍做到半小时更新一次
        bookIds.addAll(bookService.getAllBreakSourceBookIds());

        bookIds = bookIds.stream().distinct().collect(Collectors.toList());

        MsgService.produceByBook(bookIds);
        return Ret.success("success");
    }

    /**
     * 同步某本书籍
     * @param bookId 书籍id
     * @return Ret
     */
    @RequestMapping("/book/{bookId}")
    @ResponseBody
    public Ret syncByBookId(@PathVariable("bookId") String bookId) {
        try {
            TraceKeyHolder.setUserKey("topic", bookId.startsWith("3") ? Constant.TOPIC_AUDIO_FIRST : Constant.TOPIC_BOOK_FIRST);
            MsgService.produceByBook(bookId);
            return Ret.success("success");
        } catch (Exception e) {
            logger.error("书籍bookId={} 新章节抓取失败：{}", bookId, e.getMessage(), e);
            return Ret.error(-1, "书籍 " + bookId + " 新章节抓取失败");
        }
    }

    /**
     * 同步某家cp
     * @param partnerId cpid
     * @return Ret
     */
    @RequestMapping("/partner/{partnerId}")
    @ResponseBody
    public Ret syncByPartnerId(@PathVariable("partnerId") Long partnerId) {
        Partner partner = partnerService.getPartnerById(partnerId);

        if (partner == null) {
            logger.error("指定的partnerId=[{}]不存在", partnerId.toString());
            return Ret.error(-1, "指定的partnerId=[" + partnerId + "]不存在");
        }

        TraceKeyHolder.setUserKey("topic", partner.getBookType() == BookTypeEnum.AUDIO.getType() ? Constant.TOPIC_AUDIO_FIRST : Constant.TOPIC_BOOK_FIRST);
        MsgService.produceByPartner(partnerId);
        return Ret.success("success");
    }

    /**
     * 同步指定CP的所有新书
     * @param partnerId
     * @return
     */
    @RequestMapping("/partner/newBook/{partnerId}")
    @ResponseBody
    public Ret syncNewBookByPartnerId(@PathVariable("partnerId") Long partnerId) {
        Partner partner = partnerService.getPartnerById(partnerId);

        if (partner == null) {
            logger.error("指定的partnerId=[{}]不存在", partnerId.toString());
            return Ret.error(-1, "指定的partnerId=[" + partnerId + "]不存在");
        }

        TraceKeyHolder.setUserKey("topic", partner.getBookType() == BookTypeEnum.AUDIO.getType() ? Constant.TOPIC_AUDIO_FIRST : Constant.TOPIC_BOOK_FIRST);
        MsgService.produceNewBookByPartner(partnerId);
        return Ret.success("success");
    }

    /**
     * 同步某家cp的某本书
     * @param partnerId cpid
     * @param cpBookId  cpbookID
     * @return Ret
     */
    @RequestMapping("/partner/{partnerId}/{cpBookId}")
    @ResponseBody
    public Ret syncByPartnerId(@PathVariable("partnerId") Long partnerId, @PathVariable("cpBookId") String cpBookId) {
        Partner partner = partnerService.getPartnerById(partnerId);

        if (partner == null) {
            logger.error("指定的partnerId=[{}]不存在", partnerId.toString());
            return Ret.error(-1, "指定的partnerId=[" + partnerId + "]不存在");
        }

        TraceKeyHolder.setUserKey("topic", partner.getBookType() == BookTypeEnum.AUDIO.getType() ? Constant.TOPIC_AUDIO_FIRST : Constant.TOPIC_BOOK_FIRST);
        MsgService.produce(cpBookId, partnerId);
        return Ret.success("success");
    }

    /**
     * 更新指定书籍的封面、书名、简介
     * @param bookId 本地书籍id
     * @param needUpdateCover
     * @param needUpdateBookName
     * @param needUpdateIntroduction
     * @param marketStatus 书籍上架状态，1：上架，2：下架，8：入库，10：删除
     * @return
     */
    @RequestMapping(value = "/updateBookInfo/{bookId}")
    @ResponseBody
    public Ret updateBookInfo(@PathVariable("bookId") String bookId, boolean needUpdateCover, boolean needUpdateBookName, boolean needUpdateIntroduction, Integer marketStatus) {
        try {
            AdminBookVO book = adminBookRpc.getBookInfo(bookId).getData();

            if (book == null) {
                return Ret.error(-1, "未查询到bookId=[" + bookId + "]所对应的书籍信息");
            }

            Partner partner = partnerService.getPartnerById(book.getCpPartnerId());

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            ClientService clientService = ClientFactory.getInstance(partner.getId());
            CPBook cpbook = clientService.getBookInfo(partner, book.getCpBookId());

            if (cpbook != null) {
                Map<String, Object> bookMap = new HashMap<>(4);
                // 更新封面
                if (needUpdateCover) {
                    if (StringUtils.isNotBlank(cpbook.getCover())) {
                        boolean res = BookSupport.downImg(bookId, cpbook.getCover());

                        if (res == false) {
                            return Ret.success("书籍封面bookId=[" + bookId + "]更新失败");
                        }

                        bookMap.put("coverWap", bookId + BookSupport.IMG_SUFFIX);
                        SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.BOOK_COVER_UPDATE.getType());
                    } else {
                        return Ret.error(-1, "书籍bookId=[" + bookId + "]封面地址为空");
                    }
                }

                bookMap.put("bookId", bookId);
                boolean needUpdate = needUpdateCover || needUpdateBookName || needUpdateIntroduction || marketStatus != null;

                // 更新书名
                if (needUpdateBookName) {
                    bookMap.put("bookName", cpbook.getName());
                }

                // 更新简介
                if (needUpdateIntroduction) {
                    String brief = EscapeUtil.escape(cpbook.getBrief());
                    bookMap.put("introduction", StringUtil.removeEmptyLines(brief));
                }

                // 修改书籍上架状态
                if (marketStatus != null) {
                    bookMap.put("marketStatus", marketStatus);
                }

                if (needUpdate) {
                    AdminBookVO data = adminBookRpc.updateBookInfo(bookMap).getData();
                    logger.info("书籍信息更新成功，bookInfo: {}", JsonUtil.obj2Json(data));
                    SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.BOOK_UPDATE.getType());
                }
            }

            return Ret.success("书籍bookId=[" + bookId + "]信息更新成功");
        } catch (Exception e) {
            logger.error("书籍bookId={}信息更新失败!!!", bookId, e);
            return Ret.error(-1, "书籍bookId=[" + bookId + "]信息更新失败");
        }
    }

    /**
     * 更新指定cp的所有书籍封面
     * @param partnerId
     * @return
     */
    @RequestMapping(value = "/updateCoverByPartnerId/{partnerId}")
    @ResponseBody
    public Ret updateCoverByPartnerId(@PathVariable("partnerId") Long partnerId) {
        try {
            Partner partner = partnerService.getPartnerById(partnerId);
            String partnerName = partner.getName();

            ClientService instance = ClientFactory.getInstance(partner.getId());

            if (instance == null) {
                throw new BookException("未查询到partnerName={},partnerId={}所对应的ClientService实例!", partnerName, partnerId.toString());
            }

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            List<CPBook> bookList = instance.getBookList(partner);

            if (CollectionUtils.isEmpty(bookList)) {
                throw new BookException("partnerName={},partnerId={}获取书籍列表异常!", partnerName, partnerId.toString());
            }

            for (CPBook cpBook : bookList) {
                String cpBookId = cpBook.getId();
                Book book = bookService.getBookByCpBookIdAndPartnerId(cpBookId, partnerId);

                if (book == null) {
                    logger.error("没有查询到partnerName=[{}],partnerId=[{}],cpBookId=[{}]所对应的书籍信息", partnerName, partnerId.toString(), cpBookId);
                    continue;
                }

                String bookId = book.getBookId();
                CPBook bookInfo = null;

                try {
                    bookInfo = instance.getBookInfo(partner, cpBookId);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    continue;
                }

                if (bookInfo != null && StringUtils.isNotBlank(bookInfo.getCover())) {
                    boolean res = BookSupport.downImg(bookId, bookInfo.getCover());

                    if (res == false) {
                        logger.error("partnerName=[{}],partnerId=[{}]书籍封面bookId=[{}]更新失败", partnerName, partnerId, bookId);
                        continue;
                    } else {
                        logger.info("partnerName=[{}],partnerId=[{}]书籍封面bookId=[{}]更新成功", partnerName, partnerId, bookId);
                        SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.BOOK_COVER_UPDATE.getType());
                    }
                } else {
                    logger.error("partnerName=[{}],partnerId=[{}]书籍bookId=[{}]封面地址为空", partnerName, partnerId, bookId);
                    continue;
                }
            }

            return Ret.success("partnerName=[" + partnerName + "],partnerId=[" + partnerId + "]书籍封面更新成功");
        } catch (Exception e) {
            logger.error("partnerId=[{}]书籍封面更新失败!!!", partnerId, e);
            return Ret.error(-1, "partnerId=[" + partnerId + "]封面更新失败");
        }
    }

    /**
     * 更新指定创建时间范围的书籍封面
     * @param timeStart 起始时间，例如：2019-03-26，起始时间自动加后缀00:00:00
     * @param timeEnd 终止时间，例如：2019-03-27，终止时间自动加后缀23:59:59
     * @return
     */
    @RequestMapping(value = "/updateCoverByTimeRange")
    @ResponseBody
    public Ret updateCoverByTimeRange(String timeStart, String timeEnd) {
        if (StringUtils.isBlank(timeStart) || StringUtils.isBlank(timeEnd)) {
            logger.error("起止时间不能为空，且时间日期格式为：yyyy-MM-dd");
            return Ret.error(-1, "传入的时间范围不能为空，且起止时间格式为：yyyy-MM-dd");
        }

        timeStart = timeStart + " 00:00:00";
        timeEnd = timeEnd + " 23:59:59";

        try {
            boolean res = bookService.updateBookCoverByTimeRange(timeStart, timeEnd);

            if (res) {
                return Ret.success(String.format("指定时间范围[%s~%s]书籍封面更新成功", timeStart, timeEnd));
            }
        } catch (Exception e) {
            logger.error(String.format("指定时间范围[%s~%s]书籍封面更新失败", timeStart, timeEnd), e);
        }

        return Ret.error(-1, String.format("指定时间范围[%s~%s]书籍封面更新失败", timeStart, timeEnd));
    }

    /**
     * 更新所有完本状态为空的书籍信息
     */
    @RequestMapping("/updateBookStatus")
    @ResponseBody
    public void updateBookStatus() {
        try {
            List<String> allLostBookStatusBookId = bookMapper.getAllLostBookStatusBookId();

            if (allLostBookStatusBookId != null) {
                for (String bookId : allLostBookStatusBookId) {
                    Book book = bookMapper.getBookByBookId(bookId);
                    Partner partner = partnerService.getPartnerById(Long.parseLong(book.getPartnerId()));

                    if (StringUtils.isBlank(partner.getAliasId())) {
                        partner.setAliasId(partner.getId() + "");
                    }

                    CPBook cpbook = null;

                    try {
                        ClientService clientService = ClientFactory.getInstance(partner.getId());
                        cpbook = clientService.getBookInfo(partner, book.getCpBookId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }

                    if (cpbook == null) {
                        continue;
                    }

                    book.setStatus("1".equals(cpbook.getCompleteStatus()) ? Book.COMPLETE_STATUS_END : Book.COMPLETE_STATUS_SERIAL);
                    bookMapper.updateStatus(book);
                    logger.info("bookid={} 基础数据缓存插入成功", bookId);

                    SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.BOOK_UPDATE.getType());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新指定书籍的分类信息
     * @param bookId 本地书籍id
     * @return
     */
    @RequestMapping(value = "/updateCategoryByBookId/{bookId}")
    @ResponseBody
    public Ret updateCategoryByBookId(@PathVariable("bookId") String bookId) {
        try {
            Book book = bookMapper.getBookByBookId(bookId);
            Partner partner = partnerService.getPartnerById(Long.parseLong(book.getPartnerId()));

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            ClientService clientService = ClientFactory.getInstance(partner.getId());
            CPBook cpbook = clientService.getBookInfo(partner, book.getCpBookId());

            if (cpbook != null && StringUtils.isNotBlank(cpbook.getCategory())) {
                boolean res = bookCategoryService.addCategory(bookId, partner.getId(), cpbook.getCategory());

                if (res) {
                    SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.BOOK_UPDATE.getType());
                }

                return Ret.success("书籍bookId=[" + bookId + "]分类更新成功");
            } else {
                return Ret.error(-1, "书籍bookId=[" + bookId + "]原始分类为空");
            }
        } catch (Exception e) {
            logger.error("书籍[{}]分类更新失败!!!", bookId, e);
            return Ret.error(-1, "书籍bookId=[" + bookId + "]分类更新失败");
        }
    }

    /**
     * 更新指定cp的所有书籍分类
     * @param partnerId
     * @return
     */
    @RequestMapping("/updatePartnerBookCategory/{partnerId}")
    @ResponseBody
    public Ret updatePartnerBookCategory(@PathVariable("partnerId") Long partnerId) {
        try {
            Partner partner = partnerService.getPartnerById(partnerId);
            String partnerName = partner.getName();

            ClientService instance = ClientFactory.getInstance(partner.getId());

            if (instance == null) {
                throw new BookException("未查询到partnerName={},partnerId={}所对应的ClientService实例!", partnerName, partnerId.toString());
            }

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            List<CPBook> bookList = instance.getBookList(partner);

            if (CollectionUtils.isEmpty(bookList)) {
                throw new BookException("partnerName={},partnerId={}获取书籍列表异常!", partnerName, partnerId.toString());
            }

            for (CPBook cpBook : bookList) {
                String cpBookId = cpBook.getId();
                Book book = bookService.getBookByCpBookIdAndPartnerId(cpBookId, partnerId);

                if (book == null) {
                    logger.error("没有查询到partnerName=[{}],partnerId=[{}],cpBookId=[{}]所对应的书籍信息", partnerName, partnerId.toString(), cpBookId);
                    continue;
                }

                String bookId = book.getBookId();
                CPBook bookInfo = null;

                try {
                    bookInfo = instance.getBookInfo(partner, cpBookId);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    continue;
                }

                if (bookInfo != null && StringUtils.isNotBlank(bookInfo.getCategory())) {
                    String category = bookInfo.getCategory();
                    boolean res = false;

                    try {
                        res = bookCategoryService.addCategory(bookId, partnerId, category);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        continue;
                    }

                    if (res) {
                        SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.BOOK_UPDATE.getType());
                    }
                }
            }

            return Ret.success("partnerName=[" + partnerName + "],partnerId=[" + partnerId + "]书籍分类更新成功");
        } catch (Exception e) {
            logger.error("partnerId=[{}]书籍分类更新失败!!!", partnerId, e);
            return Ret.error(-1, "partnerId=[" + partnerId + "]分类更新失败");
        }
    }

    /**
     * 删除指定书籍指定章节范围的章节
     * @param bookId 书籍id
     * @param startChapterId 我方起始章节id
     * @param endChapterId 我方终止章节id
     * @return
     */
    @RequestMapping(value = "/deleteChapterByBookChapterIdRange/{bookId}")
    @ResponseBody
    public Ret deleteChapterByBookChapterIdRange(@PathVariable("bookId") String bookId, Long startChapterId, Long endChapterId) {
        try {
            Book book = bookMapper.getBookByBookId(bookId);

            if (book == null) {
                return Ret.error(-1, "书籍bookId=[" + bookId + "]不存在");
            }

            chapterMapper.deleteChapterByBookChapterIdRange(bookId, startChapterId, endChapterId);

            SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.CHAPTER_DELETE_UPDATE.getType());

            return Ret.success("书籍bookId=[" + bookId + "]删除指定章节范围[" + startChapterId + "~" + endChapterId + "]成功");
        } catch (Exception e) {
            logger.error("删除书籍[{}]指定章节范围id[{}~{}]失败!!!", bookId, startChapterId, endChapterId, e);
            return Ret.error(-1, "书籍bookId=[" + bookId + "]删除指定章节范围[" + startChapterId + "~" + endChapterId + "]失败");
        }
    }

    /**
     * 更新指定书籍指定卷的cp卷id和卷名
     * @param bookId
     * @param volumeId
     * @param cpVolumeId
     * @param name
     * @return
     */
    @RequestMapping(value = "/updateVolumeByBookIdAndVolumeId/{bookId}")
    @ResponseBody
    public Ret updateVolumeByBookIdAndVolumeId(@PathVariable("bookId") String bookId, Long volumeId, String cpVolumeId, String name) {
        try {
            Book book = bookMapper.getBookByBookId(bookId);

            if (book == null) {
                return Ret.error(-1, "书籍bookId=[" + bookId + "]不存在");
            }

            if (volumeId == null) {
                return Ret.error(-1, "传入的书籍bookId=[" + bookId + "]卷id为空");
            }

            volumeMapper.updateVolumeByBookIdAndVolumeId(bookId, volumeId, cpVolumeId, name);

            return Ret.success("书籍bookId=[" + bookId + "]volumeId=[" + volumeId + "]卷更新成功");
        } catch (Exception e) {
            logger.error("书籍[bookId={},volumeId={}]卷更新失败!!!", bookId, volumeId, e);
            return Ret.error(-1, "书籍bookId=[" + bookId + "]volumeId=[" + volumeId + "]卷更新失败");
        }
    }

    /**
     * 更新指定书籍指定章节范围的卷id
     * @param bookId
     * @param startChapterId
     * @param endChapterId
     * @param volumeId
     * @return
     */
    @RequestMapping(value = "/updateVolumeIdByBookIdAndChapterRange/{bookId}")
    @ResponseBody
    public Ret updateVolumeIdByBookIdAndChapterRange(@PathVariable("bookId") String bookId, Long startChapterId, Long endChapterId, String volumeId) {
        try {
            Book book = bookMapper.getBookByBookId(bookId);

            if (book == null) {
                return Ret.error(-1, "书籍bookId=[" + bookId + "]不存在");
            }

            if (startChapterId == null) {
                return Ret.error(-1, "传入的起始章节id为空");
            }

            if (endChapterId == null) {
                return Ret.error(-1, "传入的终止章节id为空");
            }

            chapterMapper.updateVolumeIdByBookIdAndChapterRange(bookId, startChapterId, endChapterId, volumeId);

            SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.CHAPTER_MODIFY_UPDATE.getType(), ChapterModifySyncTypeEnum.RANGE.getType(), startChapterId, endChapterId);

            return Ret.success("书籍bookId=[" + bookId + "]章节范围[" + startChapterId + "~" + endChapterId + "]的卷id更新成功，更新为[" + volumeId + "]");
        } catch (Exception e) {
            logger.error("书籍[bookId={},volumeId={}]卷更新失败!!!", bookId, volumeId, e);
            return Ret.error(-1, "书籍bookId=[" + bookId + "]章节范围[" + startChapterId + "~" + endChapterId + "]的卷id更新失败");
        }
    }

    /**
     * 获取书籍列表接口地址
     * @param partnerId
     * @return
     */
    @RequestMapping("/getBookListUrl/{partnerId}")
    @ResponseBody
    public Ret getBookListUrl(@PathVariable("partnerId") Long partnerId) {
        try {
            Partner partner = partnerService.getPartnerById(partnerId);

            if (partner == null) {
                return Ret.error(-1, "partnerId=[" + partnerId + "]不存在");
            }

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            try {
                ClientService instance = ClientFactory.getInstance(partner.getId());
                instance.getBookList(partner);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return Ret.success(TraceKeyHolder.getUserKey("url"));
        } catch (Exception e) {
            e.printStackTrace();
            return Ret.error(-1, "获取书籍列表Url失败");
        } finally {
            TraceKeyHolder.clear();
        }
    }

    /**
     * 获取书籍详情接口地址
     * @param partnerId
     * @param cpBookId
     * @return
     */
    @RequestMapping("/getBookInfoUrl/{partnerId}")
    @ResponseBody
    public Ret getBookInfoUrl(@PathVariable("partnerId") Long partnerId, String cpBookId) {
        try {
            Partner partner = partnerService.getPartnerById(partnerId);

            if (partner == null) {
                return Ret.error(-1, "partnerId=[" + partnerId + "]不存在");
            }

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            try {
                ClientService instance = ClientFactory.getInstance(partner.getId());
                instance.getBookInfo(partner, cpBookId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);

            }

            return Ret.success(TraceKeyHolder.getUserKey("url"));
        } catch (Exception e) {
            e.printStackTrace();
            return Ret.error(-1, "获取书籍详情Url失败");
        } finally {
            TraceKeyHolder.clear();
        }
    }

    /**
     * 获取章节列表接口地址
     * @param partnerId
     * @param cpBookId
     * @return
     */
    @RequestMapping("/getChapterListUrl/{partnerId}")
    @ResponseBody
    public Ret getChapterListUrl(@PathVariable("partnerId") Long partnerId, String cpBookId) {
        try {
            Partner partner = partnerService.getPartnerById(partnerId);

            if (partner == null) {
                return Ret.error(-1, "partnerId=[" + partnerId + "]不存在");
            }

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            try {
                ClientService instance = ClientFactory.getInstance(partner.getId());
                instance.getVolumeList(partner, cpBookId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return Ret.success(TraceKeyHolder.getUserKey("url"));
        } catch (Exception e) {
            e.printStackTrace();
            return Ret.error(-1, "获取章节列表Url失败");
        } finally {
            TraceKeyHolder.clear();
        }
    }

    /**
     * 获取章节内容接口地址
     * @param partnerId
     * @param cpBookId
     * @param chapterId
     * @return
     */
    @RequestMapping("/getChapterInfoUrl/{partnerId}")
    @ResponseBody
    public Ret getChapterInfoUrl(@PathVariable("partnerId") Long partnerId, String cpBookId, String chapterId) {
        try {
            Partner partner = partnerService.getPartnerById(partnerId);

            if (partner == null) {
                return Ret.error(-1, "partnerId=[" + partnerId + "]不存在");
            }

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            try {
                ClientService instance = ClientFactory.getInstance(partner.getId());
                instance.getCPChapterInfo(partner, cpBookId, null, chapterId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

            return Ret.success(TraceKeyHolder.getUserKey("url"));
        } catch (Exception e) {
            e.printStackTrace();
            return Ret.error(-1, "获取章节内容Url失败");
        } finally {
            TraceKeyHolder.clear();
        }
    }

    /**
     * 更新指定书籍章节字数
     * @param bookId
     * @return
     */
    @RequestMapping("/updateBookWordNumByBookId")
    @ResponseBody
    public Ret updateBookWordNumByBookId(String bookId) {
        try {
            boolean needSync = updateBookWordNum(bookId);

            if (needSync) {
                SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.CHAPTER_MODIFY_UPDATE.getType());
            }
        } catch (Exception e) {
            logger.info("bookId={}更新书籍章节字数失败：{}", bookId, e.getMessage(), e);
        }

        return Ret.success("success");
    }

    /**
     * 更新指定CP所有书籍章节字数
     * @param cpId
     * @return
     */
    @RequestMapping("/updateBookWordNumByCpId")
    @ResponseBody
    public Ret updateBookWordNumByCpId(Long cpId) {
        try {
            List<String> bookIds = bookMapper.getAllBookIdByPartner(cpId);
            logger.info("[cpId={}]需要更新的书籍本数：{}", cpId, bookIds.size());

            if (CollectionUtils.isNotEmpty(bookIds)) {
                for (String bookId : bookIds) {
                    boolean needSync = updateBookWordNum(bookId);

                    if (needSync) {
                        SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.CHAPTER_MODIFY_UPDATE.getType());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("cpId={}书籍章节字数更新失败：{}", cpId, e.getMessage(), e);
        }

        return Ret.success("success");
    }

    private boolean updateBookWordNum(String bookId) {
        boolean needSync = false;

        try {
            List<BookChapterVO> bookChapterVOS = chapterService.getAllChapterByBookId(bookId);

            if (CollectionUtils.isNotEmpty(bookChapterVOS)) {
                for (BookChapterVO bookChapterVO : bookChapterVOS) {
                    String chapterPath = ChapterSupport.getChapterPath(bookId, bookChapterVO.getId());
                    String content = AliOssUtil.getContent(chapterPath);

                    if (StringUtils.isNotBlank(content)) {
                        Integer wordNum = bookChapterVO.getWordNum();
                        int realLength = StringUtil.getRealLength(content);

                        if (wordNum != realLength) {
                            bookChapterVO.setWordNum(realLength);
                            Integer ret = adminBookRpc.updateBookChapter(bookChapterVO).getData();

                            if (ret == 1) {
                                logger.info("[bookId={}][chapterId={}][chapterName={}]章节字数更新成功，原字数：{}，更新后字数：{}", bookId, bookChapterVO.getId(), bookChapterVO.getName(), wordNum, realLength);
                                needSync = true;
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.info("bookId={}更新书籍章节字数失败：{}", bookId, e.getMessage(), e);
        }

        return needSync;
    }

    /**
     * 重命名指定章节ID范围的章节名称（只支持cpBookId为空的书籍）
     * @param bookId 我方书籍ID
     * @param type 章节重命名方式，1：按照“第X章”格式命名，如：第10章 2：直接按照数值编号命名，如：10
     * @param startChapterId 我方书籍起始章节ID，0表示开始
     * @param endChapterId 我方书籍终止章节ID，-1表示结束
     * @return
     */
    @RequestMapping("/renameChapterNameByChapterId/{bookId}")
    @ResponseBody
    public Ret renameChapterNameByChapterId(@PathVariable("bookId") String bookId, Integer type, Long startChapterId, Long endChapterId) {
        String prefix =  String.format("[bookId=%s][type=%s][startChapterId=%s][endChapterId=%s]", bookId, type, startChapterId, endChapterId);
        logger.info("{}开始重命名章节", prefix);

        try {
            Book book = bookService.getBookByBookId(bookId);

            if (book == null) {
                return Ret.error(-1, "未查询到bookId=[" + bookId + "]所对应的书籍信息");
            }

            if (type == null || (type != 1 && type != 2)) {
                return Ret.error(-1, "未找到该章节重命名方式");
            }

            if (StringUtils.isNotBlank(book.getCpBookId())) {
                logger.error("{}不支持原始书的章节重命名", prefix);
                return Ret.error(-1, "不支持原始书的章节重命名");
            }

            if (startChapterId != 0 && endChapterId != -1 && startChapterId > endChapterId) {
                logger.error("{}传入的起始章节ID大于终止章节ID", prefix);
                return Ret.error(-1, "传入的起始章节ID大于终止章节ID");
            }

            List<BookChapterVO> bookChapterVOS = chapterService.getAllChapterByBookId(bookId);

            if (CollectionUtils.isEmpty(bookChapterVOS)) {
                return Ret.error(-1, "书籍章节为空");
            }

            for (int i = 0; i < bookChapterVOS.size(); i++) {
                Long chapterId = bookChapterVOS.get(i).getId();

                // 只覆盖不小于传入章节id的章节内容
                if (startChapterId > 0 && chapterId < startChapterId) {
                    logger.info("{}指定章节id=[{}]小于传入的起始章节id=[{}]，该章节标题不重命名", prefix, chapterId, startChapterId);
                    continue;
                }

                // 只覆盖不大于传入章节id的章节内容
                if (endChapterId != -1 && chapterId > endChapterId) {
                    logger.info("{}指定章节id=[{}]大于传入的终止章节id=[{}]，该章节标题不重命名", prefix, chapterId, endChapterId);
                    continue;
                }

                String chapterName = type == 1 ? "第" + (i + 1) + "章" : (i + 1) + "";

                BookChapterVO bookChapter = chapterService.getBookChapter(chapterId);

                if (bookChapter != null) {
                    String name = bookChapter.getName();
                    String content = bookChapter.getContent();

                    if (StringUtils.isNotBlank(content) && content.startsWith(name)) {
                        // 修改章节标题
                        content = StringUtils.replaceOnce(content, name, chapterName);
                        bookChapter.setContent(content);
                    }

                    bookChapter.setName(chapterName);

                    chapterService.updateBookChapter(bookChapter);
                    logger.info("bookId={},chapterId={},srcChapterName={},newChapterName={}, 章节重命名成功", bookId, chapterId, name, chapterName);
                }
            }

            SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.CHAPTER_MODIFY_UPDATE.getType(), ChapterModifySyncTypeEnum.RANGE.getType(), startChapterId, endChapterId);

            return Ret.success("书籍章节重命名成功");
        } catch (Exception e) {
            logger.error("{}书籍章节名重命名失败：{}", prefix, e.getMessage(), e);
            return Ret.error(-1, "书籍章节名重命名失败");
        } finally {
            logger.info("{}重命名章节结束", prefix);
        }
    }

    /**
     * 覆盖按字数拆分书籍
     * @param bookId
     * @return
     */
    @RequestMapping("/coverBreakBookByWordNum")
    @ResponseBody
    public Ret coverBreakBookByWordNum(String bookId) {
        try {
            return bookBreakChapterSyncService.coverBreakBookByWordNum(bookId);
        } catch (Exception e) {
            logger.error("newBookId={},书籍章节覆盖失败");
            return Ret.error(-1, "书籍章节覆盖失败");
        }
    }
}
