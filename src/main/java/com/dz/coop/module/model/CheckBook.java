package com.dz.coop.module.model;

import lombok.Data;

/**
 * @project: coop-client
 * @description: 书库审核
 * @author: songwj
 * @date: 2023-05-19 7:49 下午
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2023 DIANZHONG TECH. All Rights Reserved.
 */
@Data
public class CheckBook {

    private Long id;

    /**
     * 书籍评级，-1：未评级；1：不通过；2：一级敏感；4：通过；7：一级可上架
     */
    private Integer bookGrade;

    private String bookId;

}