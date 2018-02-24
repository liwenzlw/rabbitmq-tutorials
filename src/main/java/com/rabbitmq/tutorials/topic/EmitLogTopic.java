package com.rabbitmq.tutorials.topic;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv)
                  throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.0.103");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        int messageCount = 1;
        String routingKey = "";
        while(messageCount<=10) {
            String message = "Message "+ messageCount;
            if(0 == messageCount%4) {
                routingKey = "error";
                message = "error " + message;
            } else if(0 == messageCount%3) {
                routingKey = "kern.critical";
                message = "kern.critical " + message;
            } else if(0 == messageCount%2) {
                routingKey = "auth.critical";
                message = "auth.critical " + message;
            } else {
                routingKey = "auth.info";
                message = "auth.info " + message;
            }
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
            System.out.println("[x] Sent'" + message + "'");
            messageCount +=1;
        }
        channel.close();
        connection.close();
    }
    //...
}