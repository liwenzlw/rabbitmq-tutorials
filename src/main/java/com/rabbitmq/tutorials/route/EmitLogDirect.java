package com.rabbitmq.tutorials.route;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLogDirect {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv)
                  throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.0.103");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        int messageCount = 1;
        String routingKey = "";
        while(messageCount<=10) {
            String message = "Message "+ messageCount;
            if(0 == messageCount%4) {
                routingKey = "error";
                message = "error " + message;
            } else if(0 == messageCount%3) {
                routingKey = "info";
                message = "info " + message;
            } else if(0 == messageCount%2) {
                routingKey = "warn";
                message = "warn " + message;
            } else {
                routingKey = "";
                message = "丢弃 " + message;
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