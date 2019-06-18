package tutorial3;

import java.util.Date;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class EmitLog {

	private static final String EXCHANGE_NAME = "logs";
	
	private static final String[] LOG_ARRAY = {
			"This is the Log entry #01",
			"This is the Log entry #02",
			"This is the Log entry #03",
			"This is the Log entry #04",
			"This is the Log entry #05",
			"This is the Log entry #06",
			"This is the Log entry #07",
			"This is the Log entry #08",
			"This is the Log entry #09",
			"This is the Log entry #10",
			"This is the Log entry #11",
			"This is the Log entry #12",
			"This is the Log entry #13",
			"This is the Log entry #14",
			"This is the Log entry #15",
			"This is the Log entry #16",
			"This is the Log entry #17",
			"This is the Log entry #18",
			"This is the Log entry #19",
			"This is the Log entry #20",
			"This is the Log entry #21",
			"This is the Log entry #22",
			"This is the Log entry #23",
			"This is the Log entry #24",
			"This is the Log entry #25",
			"This is the Log entry #26",
			"This is the Log entry #27",
			"This is the Log entry #28",
			"This is the Log entry #29",
			"This is the Log entry #30",
			"This is the Log entry #31",
			"This is the Log entry #32",
			"This is the Log entry #33",
			"This is the Log entry #34",
			"This is the Log entry #35",
			"This is the Log entry #36",
			"This is the Log entry #37",
			"This is the Log entry #38",
			"This is the Log entry #39",
			"This is the Log entry #40"
	};

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
			
			for (String logEntry : LOG_ARRAY) {
				String message = logEntry + "_" + new Date().toString();

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
						EXCHANGE_NAME, // exchange: the exchange to publish the message to
						"", // routingKey: the routing key ==> NO ROUTING KEY
						MessageProperties.TEXT_PLAIN, // Content-type "text/plain", deliveryMode 1 (nonpersistent), priority zero
						logEntry.getBytes("UTF-8")
				);
				
				System.out.println(" [x] Sent '" + message + "'");
				
                try {
                	System.out.println(" [x] ... doing something that takes about 1 second ... ");
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
				
			}

		}
	}

}
