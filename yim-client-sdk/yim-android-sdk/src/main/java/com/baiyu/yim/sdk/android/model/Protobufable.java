package com.baiyu.yim.sdk.android.model;

/**
 * 需要向另一端发送的结构体
 * @author baiyu
 * @data 2019-12-31 16:53
 */
public interface Protobufable {

    byte[] getByteArray();

    byte getType();
}
