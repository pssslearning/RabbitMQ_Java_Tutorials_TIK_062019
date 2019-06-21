package addenda1;

import java.util.Date;
import java.util.HashMap;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/*
 * Addenda. Send message with properties to a durable queue
 * (to test NODEJS STOMP Subscribe)
 */
public class SendWithPropsDurableQueue {
	private final static String QUEUE_NAME = "testQueue";

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
					QUEUE_NAME, // queue Name 
					true, // durable
					false, // exclusive
					false, // autoDelete
					null);
			String message = "Hello Addenda 1! (with message properties) - " + new Date().toString();
			
			
			HashMap<String, Object> messageHeaders = new HashMap<String, Object>();
			messageHeaders.put("x-customStringHdr1", "Techedge");
			messageHeaders.put("x-customStringHdr2", "TIK de Junio");
			messageHeaders.put("x-customNumberHdr3", 2019);
			messageHeaders.put("x-customBooleanHdr4", true);
			
			BasicProperties messageProperties = new BasicProperties.Builder()
					.type("A message type info")
					.correlationId(java.util.UUID.randomUUID().toString())
					.appId("Tutorial1")
					.contentEncoding("utf-8")
					.contentType("text/plain")
					.timestamp(new Date())
					.messageId(java.util.UUID.randomUUID().toString())
					.deliveryMode(new Integer(2))
					.priority(new Integer(9))
					.headers(messageHeaders)
					.build();
			
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
					messageProperties, // props
					message.getBytes("UTF-8") // body
					);
			
			System.out.println(" [x] Sent '" + message + "'");
		}
	}
}
