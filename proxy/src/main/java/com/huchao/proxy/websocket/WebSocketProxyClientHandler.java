package com.huchao.proxy.websocket;

import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * @program middleware
 * @ClassName WebSocketProxyClientHandler
 * @description:
 * @author: sky
 * @create: 2020/09/27 15:58
 */
public class WebSocketProxyClientHandler extends AbstractWebSocketHandler {

    private final WebSocketSession webSocketServerSession;

    public WebSocketProxyClientHandler(WebSocketSession webSocketServerSession) {
        this.webSocketServerSession = webSocketServerSession;
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
        webSocketServerSession.sendMessage(webSocketMessage);
    }
}
