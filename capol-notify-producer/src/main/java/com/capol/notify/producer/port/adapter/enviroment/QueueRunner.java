package com.capol.notify.producer.port.adapter.enviroment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.EnumStatusType;
import com.capol.notify.manage.domain.model.queue.UserQueueDO;
import com.capol.notify.manage.domain.repository.UserQueueMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * 自动批量创建消息队列
 */
@Slf4j
@Component
@Order(1)
public class QueueRunner implements CommandLineRunner {

    private RabbitTemplate rabbitTemplate;
    private RabbitProperties rabbitProperties;
    private UserQueueMapper userQueueMapper;

    public QueueRunner(RabbitTemplate rabbitTemplate, RabbitProperties rabbitProperties, UserQueueMapper userQueueMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitProperties = rabbitProperties;
        this.userQueueMapper = userQueueMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("-------自动创建队列到消息服务中--------");
        log.info("-->消息服务器:{} ,端口号：{} ,虚拟主机:{}", rabbitProperties.getHost(), rabbitProperties.getPort(), rabbitProperties.getVirtualHost());
        this.createQueue();
        this.deleteQueue();
        log.info("-------自动创建队列到消息服务完成--------");
    }

    /**
     * 创建队列
     *
     * @throws IOException
     */
    public void createQueue() throws IOException, TimeoutException {
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserQueueDO::getDisabled, false);
        queryWrapper.eq(UserQueueDO::getStatus, EnumStatusType.NORMAL.getCode());
        List<UserQueueDO> userQueueDOS = userQueueMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(userQueueDOS)) {
            log.warn("-->当前服务无队列配置信息!!!");
            return;
        }
        //取出所有交换机并去重
        List<String> exchanges = userQueueDOS.stream().map(exchange -> exchange.getExchange()).distinct().collect(Collectors.toList());

        final ConnectionFactory factory = rabbitTemplate.getConnectionFactory();
        final Connection connection = factory.createConnection();
        // 创建频道
        Channel channel = connection.createChannel(false);

        /**
         * 声明交换机
         * 参数1：交换机名称
         * 参数2：交换机类型，fanout、topic、direct、headers
         */
        exchanges.forEach(exchange -> {
            try {
                channel.exchangeDeclare(exchange, "direct");
                log.info("-->声明交换机:{} 成功! ", exchange);
            } catch (IOException exception) {
                log.error("声明交换机:{} 发生异常!!", exchange, exception);
                throw new ApplicationException(String.format("声明交换机:%s 发生异常!!", exchange), EnumExceptionCode.InternalServerError);
            }
        });

        /** 声明（创建）队列
         * 参数1：队列名称
         * 参数2：是否定义持久化队列
         * 参数3：是否独占本次连接
         * 参数4：是否在不使用的时候自动删除队列
         * 参数5：队列其它参数
         */
        userQueueDOS.forEach(queue -> {
            try {
                /**
                 * Map<String, Object> arguments = new HashMap<>(8);
                 * //一般设置10个优先级，数字越大，优先级越高, 官方允许是 0-255 之间 此处设置10 允许优化级范围0-10 不要设置过大 浪费CPU与内存
                 * arguments.put("x-max-priority", queue.getPriority());
                 */
                //创建队列
                channel.queueDeclare(queue.getQueue(), true, false, false, null);
                //队列绑定交换机
                channel.queueBind(queue.getQueue(), queue.getExchange(), queue.getRouting());
                log.info("-->创建队列:{} 成功! 绑定的交换机:{},路由:{}", queue.getQueue(), queue.getExchange(), queue.getRouting());
            } catch (IOException exception) {
                log.error("-->创建队列:" + queue.getQueue() + "异常! 详细内容：" + exception);
                throw new ApplicationException(String.format("创建队列: %s 异常!", queue.getQueue()), EnumExceptionCode.InternalServerError);
            }
        });

        // 关闭资源
        channel.close();
        connection.close();
    }

    /**
     * 删除队列
     *
     * @throws IOException
     */
    private void deleteQueue() throws IOException {
        LambdaQueryWrapper<UserQueueDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserQueueDO::getDisabled, true);
        List<UserQueueDO> userQueueDOS = userQueueMapper.selectList(queryWrapper);

        final ConnectionFactory factory = rabbitTemplate.getConnectionFactory();
        final Connection connection = factory.createConnection();
        // 创建频道
        Channel channel = connection.createChannel(false);
        userQueueDOS.forEach(queue -> {
            try {
                channel.queueDelete(queue.getQueue());
                log.info("-->删除队列:{} 成功! 绑定的交换机:{},路由:{}", queue.getQueue(), queue.getExchange(), queue.getRouting());
            } catch (IOException e) {
                log.error("-->删除队列:" + queue + "异常! 详细内容：" + e);
                throw new ApplicationException(String.format("删除队列: %s 异常!", queue), EnumExceptionCode.InternalServerError);
            }
        });
    }
}
