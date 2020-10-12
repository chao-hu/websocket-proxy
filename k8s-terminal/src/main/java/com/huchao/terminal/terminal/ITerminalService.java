package com.huchao.terminal.terminal;

/**
 * @author huchao
 * @date  2020/10/12 15:55
 */
public interface ITerminalService {

    /**
     * 负责连接docker
     * @param terminalSession
     * @param namespace
     * @param pod
     * @param container
     */
    public void initShell(TerminalSession terminalSession, String namespace, String pod, String container);

}
