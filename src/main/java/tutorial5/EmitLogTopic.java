package tutorial5;

import java.util.Date;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;


public class EmitLogTopic {

	private static final String EXCHANGE_NAME = "topic_logs";
	
	private static final String[] LOG_MSG_ARRAY = {
			"This is the Log entry #01 of type ",
			"This is the Log entry #02 of type ",
			"This is the Log entry #03 of type ",
			"This is the Log entry #04 of type ",
			"This is the Log entry #05 of type ",
			"This is the Log entry #06 of type ",
			"This is the Log entry #07 of type ",
			"This is the Log entry #08 of type ",
			"This is the Log entry #09 of type ",
			"This is the Log entry #10 of type ",
			"This is the Log entry #11 of type ",
			"This is the Log entry #12 of type ",
			"This is the Log entry #13 of type ",
			"This is the Log entry #14 of type ",
			"This is the Log entry #15 of type ",
			"This is the Log entry #16 of type ",
			"This is the Log entry #17 of type ",
			"This is the Log entry #18 of type ",
			"This is the Log entry #19 of type ",
			"This is the Log entry #20 of type ",
			"This is the Log entry #21 of type ",
			"This is the Log entry #22 of type ",
			"This is the Log entry #23 of type ",
			"This is the Log entry #24 of type ",
			"This is the Log entry #25 of type ",
			"This is the Log entry #26 of type ",
			"This is the Log entry #27 of type ",
			"This is the Log entry #28 of type ",
			"This is the Log entry #29 of type ",
			"This is the Log entry #30 of type ",
			"This is the Log entry #31 of type ",
			"This is the Log entry #32 of type ",
			"This is the Log entry #33 of type ",
			"This is the Log entry #34 of type ",
			"This is the Log entry #35 of type ",
			"This is the Log entry #36 of type ",
			"This is the Log entry #37 of type ",
			"This is the Log entry #38 of type ",
			"This is the Log entry #39 of type ",
			"This is the Log entry #40 of type ",
			"This is the Log entry #41 of type ",
			"This is the Log entry #42 of type ",
			"This is the Log entry #43 of type ",
			"This is the Log entry #44 of type ",
			"This is the Log entry #45 of type ",
			"This is the Log entry #46 of type ",
			"This is the Log entry #47 of type ",
			"This is the Log entry #48 of type ",
			"This is the Log entry #49 of type ",
			"This is the Log entry #50 of type ",
			"This is the Log entry #51 of type ",
			"This is the Log entry #52 of type ",
			"This is the Log entry #53 of type ",
			"This is the Log entry #54 of type ",
			"This is the Log entry #55 of type ",
			"This is the Log entry #56 of type ",
			"This is the Log entry #57 of type ",
			"This is the Log entry #58 of type ",
			"This is the Log entry #59 of type ",
			"This is the Log entry #60 of type ",
			"This is the Log entry #61 of type ",
			"This is the Log entry #62 of type ",
			"This is the Log entry #63 of type ",
			"This is the Log entry #64 of type ",
			"This is the Log entry #65 of type ",
			"This is the Log entry #66 of type ",
			"This is the Log entry #67 of type ",
			"This is the Log entry #68 of type ",
			"This is the Log entry #69 of type ",
			"This is the Log entry #70 of type ",
			"This is the Log entry #71 of type ",
			"This is the Log entry #72 of type ",
			"This is the Log entry #73 of type ",
			"This is the Log entry #74 of type ",
			"This is the Log entry #75 of type ",
			"This is the Log entry #76 of type ",
			"This is the Log entry #77 of type ",
			"This is the Log entry #78 of type ",
			"This is the Log entry #79 of type ",
			"This is the Log entry #80 of type "
	};

	private static final String[] LOG_TOPIC_ARRAY = {
			"dbus.severe",
			"systd.info",
			"systd.info",
			"kern.info",
			"kern.warning",
			"dbus.severe",
			"other.info",
			"systd.warning",
			"other.warning",
			"systd.info",
			"dbus.warning",
			"systd.critical",
			"other.warning",
			"kern.warning",
			"kern.critical",
			"other.info",
			"kern.critical",
			"other.warning",
			"other.severe",
			"dbus.severe",
			"other.severe",
			"other.severe",
			"dbus.severe",
			"other.warning",
			"dbus.info",
			"systd.warning",
			"systd.critical",
			"systd.critical",
			"kern.warning",
			"dbus.info",
			"other.info",
			"systd.severe",
			"other.info",
			"kern.severe",
			"dbus.warning",
			"dbus.warning",
			"systd.critical",
			"dbus.info",
			"dbus.info",
			"dbus.critical",
			"dbus.info",
			"systd.info",
			"dbus.info",
			"other.severe",
			"dbus.severe",
			"other.severe",
			"other.info",
			"systd.critical",
			"systd.info",
			"dbus.critical",
			"systd.severe",
			"systd.info",
			"other.critical",
			"dbus.info",
			"systd.critical",
			"systd.warning",
			"kern.severe",
			"dbus.info",
			"dbus.info",
			"other.critical",
			"systd.critical",
			"dbus.info",
			"dbus.critical",
			"dbus.warning",
			"systd.warning",
			"kern.info",
			"dbus.warning",
			"other.critical",
			"kern.warning",
			"systd.severe",
			"dbus.info",
			"dbus.severe",
			"kern.severe",
			"systd.critical",
			"kern.critical",
			"other.severe",
			"dbus.severe",
			"systd.info",
			"kern.warning",
			"kern.critical"
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
					BuiltinExchangeType.TOPIC, // Exchange type: "topic"
					true // Durable: True
			);
			
			
			for (int i=0; i < LOG_MSG_ARRAY.length; i++) {
				
	            String routingKey = LOG_TOPIC_ARRAY[i];
	            String message = LOG_MSG_ARRAY[i] + "[" + routingKey + "].";
				
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
						routingKey, // routingKey: the routing key ==> The Severity as routing key
						MessageProperties.PERSISTENT_TEXT_PLAIN, // Content-type "text/plain", deliveryMode 2 (persistent), priority zero
						message.getBytes("UTF-8")
				);
				
				System.out.println(" [x] Sent [" + routingKey + "]:[" + message + "]");
				
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
