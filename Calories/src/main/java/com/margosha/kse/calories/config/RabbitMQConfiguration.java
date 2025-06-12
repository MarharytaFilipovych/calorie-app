package com.margosha.kse.calories.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    private final RabbitSettings settings;

    public RabbitMQConfiguration(RabbitSettings settings) {
        this.settings = settings;
    }

    @Bean
    public DirectExchange exchange(){
        return new DirectExchange(settings.getExchangeName(), true, false);
    }

    @Bean
    public Queue recordEventsQueue(){
        return new Queue(settings.getQueueName(), true);
    }

    @Bean
    public Binding recordEventsBinding(Queue recordEventsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(recordEventsQueue).to(exchange).with(settings.getRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory, MessageConverter converter){
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(converter);
        template.setExchange(settings.getExchangeName());
        template.setRoutingKey(settings.getRoutingKey());
        return template;
    }
}
