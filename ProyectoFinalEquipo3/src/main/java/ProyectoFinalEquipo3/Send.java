package ProyectoFinalEquipo3;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class Send implements Callable {

	private static final String TASK_QUEUE_NAME = "ProyectoFinal";

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		String message = eventContext.getMessage().getPayloadAsString();
		publishMessage(message);
		return null;
	}
	
	public void publishMessage(String message) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

			channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,
					message.getBytes("UTF-8"));
			System.out.println(" [x] Sent '" + message + "'");
		}
	}
}