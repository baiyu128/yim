package com.baiyu.yim.sdk.client.coder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SocketChannel;

/**
 * 日志打印，添加session 的id和ip address
 * @author baiyu
 * @data 2019-12-31 15:22
 */
public class YIMLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(YIMLogger.class);

    public static YIMLogger getLogger() {
        return LoggerHolder.logger;
    }

    private YIMLogger() {

    }

    private static class LoggerHolder{
        private static YIMLogger logger = new YIMLogger();
    }


    public void messageReceived(SocketChannel session, Object message)  {
        LOGGER.info(String.format("RECEIVED" + getSessionInfo(session) + "\n%s", message));
    }

    public void messageSent(SocketChannel session, Object message)   {
        LOGGER.info(String.format("SENT" + getSessionInfo(session) + "\n%s", message));
    }

    public void sessionCreated( SocketChannel session) {
        LOGGER.info("OPENED" + getSessionInfo(session));
    }

    public void sessionIdle( SocketChannel session)   {
        LOGGER.debug("IDLE READ" + getSessionInfo(session));
    }

    public void sessionClosed( SocketChannel session)  {
        LOGGER.warn("CLOSED ID = " + session.hashCode());

    }

    public void connectFailure(long interval)  {
        LOGGER.debug("CONNECT FAILURE TRY RECONNECT AFTER " + interval +"ms");
    }

    public void startConnect(String host , int port) {
        LOGGER.info("START CONNECT REMOTE HOST:" + host + " PORT:" + port);
    }

    public void connectState(boolean isConnected)  {
        LOGGER.debug("CONNECTED:" + isConnected);
    }

    public void connectState(boolean isConnected,boolean isManualStop,boolean isDestroyed)  {
        LOGGER.debug("CONNECTED:" + isConnected + " STOPED:"+isManualStop+ " DESTROYED:"+isDestroyed);
    }

    private String getSessionInfo(SocketChannel session) {
        StringBuilder builder = new StringBuilder();
        if (session == null) {
            return "";
        }
        builder.append(" [");
        builder.append("id:").append(session.hashCode());

        try {
            if (session.socket().getLocalAddress() != null) {
                builder.append(" L:").append(session.socket().getLocalAddress()+":"+session.socket().getLocalPort());
            }
        } catch (Exception ignore) {
        }


        try {
            if (session.socket().getRemoteSocketAddress() != null) {
                builder.append(" R:").append(session.socket().getRemoteSocketAddress().toString());
            }
        } catch (Exception ignore) {
        }
        builder.append("]");
        return builder.toString();
    }
}
