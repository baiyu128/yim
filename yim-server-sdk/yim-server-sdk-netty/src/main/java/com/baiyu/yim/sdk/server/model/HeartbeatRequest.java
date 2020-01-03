package com.baiyu.yim.sdk.server.model;

import java.io.Serializable;

import com.baiyu.yim.sdk.server.constant.YIMConstant;
import com.baiyu.yim.sdk.server.model.feature.EncodeFormatable;

/**
 * 服务端心跳请求
 * @author baiyu
 * @data 2019-12-30 14:36
 */

public class HeartbeatRequest implements Serializable, EncodeFormatable {

    private static final long serialVersionUID = 1L;
    private static final String TAG = "SERVER_HEARTBEAT_REQUEST";
    private static final String CMD_HEARTBEAT_RESPONSE = "SR";
    private static HeartbeatRequest object = new HeartbeatRequest();

    private HeartbeatRequest() {

    }

    public static HeartbeatRequest getInstance() {
        return object;
    }

    public String toString() {
        return TAG;
    }

    @Override
    public byte[] getProtobufBody() {
        return CMD_HEARTBEAT_RESPONSE.getBytes();
    }

    @Override
    public byte getDataType() {
        return YIMConstant.ProtobufType.S_H_RQ;
    }
}
