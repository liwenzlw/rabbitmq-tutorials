package com.rabbitmq.tutorials.topic;

import com.rabbitmq.client.*;
import com.sun.deploy.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;

public class ReceiveLogsTopic {
  private static final String EXCHANGE_NAME = "topic_logs";
  //private static final String[] ROUTE_KEY = {"#"};
  //private static final String[] ROUTE_KEY = {"kern.*"};
  //private static final String[] ROUTE_KEY = {"*.critical"};
  private static final String[] ROUTE_KEY = {"kern.*","*.critical"};

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("192.168.0.103");
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(EXCHANGE_NAME, "topic");//声明交换器
    String queueName = channel.queueDeclare().getQueue(); //随机生成queueName。queueName包含一个随机队列名称。例如，它可能看起来像amq.gen-JzTY20BRgKO-HjmUJj0wLg。

    for (String routeKey: ROUTE_KEY) {
      channel.queueBind(queueName, EXCHANGE_NAME, routeKey); //将交换器和队列绑定
    }

    System.out.println(" [*] Waiting for [" + StringUtils.join(Arrays.asList(ROUTE_KEY)," ") + "] messages. To exit press CTRL+C");

    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope,
                                 AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Received '" + message + "'");
      }
    };
    channel.basicConsume(queueName, true, consumer);
  }
}