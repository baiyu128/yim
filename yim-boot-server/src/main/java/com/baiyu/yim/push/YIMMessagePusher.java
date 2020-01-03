package com.baiyu.yim.push;

import com.baiyu.yim.sdk.server.model.Message;

/**
 * 消息发送实接口
 * @author baiyu
 * @data 2019-12-30 17:02
 */
public interface YIMMessagePusher {

    /**
     * 向用户发送消息
     *
     * @param msg
     */
    public void push(Message msg);
}
