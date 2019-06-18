package tutorial6;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RPCClient implements AutoCloseable {
	private Connection connection;
	private Channel channel;
	private String requestQueueName = "rpc_queue";

	public RPCClient() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setVirtualHost("/");
		factory.setHost("localhost");
		factory.setPort(5672);

		connection = factory.newConnection();
		channel = connection.createChannel();
	}

	public static void main(String[] argv) {
		
		boolean activePause = false;
		
		if (argv.length >= 1) {
			if (argv[0].equalsIgnoreCase("TRUE")) {
				activePause = true;
			}
		}
		
		try (RPCClient fibonacciRpc = new RPCClient()) {
			for (int i = 0; i < 32; i++) {
				String i_str = Integer.toString(i);
				System.out.println(" [x] Requesting fib(" + i_str + ")");
				String response = fibonacciRpc.call(i_str, activePause);
				System.out.println(" [.] Got '" + response + "'");
			}
		} catch (IOException | TimeoutException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String call(String message, boolean activePause) throws IOException, InterruptedException {
		final String corrId = UUID.randomUUID().toString();

		String replyQueueName = channel.queueDeclare().getQueue();
		
		System.out.println(" [x] In 'Call Function' ");
		System.out.println("     Message .......: " + message);
		System.out.println("     corrid ........: " + corrId);
		System.out.println("     replyQueueName : " + replyQueueName);
		
		AMQP.BasicProperties props = 
				new AMQP.BasicProperties.Builder()
				.correlationId(corrId)
				.replyTo(replyQueueName)
				.build();
		
		System.out.println("     BasicProperties: ");
        System.out.println("\t" +
        		ReflectionToStringBuilder.toString(
        				props, 
        				ToStringStyle.MULTI_LINE_STYLE, 
        				false, 
        				false));

		/*
		 * Publish a message. Publishing to a non-existent exchange will result in a channel-level protocol exception, which closes the channel. Invocations of Channel#basicPublish will eventually block if a resource-driven alarm is in effect.
		 * Parameters:
		 * 		exchange: the exchange to publish the message to
		 * 		routingKey: the routing key
		 * 		props: other properties for the message - routing headers etc
		 * 		body: the message body
		 * Throws:
		 * 		java.io.IOException - if an error is encountered
		 */
		channel.basicPublish(
				"", // exchange: the exchange to publish the message to --> NO EXCHANGE
				requestQueueName, // routingKey: the routing key ==> The Queue Name
				props, // props: other properties for the message - routing headers etc
				message.getBytes("UTF-8") // body: the message body
		);

		if (activePause) {
	        try {
	        	System.out.println("     ... doing something that takes about 3 seconds ... ");
	            Thread.sleep(3000);
	        } catch (InterruptedException _ignored) {
	            Thread.currentThread().interrupt();
	        }
		}
        
		final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

		String ctag = 
				channel.basicConsume(
						replyQueueName, // Queue Name
						true, // AutoAcknowledgement --> TRUE
						(consumerTag, delivery) -> {
							if (delivery.getProperties().getCorrelationId().equals(corrId)) {
								response.offer(new String(delivery.getBody(), "UTF-8"));
							}
						}, // Callback function
						consumerTag -> {} // Consumer Cancel CallBack
		);
		
		String result = response.take();
		System.out.println("     response ......: " + result);	
		
		channel.basicCancel(ctag);
		return result;
	}

	public void close() throws IOException {
		connection.close();
	}
}
