package com.huchao.terminal.terminal;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @author: Huchao
 * @Date: 2020/10/12
 **/
@Service
public class TerminalServiceImpl implements ITerminalService {

    public static final Logger logger = LoggerFactory.getLogger("TerminalServiceImpl");

    /**
     * 初始化K8s container终端
     * @param terminalSession Session实例
     * @param namespace 命名空间
     * @param pod Pod名称
     * @param container 容器名称
     */
    @Override
    public void initShell(TerminalSession terminalSession, String namespace, String pod, String container) {
        String columns = "230";
        String lines = "380";
        PipedInputStream processInput = new PipedInputStream();
        PipedOutputStream processOutput = new PipedOutputStream();

        Config config = getConfig();
        try (final KubernetesClient client = new DefaultKubernetesClient(config);
             ExecWatch watch = client.pods().inNamespace(namespace).withName(pod).inContainer(container)
                     .writingInput(processOutput)
                     .readingOutput(processInput)
                        .writingErrorChannel(System.out)
                     .withTTY()
                     .usingListener(new SimpleListener(terminalSession))
                        .exec("env", "TERM=xterm", "COLUMNS=" + columns, "LINES=" + lines, "/bin/sh")) {
            terminalSession.setWatch(watch);
            readTtyOutput(terminalSession);

        } catch (Exception e) {
            logger.error("init pod shell error " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Config getConfig() {

        Config config = Config.autoConfigure(null);

        return config;
    }


    /**
     * 读取容器终端输出
     * @param terminalSession
     */
    private void readTtyOutput(TerminalSession terminalSession) {
        byte[] bytes = new byte[1024];
        int read;
        try {
            while ((read = terminalSession.getWatch().getOutput().read(bytes)) != -1 && terminalSession.isOpen()) {
                if (logger.isDebugEnabled()) {
                    logger.info("Receive from tty message : " + new String(bytes, 0, read));
                }
                terminalSession.getSession().getBasicRemote().sendText(new String(bytes, 0, read));
            }
        } catch (Exception e) {
            logger.error("reading TTY output error " + e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 容器tty容器监听
     */
    private static class SimpleListener implements ExecListener {
        TerminalSession terminalSession;

        public SimpleListener(TerminalSession terminalSession) {
            this.terminalSession = terminalSession;
        }

        @Override
        public void onOpen(Response response) {
            terminalSession.setOpen(true);
            logger.info("The shell is opened.");
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            terminalSession.setOpen(false);
            logger.info("The shell is failure." + t.getMessage());
        }

        @Override
        public void onClose(int code, String reason) {
            terminalSession.setOpen(false);
            logger.info("The shell is close." + reason);
        }
    }
}
