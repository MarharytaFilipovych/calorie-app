package com.margosha.kse.calorie_client.config;

import com.margosha.kse.calorie_client.dto.ErrorResponse;
import com.margosha.kse.calorie_client.exceptions.BadRequest;
import com.margosha.kse.calorie_client.exceptions.InternalServerError;
import com.margosha.kse.calorie_client.exceptions.NotFound;
import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;
import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

@Configuration
public class WebConfig {
    private  final WebClientSettings settings;

    public WebConfig(WebClientSettings settings) {
        this.settings = settings;
    }

    @Bean
    public WebClient webClient(Retry retry){
        HttpClient client = HttpClient.create()
                .responseTimeout(settings.getTimeout())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        (int) settings.getConnectionTimeout().toMillis());
        return WebClient.builder()
                .baseUrl(settings.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(client))
                .filter(errorHandlingFilter())
                .filter(((request, next) -> next.exchange(request).retryWhen(retry)))
                .build();
    }

    @Bean
    public Retry retry(){
        return Retry.backoff(settings.getMaxRetries(), settings.getRetryDelay())
                .maxBackoff(settings.getMaxRetryDelay())
                .multiplier(settings.getRetryMultiplier())
                .filter(this::isRetryableException);
    }

    private boolean isRetryableException(Throwable throwable){
        return throwable instanceof WebClientResponseException.InternalServerError ||
                throwable instanceof TimeoutException ||
                throwable instanceof ConnectException;
    }

    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse.bodyToMono(ErrorResponse.class)
                        .flatMap(errorResponse -> {
                            String message = errorResponse.message();
                            RuntimeException exception = switch (clientResponse.statusCode().value()) {
                                case 400 -> new BadRequest(message);
                                case 404 -> new NotFound(message);
                                default -> new InternalServerError(message);
                            };
                            return Mono.error(exception);
                        });
            }
            return Mono.just(clientResponse);
        });
    }
}
