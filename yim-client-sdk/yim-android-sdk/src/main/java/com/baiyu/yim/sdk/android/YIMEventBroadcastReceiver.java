package com.baiyu.yim.sdk.android;

import com.baiyu.yim.sdk.android.constant.YIMConstant;
import com.baiyu.yim.sdk.android.model.Message;
import com.baiyu.yim.sdk.android.model.ReplyBody;
import com.baiyu.yim.sdk.android.model.SentBody;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;

/**
 * 消息入口，所有消息都会经过这里
 * @author baiyu
 * @data 2019-12-31 16:59
 */
public abstract class YIMEventBroadcastReceiver extends BroadcastReceiver {

    protected Context context;

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        /*
         * 操作事件广播，用于提高service存活率
         */
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)
                || intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)
                || intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            startPushService();
        }

        /*
         * 设备网络状态变化事件
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_NETWORK_CHANGED)
                ||intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            onDevicesNetworkChanged();
        }

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
            onConnectionFailed(interval);
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
            onInnerMessageReceived((Message) intent.getSerializableExtra(Message.class.getName()), intent);
        }

        /*
         * 获取收到replybody成功事件
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_REPLY_RECEIVED)) {
            onReplyReceived((ReplyBody) intent.getSerializableExtra(ReplyBody.class.getName()));
        }


        /*
         * 获取sendbody发送成功事件
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_SENT_SUCCESSED)) {
            onSentSucceed((SentBody) intent.getSerializableExtra(SentBody.class.getName()));
        }

        /*
         * 重新连接，如果断开的话
         */
        if (intent.getAction().equals(YIMConstant.IntentAction.ACTION_CONNECTION_RECOVERY)) {
            connect(0);
        }
    }

    private void startPushService() {

        Intent intent = new Intent(context, YIMPushService.class);
        intent.setAction(YIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }

    }

    private void onInnerConnectionClosed() {
        YIMCacheManager.putBoolean(context, YIMCacheManager.KEY_YIM_CONNECTION_STATE, false);

        if (YIMPushManager.isNetworkConnected(context)) {
            connect(0);
        }

        onConnectionClosed();
    }

    private void onConnectionFailed(long reinterval) {

        if (YIMPushManager.isNetworkConnected(context)) {

            onConnectionFailed();

            connect(reinterval);
        }
    }

    private void onInnerConnectionSuccessed() {
        YIMCacheManager.putBoolean(context, YIMCacheManager.KEY_YIM_CONNECTION_STATE, true);

        boolean autoBind = YIMPushManager.autoBindAccount(context);
        onConnectionSuccessed(autoBind);
    }

    private void onDevicesNetworkChanged() {

        if (YIMPushManager.isNetworkConnected(context)) {
            connect(0);
        }

        onNetworkChanged();
    }

    private void connect(long delay) {
        Intent serviceIntent = new Intent(context, YIMPushService.class);
        serviceIntent.putExtra(YIMPushService.KEY_DELAYED_TIME, delay);
        serviceIntent.setAction(YIMPushManager.ACTION_CREATE_YIM_CONNECTION);
        YIMPushManager.startService(context,serviceIntent);
    }

    private void onInnerMessageReceived(com.baiyu.yim.sdk.android.model.Message message, Intent intent) {
        if (isForceOfflineMessage(message.getAction())) {
            YIMPushManager.stop(context);
        }

        onMessageReceived(message, intent);
    }

    private boolean isForceOfflineMessage(String action) {
        return YIMConstant.MessageAction.ACTION_999.equals(action);
    }


    public abstract void onMessageReceived(com.baiyu.yim.sdk.android.model.Message message, Intent intent);

    public void onNetworkChanged() {
        YIMListenerManager.notifyOnNetworkChanged(YIMPushManager.getNetworkInfo(context));
    }

    public void onConnectionSuccessed(boolean hasAutoBind) {
        YIMListenerManager.notifyOnConnectionSuccessed(hasAutoBind);
    }

    public void onConnectionClosed() {
        YIMListenerManager.notifyOnConnectionClosed();
    }

    public void onConnectionFailed() {
        YIMListenerManager.notifyOnConnectionFailed();
    }

    public void onReplyReceived(ReplyBody body) {
        YIMListenerManager.notifyOnReplyReceived(body);
    }

    public void onSentSucceed(SentBody body) {
        YIMListenerManager.notifyOnSentSucceed(body);
    }
}
