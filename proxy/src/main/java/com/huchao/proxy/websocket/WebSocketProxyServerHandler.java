package com.huchao.proxy.websocket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program middleware
 * @ClassName WebSocketProxyServerHandler
 * @description: websocket handler
 * @author: sky
 * @create: 2020/09/27 15:52
 */
public class WebSocketProxyServerHandler extends AbstractWebSocketHandler {
    private final Map<String, NextHop> nextHops = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        getNextHop(session);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        getNextHop(webSocketSession).sendMessageToNextHop(webSocketMessage);
    }

    private NextHop getNextHop(WebSocketSession webSocketSession) {
        NextHop nextHop = nextHops.get(webSocketSession.getId());
        if (nextHop == null) {
            nextHop = new NextHop(webSocketSession);
            nextHops.put(webSocketSession.getId(), nextHop);
        }
        return nextHop;
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        cleanSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        cleanSession(session);
    }

    private void cleanSession(WebSocketSession session) {
        nextHops.remove(session.getId());
    }
}
