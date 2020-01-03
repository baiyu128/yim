package com.baiyu.yim.sdk.server.model;

/**
 * websocket握手响应结果
 * @author baiyu
 * @data 2019-12-30 14:35
 */
public class HandshakerResponse {

    private String token;

    public HandshakerResponse(String token) {
        this.token = token;
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 101 Switching Protocols");
        builder.append("\r\n");
        builder.append("Upgrade: websocket");
        builder.append("\r\n");
        builder.append("Connection: Upgrade");
        builder.append("\r\n");
        builder.append("Sec-WebSocket-Accept:").append(token);
        builder.append("\r\n");
        builder.append("\r\n");
        return builder.toString();

    }
}
