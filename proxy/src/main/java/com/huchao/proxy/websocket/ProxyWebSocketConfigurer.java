package com.huchao.proxy.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * @program middleware
 * @ClassName ProxyWebSocketConfigurer
 * @description: websocket代理配置
 * @author: sky
 * @create: 2020/09/27 15:50
 */
@Configuration
@EnableWebSocket
public class ProxyWebSocketConfigurer implements WebSocketConfigurer {


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(proxyServerHandler(), "/**")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketProxyServerHandler proxyServerHandler(){
        return new WebSocketProxyServerHandler();
    }
}
