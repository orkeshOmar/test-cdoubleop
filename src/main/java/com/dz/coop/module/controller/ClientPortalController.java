package com.dz.coop.module.controller;

import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.entity.ClientRequest;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.PartnerCategoryModel;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPCategory;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.BookCategoryService;
import com.dz.coop.module.service.BookCommonService;
import com.dz.coop.module.service.ClientFactory;
import com.dz.coop.module.service.PartnerService;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.tools.TraceKeyHolder;
import com.dz.vo.Ret;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author panqz 2018-12-03 5:42 PM
 */
@RestController
@RequestMapping("/portal")
public class ClientPortalController {

    private static final Logger logger = LoggerFactory.getLogger(ClientPortalController.class);

    @Resource
    private BookCommonService bookCommonService;

    @Resource
    private PartnerService partnerService;

    @Resource
    private BookCategoryService bookCategoryService;

    @RequestMapping("/books")
    @ResponseBody
    public Ret books(@RequestBody ClientRequest requet) {
        try {

            Partner partner = requet.getPartner();

            ClientService instance = ClientFactory.getInstance(partner.getId());

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            List<CPBook> bookList = instance.getBookList(partner);

            return Ret.success(bookList);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Ret.error(-1, "书籍列表获取异常");
        }
    }

    @RequestMapping("/book")
    @ResponseBody
    public Ret book(@RequestBody ClientRequest requet) {
        try {

            Partner partner = requet.getPartner();
            String bookId = requet.getBookId();

            ClientService instance = ClientFactory.getInstance(partner.getId());

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            CPBook bookInfo = instance.getBookInfo(partner, bookId);

            return Ret.success(bookInfo);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Ret.error(-1, "书籍详情获取异常");
        }
    }

    @RequestMapping("/chapters")
    @ResponseBody
    public Ret chapters(@RequestBody ClientRequest requet) {
        try {

            Partner partner = requet.getPartner();
            String bookId = requet.getBookId();


            ClientService instance = ClientFactory.getInstance(partner.getId());

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            List<CPVolume> volumeList = instance.getVolumeList(partner, bookId);

            return Ret.success(volumeList);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Ret.error(-1, "章节列表获取异常");
        }
    }

    @RequestMapping("/chapter")
    @ResponseBody
    public Ret chapter(@RequestBody ClientRequest requet) {
        try {
            Partner partner = requet.getPartner();
            String bookId = requet.getBookId();
            String volumeId = requet.getVolumeId();
            String chapterId = requet.getChapterId();

            ClientService instance = ClientFactory.getInstance(partner.getId());

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            CPChapter cpChapterInfo = instance.getCPChapterInfo(partner, bookId, volumeId, chapterId);

            return Ret.success(cpChapterInfo);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Ret.error(-1, "章节内容获取异常");
        }
    }

    /**
     * 获取指定CP的所有分类信息
     *
     * @param partnerId
     * @return
     */
    @RequestMapping("/cpCategoryList/{partnerId}")
    @ResponseBody
    public Ret cpCategoryList(@PathVariable("partnerId") Long partnerId) {
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
                List<CPCategory> categoryList = instance.getCpCategoryList(partner);
                if (Objects.isNull(categoryList)) {
                    return Ret.error(-2, "该CP不支持获取指定的所有分类信息 " + partnerId);
                } else {
                    return Ret.success(categoryList);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return Ret.error(-1, "获取指定CP的所有分类信息失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Ret.error(-1, "获取指定CP的所有分类信息失败");
        } finally {
            TraceKeyHolder.clear();
        }
    }

    /**
     * 获取指定书籍的CP分类信息, 及其按我方匹配后的值。
     *
     * @param partnerId
     * @param cpBookId
     * @return
     */
    @RequestMapping("/cpCategory/{partnerId}/{cpBookId}")
    @ResponseBody
    public Ret cpCategory(@PathVariable("partnerId") Long partnerId, @PathVariable("cpBookId") String cpBookId) {
        try {

            if (StringUtils.isBlank(cpBookId)) {
                return Ret.error(-1, "cpBookId 不能为空");
            }

            Partner partner = partnerService.getPartnerById(partnerId);

            if (partner == null) {
                return Ret.error(-1, "partnerId=[" + partnerId + "]不存在");
            }

            if (StringUtils.isBlank(partner.getAliasId())) {
                partner.setAliasId(partner.getId() + "");
            }

            try {
                ClientService instance = ClientFactory.getInstance(partner.getId());
                CPBook cpBook = instance.getBookInfo(partner, cpBookId);
                if (Objects.isNull(cpBook)) {
                    return Ret.error(-2, "查无此书 " + partnerId + cpBookId);
                }
                Map<String, String> cpCategoryMap = Optional.ofNullable(instance.getCpCategoryList(partner))
                        .map(l -> l.stream().collect(Collectors.toMap(CPCategory::getId, CPCategory::getName, (a, b) -> b)))
                        .orElse(null);
                if (Objects.isNull(cpCategoryMap)) {
                    logger.warn("该cp无分类接口 {} {}", partnerId, cpBookId);
                } else {
                    cpBook.setReadableCategory(cpCategoryMap.get(cpBook.getCategory()));
                }
                PartnerCategoryModel partnerCategoryModel = bookCategoryService.getCategory(partnerId, cpBook.getCategory());
                cpBook.setPartnerCategoryModel(partnerCategoryModel);
                return Ret.success(cpBook);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return Ret.error(-1, "获取指定书籍的CP分类信息, 及其按我方匹配后的值 失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Ret.error(-1, "获取指定书籍的CP分类信息, 及其按我方匹配后的值 失败");
        } finally {
            TraceKeyHolder.clear();
        }
    }

    @RequestMapping("/bookSearch")
    @ResponseBody
    public String bookSearch(String action, String param) {
        if (StringUtils.isBlank(action)) {
            return failResponse("action参数为空");
        }

        if (StringUtils.isBlank(param)) {
            return failResponse("param参数为空");
        }

        String data = null;
        try {
            data = URLDecoder.decode(param, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        logger.info("[book search begin] receive requeset, action={}, data={}", action, data);

        Long start = System.currentTimeMillis();

        try {
            ClientRequest params = JSONObject.parseObject(data, ClientRequest.class);
            Partner owchPartner = params.getPartner();
            switch (action) {
                case "bookList":
                    return successResponse("书籍列表获取成功", bookCommonService.getBookList(owchPartner));
                case "bookInfo":
                    return successResponse("书籍详情获取成功", bookCommonService.getBookInfo(owchPartner, params.getBookId()));
                case "chapterList":
                    return successResponse("章节列表获取成功", bookCommonService.getVolumeList(owchPartner, params.getBookId()));
                case "chapterInfo":
                    return successResponse("章节详情获取成功", bookCommonService.getCPChapterInfo(owchPartner, params.getBookId(), params.getVolumeId(), params.getChapterId()));
                case "maxChapterInfo":
                    return successResponse("最大章节获取成功", bookCommonService.getMaxCpChapterInfo(owchPartner,params.getBookId()));
                default:
                    return failResponse("无此方法");
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            logger.info("[book search end] spend time {} s", (System.currentTimeMillis() - start) / 1000.0);
        }

        return failResponse("系统异常");
    }

    private String failResponse(String msg) {
        Map<String, Object> ret = new HashMap<>(2);
        ret.put("result", 0);
        ret.put("msg", msg);
        return JSONObject.toJSONString(ret);
    }

    private String successResponse(String msg, Object object) {
        Map<String, Object> ret = new HashMap<>(3);
        ret.put("result", 1);
        ret.put("msg", msg);
        ret.put("data", object);
        return JSONObject.toJSONString(ret);
    }

}
