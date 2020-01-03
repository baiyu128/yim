package com.baiyu.yim.sdk.server.handler;

import com.baiyu.yim.sdk.server.model.SentBody;
import com.baiyu.yim.sdk.server.model.YIMSession;

/**
 * 心跳handler，主要是让netty重置cheannel的空闲时间
 * @author baiyu
 * @data 2019-12-30 14:16
 */
public class HeartbeatHandler implements YIMRequestHandler {
    public void process(YIMSession session, SentBody body) {}
}
