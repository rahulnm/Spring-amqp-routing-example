
package hello;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    final static String queueNameOne = "spring-boot-routing-one";
    final static String queueNameTwo = "spring-boot-routing-two";
    final static String EXCHANGE_NAME = "spring-boot-exchange";
    final static String LISTENER_ROUTING_KEY = "spring.boot.trial.routing.*";
    final static String LISTENER_ROUTING_KEY2 = "spring.boot.trial.routing2.*";

//    RabbitMQ: producer talks to exchange and listeners/consumers create queue and bind to exchnage using routing key.

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    @Bean
    public RabbitAdmin admin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    Queue queueOne() {
        return new Queue(queueNameOne, false);
    }

    @Bean
    Queue queueTwo() {
        return new Queue(queueNameTwo, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding binding1(Queue queueOne, TopicExchange exchange) {
        return BindingBuilder.bind(queueOne).to(exchange).with(LISTENER_ROUTING_KEY);
    }

    @Bean
    Binding binding2(Queue queueTwo, TopicExchange exchange) {
        return BindingBuilder.bind(queueTwo).to(exchange).with(LISTENER_ROUTING_KEY2);
    }


    SimpleMessageListenerContainer simpleContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, Queue queueOne) {
        SimpleMessageListenerContainer container = this.simpleContainer(connectionFactory);
        container.setQueues(queueOne);
        container.setMessageListener(new MessageListenerAdapter(new Receiver(), "receiveMessage"));
        return container;
    }

    @Bean
    SimpleMessageListenerContainer container2(ConnectionFactory connectionFactory,
                                              Queue queueTwo) {
        SimpleMessageListenerContainer container = this.simpleContainer(connectionFactory);
        container.setQueues(queueTwo);
        container.setMessageListener(new MessageListenerAdapter(new ReceiverFor2(), "receiveMessageFor2"));
        return container;
    }

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Application.class, args);
    }

}
