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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RabbitConfig {
    private static final String queueName = "spring-boot";

    private static final String topicExchangeName = "spring-boot-exchange";

    @Bean
    Queue queue() {
        // 여기서 서버별로 임시큐를 생성해주면 되지 않을까??
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
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
        container.setQueueNames(queueName);
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
