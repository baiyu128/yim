package com.baiyu.yim.sdk.client;

import com.baiyu.yim.sdk.client.model.Message;
import com.baiyu.yim.sdk.client.model.ReplyBody;

/**
 * YIM 主要事件接口
 * @author baiyu
 * @data 2019-12-31 15:19
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
     * 当连接服务器成功时回调
     *
     * @param hasAutoBind
     *            : true 已经自动绑定账号到服务器了，不需要再手动调用bindAccount
     */
    void onConnectionSuccessed(boolean hasAutoBind);

    /**
     * 当断开服务器连接的时候回调
     */
    void onConnectionClosed();

    /**
     * 当服务器连接失败的时候回调
     *
     */
    void onConnectionFailed();

    /**
     * 监听器在容器里面的排序。值越大则越先接收
     */
    int getEventDispatchOrder();
}
