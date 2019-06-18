package tutorial2;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class NewTask {
	
	private static final String TASK_QUEUE_NAME = "task_queue";

	
	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setVirtualHost("/");
		factory.setHost("localhost");
		factory.setPort(5672);
		
		try (Connection connection = factory.newConnection(); 
			 Channel channel = connection.createChannel()) {
			
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
					null // Other properties
					);

			String message = String.join(" ", argv);

			/*
			 * Publish a message. 
			 * Publishing to a non-existent exchange will result in 
			 * a channel-level protocol exception, which closes the channel. 
			 * Invocations of Channel#basicPublish will eventually block 
			 * if a resource-driven alarm is in effect.
			 * Parameters:
			 *   exchange: the exchange to publish the message to
			 *   routingKey: the routing key
			 *   props: other properties for the message - routing headers etc
			 *   body: the message body
			 */
			channel.basicPublish(
					"", // Exchange
					TASK_QUEUE_NAME, // Queue Name as Routing-Key 
					MessageProperties.PERSISTENT_TEXT_PLAIN, // Content-type "text/plain", deliveryMode 2 (persistent), priority zero
					message.getBytes("UTF-8") // The message body
					);
			
			System.out.println(" [x] Sent '" + message + "'");
		}
	}
}
