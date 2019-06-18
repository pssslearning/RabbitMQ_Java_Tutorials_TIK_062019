package tutorial2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Worker {
    private static final String TASK_QUEUE_NAME = "task_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setVirtualHost("/");
		factory.setHost("localhost");
		factory.setPort(5672);
       
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        
		/*
		 * Declare a queue Parameters: 
		 * queue: the name of the queue 
		 * durable true if we are declaring a durable queue 
		 *         (the queue will survive a server restart)
		 * exclusive: true if we are declaring an exclusive queue (restricted to this
		 *        connection) 
		 * autoDelete: true if we are declaring an autodelete queue (server
		 *        will delete it when no longer in use) 
		 * arguments: other properties
		 *        (construction arguments) for the queue
		 */
        channel.queueDeclare(
        		TASK_QUEUE_NAME, // Queue Name 
        		true, // durable
        		false, // exclusive
        		false, // autoDelete
        		null // Other Properties
        );
        
        
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // Prefetch Count: 1
        channel.basicQos(1);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            System.out.println("\n-------------------------------------------------------");
            System.out.println(" [x] Received '" + message + "'");
            
            try {
                doWork(message);
            } finally {
                System.out.println(" [x] Task Done");
                System.out.println(" [x] Doing Basic Ack with DeliveryTag[" + delivery.getEnvelope().getDeliveryTag() + "]");
                channel.basicAck(
                		delivery.getEnvelope().getDeliveryTag(), // long Delivery Tag 
                		false // Boolean Multiple
                		);
                System.out.println(" [x] Basic Acknowledge Done\n");
            }
        };
        
        channel.basicConsume(
        		TASK_QUEUE_NAME, // Queue Name
        		false, // AutoAcknowledgement --> FALSE
        		deliverCallback, // Calback function
        		consumerTag -> { } // Consumer Cancel CallBack
        );
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                	System.out.println(" [x] doing something that takes about 1 second ... ");
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
}
}
