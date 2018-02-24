package com.rabbitmq.tutorials.workqueues.one_autoack_ture;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Created by zenglw on 2018/2/14.
 */
public class NewTask {
    /**
     * 队列名称
     */
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {

        //step 1: create a connection to the server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.0.103");//主机名称或IP地址
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();//创建频道

        //step 2: To send, we must declare a queue for us to send to; then we can publish a message to the queue:
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);//如果队列已经存在不会再创建
        int messageCount = 1;
        while(messageCount<=10) {
            String message = "Message "+ messageCount;
            channel.basicPublish("","hello",null,message.getBytes());
            System.out.println("[x] Sent'" + message + "'");
            messageCount++;
        }

        //step 3: Lastly, we close the channel and the connection;
        channel.close();
        connection.close();
    }
}
