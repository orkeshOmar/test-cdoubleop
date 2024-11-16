package com.dz.coop.module.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dz.content.api.book.rpc.AdminBookFileRpc;
import com.dz.content.api.book.rpc.AdminBookPropertyRpc;
import com.dz.content.api.book.rpc.AdminBookRpc;
import com.dz.content.api.book.support.OrderByItem;
import com.dz.content.api.book.vo.AdminBookQueryRequest;
import com.dz.content.api.book.vo.AdminBookVO;
import com.dz.content.api.book.vo.BreakChapterConfRequest;
import com.dz.content.api.book.vo.BreakChapterConfVO;
import com.dz.coop.common.exception.BookException;
import com.dz.coop.common.util.BeanUtil;
import com.dz.coop.common.util.EscapeUtil;
import com.dz.coop.common.util.SendMQMsgUtil;
import com.dz.coop.common.util.StringUtil;
import com.dz.coop.module.constant.*;
import com.dz.coop.module.mapper.BookIdMapper;
import com.dz.coop.module.mapper.BookMapper;
import com.dz.coop.module.mapper.UpdateMsgqueueMapper;
import com.dz.coop.module.model.*;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.service.*;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.coop.module.support.BookSupport;
import com.dz.glory.common.vo.Page;
import com.dz.glory.common.vo.Ret;
import com.dz.utils.RandomUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author panqz 2018-10-29 10:45 AM
 */
@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    @Resource
    private BookIdMapper bookIdMapper;

    @Resource
    private BookMapper bookMapper;

    @Resource
    private PartnerService partnerService;

    @Resource
    private UpdateMsgqueueMapper updateMsgqueueMapper;

    @Resource
    private BookCategoryService bookCategoryService;

    @Resource
    private ChapterService chapterService;

    @Reference
    private AdminBookRpc adminBookRpc;

    @Reference
    private AdminBookPropertyRpc adminBookPropertyRpc;

    @Reference
    private AdminBookFileRpc adminBookFileRpc;

    @Override
    public String generateBookId() {
        BookId bookId = new BookId();
        bookIdMapper.insertBookId(bookId);
        return bookId.getId().toString();
    }

    @Override
    public String generateAudioBookId() {
        BookId bookId = new BookId();
        bookIdMapper.insertAudioBookId(bookId);
        return bookId.getId().toString();
    }

    @Override
    public Integer getBookType(Partner partner) {
        return partner != null ? partner.getBookType() : BookTypeEnum.TXT.getType();
    }

    @Override
    public boolean isAudio(int bookType) {
        return bookType == BookTypeEnum.AUDIO.getType() ? true : false;
    }

    @Override
    public boolean isAudio(Partner partner) {
        int bookType = partner != null ? partner.getBookType() : BookTypeEnum.TXT.getType();
        return bookType == BookTypeEnum.AUDIO.getType() ? true : false;
    }

    @Override
    public String saveBook(CPBook cpBook) {
        try {
            AdminBookQueryRequest request = new AdminBookQueryRequest();
            request.setCpBookId(cpBook.getId());
            request.setCpPartnerId(cpBook.getCpId());
            AdminBookVO book = adminBookRpc.getBookByQueryRequest(request).getData();
            //Book book = bookService.getBookByCpBookIdAndPartnerId(cpBook.getId(), Long.parseLong(cpBook.getCpId().toString()));

            if (book != null) {
                return book.getBookId();
            }

            Partner partner = partnerService.getPartnerById(cpBook.getCpId());

            String bookId = null;
            int bookType = getBookType(partner);
            // 有声读物的书籍id从3开头
            if (bookType == BookTypeEnum.AUDIO.getType()) {
                bookId = generateAudioBookId();
            } else {
                bookId = generateBookId();
            }

            //book = new Book();
            book = new AdminBookVO();

            book.setBookId(bookId);
            book.setCpBookId(cpBook.getId());
            book.setCpPartnerId(cpBook.getCpId());
            book.setAuthor(cpBook.getAuthor());
            book.setName(cpBook.getName());
            book.setBookName(cpBook.getName());
            book.setCoverWap(BookSupport.getCoverWap(bookId));
            String introdoc = cpBook.getBrief();
            if (StringUtils.isNotBlank(introdoc)) {
                introdoc = EscapeUtil.escape(introdoc);
                introdoc = StringUtil.removeEmptyLines(introdoc);
                // 数据库中简介的长度限制为500个字符
                book.setIntroduction(introdoc.length() > 492 ? introdoc.substring(0, 492) + "..." : introdoc);
            }
            book.setClickNum((long)RandomUtil.getRandom(100, 200));
            book.setTagNew(cpBook.getTag());
            boolean isQingTing = partner.getId() == ThirdPart.QING_TING_FM.getCpId().longValue();
            // 蜻蜓FM的使用对方的书籍价格，除此之外，文本书籍默认是0.15，音频书籍默认是0.2
            String price = isQingTing ? cpBook.getPrice() : (bookType == BookTypeEnum.TXT.getType() ? Book.SERIAL_PRICE : Book.AUDIO_SERIAL_PRICE);
            book.setPrice(price);
            book.setRecommendPrice(isQingTing ? cpBook.getRecommendPrice() : price);
            book.setUnit(isQingTing ? cpBook.getUnit() : Book.SERIAL_UNIT);
            book.setStatus(cpBook.isFinished() ? Book.COMPLETE_STATUS_END : Book.COMPLETE_STATUS_SERIAL);
            book.setBookType(bookType);
            book.setPraiseNum(isQingTing ? cpBook.getPraiseNum().intValue() : 0);
            book.setMarketStatus(MarketStatusEnum.STORAGE.getType());

            if (isQingTing) {
                String extend = cpBook.getExtend();
                JSONObject jsonObject = JSON.parseObject(extend);
                book.setItemId(jsonObject.getString("itemId"));
                book.setComScore(jsonObject.getDouble("comScore"));
                book.setShowScore(jsonObject.getDouble("showScore"));
            }

            EscapeUtil.escape(book);

            AdminBookVO bookVO = adminBookRpc.saveBookInfo(book).getData();
            //bookMapper.save(book);

            if (bookVO == null) {
                throw new BookException(String.format("cpId=%s,cpBookId=%s,name=%s 书籍保存失败", cpBook.getCpId(), cpBook.getId(), cpBook.getName()));
            }

            logger.info(String.format("cpId=%s,cpBookId=%s,name=%s 书籍插入成功", cpBook.getCpId(), cpBook.getId(), cpBook.getName()));

            bookCategoryService.addCategory(bookId, cpBook.getCpId(), cpBook.getCategory());

            return bookId;

        } catch (Exception e) {
            throw new BookException(String.format("cpId=%s,cpBookId=%s,name=%s 书籍保存失败", cpBook.getCpId(), cpBook.getId(), cpBook.getName()), e);
        }
    }

    @Override
    public void updateBookAfterSaveChapter(String bookId) {
        Book book = new Book();
        book.setBookId(bookId);

        Map<String, Object> map = chapterService.countBookWordNumAndChapterNum(bookId);
        book.setTotalWordSize(MapUtils.getString(map, "wordNum", "0"));
        book.setTotalChapterNum(MapUtils.getString(map, "chapterNum", "0"));

        Chapter lastChapter = chapterService.getLastChapter(bookId);
        if (lastChapter != null) {
            book.setLastChapterId(lastChapter.getId());
            book.setLastChapterName(lastChapter.getName());
            book.setLastChapterUtime(lastChapter.getCtime());
        }

        bookMapper.update(book);
    }

    @Override
    public Book getBookByBookId(String bookId) {
        AdminBookVO adminBookVO = adminBookRpc.getBookInfo(bookId).getData();
        return BeanUtil.convert2Book(adminBookVO);
    }

    @Override
    public boolean updateBookCoverByTimeRange(String timeStart, String timeEnd) {
        try {
            List<String> booksId = bookMapper.getBookIdByTimeRange(timeStart, timeEnd);

            if (booksId != null && booksId.size() > 0) {
                for (String bookId : booksId) {
                    Book book = bookMapper.getBookByBookId(bookId);

                    if (book == null || book.getPartnerId() == null) {
                        logger.error(String.format("书籍bookId=[%s]封面更新失败，partnerId为空", bookId));
                        continue;
                    }

                    Partner partner = partnerService.getPartnerById(Long.parseLong(book.getPartnerId()));

                    if (partner == null) {
                        logger.error(String.format("书籍bookId=[%s]封面更新失败，找不到该书籍所对应的Partner", bookId));
                        continue;
                    }

                    if (StringUtils.isBlank(partner.getAliasId())) {
                        partner.setAliasId(partner.getId() + "");
                    }

                    CPBook cpbook = null;

                    try {
                        ClientService clientService = ClientFactory.getInstance(partner.getId());
                        cpbook = clientService.getBookInfo(partner, book.getCpBookId());
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        continue;
                    }

                    if (cpbook != null && StringUtils.isNotBlank(cpbook.getCover())) {
                        boolean res = BookSupport.downImg(bookId, cpbook.getCover());

                        if (res == false) {
                            logger.error("书籍封面bookId=[{}]更新失败", bookId);
                            continue;
                        }

                        logger.info("书籍封面bookId=[{}]更新成功", bookId);
                        SendMQMsgUtil.send(bookId, UpdateBookTypeEnum.BOOK_COVER_UPDATE.getType());
                    } else {
                        logger.error("书籍bookId=[{}]封面地址为空", bookId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(String.format("指定时间范围[%s~%s]书籍封面更新失败", timeStart, timeEnd), e);
            return false;
        }

        return true;
    }

    @Override
    public void updateCpBookId(String bookId, String cpBookId) {
        bookMapper.updateCpBookId(bookId, cpBookId);
    }

    @Override
    public Book getBookByCpBookIdAndPartnerId(String cpBookId, Long partnerId) {
        if (StringUtils.isBlank(cpBookId) || partnerId == null) {
            return null;
        }

        AdminBookQueryRequest request = new AdminBookQueryRequest();
        request.setCpBookId(cpBookId);
        request.setCpPartnerId(partnerId);
        AdminBookVO adminBookVO = adminBookRpc.getBookByQueryRequest(request).getData();
        return BeanUtil.convert2Book(adminBookVO);
    }

    @Override
    public List<String> listRecentUpdateBooks() {
        AdminBookQueryRequest request = new AdminBookQueryRequest();

        request.setLimit(Constant.LIMIT);
        request.setBookType(BookTypeEnum.TXT.getType());
        request.setMarketStatus(MarketStatusEnum.ON_SHELF.getType());
        request.setStatus(Book.COMPLETE_STATUS_SERIAL);
        request.setInterval(7);

        Page<AdminBookVO> adminBookVOPage = adminBookRpc.listRecentUpdateBooks(request).getData();
        List<AdminBookVO> adminBookVOList = adminBookVOPage.getData();
        List<String> bookIds = adminBookVOList.stream().map(adminBookVO -> adminBookVO.getBookId()).collect(Collectors.toList());
        long totalPage = adminBookVOPage.getTotalPage();

        if (totalPage > 1) {
            for (int i = 2; i <= totalPage; i++) {
                request.setPageNo(i);
                List<AdminBookVO> data = adminBookRpc.listRecentUpdateBooks(request).getData().getData();
                bookIds.addAll(data.stream().map(adminBookVO -> adminBookVO.getBookId()).collect(Collectors.toList()));
            }
        }

        return bookIds;
    }

    @Override
    public List<String> getAllBreakSourceBookIds() {
        BreakChapterConfRequest breakRequest = new BreakChapterConfRequest();
        breakRequest.setLimit(Constant.LIMIT);
        List<OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new OrderByItem("id", true));
        breakRequest.setOrders(orderByItems);

        Page<BreakChapterConfVO> breakChapterConfVOPage = adminBookPropertyRpc.getALlBreakChapter(breakRequest).getData();
        List<BreakChapterConfVO> chapterConfVOList = breakChapterConfVOPage.getData();
        List<String> bookIds = chapterConfVOList.stream().map(breakChapterConfVO -> breakChapterConfVO.getBookId()).collect(Collectors.toList());
        long totalPage1 = breakChapterConfVOPage.getTotalPage();

        if (totalPage1 > 1) {
            for (int i = 2; i <= totalPage1; i++) {
                breakRequest.setPageNo(i);
                List<BreakChapterConfVO> data = adminBookPropertyRpc.getALlBreakChapter(breakRequest).getData().getData();
                bookIds.addAll(data.stream().map(breakChapterConfVO -> breakChapterConfVO.getBookId()).collect(Collectors.toList()));
            }
        }

        return bookIds;
    }

    @Override
    public List<String> getAllNeedUpdateAudioBookId() {
        AdminBookQueryRequest request = new AdminBookQueryRequest();
        request.setLimit(Constant.LIMIT);
        request.setBookType(BookTypeEnum.AUDIO.getType());
        Long[] excludeCpIds = {ThirdPart.NAN_JING_MENG_LU.getCpId(), ThirdPart.QING_TING_FM.getCpId()};
        request.setExcludeCpPartnerId(StringUtils.join(excludeCpIds, ","));
        Page<AdminBookVO> adminBookVOPage = adminBookRpc.getAllNeedUpdateBookId(request).getData();
        List<AdminBookVO> adminBookVOs = adminBookVOPage.getData();
        List<String> bookIds = adminBookVOs.stream().map(adminBookVO -> adminBookVO.getBookId()).collect(Collectors.toList());
        long totalPage = adminBookVOPage.getTotalPage();

        if (totalPage > 1) {
            for (int i = 2; i <= totalPage; i++) {
                request.setPageNo(i);
                List<AdminBookVO> data = adminBookRpc.getAllNeedUpdateBookId(request).getData().getData();
                bookIds.addAll(data.stream().map(adminBookVO -> adminBookVO.getBookId()).collect(Collectors.toList()));
            }
        }

        return bookIds;
    }

    @Override
    public boolean uploadCover(String bookId, InputStream in) {
        try {
            Ret<String> ret = adminBookFileRpc.uploadCover(bookId, streamToByte(in));
            if (ret.getStatus() == 0) {
                logger.info("bookId={},书籍封面上传成功", bookId);
                return true;
            }
        } catch (Exception e) {
            logger.error("bookId={},书籍封面上传失败：{}", bookId, e.getMessage(), e);
        }

        return false;
    }

    private byte[] streamToByte(InputStream in) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bt = new byte[1024];
        int i = -1;

        try {
            while ((i = in.read(bt)) != -1) {
                outputStream.write(bt, 0, i);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return outputStream.toByteArray();
    }

}
