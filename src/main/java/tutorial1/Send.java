package tutorial1;

import java.util.Date;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {
	private final static String QUEUE_NAME = "hello";

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
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
					QUEUE_NAME, // queue Name 
					false, // durable
					false, // exclusive
					false, // autoDelete
					null);
			String message = "Hello Tutorial1! -" + new Date().toString();
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
					QUEUE_NAME, // Queue Name as Routing-Key
					null, // props
					message.getBytes("UTF-8") // body
					);
			
			System.out.println(" [x] Sent '" + message + "'");
		}
	}
}
