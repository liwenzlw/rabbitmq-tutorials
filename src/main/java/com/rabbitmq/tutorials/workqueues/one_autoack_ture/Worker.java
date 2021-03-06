package com.rabbitmq.tutorials.workqueues.one_autoack_ture;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Created by zenglw on 2018/2/14.
 */
public class Worker {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {

        //step 1: create a connection to the server
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.0.103");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //step 2: To send, we must declare a queue for us to receive from; then we can receive a message from the queue:
        //请注意，我们也在这里声明队列。因为我们可能会在发布者之前启动消费者，所以我们希望确保队列存在，然后再试图使用消息。
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //告诉服务器将队列中的消息传递给我们。由于它会异步推送消息，因此我们以对象的形式提供回调，缓冲消息直到准备好使用它们。这是一个DefaultConsumer子类所做的
        final Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");

                System.out.println(" [x] Received '" + message + "'");
                try {
                    doWork(message);
                } finally {
                    System.out.println(" [x] Done");
                }
            }
        };
        boolean autoAck = true; // acknowledgment is covered below
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
