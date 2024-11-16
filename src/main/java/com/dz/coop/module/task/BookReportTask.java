package com.dz.coop.module.task;

import com.dz.content.api.book.support.OrderByItem;
import com.dz.content.api.book.vo.AdminCpPartnerQueryRequest;
import com.dz.coop.module.constant.BookTypeEnum;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.report.HourReportModel;
import com.dz.coop.module.model.report.NewBookModel;
import com.dz.coop.module.model.report.TopUpdateBookModel;
import com.dz.coop.module.service.BookService;
import com.dz.coop.module.service.ClientFactory;
import com.dz.coop.module.service.MailService;
import com.dz.coop.module.service.PartnerService;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.coop.module.service.report.BookReportService;
import com.dz.glory.common.jedis.client.JedisClient;
import com.dz.glory.common.jedis.support.Key;
import com.dz.glory.common.utils.NetWorkUtil;
import com.dz.tools.TraceKeyHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @project: coop-client
 * @description: 书籍报表任务
 * @author: songwj
 * @date: 2019-03-22 21:35
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@Component
public class BookReportTask {

    private static final Logger logger = LoggerFactory.getLogger(BookReportTask.class);

    @Resource
    private BookReportService bookReportService;

    @Resource
    private MailService mailService;

    @Resource
    private PartnerService partnerService;

    @Resource
    private BookService bookService;

    @Resource
    private JedisClient jedisClient;

    @Value("${email.toJavaGroup}")
    private String toJavaGroup;

    @Value("${email.toAdmin}")
    private String toAdmin;

    @Scheduled(cron = "50 59 6,11,18,23 ? * *")
    public void hourReportJob() {
        if (StringUtils.equals(System.getProperty("email.switch"), "on") && jedisClient.setnx(2, new Key("REP"), 1, 5 * 60 * 60) > 0) {
            TraceKeyHolder.setRequestId("coopclient" + NetWorkUtil.getLocalIPV4Last() + System.currentTimeMillis());
            try {
                logger.info("【定时任务】开始日常报表任务");
                Integer currentDayUpdateNum = bookReportService.getCurrentDayUpdateNum();
                List<HourReportModel> hourReportRecord = bookReportService.getHourReportRecord();
                List<TopUpdateBookModel> topUpdateBookRecord = bookReportService.getTopUpdateBookRecord(20);
                Date date = new Date();
                String datetime = DateFormatUtils.format(date, "yyyy-MM-dd");

                StringBuilder sb = new StringBuilder("<b>从");
                sb.append(datetime).append(" 00:00开始至当前书籍更新数量：").append(currentDayUpdateNum).append("本</b><br/>");
                sb.append("<br/>更新书籍小时报表：<br/>");
                sb.append("<table border='1' cellspacing='0' width='400'>");
                sb.append("<tr bgcolor='#cccccc'><th>时间范围</th><th>每小时书籍章节更新数量</th></tr>");

                if (hourReportRecord != null && hourReportRecord.size() > 0) {
                    for (HourReportModel hrRepModel : hourReportRecord) {
                        sb.append("<tr align='center'>");
                        sb.append("<td>").append(hrRepModel.getDtime()).append("</td>");
                        sb.append("<td>").append(hrRepModel.getPerHrUpdateNum()).append("</td>");
                        sb.append("</tr>");
                    }
                }

                sb.append("</table>");
                sb.append("<br/>");
                sb.append("从").append(datetime).append(" 00:00开始各cp中更新书籍数量前20名报表：<br/>");
                sb.append("<table border='1' cellspacing='0' width='400'>");
                sb.append("<tr bgcolor='#cccccc'><th>cpId</th><th>cp名称</th><th>更新书籍数量</th></tr>");

                if (topUpdateBookRecord != null && topUpdateBookRecord.size() > 0) {
                    for (TopUpdateBookModel book : topUpdateBookRecord) {
                        sb.append("<tr align='center'>");
                        sb.append("<td>").append(book.getPartnerId()).append("</td>");
                        sb.append("<td>").append(book.getPartnerName()).append("</td>");
                        sb.append("<td>").append(book.getUpdateBookNum()).append("</td>");
                        sb.append("</tr>");
                    }
                }

                sb.append("</table>");

                mailService.sendEmail(toJavaGroup, DateFormatUtils.format(date, "yyyy-MM-dd HH") + "时抓书日常报表", sb.toString(), true, true);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                TraceKeyHolder.clear();
                logger.info("【定时任务】日常报表任务结束");
            }
        }
    }

    /**
     * 新书日抓取报告
     */
    @Scheduled(cron = "0 30 11,23 ? * *")
    public void newBookReport() {
        if (StringUtils.equals(System.getProperty("email.switch"), "on") && jedisClient.setnx(2, new Key("NEW"), 1, 10 * 60 * 60) > 0) {
            TraceKeyHolder.setRequestId("coopclient" + NetWorkUtil.getLocalIPV4Last() + System.currentTimeMillis());
            try {
                logger.info("【定时任务】开始日常抓取新书报表任务");
                Date date = new Date();
                String datetime = DateFormatUtils.format(date, "yyyy-MM-dd");
                String datetime2 = DateFormatUtils.format(date, "yyyy-MM-dd HH:mm");
                // 存放所有新书中未抓取过来的书籍
                List<NewBookModel> newBooks = new ArrayList<>();
                List<NewBookModel> newBooksRecord = bookReportService.getCurrentDayAddBookRecord();

                AdminCpPartnerQueryRequest request = new AdminCpPartnerQueryRequest();
                request.setLimit(Constant.LIMIT);
                request.setIsSync(1);
                request.setBookType(BookTypeEnum.TXT.getType() + "");
                List<OrderByItem> orderByItems = new ArrayList<>();
                orderByItems.add(new OrderByItem("id", true));
                request.setOrders(orderByItems);

                List<Partner> partners = partnerService.listAllPartners(request);

                if (CollectionUtils.isNotEmpty(partners)) {
                    for (Partner partner : partners) {
                        Long partnerId = partner.getId();
                        ClientService instance = null;

                        try {
                            instance = ClientFactory.getInstance(partnerId);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }

                        if (instance == null) {
                            logger.warn("未查询到partnerName={},partnerId={}所对应的ClientService实例!", partner.getName(), partnerId.toString());
                            continue;
                        }

                        if (StringUtils.isBlank(partner.getAliasId())) {
                            partner.setAliasId(partnerId + "");
                        }

                        List<CPBook> bookList = null;

                        try{
                            bookList = instance.getBookList(partner);
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }

                        if (CollectionUtils.isEmpty(bookList)) {
                            logger.warn("partnerName={},partnerId={}获取书籍列表异常!", partner.getName(), partnerId.toString());
                            continue;
                        }

                        for (CPBook cpBook : bookList) {
                            String cpBookId = cpBook.getId();
                            Book dbBook = bookService.getBookByCpBookIdAndPartnerId(cpBookId, partnerId);
                            if (dbBook == null) {
                                NewBookModel model = new NewBookModel();
                                model.setCpPartnerId(partnerId);
                                model.setCpName(partner.getName());
                                model.setCpBookId(cpBook.getId());
                                model.setCpBookName(cpBook.getName());
                                newBooks.add(model);
                                logger.info("新书cpPartnerId=[{}],cpName=[{}],cpBookId=[{}],cpBookName=[{}]", partnerId, partner.getName(), cpBook.getId(), cpBook.getName());
                            }
                        }
                    }
                }

                StringBuilder sb = new StringBuilder("<b>从");
                sb.append(datetime).append(" 00:00开始至").append(datetime2).append("抓取到的新书数量：").append(newBooksRecord.size()).append("本</b><br/>");
                sb.append("<br/>新抓取到的书籍报表：<br/>");
                sb.append("<table border='1' cellspacing='0' width='700'>");
                sb.append("<tr bgcolor='#cccccc'><th>序号</th><th>cpId</th><th>cpName</th><th>bookId</th><th>cpBookId</th><th>bookName</th></tr>");

                int row = 0;
                if (newBooksRecord != null && newBooksRecord.size() > 0) {
                    for (NewBookModel model : newBooksRecord) {
                        sb.append("<tr align='center'>");
                        sb.append("<td>").append(++row).append("</td>");
                        sb.append("<td>").append(model.getCpPartnerId()).append("</td>");
                        sb.append("<td>").append(model.getCpName()).append("</td>");
                        sb.append("<td>").append(model.getBookId()).append("</td>");
                        sb.append("<td>").append(model.getCpBookId()).append("</td>");
                        sb.append("<td>").append(model.getCpBookName()).append("</td>");
                        sb.append("</tr>");
                    }
                }

                sb.append("</table>");
                sb.append("<br/><b>从");
                sb.append(datetime).append(" 00:00开始至").append(datetime2).append("未抓取到的新书数量：").append(newBooks.size()).append("本</b><br/>");
                sb.append("<br/>未抓取到的新书报表：<br/>");
                sb.append("<table border='1' cellspacing='0' width='700'>");
                sb.append("<tr bgcolor='#cccccc'><th>序号</th><th>cpId</th><th>cpName</th><th>cpBookId</th><th>bookName</th></tr>");

                row = 0;
                if (newBooks != null && newBooks.size() > 0) {
                    for (NewBookModel model : newBooks) {
                        sb.append("<tr align='center'>");
                        sb.append("<td>").append(++row).append("</td>");
                        sb.append("<td>").append(model.getCpPartnerId()).append("</td>");
                        sb.append("<td>").append(model.getCpName()).append("</td>");
                        sb.append("<td>").append(model.getCpBookId()).append("</td>");
                        sb.append("<td>").append(model.getCpBookName()).append("</td>");
                        sb.append("</tr>");
                    }
                }

                sb.append("</table>");

                mailService.sendEmail(toAdmin, datetime2 + "新书抓取日常报表", sb.toString(), true, true);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                TraceKeyHolder.clear();
                logger.info("【定时任务】日常抓取新书报表任务结束");
            }
        }
    }

}
