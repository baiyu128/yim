package com.baiyu.yim.sdk.client.model;

/**
 * 需要向另一端发送的结构体
 * @author baiyu
 * @data 2019-12-31 15:28
 */
public interface Protobufable {

    byte[] getByteArray();

    byte getType();
}
