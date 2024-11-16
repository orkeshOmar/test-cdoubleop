package com.dz.coop.module.service.report;

import com.dz.coop.module.model.report.HourReportModel;
import com.dz.coop.module.model.report.NewBookModel;
import com.dz.coop.module.model.report.TopUpdateBookModel;

import java.util.List;

/**
 * @project: coop-client
 * @description: 书籍报表服务
 * @author: songwj
 * @date: 2019-03-22 20:19
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public interface BookReportService {

    List<HourReportModel> getHourReportRecord();

    List<TopUpdateBookModel> getTopUpdateBookRecord(Integer topNum);

    Integer getCurrentDayUpdateNum();

    /**
     * 获取当天新抓取书籍数量
     * @return
     */
    Integer getCurrentDayAddBookNum();

    /**
     * 获取当天新抓取书籍
     * @return
     */
    List<NewBookModel> getCurrentDayAddBookRecord();

}
