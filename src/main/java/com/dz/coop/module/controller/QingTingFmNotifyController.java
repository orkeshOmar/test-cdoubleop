package com.dz.coop.module.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.util.EscapeUtil;
import com.dz.coop.common.util.StringUtil;
import com.dz.coop.conf.properties.QingTingFMConf;
import com.dz.coop.module.constant.MarketStatusEnum;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.mapper.BookMapper;
import com.dz.coop.module.mapper.UpdateMsgqueueMapper;
import com.dz.coop.module.model.Book;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.UpdateMsg;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.service.BookService;
import com.dz.coop.module.service.ClientFactory;
import com.dz.coop.module.service.PartnerService;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.tools.JsonUtil;
import com.dz.vo.Ret;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @project: coop-client
 * @description: 蜻蜓FM更新通知接口
 * @author: songwj
 * @date: 2019-11-25 20:30
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
@RestController
@RequestMapping("/qingtingfm/callback")
public class QingTingFmNotifyController {

    private Logger logger = LoggerFactory.getLogger(QingTingFmNotifyController.class);

    @Resource
    private QingTingFMConf qingTingFMConf;

    @Resource
    private PartnerService partnerService;

    @Resource
    private BookMapper bookMapper;

    @Resource
    private UpdateMsgqueueMapper updateMsgqueueMapper;

    @Resource
    private BookService bookService;

    /**
     * 蜻蜓FM更新通知
     * @param qtSign
     * @param body
     * @return
     */
    @PostMapping("/update")
    public Ret update(@RequestHeader(value = "QT-Sign") String qtSign, @RequestBody String body) {
        try {
            if (StringUtils.isBlank(qtSign)) {
                return Ret.error(1, "签名为空");
            }

            if (StringUtils.isBlank(body)) {
                return Ret.error(2, "请求body为空");
            }

            JSONObject updateObj = JsonUtil.readValue(body, JSONObject.class);
            body = updateObj.toString();
            logger.info("蜻蜓FM更新通知qtSign: {}, body: {}", qtSign, body);
            // 签名 = MD5(body + client_secret).toUpperCase()
            String sign = DigestUtils.md5Hex(body + qingTingFMConf.getClientSecret()).toUpperCase();

            if (!StringUtils.equals(sign, qtSign)) {
                return Ret.error(3, "签名不正确");
            }

            // 专辑上线或更新：channel_online，专辑下线：channel_offline
            String type = updateObj.getString("type");
            String cpBookId = updateObj.getJSONObject("data").getString("id");
            Long cpId = ThirdPart.QING_TING_FM.getCpId();
            updateBookInfo(cpBookId, cpId, type);

            return Ret.success("SUCCESS");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Ret.error(-1, "ERROR");
        }
    }

    /**
     * 更新蜻蜓FM指定书籍信息
     * @param bookId
     * @return
     */
    @RequestMapping("/updateQingTingFMBookInfo/{bookId}")
    public Ret updateQingTingFMBookInfo(@PathVariable("bookId") String bookId) {
        try {
            Book book = bookMapper.getBookByBookId(bookId);

            if (book == null) {
                return Ret.error(-1, "未查询到bookId=[" + bookId + "]所对应的书籍信息");
            }

            Long partnerId = Long.parseLong(book.getPartnerId());

            if (ThirdPart.QING_TING_FM.getCpId().equals(partnerId)) {
                updateBookInfo(book.getCpBookId(), partnerId, Book.CHANNEL_ONLINE);
            } else {
                logger.info("传入的书籍bookId={}不是蜻蜓FM书籍，暂不做处理");
            }

            return Ret.success("书籍bookId=[" + bookId + "]信息更新成功");
        } catch (Exception e) {
            logger.error("书籍bookId={}信息更新失败!!!", bookId, e);
            return Ret.error(-1, "书籍bookId=[" + bookId + "]信息更新失败");
        }
    }

    /**
     * 更新书籍信息
     * @param cpBookId
     * @param partnerId
     * @param type 更新类型，专辑上线或更新：channel_online，专辑下线：channel_offline
     * @throws Exception
     */
    private void updateBookInfo(String cpBookId, Long partnerId, String type) throws Exception {
        Book book = bookService.getBookByCpBookIdAndPartnerId(cpBookId, partnerId);

        if (book != null) {
            if (StringUtils.equals(type, Book.CHANNEL_ONLINE)) {
                Partner partner = partnerService.getPartnerById(partnerId);

                if (StringUtils.isBlank(partner.getAliasId())) {
                    partner.setAliasId(partner.getId() + "");
                }

                ClientService instance = ClientFactory.getInstance(partnerId);
                CPBook bookInfo = instance.getBookInfo(partner, cpBookId);

                book.setName(bookInfo.getName());
                book.setAuthor(bookInfo.getAuthor());
                String intro = bookInfo.getBrief();
                if (StringUtils.isNotBlank(intro)) {
                    intro = StringUtil.removeEmptyLines(EscapeUtil.escape(intro));
                    // 数据库中简介的长度限制为500个字符
                    book.setIntroduction(intro.length() > 492 ? intro.substring(0, 492) + "..." : intro);
                }
                book.setUnit(bookInfo.getUnit());
                book.setStatus(bookInfo.isFinished() ? Book.COMPLETE_STATUS_END : Book.COMPLETE_STATUS_SERIAL);
                book.setPrice(bookInfo.getPrice());
                book.setRecommendPrice(bookInfo.getRecommendPrice());
                // 通知下架则强制下架，否则不变
                book.setMarketStatus(StringUtils.equals(type, Book.CHANNEL_OFFLINE) ? MarketStatusEnum.UNSHELVE.getType() : null);
                String extend = bookInfo.getExtend();

                // extend含有itemId字段则更新
                if (StringUtils.isNotBlank(extend)) {
                    JSONObject jsonObject = JSON.parseObject(extend);

                    if (jsonObject.containsKey("itemId")) {
                        String dbExtend = book.getExtend();
                        Map<String, Object> map = StringUtils.isNotBlank(dbExtend) ? JsonUtil.readValue(dbExtend, Map.class) : new HashMap<>(1);
                        map.put("itemId", jsonObject.get("itemId"));
                        book.setExtend(JSON.toJSONString(map));
                    } else {
                        book.setExtend(null);
                    }
                } else {
                    book.setExtend(null);
                }
            } else if (StringUtils.equals(type, Book.CHANNEL_OFFLINE)) {
                if (book.getMarketStatus() == MarketStatusEnum.ON_SHELF.getType()) {
                    logger.info("[蜻蜓FM]bookId={},cpBookId={},bookName={} 接收到通知，做下架处理！", book.getBookId(), cpBookId, book.getName());
                    book.setMarketStatus(MarketStatusEnum.UNSHELVE.getType());
                }
            }

            bookMapper.updateBookInfo(book);
            logger.info("[蜻蜓FM]bookId={},cpBookId={},bookName={} 书籍信息更新成功", book.getBookId(), cpBookId, book.getName());
        } else {
            logger.info("[蜻蜓FM]本地没有找到cpBookId={}所对应的书籍", cpBookId);
        }
    }

}
