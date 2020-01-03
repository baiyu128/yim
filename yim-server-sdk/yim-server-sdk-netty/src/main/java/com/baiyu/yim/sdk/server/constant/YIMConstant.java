package com.baiyu.yim.sdk.server.constant;

/**
 * 常量
 * @author baiyu
 * @data 2019-12-30 13:58
 */
public interface YIMConstant {

    // 消息头长度为3个字节，第一个字节为消息类型，第二，第三字节 转换int后为消息长度
    int DATA_HEADER_LENGTH = 3;

    public static interface ReturnCode {

        String CODE_200 = "200";

        String CODE_404 = "404";

        String CODE_403 = "403";

        String CODE_500 = "500";

    }

    String KEY_ACCOUNT = "account";

    String KEY_QUIETLY_CLOSE = "quietlyClose";

    String HEARTBEAT_KEY = "heartbeat";


    String CLIENT_WEBSOCKET_HANDSHAKE = "client_websocket_handshake";

    String CLIENT_HEARTBEAT = "client_heartbeat";

    String CLIENT_CONNECT_CLOSED = "client_closed";

    public static interface ProtobufType {
        byte S_H_RQ = 1;
        byte C_H_RS = 0;
        byte MESSAGE = 2;
        byte SENTBODY = 3;
        byte REPLYBODY = 4;
    }

    public static interface MessageAction {
        // 被其他设备登录挤下线消息
        String ACTION_999 = "999";
    }
}
