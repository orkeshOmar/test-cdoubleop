package com.dz.coop.module.service.cp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dz.coop.common.annotation.AccessLimit;
import com.dz.coop.common.util.HttpUtil;
import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.model.cp.CPBook;
import com.dz.coop.module.model.cp.CPCategory;
import com.dz.coop.module.model.cp.CPChapter;
import com.dz.coop.module.model.cp.CPVolume;
import com.dz.coop.module.service.ClientFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author panqz 2018-11-26 8:49 PM
 */
public interface ClientService {

    @AccessLimit(cpPartnerIds = {100400, 432, 356, 14}, limitSecond = {1, 1, 2, 1}, limitCount = {40, 40, 100, 5}, sleepMillisecond = {0, 0, 50, 450})
    List<CPBook> getBookList(Partner owchPartner) throws Exception;

    @AccessLimit(cpPartnerIds = {100400, 432, 356, 14}, limitSecond = {1, 1, 2, 1}, limitCount = {40, 40, 100, 5}, sleepMillisecond = {0, 0, 50, 450})
    CPBook getBookInfo(Partner owchPartner, String cpBookId) throws Exception;

    @AccessLimit(cpPartnerIds = {100400, 432, 356, 14}, limitSecond = {1, 1, 2, 1}, limitCount = {40, 40, 100, 5}, sleepMillisecond = {0, 0, 50, 450})
    List<CPVolume> getVolumeList(Partner owchPartner, String cpBookId) throws Exception;

    CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String cpChapterId) throws Exception;

    @AccessLimit(cpPartnerIds = {100400, 432, 356, 14}, limitSecond = {1, 1, 2, 1}, limitCount = {40, 40, 100, 5}, sleepMillisecond = {0, 0, 50, 450})
    CPChapter getCPChapterInfo(Partner owchPartner, String cpBookId, String cpVolumeId, String cpChapterId) throws Exception;

    ThirdPart[] getClient();

    /**
     * 获取CP的分类信息
     * @param owchPartner
     * @return
     * @throws Exception
     */
    default List<CPCategory> getCpCategoryList(Partner owchPartner) throws Exception {
        String categoryUrl = ClientFactory.getUrl(owchPartner.getId()).getCategoryListUrl(owchPartner);
        // 大部分非自有CP都未配置该项
        if (Objects.isNull(categoryUrl)) {
            return null;
        }

        String resultStr = HttpUtil.sendGet(categoryUrl);
        if (StringUtils.isBlank(resultStr)) {
            return null;
        }
        JSONArray categoryJson = JSON.parseArray(resultStr);

        if (categoryJson == null || categoryJson.isEmpty()) {
            return null;
        }

        List<CPCategory> categorys = new ArrayList<>(categoryJson.size());

        for (int i = 0; i < categoryJson.size(); i++) {
            JSONObject book = categoryJson.getJSONObject(i);
            categorys.add(new CPCategory(book.getString("id"), book.getString("name")));
        }

        return categorys;
    }

}
