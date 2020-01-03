package com.baiyu.yim.sdk.android;

import android.net.NetworkInfo;
import android.util.Log;
import com.baiyu.yim.sdk.android.model.Message;
import com.baiyu.yim.sdk.android.model.ReplyBody;
import com.baiyu.yim.sdk.android.model.SentBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * YIM 消息监听器管理
 * @author baiyu
 * @data 2019-12-31 16:57
 */
public class YIMListenerManager {

    private static ArrayList<YIMEventListener> cimListeners = new ArrayList<YIMEventListener>();
    private static YIMMessageReceiveComparator comparator = new YIMMessageReceiveComparator();

    public static void registerMessageListener(YIMEventListener listener) {

        if (!cimListeners.contains(listener)) {
            cimListeners.add(listener);
            Collections.sort(cimListeners, comparator);
        }
    }

    public static void removeMessageListener(YIMEventListener listener) {
        for (int i = 0; i < cimListeners.size(); i++) {
            if (listener.getClass() == cimListeners.get(i).getClass()) {
                cimListeners.remove(i);
            }
        }
    }

    public static void notifyOnNetworkChanged(NetworkInfo info) {
        for (YIMEventListener listener : cimListeners) {
            listener.onNetworkChanged(info);
        }
    }

    public static void notifyOnConnectionSuccessed(boolean hasAutoBind) {
        for (YIMEventListener listener : cimListeners) {
            listener.onConnectionSuccessed(hasAutoBind);
        }
    }

    public static void notifyOnMessageReceived(Message message) {
        for (YIMEventListener listener : cimListeners) {
            listener.onMessageReceived(message);
        }
    }

    public static void notifyOnConnectionClosed() {
        for (YIMEventListener listener : cimListeners) {
            listener.onConnectionClosed();
        }
    }

    public static void notifyOnConnectionFailed() {
        for (YIMEventListener listener : cimListeners) {
            listener.onConnectionFailed();
        }
    }

    public static void notifyOnReplyReceived(ReplyBody body) {
        for (YIMEventListener listener : cimListeners) {
            listener.onReplyReceived(body);
        }
    }

    public static void notifyOnSentSucceed(SentBody body) {
        for (YIMEventListener listener : cimListeners) {
            listener.onSentSuccessed(body);
        }
    }

    public static void destory() {
        cimListeners.clear();
    }

    public static void logListenersName() {
        for (YIMEventListener listener : cimListeners) {
            Log.i(YIMEventListener.class.getSimpleName(), "#######" + listener.getClass().getName() + "#######");
        }
    }

    /**
     * 消息接收activity的接收顺序排序，YIM_RECEIVE_ORDER倒序
     */
    private static class YIMMessageReceiveComparator implements Comparator<YIMEventListener> {

        @Override
        public int compare(YIMEventListener arg1, YIMEventListener arg2) {

            int order1 = arg1.getEventDispatchOrder();
            int order2 = arg2.getEventDispatchOrder();
            return order2 - order1;
        }

    }
}
