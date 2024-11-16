package com.dz.coop.conf;

import java.util.HashMap;
import java.util.Map;

/**
 * @project: coop-client
 * @description: 书籍过滤列表
 * @author: songwj
 * @date: 2019-07-10 21:36
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class BookFilterConf {

    public static Map<String, String> bookFilterMap = new HashMap<>();

    static {
        // key: cpId_cpBookId, value: bookId
        bookFilterMap.put("11_482549", "11000090014");
        bookFilterMap.put("11_558055", "11000149253");
        bookFilterMap.put("11_487698", "11010025769");
        bookFilterMap.put("50_2221", "11000131430");
        bookFilterMap.put("117_99965431", "11010025770");
        bookFilterMap.put("197_54", "11000158946");
        bookFilterMap.put("7000_10003859", "11000092311");
        bookFilterMap.put("114_100048", "11000107572");
    }

}
