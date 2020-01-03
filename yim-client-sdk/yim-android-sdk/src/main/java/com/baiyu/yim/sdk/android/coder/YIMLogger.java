package com.baiyu.yim.sdk.android.coder;

import android.util.Log;

import java.nio.channels.SocketChannel;

/**
 * 日志打印，添加session 的id和ip address
 * @author baiyu
 * @data 2019-12-31 15:53
 */
public class YIMLogger {
    private final static String TAG = "YIM";
    private boolean debug = true;

    public static YIMLogger getLogger() {
        return LoggerHolder.logger;
    }

    private YIMLogger() {

    }

    private static class LoggerHolder{
        private static YIMLogger logger = new YIMLogger();
    }

    public void  debugMode(boolean mode) {
        debug = mode;
    }

    public void messageReceived(SocketChannel session, Object message)  {
        if(debug) {
            Log.i(TAG,String.format("[RECEIVED]" + getSessionInfo(session) + "\n%s", message));
        }
    }

    public void messageSent(SocketChannel session, Object message)   {
        if(debug) {
            Log.i(TAG,String.format("[  SENT  ]" + getSessionInfo(session) + "\n%s", message));
        }
    }

    public void sessionCreated( SocketChannel session) {
        if(debug) {
            Log.i(TAG,"[ OPENED ]" + getSessionInfo(session));
        }
    }

    public void sessionIdle( SocketChannel session)   {
        if(debug) {
            Log.d(TAG,"[  IDLE  ]" + getSessionInfo(session));
        }
    }

    public void sessionClosed( SocketChannel session)  {
        if(debug) {
            Log.w(TAG,"[ CLOSED ] ID = " + session.hashCode());
        }
    }

    public void connectFailure(long interval)  {
        if(debug) {
            Log.d(TAG,"CONNECT FAILURE, TRY RECONNECT AFTER " + interval +"ms");
        }
    }

    public void startConnect(String host , int port) {
        if(debug) {
            Log.i(TAG,"START CONNECT REMOTE HOST:" + host + " PORT:" + port);
        }
    }

    public void invalidHostPort(String host , int port) {
        if(debug) {
            Log.d(TAG,"INVALID SOCKET ADDRESS -> HOST:" + host + " PORT:" + port);
        }
    }

    public void connectState(boolean isConnected)  {
        if(debug) {
            Log.d(TAG,"CONNECTED:" + isConnected);
        }
    }

    public void connectState(boolean isConnected,boolean isManualStop,boolean isDestroyed)  {
        if(debug) {
            Log.d(TAG,"CONNECTED:" + isConnected + " STOPED:"+isManualStop+ " DESTROYED:"+isDestroyed);
        }
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
