package com.rabbitmq.tutorials.cc;



import com.rabbitmq.client.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过CC或BCC指定多个routing key
 */
public class Send {
    /**
     * 队列名称
     */
    private final static String QUEUE_NAME = "tryCC.myGroup";
    private final static String EXCHANGE_NAME = "tryCC";

    public static void main(String[] argv) throws Exception {

        //step 1: create a connection to the server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");//主机名称或IP地址
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();//创建频道

         channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        //step 2: To send, we must declare a queue for us to send to; then we can publish a message to the queue:
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);//如果队列已经存在不会再创建
        channel.queueDeclare(QUEUE_NAME+2, false, false, false, null);//如果队列已经存在不会再创建
        channel.queueDeclare(QUEUE_NAME+3, false, false, false, null);//如果队列已经存在不会再创建

        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"routingkey1");
        channel.queueBind(QUEUE_NAME+2,EXCHANGE_NAME,"routingkey1");
        channel.queueBind(QUEUE_NAME+3,EXCHANGE_NAME,"routingkey1");
        String message = "Hello World!";

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        Map<String, Object> headers = new HashMap<String, Object>();
        List<String> ccList = new ArrayList<String>();
        ccList.add("routingkey2");
        ccList.add("routingkey3");
        headers.put("BCC", ccList);
        AMQP.BasicProperties props = builder.headers(headers).build();
        channel.basicPublish(EXCHANGE_NAME, "routingkey1", props, message.getBytes());


        System.out.println(" [x] Sent '" + message + "'");

        //step 3: Lastly, we close the channel and the connection;
        channel.close();
        connection.close();
    }
}
