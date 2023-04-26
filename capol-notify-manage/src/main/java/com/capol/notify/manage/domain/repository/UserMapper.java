package com.capol.notify.manage.domain.repository;

import com.capol.notify.manage.domain.model.user.UserDO;
import com.capol.notify.manage.domain.model.user.UserId;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapperX<UserDO>{

    UserDO findByUserId(UserId userId);
    UserDO findByAccount(@Param("account") String account);

}
