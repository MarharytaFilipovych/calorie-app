package com.margosha.kse.CaloriesConsumer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.util.ErrorHandler;

@Slf4j
public class SilentRejectErrorHandler implements ErrorHandler {
    @Override
    public void handleError(Throwable t) {
        if (t instanceof ListenerExecutionFailedException) {
            Throwable cause = t.getCause();
            if (cause instanceof AmqpRejectAndDontRequeueException) {
                return; // Silent ignore
            }
        }
        log.error("Unexpected error in message listener", t);
    }
}
