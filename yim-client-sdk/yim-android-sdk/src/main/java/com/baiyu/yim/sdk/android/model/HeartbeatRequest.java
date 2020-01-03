package com.baiyu.yim.sdk.android.model;

import com.baiyu.yim.sdk.android.constant.YIMConstant;

import java.io.Serializable;

/**
 *服务端心跳请求
 * @author baiyu
 * @data 2019-12-31 16:51
 */
public class HeartbeatRequest implements Serializable, Protobufable  {

    private static final long serialVersionUID = 1L;
    private static final String TAG = "SERVER_HEARTBEAT_REQUEST";
    private static final String CMD_HEARTBEAT_REQUEST = "SR";

    private static HeartbeatRequest object = new HeartbeatRequest();

    private HeartbeatRequest() {

    }

    public static HeartbeatRequest getInstance() {
        return object;
    }

    @Override
    public byte[] getByteArray() {
        return CMD_HEARTBEAT_REQUEST.getBytes();
    }

    public String toString() {
        return TAG;
    }

    @Override
    public byte getType() {
        return YIMConstant.ProtobufType.S_H_RQ;
    }
}
