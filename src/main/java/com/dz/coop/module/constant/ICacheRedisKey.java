package com.dz.coop.module.constant;

/**
 * @project: coop-client
 * @description: Redis常量
 * @author: songwj
 * @date: 2020-11-11 17:30
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2020 DIANZHONG TECH. All Rights Reserved.
 */
public interface ICacheRedisKey {

    int DB_1 = 1;

    int DB_2_TRAILER = 2;

    /**
     * CP访问限制key，limit_cp_{cpId}
     */
    String LIMIT_CP_KEY = "limit_cp_%s";

    /**
     * 书籍片花缓存key，trailer_{bookId}
     */
    String TRAILER_KEY = "trailer_%s";

}
