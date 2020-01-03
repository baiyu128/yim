package com.baiyu.yim.sdk.android;

import android.net.NetworkInfo;
import com.baiyu.yim.sdk.android.model.Message;
import com.baiyu.yim.sdk.android.model.ReplyBody;
import com.baiyu.yim.sdk.android.model.SentBody;

/**
 * YIM 主要事件接口
 * @author baiyu
 * @data 2019-12-31 16:58
 */
public interface YIMEventListener {

    /**
     * 当收到服务端推送过来的消息时调用
     *
     * @param message
     */
    void onMessageReceived(Message message);

    /**
     * 当调用CIMPushManager.sendRequest()向服务端发送请求，获得相应时调用
     *
     * @param replybody
     */
    void onReplyReceived(ReplyBody replybody);

    /**
     * 当调用CIMPushManager.sendRequest()向服务端发送请求成功时
     *
     * @param body
     */
    void onSentSuccessed(SentBody body);

    /**
     * 当手机网络发生变化时调用
     *
     * @param networkinfo
     */
    void onNetworkChanged(NetworkInfo networkinfo);

    /**
     * 当连接服务器成功时回调
     *
     * @param hasAutoBind
     *            : true 已经自动绑定账号到服务器了，不需要再手动调用bindAccount
     */
    void onConnectionSuccessed(boolean hasAutoBind);

    /**
     * 当断开服务器连接的时候回调
     *
     */
    void onConnectionClosed();

    /**
     * 当连接服务器失败的时候回调
     *
     */
    void onConnectionFailed();

    /**
     * 监听器在容器里面的排序。值越大则越先接收
     */
    int getEventDispatchOrder();
}
