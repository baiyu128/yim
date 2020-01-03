package com.baiyu.yim.sdk.server.model.feature;

/**
 * 需要向另一端发送的结构体
 * @author baiyu
 * @data 2019-12-30 14:17
 */
public interface EncodeFormatable {
    byte[] getProtobufBody();

    byte getDataType();
}
