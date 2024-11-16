package com.dz.coop.common.util;

import com.dz.coop.module.constant.ChapterModifySyncTypeEnum;
import com.dz.coop.module.constant.UpdateBookTypeEnum;
import com.dz.coop.module.model.Book;
import com.dz.rocketmq.producer.RocketMQProducer;
import com.dz.tools.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @project: coop-client
 * @description: MQ消息发送工具
 * @author: songwj
 * @date: 2019-05-05 16:06
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class SendMQMsgUtil {

    private static Logger logger = LoggerFactory.getLogger(SendMQMsgUtil.class);

    /**
     * 发送MQ消息
     * @param bookId 书籍id
     * @param msgType 更新类型
     */
    public static void send(String bookId, int msgType) {
        send(bookId, msgType, ChapterModifySyncTypeEnum.RECENTLY.getType(), null, null);
    }

    /**
     * 发送MQ消息
     * @param bookId 书籍id
     * @param type 更新类型
     * @param chapterModifyType -1为全本同步，1为最近半小时更新章节同步, 2为指定章节范围同步
     * @param startChapterId 起始章节ID，当chapterModifyType=2时启用
     * @param endChapterId 终止章节ID，当chapterModifyType=2时启用
     */
    public static void send(String bookId, int type, Integer chapterModifyType, Long startChapterId, Long endChapterId) {
        // 主从同步有延迟，所以使用延时消息队列
        Map<String, Object> msgMap = new HashMap<>(2);
        msgMap.put("bookId", bookId);
        msgMap.put("type", type);

        if (type == UpdateBookTypeEnum.CHAPTER_MODIFY_UPDATE.getType()) {
            msgMap.put("chapterModifyType", chapterModifyType);

            if (chapterModifyType == ChapterModifySyncTypeEnum.RANGE.getType()) {
                msgMap.put("startChapterId", startChapterId);
                msgMap.put("endChapterId", endChapterId);
            }
        }

        String topic = type == UpdateBookTypeEnum.CHAPTER_MODIFY_UPDATE.getType() ? "glory_book_modify" : "glory_book_update";

        if (RocketMQProducer.send(topic, msgMap, 3)) {
            logger.info("同步消息队列插入成功：[{}]{}", topic, JsonUtil.obj2Json(msgMap));
        } else {
            logger.info("同步消息队列插入失败：[{}]{}", topic, JsonUtil.obj2Json(msgMap));
        }
    }

}
