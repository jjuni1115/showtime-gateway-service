package com.showtime.gatewayservice.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config>{

    public LoggingFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();


            if(config.preLogger){
                log.info("Request uri -> ",request.getURI().getPath().toString());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                if(config.postLogger){
                    log.info("response code -> ",response.getStatusCode().toString());
                }
            }));

        };
    }

    @Getter
    @Setter
    public static class Config{



        private boolean preLogger;
        private boolean postLogger;

    }

}
