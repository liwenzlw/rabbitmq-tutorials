package com.rabbitmq.tutorials.rpc;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RPCClient {

  private Connection connection;
  private Channel channel;
  private String requestQueueName = "rpc_queue";
  private String replyQueueName;

  public RPCClient() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("192.168.0.103");

    connection = factory.newConnection();
    channel = connection.createChannel();

    //为回复声明独占的“callback”队列。
    replyQueueName = channel.queueDeclare().getQueue();
  }

  //会生成实际的RPC请求
  public String call(String message) throws IOException, InterruptedException {
    final String corrId = UUID.randomUUID().toString();

    AMQP.BasicProperties props = new AMQP.BasicProperties
            .Builder()
            .correlationId(corrId)
            .replyTo(replyQueueName)
            .build();

    //发布具有两个属性的请求消息：  replyTo和correlationId
    channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

    //由于消费者交付处理是在另一个线程中执行，因此我们需要在响应到达之前暂停主线程。BlockingQueue是可能的解决方案之一。这里我们创建的 容量设置为1的ArrayBlockingQueue，
    // 因为我们只需要等待一个响应。
    final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);

    //订阅'callback'队列，以便我们可以接收RPC响应
    channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
      @Override
      //该handleDelivery方法是做一个很简单的工作，对每一位消费响应消息它会检查的correlationID 是我们要找的人。如果是这样，它将响应BlockingQueue
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        if (properties.getCorrelationId().equals(corrId)) {
          response.offer(new String(body, "UTF-8"));
        }
      }
    });
    //从response中获取响应
    return response.take();
  }

  public void close() throws IOException {
    connection.close();
  }

  public static void main(String[] argv) {
    RPCClient fibonacciRpc = null;
    String response = null;
    try {
      fibonacciRpc = new RPCClient();

      System.out.println(" [x] Requesting fib(30)");
      response = fibonacciRpc.call("30");
      System.out.println(" [.] Got '" + response + "'");
    }
    catch  (IOException | TimeoutException | InterruptedException e) {
      e.printStackTrace();
    }
    finally {
      if (fibonacciRpc!= null) {
        try {
          fibonacciRpc.close();
        }
        catch (IOException _ignore) {}
      }
    }
  }
}