package com.dz.coop.module.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

public class UpdateMsg {

    private Long id;
    private String tableName;
    private String bookId;

    /**
     * 1 加载更新 2 删除
     */
    private Integer actionType;

    /**
     * 1 定时扫描更新 2 推荐书库 3代支付 4原始书库 5上架下架 6目录替换 7扒书
     */
    private Integer type;
    private Date ctime;
    private Date utime;

    public UpdateMsg(Table table, String bookId, Action action, Type type) {
        this.tableName = table.getName();
        this.bookId = bookId;
        this.actionType = action.getAction();
        this.type = type.getType();
    }


    public Integer getActionType() {
        return actionType;
    }


    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableName() {
        if (!StringUtils.isBlank(tableName)) {
            return tableName.toLowerCase();
        }
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }


    public Integer getType() {
        return type;
    }


    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getUtime() {
        return utime;
    }

    public void setUtime(Date utime) {
        this.utime = utime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public enum Table {

        BOOK("b_book"),

        CHAPTER("b_owchcp_chapter");

        private String name;

        Table(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Action {
        UPDATE(1), DELETE(2);
        private Integer action;

        Action(Integer action) {
            this.action = action;
        }

        public Integer getAction() {
            return action;
        }
    }

    public enum Type {
        SYNC(1), TIMER(4);
        private Integer type;

        Type(Integer type) {
            this.type = type;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }
    }

}
