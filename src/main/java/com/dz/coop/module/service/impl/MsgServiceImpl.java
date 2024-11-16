package com.dz.coop.module.service.impl;


import com.alibaba.dubbo.config.annotation.Reference;
import com.dz.content.api.book.rpc.AdminBookRpc;
import com.dz.content.api.book.vo.AdminBookQueryRequest;
import com.dz.content.api.book.vo.AdminBookVO;
import com.dz.content.api.book.vo.BookChapterVO;
import com.dz.coop.common.exception.BookException;
import com.dz.coop.common.util.BeanUtil;
import com.dz.coop.conf.BookFilterConf;
import com.dz.coop.module.constant.BookTypeEnum;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.mapper.BookMapper;
import com.dz.coop.module.mapper.PartnerMapper;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.model.BookMsg;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.service.*;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.rocketmq.producer.RocketMQProducer;
import com.dz.tools.TraceKeyHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author panqz 2018-10-27 10:52 AM
 */
@Service
public class MsgServiceImpl implements MsgService {

    private static final Logger logger = LoggerFactory.getLogger(MsgServiceImpl.class);

    @Resource
    private PartnerService partnerService;

    @Resource
    private MailService mailService;

    @Resource
    private BookService bookService;

    @Resource
    private ChapterService chapterService;

    @Reference
    private AdminBookRpc adminBookRpc;

    @Override
    public void produceByPartner(List<Partner> partners) {
        if (CollectionUtils.isNotEmpty(partners)) {
            logger.info("当前同步更新CP数量：{}", partners.size());
            partners.forEach(this::produceByPartner);
        }
    }

    @Override
    public void produceByPartner(Long partnerId) {
        produceByPartner(partnerService.getPartnerById(partnerId));
    }

    @Override
    public void produceByPartner(Partner partner) {
        try {
            ClientService instance = ClientFactory.getInstance(partner.getId());
            if (instance == null) {
                throw new BookException("未查询到partnerName={},partnerId={}所对应的ClientService实例!", partner.getName(), partner.getId().toString());
            }

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            List<CPBook> bookList = instance.getBookList(partner);
            if (CollectionUtils.isEmpty(bookList)) {
                throw new BookException("partnerName={},partnerId={}获取书籍列表异常!", partner.getName(), partner.getId().toString());
            }
            bookList.forEach(book -> {
                String cpBookId = book.getId();

                if (BookFilterConf.bookFilterMap.containsKey(partner.getId() + "_" + cpBookId)) {
                    logger.info("书籍partnerId={},cpBookId={},bookName={}在过滤列表当中，不进行抓取更新操作", partner.getId(), cpBookId, book.getName());
                    return;
                }

                Book dbBook = bookService.getBookByCpBookIdAndPartnerId(cpBookId, partner.getId());

                if (dbBook == null) {
                    produce(cpBookId, partner);
                    logger.info("[{}][{}][cpBookId={}][bookName={}]存放消息队列成功", partner.getId(), partner.getName(), cpBookId, book.getName());
                } else {
                    if (dbBook.getMarketStatus() == 10) {
                        logger.info("书籍{}的状态为删除，不进行同步更新操作", dbBook.getBookId());
                        return;
                    }

                    BookChapterVO bookChapterVO = adminBookRpc.getLastChapter(dbBook.getBookId()).getData();

                    Integer lastChapterIndex = chapterService.countChapters(dbBook.getBookId());

                    if (bookChapterVO == null) {
                        produce(dbBook.getBookId(), dbBook.getCpBookId(), null, lastChapterIndex, partner);
                    } else {
                        produce(dbBook.getBookId(), dbBook.getCpBookId(), bookChapterVO.getCpChapterId(), lastChapterIndex, partner);
                    }

                    logger.info("[{}][{}][bookId={}][bookName={}][cpBookId={}]存放消息队列成功", partner.getId(), partner.getName(), dbBook.getBookId(), dbBook.getName(), cpBookId);
                }

            });

        } catch (Exception e) {
            String errorMsg = e.getMessage();

            if (StringUtils.isNotBlank(errorMsg) && errorMsg.contains("书籍列表获取异常")) {
                mailService.sendEmail(errorMsg, true);
            } else {
                mailService.sendEmail(errorMsg);
            }

            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void produceNewBookByPartner(Long partnerId) {
        try {
            Partner partner = partnerService.getPartnerById(partnerId);
            ClientService instance = ClientFactory.getInstance(partner.getId());
            if (instance == null) {
                throw new BookException("未查询到partnerName={},partnerId={}所对应的ClientService实例!", partner.getName(), partner.getId().toString());
            }

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            List<CPBook> bookList = instance.getBookList(partner);
            if (CollectionUtils.isEmpty(bookList)) {
                throw new BookException("partnerName={},partnerId={}获取书籍列表异常!", partner.getName(), partner.getId().toString());
            }

            bookList.forEach(book -> {
                String cpBookId = book.getId();

                if (BookFilterConf.bookFilterMap.containsKey(partner.getId() + "_" + cpBookId)) {
                    logger.info("书籍partnerId={},cpBookId={}在过滤列表当中，不进行抓取更新操作", partner.getId(), cpBookId);
                    return;
                }

                Book dbBook = bookService.getBookByCpBookIdAndPartnerId(cpBookId, partner.getId());

                if (dbBook == null) {
                    produce(cpBookId, partner);
                    logger.info("[{}][{}][cpBookId={}][bookName={}]存放消息队列成功", partner.getId(), partner.getName(), cpBookId, book.getName());
                }
            });
        } catch (Exception e) {
            String errorMsg = e.getMessage();

            if (StringUtils.isNotBlank(errorMsg) && errorMsg.contains("书籍列表获取异常")) {
                mailService.sendEmail(errorMsg, true);
            } else {
                mailService.sendEmail(errorMsg);
            }

            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void produceByBook(List<String> books) {
        logger.info("一周之内更新连载上架数量：{}", books.size());
        if (CollectionUtils.isNotEmpty(books)) {
            books.forEach(this::produceByBook);
        }
    }

    @Override
    public void produceByBook(String bookId) {

        try {
            Book dbBook = bookService.getBookByBookId(bookId);

            if (dbBook == null) {
                logger.info("数据库中没有查询到bookId={}所对应的书籍信息", bookId);
                return;
            }
            String cpBookId = dbBook.getCpBookId();
            if (StringUtils.isBlank(cpBookId)) {
                logger.info("书籍bookId={}所对应的cpBookId为空", bookId);
                return;
            }

            String partnerId = dbBook.getPartnerId();

            if (BookFilterConf.bookFilterMap.containsKey(partnerId + "_" + cpBookId)) {
                logger.info("书籍bookId={}在过滤列表当中，不进行抓取更新操作", bookId);
                return;
            }

            if (dbBook.getMarketStatus() == 10) {
                logger.info("书籍{}的状态为删除，不进行同步更新操作", bookId);
                return;
            }

            Partner partner = partnerService.getPartnerById(Long.parseLong(partnerId));
            if (partner == null) {
                logger.info("书籍bookId={},bookName={},cpBookId={}所对应的Partner为空", bookId, dbBook.getName(),cpBookId);
                return;
            }
            if (!partner.isSync()) {
                logger.info("书籍bookId={},bookName={},cpBookId={}同步状态为：不同步", bookId, dbBook.getName(),cpBookId);
                return;
            }

            BookChapterVO bookChapterVO = adminBookRpc.getLastChapter(dbBook.getBookId()).getData();
            //Chapter lastChapter = dbBook.getLastChapter();

            Integer lastChapterIndex = chapterService.countChapters(dbBook.getBookId());

            if (bookChapterVO == null) {
                produce(bookId, dbBook.getCpBookId(), null, lastChapterIndex, partner);
            } else {
                produce(bookId, dbBook.getCpBookId(), bookChapterVO.getCpChapterId(), lastChapterIndex, partner);
            }

            logger.info("[{}][{}]bookId={},bookName={},cpBookId={} 存放消息队列成功", partner.getId(), partner.getName(), dbBook.getBookId(), dbBook.getName(), cpBookId);

        } catch (Exception e) {
            mailService.sendEmail(e.getMessage());

            logger.error("书籍bookId={}更新失败：{}", bookId, e.getMessage(), e);
        }
    }

    @Override
    public void produce(String cpBookId, Long partnerId) {
        produce(null, cpBookId, null, null, partnerId);
    }

    @Override
    public void produce(String cpBookId, Partner partner) {
        produce(null, cpBookId, null, null, partner);
    }

    @Override
    public void produce(String bookId, String cpBookId, String lastChapterId, Integer lastChapterIndex, Long partnerId) {
        produce(bookId, cpBookId, lastChapterId, lastChapterIndex, partnerService.getPartnerById(partnerId));
    }

    @Override
    public void produce(String bookId, String cpBookId, String lastChapterId, Integer lastChapterIndex, Partner partner) {
        if (BookFilterConf.bookFilterMap.containsKey(partner.getId() + "_" + cpBookId)) {
            logger.info("书籍bookId={},partnerId={},cpBookId={}在过滤列表当中，不进行抓取更新操作", bookId, partner.getId(), cpBookId);
            return;
        }

        BookMsg bookMsg = new BookMsg();
        bookMsg.setPartner(partner);
        bookMsg.setBookId(bookId);
        bookMsg.setCpBookId(cpBookId);
        bookMsg.setLastChapterId(lastChapterId);
        bookMsg.setLastChapterIndex(lastChapterIndex);

        RocketMQProducer.send(StringUtils.defaultIfBlank(TraceKeyHolder.getUserKey("topic"), partner.getBookType() == BookTypeEnum.TXT.getType() ? Constant.TOPIC_BOOK_COMMON : Constant.TOPIC_AUDIO_COMMON), bookMsg);
    }

}
