package com.capol.notify.job.application;

import com.capol.notify.manage.application.message.MessageService;
import com.capol.notify.manage.domain.model.message.UserQueueMessageDO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Component
public class UserQueueMessageDeleteJobHandler {
    private MessageService messageService;

    public UserQueueMessageDeleteJobHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    public void deleteInit() {
        log.info("-->定时任务初始化, 用途:定时检测需要删除的消息!");
    }

    public void deleteDestroy() {
        log.info("-->定时任务销毁, 用途:定时检测需要删除的消息!");
    }

    @XxlJob(value = "deleteJobHandler", init = "deleteInit", destroy = "deleteDestroy")
    public ReturnT<String> checkDeleteMessageJob() throws Exception {
        try {
            //获取任务配置中的(任务参数*)参数
            String param = XxlJobHelper.getJobParam();
            if (StringUtils.isEmpty(param)) {
                log.error("-->deleteJobHandler 任务参数配置缺失, 配置参数格式, 例如：{} 第一个参数单位为(天),第二个参数单位为(天)", param, "5,2");
                return ReturnT.FAIL;
            }
            String[] params = param.split(",");
            if (params.length != 2) {
                log.error("-->deleteJobHandler 任务参数配置不合法, 当前配置参数：{}, 正确配置参数格式, 例如：{} 第一个和第二个参数单位均为(天)", param, "5,2");
                return ReturnT.FAIL;
            }
            //开始时间为3天之前的
            LocalDateTime startDateTime = LocalDateTime.now().minusDays(Long.valueOf(params[0]));
            //结束时间为当前时间1天之前的
            LocalDateTime endDateTime = LocalDateTime.now().minusDays(Long.valueOf(params[1]));

            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String startTime = dateTimeFormatter.format(startDateTime);
            String endTime = dateTimeFormatter.format(endDateTime);
            log.info("-->消息<删除处理器>进来了,开始时间:{} 结束时间:{}!", startTime, endTime);

            List<UserQueueMessageDO> userQueueMessageDOS = messageService.getMessageByDateTime(startTime, endTime);

            if (CollectionUtils.isEmpty(userQueueMessageDOS)) {
                log.info("-->暂无符合<删除>条件的消息记录! 查询时间区间, 开始时间:{} 结束时间:{}", startTime, endTime);
                return ReturnT.SUCCESS;
            }

            List<Long> ids = userQueueMessageDOS.stream().map(UserQueueMessageDO::getId).collect(Collectors.toList());
            messageService.deleteMessageByIds(ids);
            log.info("-->消息<删除>监控Job任务处理器执行成功!!!!");
            return ReturnT.SUCCESS;
        } catch (Exception exception) {
            log.error("-->消息<删除>监控Job任务处理器执行异常, 异常详情:{}", exception);
            return ReturnT.FAIL;
        }
    }
}
