package com.dz.coop.module.model;

import com.dz.glory.common.utils.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @project: coop-client
 * @description: 合作伙伴分类映射模型
 * @author: songwj
 * @date: 2019-05-17 16:32
 * @company: DIANZHONG TECH
 * @copyright: Copyright © 2014-2019 DIANZHONG TECH. All Rights Reserved.
 */
public class PartnerCategoryModel {

    private Integer id;

    /**
     * CP合作伙伴ID
     */
    private Long cpPartnerId;

    /**
     * CP分类ID
     */
    private String cpCategoryId;

    /**
     * CP分类名
     */
    private String cpCategoryName;

    /**
     * 我方分类ID
     */
    private Integer typeId;

    /**
     * 我方分类级别，2：对应我方二级分类，3：对应我方三级分类，只能2或3
     */
    private Integer typeLevel;

    /**
     * 我方分类名
     */
    private String typeName;

    /**
     * 我方新版分类ID
     */
    private Integer typeV3Id;

    /**
     * 我方新版分类级别，2：对应我方二级分类，3：对应我方三级分类，只能2或3
     */
    private Integer typeV3Level;

    /**
     * 我方新版分类名
     */
    private String typeV3Name;

    /**
     * 我方分类id集合
     */
    private String typeIdSet;

    /**
     * 我方新版分类id集合
     */
    private String typeV3IdSet;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getCpPartnerId() {
        return cpPartnerId;
    }

    public void setCpPartnerId(Long cpPartnerId) {
        this.cpPartnerId = cpPartnerId;
    }

    public String getCpCategoryId() {
        return cpCategoryId;
    }

    public void setCpCategoryId(String cpCategoryId) {
        this.cpCategoryId = cpCategoryId;
    }

    public String getCpCategoryName() {
        return cpCategoryName;
    }

    public void setCpCategoryName(String cpCategoryName) {
        this.cpCategoryName = cpCategoryName;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getTypeLevel() {
        return typeLevel;
    }

    public void setTypeLevel(Integer typeLevel) {
        this.typeLevel = typeLevel;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getTypeV3Id() {
        return typeV3Id;
    }

    public void setTypeV3Id(Integer typeV3Id) {
        this.typeV3Id = typeV3Id;
    }

    public Integer getTypeV3Level() {
        return typeV3Level;
    }

    public void setTypeV3Level(Integer typeV3Level) {
        this.typeV3Level = typeV3Level;
    }

    public String getTypeV3Name() {
        return typeV3Name;
    }

    public void setTypeV3Name(String typeV3Name) {
        this.typeV3Name = typeV3Name;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getTypeIdSet() {
        // typeIdSet 不存在时, 取原来的 typeId
        if (StringUtils.isEmpty(typeIdSet)) {
            return (null != getTypeId() ? String.valueOf(getTypeId()) : "");
        } else if (typeIdSet.startsWith(",")) {
            return typeIdSet.substring(1, typeIdSet.length() - 1);
        }
        return typeIdSet;
    }

    public void setTypeIdSet(String typeIdSet) {
        this.typeIdSet = typeIdSet;
    }

    public String getTypeV3IdSet() {
        // typeIdV3Set 不存在时, 取原来的 typeIdV3
        if (StringUtils.isEmpty(typeV3IdSet)) {
            return (null != getTypeV3Id() ? String.valueOf(getTypeV3Id()) : "");
        } else if (typeV3IdSet.startsWith(",")){
            return typeV3IdSet.substring(1, typeV3IdSet.length() - 1);
        }
        return typeV3IdSet;
    }

    public void setTypeV3IdSet(String typeV3IdSet) {
        this.typeV3IdSet = typeV3IdSet;
    }
}
