package com.baiyu.yim.sdk.client;

import com.baiyu.yim.sdk.client.model.Intent;
import com.baiyu.yim.sdk.client.model.SentBody;

/**
 * 与服务端连接服务
 * @author baiyu
 * @data 2019-12-31 15:15
 */
public class YIMPushService {

    protected final static int DEF_YIM_PORT = 23456;
    private YIMConnectorManager manager;

    private static YIMPushService service;

    public static YIMPushService getInstance() {
        if (service == null) {
            service = new YIMPushService();
        }
        return service;
    }

    public YIMPushService() {
        manager = YIMConnectorManager.getManager();
    }

    public void onStartCommand(Intent intent) {

        intent = (intent == null ? new Intent(YIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE) : intent);

        String action = intent.getAction();

        if (YIMPushManager.ACTION_CREATE_YIM_CONNECTION.equals(action)) {
            String host = YIMCacheManager.getInstance().getString(YIMCacheManager.KEY_YIM_SERVIER_HOST);
            int port = YIMCacheManager.getInstance().getInt(YIMCacheManager.KEY_YIM_SERVIER_PORT);
            manager.connect(host, port);
        }

        if (YIMPushManager.ACTION_SEND_REQUEST_BODY.equals(action)) {
            manager.send((SentBody) intent.getExtra(SentBody.class.getName()));
        }

        if (YIMPushManager.ACTION_CLOSE_YIM_CONNECTION.equals(action)) {
            manager.closeSession();
        }

        if (YIMPushManager.ACTION_DESTORY.equals(action)) {
            manager.destroy();
        }

        if (YIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action) && !manager.isConnected()) {

            String host = YIMCacheManager.getInstance().getString(YIMCacheManager.KEY_YIM_SERVIER_HOST);
            int port = YIMCacheManager.getInstance().getInt(YIMCacheManager.KEY_YIM_SERVIER_PORT);
            manager.connect(host, port);
        }
    }
}
