package com.baiyu.yim.sdk.client;

import com.baiyu.yim.sdk.client.model.Message;
import com.baiyu.yim.sdk.client.model.ReplyBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * YIM 消息监听器管理
 * @author baiyu
 * @data 2019-12-31 15:18
 */
public class YIMListenerManager {

    private static ArrayList<YIMEventListener> cimListeners = new ArrayList<YIMEventListener>();
    private static YIMMessageReceiveComparator comparator = new YIMMessageReceiveComparator();
    private static final Logger LOGGER = LoggerFactory.getLogger(YIMListenerManager.class);

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

    public static void notifyOnConnectionSuccessed(boolean antoBind) {
        for (YIMEventListener listener : cimListeners) {
            listener.onConnectionSuccessed(antoBind);
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

    public static void notifyOnReplyReceived(ReplyBody body) {
        for (YIMEventListener listener : cimListeners) {
            listener.onReplyReceived(body);
        }
    }

    public static void notifyOnConnectionFailed() {
        for (YIMEventListener listener : cimListeners) {
            listener.onConnectionFailed();
        }
    }

    public static void destory() {
        cimListeners.clear();
    }

    public static void logListenersName() {
        for (YIMEventListener listener : cimListeners) {
            LOGGER.debug("#######" + listener.getClass().getName() + "#######");
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
