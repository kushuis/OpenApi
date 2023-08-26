package com.kushui.project.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import static com.kushui.kuapicommon.model.constant.RabbitmqConstant.*;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
public class MqInitMain {

    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String EXCHANGE_NAME = EXCHANGE_INTERFACE_CONSISTENT;
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            // 创建队列，随机分配一个队列名称
            String queueName = QUEUE_INTERFACE_CONSISTENT;
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY_INTERFACE_CONSISTENT);

            System.out.println("================rabbitmq中创建交换机和队列成功===================");

        } catch (Exception e) {

        }

    }
}
