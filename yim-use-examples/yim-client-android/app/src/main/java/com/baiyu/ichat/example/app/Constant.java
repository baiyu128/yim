package com.baiyu.ichat.example.app;

/**
 * @author baiyu
 * @data 2020-01-03 9:39
 */
public interface Constant {

    //服务端IP地址
    String YIM_SERVER_HOST = "192.168.50.80";

    //注意，这里的端口不是tomcat的端口，没改动就使用默认的23456
    int YIM_SERVER_PORT = 23456;

    interface MessageAction {
        //下线类型
        String ACTION_999 = "999";
    }
}
