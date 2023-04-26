package com.capol.notify.manage.application.message;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.capol.notify.manage.domain.*;
import com.capol.notify.manage.domain.model.message.UserQueueMessageDO;
import com.capol.notify.manage.domain.repository.UserQueueMessageMapper;
import com.capol.notify.sdk.EnumMessageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息处理应用层服务
 *
 * @author heyong
 * @since 2023-04-21 16:52:11
 */
@Slf4j
@Service
public class MessageService {

    private UserQueueMessageMapper userQueueMessageMapper;

    public MessageService(UserQueueMessageMapper userQueueMessageMapper) {
        this.userQueueMessageMapper = userQueueMessageMapper;
    }

    /**
     * 获取指定MessageID的消息
     *
     * @param id
     * @return
     */
    public UserQueueMessageDO getMessageById(Long id) {
        LambdaQueryWrapper<UserQueueMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserQueueMessageDO::getId, id);
        queryWrapper.eq(UserQueueMessageDO::getStatus, EnumStatusType.NORMAL.getCode());
        return userQueueMessageMapper.selectOne(queryWrapper);
    }

    /**
     * 删除指定Ids的消息(物理删除)
     *
     * @param ids
     */
    public void deleteMessageByIds(List<Long> ids) {
        int rows = userQueueMessageMapper.deleteBatchIds(ids);
        log.info("-->物理删除消息数:{}条! Ids：{}", rows, JSON.toJSONString(ids));
    }

    /**
     * 分页获取指定消息类型的消息
     *
     * @param processStatusTypes
     * @param messageTypes
     * @param startDateTime
     * @param endDataTime
     * @param pageParam
     * @return
     */
    public PageResult<UserQueueMessageDO> getMessageByPage(List<EnumProcessStatusType> processStatusTypes, List<EnumMessageType> messageTypes,
                                                           String startDateTime, String endDataTime,
                                                           PageParam pageParam) {

        List<Integer> statusTypes = processStatusTypes.stream().map(EnumProcessStatusType::getCode).collect(Collectors.toList());
        List<String> types = messageTypes.stream().map(EnumMessageType::getCode).collect(Collectors.toList());

        IPage<UserQueueMessageDO> page = new Page<>(pageParam.getPageNo(), pageParam.getPageSize());
        LambdaQueryWrapper<UserQueueMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserQueueMessageDO::getProcessStatus, statusTypes);
        queryWrapper.in(UserQueueMessageDO::getMessageType, types);
        queryWrapper.eq(UserQueueMessageDO::getStatus, EnumStatusType.NORMAL.getCode());
        queryWrapper.between(UserQueueMessageDO::getCreatedDatetime, startDateTime, endDataTime);
        queryWrapper.orderByAsc(UserQueueMessageDO::getCreatedDatetime);
        IPage<UserQueueMessageDO> userQueueMessageDOIPage = userQueueMessageMapper.selectPage(page, queryWrapper);

        List<UserQueueMessageDO> userQueueMessageDOS = userQueueMessageDOIPage.getRecords();
        Long total = userQueueMessageDOIPage.getTotal();

        log.info("-->获取到符合条件的消息记录条数：{} 条!", total);

        return new PageResult<>(userQueueMessageDOS, total);
    }

    /**
     * 获取指定条件范围的消息总记录数
     *
     * @param processStatusTypes
     * @param messageTypes
     * @param startDateTime
     * @param endDataTime
     * @return
     */
    public Long getTotalCountByParam(List<EnumProcessStatusType> processStatusTypes, List<EnumMessageType> messageTypes,
                                     String startDateTime, String endDataTime) {
        List<Integer> statusTypes = processStatusTypes.stream().map(EnumProcessStatusType::getCode).collect(Collectors.toList());
        List<String> types = messageTypes.stream().map(EnumMessageType::getCode).collect(Collectors.toList());

        LambdaQueryWrapper<UserQueueMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserQueueMessageDO::getProcessStatus, statusTypes);
        queryWrapper.in(UserQueueMessageDO::getMessageType, types);
        queryWrapper.eq(UserQueueMessageDO::getStatus, EnumStatusType.NORMAL.getCode());
        queryWrapper.between(UserQueueMessageDO::getCreatedDatetime, startDateTime, endDataTime);
        queryWrapper.orderByAsc(UserQueueMessageDO::getCreatedDatetime);

        return userQueueMessageMapper.selectCount(queryWrapper);
    }

    /**
     * 获取指定条件范围的消息总记录数
     *
     * @param startDateTime
     * @param endDataTime
     * @return
     */
    public List<UserQueueMessageDO> getMessageByDateTime(String startDateTime, String endDataTime) {
        LambdaQueryWrapper<UserQueueMessageDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserQueueMessageDO::getStatus, EnumStatusType.NORMAL.getCode());
        queryWrapper.between(UserQueueMessageDO::getCreatedDatetime, startDateTime, endDataTime);
        queryWrapper.orderByAsc(UserQueueMessageDO::getCreatedDatetime);

        return userQueueMessageMapper.selectList(queryWrapper);
    }

    /**
     * 保存或更新消息
     *
     * @param messageDO
     * @return
     */
    public Long saveOrUpdateMessage(UserQueueMessageDO messageDO) {
        UserQueueMessageDO userQueueMessageDO = userQueueMessageMapper.findByMessageId(messageDO.getId());
        if (userQueueMessageDO == null) {
            int rows = userQueueMessageMapper.insert(messageDO);
            if (rows > 0) {
                log.info("-->保存消息成功!");
            } else {
                log.error("-->保存消息失败, 消息ID:{}", messageDO.getId());
            }
        } else {
            messageDO.buildBaseInfo();
            int rows = userQueueMessageMapper.updateById(messageDO);
            if (rows > 0) {
                log.info("-->更新消息成功!");
            } else {
                log.error("-->更新消息失败, 消息ID:{}", messageDO.getId());
            }
        }

        return messageDO.getId();
    }
}
