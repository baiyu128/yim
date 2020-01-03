package com.baiyu.yim.sdk.server.handler;

import com.baiyu.yim.sdk.server.model.SentBody;
import com.baiyu.yim.sdk.server.model.YIMSession;

/**
 * 请求处理接口,所有的请求实现必须实现此接口
 * @author baiyu
 * @data 2019-12-30 14:15
 */
public interface YIMRequestHandler {
    void process(YIMSession session, SentBody message);
}
