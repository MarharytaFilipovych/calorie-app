package com.margosha.kse.CaloriesConsumer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMqConfig {

    private final RabbitSettings settings;

    public RabbitMqConfig(RabbitSettings settings) {
        this.settings = settings;
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory factory(ConnectionFactory connectionFactory, MessageConverter converter){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setDefaultRequeueRejected(false);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setErrorHandler(new SilentRejectErrorHandler());
        return factory;
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
}