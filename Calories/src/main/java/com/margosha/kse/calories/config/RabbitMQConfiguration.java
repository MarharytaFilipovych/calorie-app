package com.margosha.kse.calories.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
    public DirectExchange deadLetterExchange(){
        return new DirectExchange(settings.getExchangeName() + ".dlx", true, false);
    }

    @Bean
    public Queue recordEventsQueue(){
        return QueueBuilder.durable(settings.getQueueName())
                .withArgument("x-dead-letter-exchange", settings.getExchangeName() + ".dlx")
                .withArgument("x-dead-letter-routing-key", settings.getRoutingKey() + ".dlq")
                .build();
    }

    @Bean
    public Queue deadLetterQueue(){
        return new Queue(settings.getQueueName() + ".dlq", true);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange){
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(settings.getRoutingKey()+".dlq");
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
