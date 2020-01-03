package com.baiyu.yim.sdk.server.model;

import java.io.Serializable;

/**
 * 客户端心跳响应
 * @author baiyu
 * @data 2019-12-30 14:39
 */
public class HeartbeatResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String TAG = "CLIENT_HEARTBEAT_RESPONSE";
    public static final String CMD_HEARTBEAT_RESPONSE = "CR";
    private static HeartbeatResponse object = new HeartbeatResponse();

    private HeartbeatResponse() {

    }

    public static HeartbeatResponse getInstance() {
        return object;
    }

    public String toString() {
        return TAG;
    }
}
