package com.dz.coop.module.task;

import com.dz.content.api.book.support.OrderByItem;
import com.dz.content.api.book.vo.AdminCpPartnerQueryRequest;
import com.dz.coop.common.exception.BookException;
import com.dz.coop.module.constant.BookTypeEnum;
import com.dz.coop.module.constant.Constant;
import com.dz.coop.module.service.BookService;
import com.dz.coop.module.service.ShortStoryService;
import com.dz.coop.module.service.MailService;
import com.dz.coop.module.service.PartnerService;
import com.dz.glory.common.jedis.client.JedisClient;
import com.dz.glory.common.jedis.support.Key;
import com.dz.tools.TraceKeyHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author panqz 2018-10-26 7:29 PM
 * 热门书籍不同任务
 */
@Component
public class ScheduleTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTask.class);

    @Resource
    private com.dz.coop.module.service.MsgService MsgService;

    @Resource
    private PartnerService partnerService;

    @Resource
    private BookService bookService;

    @Resource
    private MailService mailService;

    @Resource
    private JedisClient jedisClient;

    @Autowired
    private ShortStoryService shortStoryService;

    /**
     * 同步所有CP书籍
     */
    @Scheduled(cron = "0 0 */6 ? * *")
    public void sync() {
        if (StringUtils.equals(System.getProperty(Constant.TEXT_TASK_SWITCH), Constant.SWITCH_ON) && jedisClient.setnx(2, new Key("COM"), 1, 5 * 60 * 60) > 0) {
            // 全量任务开启，则优先追更任务不开启，因为已经包含在内
            jedisClient.setnx(2, new Key("HOT"), 1, 29 * 60);
            logger.info("【定时任务】非优先追更书籍开始存入队列");
            long start = System.currentTimeMillis();

            try {
                TraceKeyHolder.setUserKey("topic", Constant.TOPIC_BOOK_COMMON);

                AdminCpPartnerQueryRequest request = new AdminCpPartnerQueryRequest();
                request.setLimit(Constant.LIMIT);
                request.setIsSync(1);
                request.setBookType(BookTypeEnum.TXT.getType() + "");
                List<OrderByItem> orderByItems = new ArrayList<>();
                orderByItems.add(new OrderByItem("id", true));
                request.setOrders(orderByItems);

                MsgService.produceByPartner(partnerService.listAllPartners(request));

            } catch (BookException e) {

                mailService.sendEmail(e.getMessage());

                logger.error(e.getMessage(), e);
            } catch (Exception e) {

                mailService.sendEmail("同步异常，请检查");

                logger.error(e.getMessage(), e);
            } finally {
                TraceKeyHolder.clear();
                logger.info("【定时任务】非优先追更书籍存入队列结束，cost time {} ms", System.currentTimeMillis() - start);
            }

        }
    }

    /**
     * 优先追更库（上架，连载，最近7天有更新）
     */
    @Scheduled(cron = "0 1/30 * ? * *")
    public void syncPopular() {
        if (StringUtils.equals(System.getProperty(Constant.TEXT_TASK_SWITCH), Constant.SWITCH_ON) && jedisClient.setnx(2, new Key("HOT"), 1, 29 * 60) > 0) {
            logger.info("【定时任务】优先追更书籍开始存入队列");
            long start = System.currentTimeMillis();

            try {
                TraceKeyHolder.setUserKey("topic", Constant.TOPIC_BOOK_POPULAR);

                List<String> bookIds = bookService.listRecentUpdateBooks();

                // 拆分的原始书籍做到半小时更新一次
                bookIds.addAll(bookService.getAllBreakSourceBookIds());

                bookIds = bookIds.stream().distinct().collect(Collectors.toList());

                MsgService.produceByBook(bookIds);

            } catch (BookException e) {
                mailService.sendEmail(e.getMessage());

                logger.error(e.getMessage(), e);
            } catch (Exception e) {

                mailService.sendEmail("同步异常，请检查");

                logger.error(e.getMessage(), e);
            } finally {
                TraceKeyHolder.clear();
                logger.info("【定时任务】优先追更书籍存入队列结束，cost time {} ms", System.currentTimeMillis() - start);
            }
        }
    }

    /**
     * 同步听书所有cp书籍
     */
    @Scheduled(cron = "0 30 */6 ? * *")
    public void syncAllAudio() {
        if (StringUtils.equals(System.getProperty(Constant.AUDIO_TASK_SWITCH), Constant.SWITCH_ON) && jedisClient.setnx(2, new Key("ALL"), 1, 5 * 60 * 60) > 0) {
            // 全量任务开启，则优先追更任务不开启，因为已经包含在内
            jedisClient.setnx(2, new Key("POP"), 1, 59 * 60);
            logger.info("【定时任务】听书非优先追更书籍开始存入队列");
            long start = System.currentTimeMillis();

            try {
                TraceKeyHolder.setUserKey("topic", Constant.TOPIC_AUDIO_COMMON);

                AdminCpPartnerQueryRequest request = new AdminCpPartnerQueryRequest();
                request.setLimit(Constant.LIMIT);
                request.setIsSync(1);
                request.setBookType(BookTypeEnum.AUDIO.getType() + "");
                List<OrderByItem> orderByItems = new ArrayList<>();
                orderByItems.add(new OrderByItem("id", true));
                request.setOrders(orderByItems);

                MsgService.produceByPartner(partnerService.listAllPartners(request));
            } catch (BookException e) {

                mailService.sendEmail(e.getMessage());

                logger.error(e.getMessage(), e);
            } catch (Exception e) {

                mailService.sendEmail("同步异常，请检查");

                logger.error(e.getMessage(), e);
            } finally {
                TraceKeyHolder.clear();
                logger.info("【定时任务】听书非优先追更书籍存入队列结束，cost time {} ms", System.currentTimeMillis() - start);
            }

        }
    }

    /**
     * 优先追更库（上架，连载）
     */
    @Scheduled(cron = "0 31 */1 ? * *")
    public void syncPopularAudio() {
        if (StringUtils.equals(System.getProperty(Constant.AUDIO_TASK_SWITCH), Constant.SWITCH_ON) && jedisClient.setnx(2, new Key("POP"), 1, 59 * 60) > 0) {
            logger.info("【定时任务】听书优先追更书籍开始存入队列");
            long start = System.currentTimeMillis();

            try {
                TraceKeyHolder.setUserKey("topic", Constant.TOPIC_AUDIO_POPULAR);

                // 查询除南京萌鹿、蜻蜓FM之外的有声书籍ID
                List<String> bookIds = bookService.getAllNeedUpdateAudioBookId();

                MsgService.produceByBook(bookIds);

            } catch (BookException e) {
                mailService.sendEmail(e.getMessage());

                logger.error(e.getMessage(), e);
            } catch (Exception e) {

                mailService.sendEmail("同步异常，请检查");

                logger.error(e.getMessage(), e);
            } finally {
                TraceKeyHolder.clear();
                logger.info("【定时任务】听书优先追更书籍存入队列结束，cost time {} ms", System.currentTimeMillis() - start);
            }
        }
    }

}
