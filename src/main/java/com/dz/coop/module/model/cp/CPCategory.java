package com.dz.coop.module.model.cp;

import lombok.Data;

@Data
public class CPCategory {
    /**
     * cp分类id
     */
    private String id;

    /**
     * cp分类名称
     */
    private String name;

    public CPCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
