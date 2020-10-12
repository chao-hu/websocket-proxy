package com.huchao.terminal.terminal;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.*;

/**
 * @author: Huchao
 * @Date: 2020/9/14
 **/

@ServerEndpoint("/ws/{namespace}/{pod}/{container}")
@Component
@Slf4j
public class WebSocketServer {

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的WebSocketServer对象。
     */
    private static ConcurrentHashMap<String, TerminalSession> sessionPools = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, String> pods = new ConcurrentHashMap<>();

    static ITerminalService terminalServiceImpl;

    @Autowired
    public void setITerminalService(ITerminalService terminal) {
        terminalServiceImpl = terminal;
    }

    /**
     * 建立连接成功调用
     *
     * @param session
     * @param namespace
     * @param pod
     * @param container
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "namespace") String namespace, @PathParam(value = "pod") String pod, @PathParam(value = "container") String container) {
        log.info("有新窗口开始监听: session ID: " + session.getId());
        TerminalSession terminalSession = new TerminalSession();
        terminalSession.setSid(session.getId());
        terminalSession.setSession(session);
        terminalSession.setOpen(true);

        sessionPools.put(pod, terminalSession);
        pods.put(session.getId(), pod);

        log.info("连接成功！" + session.getId());

        //ExecutorService singleThread = Executors.newFixedThreadPool(1);
        ExecutorService singleThread = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1));
        singleThread.execute(() -> {
            terminalServiceImpl.initShell(terminalSession, namespace, pod, container);
        });

    }

    /**
     * 连接关闭时执行
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        log.info("连接关闭！" + session.getId());

        cleanSession(session);
    }

    /**
     * 连接出错时执行
     *
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {

        log.error("发生错误了 " + session.getId() + " " + throwable.getMessage());
        throwable.printStackTrace();
        cleanSession(session);
    }

    /**
     * 收到客户端消息时执行
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        if (log.isDebugEnabled()) {
            log.info("receive message:" + message);
        }

        try {
            String podname = pods.get(session.getId());
            OutputStream processout = sessionPools.get(podname).getWatch().getInput();
            processout.write(message.getBytes());
            processout.flush();
        } catch (IOException e) {
            log.error("send msg to tty error " + session.getId() + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cleanSession(Session session) {
        String pod = pods.get(session.getId());
        if (null != pod) {
            try {
                sessionPools.get(pod).getWatch().close();

                sessionPools.remove(pod);
                pods.remove(session.getId());
            } catch (Exception e) {
                log.error("remove session error " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

}
