package com.baiyu.yim.sdk.client;

import com.baiyu.yim.sdk.client.constant.YIMConstant;
import com.baiyu.yim.sdk.client.model.Intent;
import com.baiyu.yim.sdk.client.model.Message;
import com.baiyu.yim.sdk.client.model.ReplyBody;
import com.baiyu.yim.sdk.client.model.SentBody;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 消息入口，所有消息都会经过这里
 * @author baiyu
 * @data 2019-12-31 15:19
 */
public class YIMEventBroadcastReceiver {

    Random random = new Random();
    private static YIMEventBroadcastReceiver recerver;
    private YIMEventListener listener;
    private Timer connectionHandler = new Timer();;

    public static YIMEventBroadcastReceiver getInstance() {
        if (recerver == null) {
            recerver = new YIMEventBroadcastReceiver();
        }
        return recerver;
    }

    public void setGlobalYIMEventListener(YIMEventListener ls) {
        listener = ls;
    }

    public void onReceive(Intent intent) {

        /*
         * cim断开服务器事件
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_CONNECTION_CLOSED)) {
            onInnerConnectionClosed();
        }

        /*
         * cim连接服务器失败事件
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_CONNECTION_FAILED)) {
            long interval = intent.getLongExtra("interval", YIMConstant.RECONN_INTERVAL_TIME);
            onInnerConnectionFailed(interval);
        }

        /*
         * cim连接服务器成功事件
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_CONNECTION_SUCCESSED)) {
            onInnerConnectionSuccessed();
        }

        /*
         * 收到推送消息事件
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_MESSAGE_RECEIVED)) {
            onInnerMessageReceived((Message) intent.getExtra(Message.class.getName()));
        }

        /*
         * 获取收到replybody成功事件
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_REPLY_RECEIVED)) {
            listener.onReplyReceived((ReplyBody) intent.getExtra(ReplyBody.class.getName()));
        }

        /*
         * 获取sendbody发送成功事件
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_SENT_SUCCESSED)) {
            onSentSucceed((SentBody) intent.getExtra(SentBody.class.getName()));
        }

        /*
         * 获取cim数据传输异常事件
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_UNCAUGHT_EXCEPTION)) {
            onUncaughtException((Exception) intent.getExtra(Exception.class.getName()));
        }

        /*
         * 重新连接，如果断开的话
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_CONNECTION_RECOVERY)) {
            YIMPushManager.connect();
        }
    }

    private void onInnerConnectionClosed() {

        listener.onConnectionClosed();

        YIMCacheManager.getInstance().putBoolean(YIMCacheManager.KEY_YIM_CONNECTION_STATE, false);
        YIMPushManager.connect();

    }

    private void onInnerConnectionFailed(long interval) {

        connectionHandler.schedule(new ConnectionTask(), interval);

        listener.onConnectionFailed();
    }

    private void onInnerConnectionSuccessed() {
        YIMCacheManager.getInstance().putBoolean(YIMCacheManager.KEY_YIM_CONNECTION_STATE, true);

        boolean autoBind = YIMPushManager.autoBindDeviceId();

        listener.onConnectionSuccessed(autoBind);
    }

    private void onUncaughtException(Throwable arg0) {
    }

    private void onInnerMessageReceived(com.baiyu.yim.sdk.client.model.Message message) {
        if (isForceOfflineMessage(message.getAction())) {
            YIMPushManager.stop();
        }

        listener.onMessageReceived(message);
    }

    private boolean isForceOfflineMessage(String action) {
        return YIMConstant.MessageAction.ACTION_999.equals(action);
    }


    private void onSentSucceed(SentBody body) {
    }

    class ConnectionTask extends TimerTask {

        public void run() {
            YIMPushManager.connect();
        }
    }
}
