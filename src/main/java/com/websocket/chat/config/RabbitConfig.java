package com.websocket.chat.config;

import com.websocket.chat.pubsub.RabbitSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.UUID;

@RequiredArgsConstructor
@Configuration
public class RabbitConfig {

    @Value("${spring.rabbitmq.exchange-name}")
    private String EXCHANGE_NAME;

    @Value("${spring.rabbitmq.queue-prefix}")
    private String QUEUE_PREFIX;

    private static String QUEUE_NAME;

    @PostConstruct
    private void tempQueueName() {
        QUEUE_NAME = this.QUEUE_PREFIX + UUID.randomUUID().toString();
        ;
    }

    @Bean
    Queue queue() {
        // 여기서 서버별로 임시큐를 생성해주면 되지 않을까??
        return new Queue(QUEUE_NAME, false, false, true);
//        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
//        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        // 바인딩 해줄 때 #,* 등을 사용해서 라우팅키를 만들어주고
        // 실제 보낼 때는 완전한 #,*이 없는 완전한 routingKey를 사용해서 보내주면 된다.
        return BindingBuilder.bind(queue).to(exchange).with("chat.server.#");
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                  MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    @Bean
    MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    //Message Listener Container
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter rabbitListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(QUEUE_NAME);
        container.setMessageListener(rabbitListenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter rabbitListenerAdapter(RabbitSubscriber subscriber) {
//        return new MessageListenerAdapter(subscriber, "receiveMessage");

        MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(subscriber);
        messageListenerAdapter.setDefaultListenerMethod("receiveMessage"); // 실행할 메소드 지정.
        messageListenerAdapter.setMessageConverter(messageConverter()); // Json형태로 받기 위해 MessageConverter 설정.
        return messageListenerAdapter;


    }
}
