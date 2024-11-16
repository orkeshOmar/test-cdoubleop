package com.dz.coop.module.mapper;


import com.dz.coop.module.model.UpdateMsg;

import java.util.List;


public interface UpdateMsgqueueMapper {

    void insertBatch(List<UpdateMsg> updateMsgList);

    void insert(UpdateMsg updateMsg);

}
