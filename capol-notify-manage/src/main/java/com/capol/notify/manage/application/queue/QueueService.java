package com.capol.notify.manage.application.queue;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.capol.notify.manage.application.user.querystack.UserQueueDTO;
import com.capol.notify.manage.domain.EnumStatusType;
import com.capol.notify.manage.domain.model.queue.UserQueueDO;
import com.capol.notify.manage.domain.repository.UserQueueMapper;
import com.capol.notify.sdk.EnumMessageBusinessType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 队列处理应用层服务
 *
 * @author heyong
 * @since 2023-04-21 16:52:21
 */
@Slf4j
@Service
public class QueueService {

    private final UserQueueMapper userQueueMapper;

    public QueueService(UserQueueMapper userQueueMapper) {
        this.userQueueMapper = userQueueMapper;
    }

    public UserQueueDTO getUserQueueByIdAndType(Long userId, EnumMessageBusinessType businessType) {
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserQueueDO::getUserId, userId);
        queryWrapper.eq(UserQueueDO::getBusinessType, businessType.getCode());
        queryWrapper.eq(UserQueueDO::getStatus, EnumStatusType.NORMAL.getCode());
        queryWrapper.eq(UserQueueDO::getDisabled,false);

        UserQueueDO userQueueDO = userQueueMapper.selectOne(queryWrapper);
        if (userQueueDO != null && !userQueueDO.getDisabled()) {
            return new UserQueueDTO(
                    userQueueDO.getId(),
                    userQueueDO.getUserId(),
                    userQueueDO.getExchange(),
                    userQueueDO.getRouting(),
                    userQueueDO.getQueue(),
                    userQueueDO.getPriority(),
                    userQueueDO.getBusinessType(),
                    userQueueDO.getDisabled()
            );
        } else {
            log.warn("-->用户ID:{} 业务类型:{} 的消息队列已禁用或不存在,请检查配置!", userId, businessType);
            return null;
        }
    }
}
