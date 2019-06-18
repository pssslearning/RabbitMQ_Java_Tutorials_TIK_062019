package tutorial6;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.rabbitmq.client.*;

public class RPCServer {
	private static final String RPC_QUEUE_NAME = "rpc_queue";

	private static int fib(int n) {
		if (n == 0)
			return 0;
		if (n == 1)
			return 1;
		return fib(n - 1) + fib(n - 2);
	}

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setVirtualHost("/");
		factory.setHost("localhost");
		factory.setPort(5672);

		try (Connection connection = factory.newConnection(); 
				Channel channel = connection.createChannel()) {
			
			channel.queueDeclare(
					RPC_QUEUE_NAME, // queue Nam
					false, // durable
					false, // exclusive
					false, // autoDelete
					null // other properties
			);
			channel.queuePurge(RPC_QUEUE_NAME);
			
			channel.basicQos(1); // Prefetch count

			System.out.println(" [x] Awaiting RPC requests");

			Object monitor = new Object();
			DeliverCallback deliverCallback = 
					(consumerTag, delivery) -> {
						AMQP.BasicProperties replyProps = 
								new AMQP.BasicProperties.Builder()
								.correlationId(delivery.getProperties().getCorrelationId())
								.build();
		
						String response = "";
						
						System.out.println(" [.] RPC request with corrid......[" + delivery.getProperties().getCorrelationId() + "]");
						System.out.println(" [.] RPC request with ReplyToQueue[" + delivery.getProperties().getReplyTo() + "]");
						System.out.println(" [.] RPC request with DeliveryTag [" + delivery.getEnvelope().getDeliveryTag() + "]");
						try {
							String message = new String(delivery.getBody(), "UTF-8");
							int n = Integer.parseInt(message);
		
							System.out.println(" [.] RPC request with DeliveryTag [" + delivery.getEnvelope().getDeliveryTag() + "]");
							System.out.println(" [.] RPC request  (fibonacci) --->[" + message + "]");
							response += fib(n);
							System.out.println(" [.] RPC response (fibonacci) <---[" + response + "]");
						} catch (RuntimeException e) {
							System.out.println(" [.] " + e.toString());
						} finally {
							channel.basicPublish(
									"", // exchange: the exchange to publish the message to --> NO EXCHANGE
									delivery.getProperties().getReplyTo(), // routingKey: the routing key ==> The ReplyToQueue Name
									replyProps, // props: other properties for the message - routing headers etc
									response.getBytes("UTF-8") // body: the message body
							);


							System.out.println(" [.] replyProps ..................:");
					        System.out.println("\t" +
					        		ReflectionToStringBuilder.toString(
					        				replyProps, 
					        				ToStringStyle.MULTI_LINE_STYLE, 
					        				false, 
					        				false));
							
					        
					        /*
					         * Acknowledge one or several received messages. 
					         * 		Supply the deliveryTag from the com.rabbitmq.client.AMQP.Basic.GetOk 
					         * 		or com.rabbitmq.client.AMQP.Basic.Deliver method 
					         * 		containing the received message being acknowledged.
					         * Parameters:
					         * 		deliveryTag: the tag from the received com.rabbitmq.client.AMQP.Basic.GetOk 
					         *                   or com.rabbitmq.client.AMQP.Basic.Deliver
					         * 		multiple: true to acknowledge all messages up to and including the supplied delivery tag; 
					         *                false to acknowledge just the supplied delivery tag.
					         * Throws:
					         * 		java.io.IOException - if an error is encountered
					         */
					        
							channel.basicAck(
									delivery.getEnvelope().getDeliveryTag(), // deliveryTag (long)
									false
							);
							// RabbitMq consumer worker thread notifies the RPC server owner thread
							synchronized (monitor) {
								monitor.notify();
							}
						}
					};

					channel.basicConsume(
							RPC_QUEUE_NAME, // Queue Name
							false, // AutoAcknowledgement --> FALSE
							deliverCallback, // Consumer Cancel CallBack
							(consumerTag -> {}) // Consumer Cancel CallBack
					);
					
				
					
			// Wait and be prepared to consume the message from RPC client.
			while (true) {
				synchronized (monitor) {
					try {
						monitor.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
