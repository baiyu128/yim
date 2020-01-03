package com.baiyu.yim.service;

import com.baiyu.yim.sdk.server.model.Message;

/**
 * @author baiyu
 * @data 2019-12-30 16:53
 */
public interface ApnsService {
    void push(Message message, String deviceToken);
}
