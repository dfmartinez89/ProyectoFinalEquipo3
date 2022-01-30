package ProyectoFinalEquipo3;

import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class Recv implements Callable {

	private static final String TASK_QUEUE_NAME = "task_queue";

	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		final Connection connection = factory.newConnection();
		final Channel channel = connection.createChannel();

		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

		channel.basicQos(1); // CONSUMIR 1 MENSAJE

		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			channel.basicCancel(consumerTag);
			if (delivery.getEnvelope().getDeliveryTag() == 1) {
				String message = new String(delivery.getBody(), "UTF-8");
				System.out.println(" [x] Received '" + message + "'");
				try {
					doWork(message);
				} finally {
					System.out.println(" [x] Done");
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

				}
			}
		};

		channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {

		});

		return null;
	}

	private static void doWork(String task) {
		for (char ch : task.toCharArray()) {
			if (ch == '.') {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException _ignored) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}
