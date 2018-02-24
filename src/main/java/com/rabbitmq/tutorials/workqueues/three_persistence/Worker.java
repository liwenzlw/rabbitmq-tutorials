package com.rabbitmq.tutorials.workqueues.three_persistence;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Created by zenglw on 2018/2/14.
 */
public class Worker {
    private final static String QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {

        //step 1: create a connection to the server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.0.103");
        Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();

        //step 2: To send, we must declare a queue for us to receive from; then we can receive a message from the queue:
        //请注意，我们也在这里声明队列。因为我们可能会在发布者之前启动消费者，所以我们希望确保队列存在，然后再试图使用消息。
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        channel.basicQos(1); // accept only one unack-ed message at a time (see below)
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                System.out.println(" [x] Received '" + message + "'");
                try {
                    doWork(message);
                } finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);

    }

    private static void doWork(String task) {
        try {
            int sleepTime = Integer.valueOf(task.substring(task.length()-1))*1000;
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {

        }

    }
}
