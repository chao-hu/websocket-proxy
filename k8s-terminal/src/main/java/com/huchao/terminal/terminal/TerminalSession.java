package com.huchao.terminal.terminal;

import io.fabric8.kubernetes.client.dsl.ExecWatch;

import javax.websocket.Session;

/**
 * @author: Huchao
 * @Date: 2020/10/12
 **/
public class TerminalSession {

    private Session session;
    private String sid;
    private volatile boolean open;

    private ExecWatch watch;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public ExecWatch getWatch() {
        return watch;
    }

    public void setWatch(ExecWatch watch) {
        this.watch = watch;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
}
