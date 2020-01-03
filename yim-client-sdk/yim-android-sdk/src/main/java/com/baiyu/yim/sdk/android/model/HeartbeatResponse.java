package com.baiyu.yim.sdk.android.model;

import com.baiyu.yim.sdk.android.constant.YIMConstant;

import java.io.Serializable;

/**
 * 客户端心跳响应
 * @author baiyu
 * @data 2019-12-31 16:52
 */
public class HeartbeatResponse implements Serializable, Protobufable {

    private static final long serialVersionUID = 1L;
    private static final String TAG = "CLIENT_HEARTBEAT_RESPONSE";
    private static final String CMD_HEARTBEAT_RESPONSE = "CR";

    private static HeartbeatResponse object = new HeartbeatResponse();

    private HeartbeatResponse() {

    }

    public static HeartbeatResponse getInstance() {
        return object;
    }

    @Override
    public byte[] getByteArray() {
        return CMD_HEARTBEAT_RESPONSE.getBytes();
    }

    public String toString() {
        return TAG;
    }

    @Override
    public byte getType() {
        return YIMConstant.ProtobufType.C_H_RS;
    }
}
