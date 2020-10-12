package com.huchao.proxy.websocket;


import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @program middleware
 * @ClassName NextHop
 * @description: socket 代理
 * @author: sky
 * @create: 2020/09/27 15:55
 */
public class NextHop {

    private final WebSocketSession webSocketClientSession;

    public NextHop(WebSocketSession webSocketServerSession) {

        webSocketClientSession = createWebSocketClientSession(webSocketServerSession);
    }

    private WebSocketSession createWebSocketClientSession(WebSocketSession webSocketServerSession) {
        try {
            String proxyUri = getProxyUri(webSocketServerSession);
            return new StandardWebSocketClient()
                    .doHandshake(new WebSocketProxyClientHandler(webSocketServerSession), null, new URI(proxyUri))
                    .get(1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getProxyUri(WebSocketSession webSocketServerSession) {
        URI uri = webSocketServerSession.getUri();

        // 对 uri 做些处理 获取真正的 webSocket 地址
        return  uri.toString();
    }

    public void sendMessageToNextHop(WebSocketMessage<?> webSocketMessage) throws IOException {

        webSocketClientSession.sendMessage(webSocketMessage);
    }
}
