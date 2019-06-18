package tutorial3;


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.AMQP.BasicProperties;

public class ReceiveLogs {
	
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
    	
        ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setVirtualHost("/");
		factory.setHost("localhost");
		factory.setPort(5672);
        
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

		/*
		 * Actively declare a non-autodelete, non-durable exchange with no extra arguments
		 * Parameters:
		 *    exchange: the name of the exchange
		 *    type: the exchange type
		 *    durable: true if we are declaring a durable exchange (the exchange will survive a server restart)
		 * Returns:
		 *    a declaration-confirm method to indicate the exchange was successfully declared
		 */

        channel.exchangeDeclare(
        		EXCHANGE_NAME, // the Exchange Name
        		BuiltinExchangeType.FANOUT, // Exchange type: "fanout"
        		true // Durable: True
        );
        
        String queueName = channel.queueDeclare().getQueue();
        
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        System.out.println(" [*] Using Queue name[" + queueName + "]");

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
            				ToStringStyle.JSON_STYLE, 
            				false, 
            				false));
        };
        
        channel.basicConsume(
        		queueName, // Queue Name
        		true, // AutoAcknowledgement --> TRUE
        		deliverCallback, // Callback function
        		consumerTag -> { } // Consumer Cancel CallBack
        ); 
        
    }
	

}
