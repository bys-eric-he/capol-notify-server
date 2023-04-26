CREATE TABLE IF NOT EXISTS `t_user`
(
    `id`                       bigint       NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `account`                  varchar(80)  NOT NULL COMMENT '账号',
    `password`                 varchar(64)  NULL     DEFAULT NULL COMMENT '登录密码',
    `service_id`               varchar(64)  NULL     DEFAULT NULL COMMENT '业务系统id',
    `service_name`             varchar(128) NULL     DEFAULT NULL COMMENT '业务系统名称',
    `salt`                     varchar(6)   NULL     DEFAULT NULL COMMENT '密码Salt',
    `disabled`                 bit(1)       NULL     DEFAULT NULL COMMENT '是否禁用(0-未禁用 1-禁用)',
    `status`                   tinyint      NOT NULL DEFAULT 1 COMMENT '记录状态 (0.删除 1.正常)',
    `latest_login_datetime`    datetime(6)  NULL     DEFAULT NULL COMMENT '最近一次登录时间',
    `created_datetime`         datetime(6)  NULL     DEFAULT NULL COMMENT '创建时间',
    `latest_modified_datetime` datetime(6)  NULL     DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `UK_account` (`account`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '业务系统用户表';

CREATE TABLE IF NOT EXISTS `t_user_queue`
(
    `id`                       bigint       NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `user_id`                  bigint       NOT NULL COMMENT '用户id',
    `exchange`                 varchar(80)  NOT NULL COMMENT '队列使用的交换机',
    `routing`                  varchar(100) NULL     DEFAULT NULL COMMENT '队列使用的路由',
    `queue`                    varchar(100) NULL     DEFAULT NULL COMMENT '队列名称',
    `business_type`            varchar(20)  NULL     DEFAULT NULL COMMENT '队列业务类型(通过枚举值定义)',
    `priority`                 int          NULL     DEFAULT 0 COMMENT '队列优先级',
    `disabled`                 bit(1)       NULL     DEFAULT 0 COMMENT '是否禁用(0-未禁用 1-禁用)',
    `status`                   tinyint      NOT NULL DEFAULT 1 COMMENT '记录状态 (0.删除 1.正常)',
    `created_datetime`         datetime(6)  NULL     DEFAULT NULL COMMENT '创建时间',
    `latest_modified_datetime` datetime(6)  NULL     DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `UK_queue` (`queue`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '业务系统使用的队列';



INSERT INTO t_user_queue (user_id,exchange,routing,queue,business_type,priority,disabled,status,created_datetime,latest_modified_datetime) VALUES
(1830828142927666,'capol_notify_exchange','capol_notify_routing_apply_for_leave','capol_notify_queue_apply_for_leave','ASK-FOR-LEAVE',1,0,1,'2023-04-18 15:09:05','2023-04-18 15:09:05'),
(1830828142927666,'capol_notify_exchange','capol_notify_routing_apply_for_compensatory_leave','capol_notify_queue_apply_for_compensatory_leave','COMPENSATORY-LEAVE',1,0,1,'2023-04-18 15:10:18','2023-04-18 15:10:18'),
(1830828142927666,'capol_notify_exchange','capol_notify_routing_apply_for_ot','capol_notify_queue_apply_for_ot','APPLY-FOR-OT',1,0,1,'2023-04-18 15:10:21','2023-04-18 15:10:21'),
(1830828142927666,'capol_notify_exchange','capol_notify_routing_apply_for_loan','capol_notify_queue_apply_for_loan','APPLY-FOR-LOAN',1,0,1,'2023-04-18 15:15:11','2023-04-18 15:15:11');

CREATE TABLE IF NOT EXISTS `t_user_queue_message`
(
    `id`                  bigint      NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `service_id`          varchar(64) NULL DEFAULT NULL COMMENT '业务系统id',
    `user_id`                  bigint       NOT NULL COMMENT '用户id',
    `queue_id`            bigint      NOT NULL COMMENT '队列id',
    `message_type`        varchar(100)     NOT NULL COMMENT '消息类型(1-钉钉普通消息 2-钉钉群组消息 3-邮件消息)',
    `business_type`       varchar(100)     NOT NULL COMMENT '消息业务类型',
    `priority`            integer     NOT NULL DEFAULT 0 COMMENT '消息优先级',
    `content`             mediumtext  NULL DEFAULT NULL COMMENT '消息内容',
    `send_response`       text        NULL DEFAULT NULL COMMENT '消息发送响应内容',
    `retry_count`         integer     NOT NULL DEFAULT 0 COMMENT '消息处理重试次数',
    `process_status`      tinyint     NOT NULL COMMENT '消息处理状态(0-待处理 1-成功 2-失败)',
    `status`              tinyint     NOT NULL COMMENT '记录状态(0-删除 1-正常)',
    `consumer_start_time` datetime(6) NULL DEFAULT NULL COMMENT '消息消费开始时间',
    `consumer_end_time`   datetime(6) NULL DEFAULT NULL COMMENT '消息消费结束时间',
    `created_datetime`        datetime(6) NULL DEFAULT NULL COMMENT '创建时间',
    `latest_modified_datetime`  datetime(6) NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '业务系统消息表';



