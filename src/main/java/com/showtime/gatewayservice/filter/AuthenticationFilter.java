package com.showtime.gatewayservice.filter;

import com.showtime.coreapi.exception.CustomRuntimeException;
import com.showtime.gatewayservice.type.GatewayErrorCode;
import com.showtime.gatewayservice.util.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {


    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationFilter(JwtTokenProvider jwtTokenProvider){

        super(Config.class);
        this.jwtTokenProvider = jwtTokenProvider;
    }



    @Override
    public GatewayFilter apply(AuthenticationFilter.Config config) {
        return (exchange, chain) -> {


            List<String> defaultWhiteList = List.of("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**");
            List<String> totalList = new ArrayList<>(config.getWhiteList());
            totalList.addAll(defaultWhiteList);


            if (totalList != null && totalList.contains(exchange.getRequest().getURI().getPath().toString())) {
                return chain.filter(exchange);
            }

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
                String token = authorizationHeader.substring(7);


                try {


                    if (!jwtTokenProvider.validateToken(token)) {
                        throw new CustomRuntimeException(GatewayErrorCode.UNAUTHORIZED);
                    } else {

                        ServerHttpRequest extraReuqest = exchange.getRequest().mutate()
                                .header("user-email", jwtTokenProvider.getUserEmail(token))
                                .header("user-role", jwtTokenProvider.getUserId(token))
                                .build();


                        exchange = exchange.mutate().request(extraReuqest).build();

                    }
                }catch (Exception e){
                    if( e instanceof ExpiredJwtException){
                        throw new CustomRuntimeException(GatewayErrorCode.TOKEN_EXPIRED_EXCEPTION);
                    }else{
                        throw new CustomRuntimeException(GatewayErrorCode.INTERNAL_SERVER_ERROR);
                    }
                }

            } else {
                throw new CustomRuntimeException(GatewayErrorCode.UNAUTHORIZED);
            }

            return chain.filter(exchange);

        };
    }


    @Getter
    @Setter
    public static class Config {

        private List<String> whiteList;

    }

}
