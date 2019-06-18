package tutorial4;

import com.rabbitmq.client.*;

public class ReceiveLogsDirectALL {

	private static final String EXCHANGE_NAME = "direct_logs";
	private static final String QUEUE_NAME = "AllSeverityLogs";

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
				EXCHANGE_NAME, // the name of the exchange
				BuiltinExchangeType.DIRECT, // the exchange type
				true // durable
		);
		
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
				QUEUE_NAME, // queue Name 
				true, // durable
				false, // exclusive
				false, // autoDelete
				null // other properties
		);
		
		/*
		 * Bind a queue to an exchange, with no extra arguments.
		 * Parameters:
		 * 		queue: the name of the queue
		 * 		exchange: the name of the exchange
		 * 		routingKey: the routing key to use for the binding
		 * Returns:
		 * 		a binding-confirm method if the binding was successfully created
		 * Throws:
		 * 		java.io.IOException - if an error is encountered
		 */
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "info");
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "warning");
		channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "error");
		
		System.out.println(" [*] Waiting for messages of ALL SEVERITY CODES. To exit press CTRL+C");
		
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received. Routing Key[" + delivery.getEnvelope().getRoutingKey() + "] Message[" + message + "]");
        };

        channel.basicConsume(
        		QUEUE_NAME, // Queue Name
        		true, // AutoAcknowledgement --> TRUE
        		deliverCallback, // Callback function
        		consumerTag -> {} // Consumer Cancel CallBack
        ); 
	}

}