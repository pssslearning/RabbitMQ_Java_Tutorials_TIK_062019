package tutorial1;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Recv {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setVirtualHost("/");
		factory.setHost("localhost");
		factory.setPort(5672);
        
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            // Listing Message Properties
            BasicProperties messageProperties = delivery.getProperties();
            
            System.out.println("\n****************************************************");
            System.out.println(" [x] Received '" + message + "'");
            System.out.println("     Properties:");
            System.out.println("\t" +
            		ReflectionToStringBuilder.toString(
            				messageProperties, 
            				ToStringStyle.MULTI_LINE_STYLE, 
            				false, 
            				false));
        };
        channel.basicConsume(
        		QUEUE_NAME, // Queue Name 
        		true, // Auto Acknowledge --> TRUE
        		deliverCallback, // CallBack Function
        		consumerTag -> { }
        );
    }	
}
