package tutorial4;

import java.util.Date;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;


public class EmitLogDirect {

	private static final String EXCHANGE_NAME = "direct_logs";
	
	private static final String[] LOG_ARRAY = {
			"This is the Log entry #01 of type warning",
			"This is the Log entry #02 of type warning",
			"This is the Log entry #03 of type warning",
			"This is the Log entry #04 of type error",
			"This is the Log entry #05 of type warning",
			"This is the Log entry #06 of type warning",
			"This is the Log entry #07 of type error",
			"This is the Log entry #08 of type warning",
			"This is the Log entry #09 of type error",
			"This is the Log entry #10 of type error",
			"This is the Log entry #11 of type warning",
			"This is the Log entry #12 of type warning",
			"This is the Log entry #13 of type info",
			"This is the Log entry #14 of type warning",
			"This is the Log entry #15 of type warning",
			"This is the Log entry #16 of type warning",
			"This is the Log entry #17 of type warning",
			"This is the Log entry #18 of type warning",
			"This is the Log entry #19 of type info",
			"This is the Log entry #20 of type info",
			"This is the Log entry #21 of type error",
			"This is the Log entry #22 of type info",
			"This is the Log entry #23 of type error",
			"This is the Log entry #24 of type warning",
			"This is the Log entry #25 of type info",
			"This is the Log entry #26 of type error",
			"This is the Log entry #27 of type info",
			"This is the Log entry #28 of type error",
			"This is the Log entry #29 of type warning",
			"This is the Log entry #30 of type info",
			"This is the Log entry #31 of type info",
			"This is the Log entry #32 of type error",
			"This is the Log entry #33 of type info",
			"This is the Log entry #34 of type info",
			"This is the Log entry #35 of type warning",
			"This is the Log entry #36 of type warning",
			"This is the Log entry #37 of type warning",
			"This is the Log entry #38 of type info",
			"This is the Log entry #39 of type error",
			"This is the Log entry #40 of type error",
			"This is the Log entry #41 of type info",
			"This is the Log entry #42 of type error",
			"This is the Log entry #43 of type error",
			"This is the Log entry #44 of type warning",
			"This is the Log entry #45 of type info",
			"This is the Log entry #46 of type warning",
			"This is the Log entry #47 of type info",
			"This is the Log entry #48 of type warning",
			"This is the Log entry #49 of type warning",
			"This is the Log entry #50 of type info",
			"This is the Log entry #51 of type warning",
			"This is the Log entry #52 of type warning",
			"This is the Log entry #53 of type error",
			"This is the Log entry #54 of type warning",
			"This is the Log entry #55 of type error",
			"This is the Log entry #56 of type warning",
			"This is the Log entry #57 of type error",
			"This is the Log entry #58 of type info",
			"This is the Log entry #59 of type error",
			"This is the Log entry #60 of type warning"
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
					BuiltinExchangeType.DIRECT, // Exchange type: "direct"
					true // Durable: True
			);
			
			
			for (String logEntry : LOG_ARRAY) {
				
				String severity = "info";
				if (logEntry.endsWith("info")) {
					severity = "info";
				} else	if (logEntry.endsWith("warning")) {
					severity = "warning";
				} else {
					severity = "error";
				}

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
						severity, // routingKey: the routing key ==> The Severity as routing key
						MessageProperties.PERSISTENT_TEXT_PLAIN, // Content-type "text/plain", deliveryMode 2 (persistent), priority zero
						logEntry.getBytes("UTF-8")
				);
				
				System.out.println(" [x] Sent [" + severity + "]:[" + logEntry + "]");
				
                try {
                	System.out.println(" [x] ... doing something that takes about 2 seconds ... ");
                    Thread.sleep(2000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
				
			}

		}
	}

}
