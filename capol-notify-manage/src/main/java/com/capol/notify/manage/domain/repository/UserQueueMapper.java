package com.capol.notify.manage.domain.repository;

import com.capol.notify.manage.domain.model.queue.UserQueueDO;
import com.capol.notify.manage.domain.model.user.UserId;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserQueueMapper extends BaseMapperX<UserQueueDO> {
    List<UserQueueDO> findByUserId(UserId userId);
}
