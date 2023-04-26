package com.capol.notify.manage.domain.repository;

import com.capol.notify.manage.domain.model.message.UserQueueMessageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserQueueMessageMapper extends BaseMapperX<UserQueueMessageDO> {

    UserQueueMessageDO findByMessageId(@Param("id") Long id);
}
