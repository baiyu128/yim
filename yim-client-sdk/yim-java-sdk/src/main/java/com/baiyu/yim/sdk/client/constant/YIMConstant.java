package com.baiyu.yim.sdk.client.constant;

/**
 * 常量
 * @author baiyu
 * @data 2019-12-31 15:24
 */
public interface YIMConstant {

    long RECONN_INTERVAL_TIME = 30 * 1000;
    // 消息头长度为3个字节，第一个字节为消息类型，第二，第三字节 转换int后为消息长度
    int DATA_HEADER_LENGTH = 3;

    public static interface ReturnCode {

        String CODE_404 = "404";

        String CODE_403 = "403";

        String CODE_405 = "405";

        String CODE_200 = "200";

        String CODE_206 = "206";

        String CODE_500 = "500";

    }

    public static interface ConfigKey {

        public static String DEVICE_MODEL = "client.model";
        public static String CLIENT_VERSION = "client.version";
        public static String CLIENT_ACCOUNT = "client.account";
        public static String CLIENT_DEVICEID = "client.deviceid";

    }

    public static interface ProtobufType {
        byte C_H_RS = 0;
        byte S_H_RQ = 1;
        byte MESSAGE = 2;
        byte SENTBODY = 3;
        byte REPLYBODY = 4;
    }

    public static interface RequestKey {

        String CLIENT_BIND = "client_bind";

        String CLIENT_LOGOUT = "client_logout";

        @Deprecated
        String CLIENT_PULL_MESSAGE = "client_pull_message";

    }

    public static interface MessageAction {

        // 被其他设备登录挤下线消息
        String ACTION_999 = "999";
        // 被系统禁用消息
        String ACTION_444 = "444";
    }

    public static interface IntentAction {

        // 消息广播action
        String ACTION_MESSAGE_RECEIVED = "com.baiyu.yim.MESSAGE_RECEIVED";

        // 发送sendbody成功广播
        String ACTION_SENT_SUCCESSED = "com.baiyu.yim.SENT_SUCCESSED";

        // 链接意外关闭广播
        String ACTION_CONNECTION_CLOSED = "com.baiyu.yim.CONNECTION_CLOSED";

        // 链接失败广播
        String ACTION_CONNECTION_FAILED = "com.baiyu.yim.CONNECTION_FAILED";

        // 链接成功广播
        String ACTION_CONNECTION_SUCCESSED = "com.baiyu.yim.CONNECTION_SUCCESSED";

        // 发送sendbody成功后获得replaybody回应广播
        String ACTION_REPLY_RECEIVED = "com.baiyu.yim.REPLY_RECEIVED";

        // 网络变化广播
        String ACTION_NETWORK_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";

        // 未知异常
        String ACTION_UNCAUGHT_EXCEPTION = "com.baiyu.yim.UNCAUGHT_EXCEPTION";

        // 重试连接
        String ACTION_CONNECTION_RECOVERY = "com.baiyu.yim.CONNECTION_RECOVERY";
    }
}
