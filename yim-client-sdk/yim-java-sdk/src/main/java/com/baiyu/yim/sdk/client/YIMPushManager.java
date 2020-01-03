package com.baiyu.yim.sdk.client;

import com.baiyu.yim.sdk.client.constant.YIMConstant;
import com.baiyu.yim.sdk.client.model.Intent;
import com.baiyu.yim.sdk.client.model.SentBody;

import java.util.Properties;
import java.util.UUID;

/**
 * YIM 功能接口
 * @author baiyu
 * @data 2019-12-31 15:17
 */
public class YIMPushManager {
    static String ACTION_ACTIVATE_PUSH_SERVICE = "ACTION_ACTIVATE_PUSH_SERVICE";

    static String ACTION_CREATE_YIM_CONNECTION = "ACTION_CREATE_YIM_CONNECTION";

    static String ACTION_SEND_REQUEST_BODY = "ACTION_SEND_REQUEST_BODY";

    static String ACTION_CLOSE_YIM_CONNECTION = "ACTION_CLOSE_YIM_CONNECTION";

    static String ACTION_DESTORY = "ACTION_DESTORY";

    static String KEY_YIM_CONNECTION_STATUS = "KEY_YIM_CONNECTION_STATUS";

    // 被销毁的destroy()
    public static final int STATE_DESTROYED = 0x0000DE;
    // 被销停止的 stop()
    public static final int STATE_STOPED = 0x0000EE;

    public static final int STATE_NORMAL = 0x000000;

    /**
     * 初始化,连接服务端，在程序启动页或者 在Application里调用
     *
     * @param context
     * @param ip
     * @param port
     */

    public static void connect(String ip, int port) {

        YIMCacheManager.getInstance().putBoolean(YIMCacheManager.KEY_YIM_DESTROYED, false);
        YIMCacheManager.getInstance().putBoolean(YIMCacheManager.KEY_MANUAL_STOP, false);

        YIMCacheManager.getInstance().putString(YIMCacheManager.KEY_YIM_SERVIER_HOST, ip);
        YIMCacheManager.getInstance().putInt(YIMCacheManager.KEY_YIM_SERVIER_PORT, port);

        Intent serviceIntent = new Intent();
        serviceIntent.putExtra(YIMCacheManager.KEY_YIM_SERVIER_HOST, ip);
        serviceIntent.putExtra(YIMCacheManager.KEY_YIM_SERVIER_PORT, port);
        serviceIntent.setAction(ACTION_CREATE_YIM_CONNECTION);
        startService(serviceIntent);

    }

    private static void startService(Intent intent) {
        YIMPushService.getInstance().onStartCommand(intent);
    }

    protected static void connect() {

        boolean isManualStop = YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_MANUAL_STOP);
        boolean isManualDestory = YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_YIM_DESTROYED);

        if (isManualStop || isManualDestory) {
            return;
        }

        String host = YIMCacheManager.getInstance().getString(YIMCacheManager.KEY_YIM_SERVIER_HOST);
        int port = YIMCacheManager.getInstance().getInt(YIMCacheManager.KEY_YIM_SERVIER_PORT);

        connect(host, port);

    }

    private static void sendBindRequest(String account) {

        YIMCacheManager.getInstance().putBoolean(YIMCacheManager.KEY_MANUAL_STOP, false);
        SentBody sent = new SentBody();
        Properties sysPro = System.getProperties();
        sent.setKey(YIMConstant.RequestKey.CLIENT_BIND);
        sent.put("account", account);
        sent.put("deviceId", getDeviceId());
        sent.put("channel", "java");
        sent.put("device", sysPro.getProperty("os.name"));
        sent.put("version", getClientVersion());
        sent.put("osVersion", sysPro.getProperty("os.version"));
        sendRequest(sent);
    }

    /**
     * 设置一个账号登录到服务端
     *
     * @param account
     *            用户唯一ID
     */
    public static void bindAccount(String account) {

        boolean isManualDestory = YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_YIM_DESTROYED);
        if (isManualDestory || account == null || account.trim().length() == 0) {
            return;
        }
        sendBindRequest(account);

    }

    protected static boolean autoBindDeviceId() {

        String account = getAccount();

        boolean isManualDestory = YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_YIM_DESTROYED);
        boolean isManualStoped = YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_MANUAL_STOP);
        if (isManualStoped || account == null || account.trim().length() == 0 || isManualDestory) {
            return false;
        }

        sendBindRequest(account);

        return true;
    }

    /**
     * 发送一个YIM请求
     *
     * @param context
     * @body
     */
    public static void sendRequest(SentBody body) {

        boolean isManualStop = YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_MANUAL_STOP);
        boolean isManualDestory = YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_YIM_DESTROYED);

        if (isManualStop || isManualDestory) {
            return;
        }

        Intent serviceIntent = new Intent();
        serviceIntent.putExtra(SentBody.class.getName(), body);
        serviceIntent.setAction(ACTION_SEND_REQUEST_BODY);
        startService(serviceIntent);

    }

    /**
     * 停止接受推送，将会退出当前账号登录，端口与服务端的连接
     *
     * @param context
     */
    public static void stop() {

        boolean isManualDestory = YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_YIM_DESTROYED);
        if (isManualDestory) {
            return;
        }

        YIMCacheManager.getInstance().putBoolean(YIMCacheManager.KEY_MANUAL_STOP, true);

        startService(new Intent(ACTION_CLOSE_YIM_CONNECTION));

    }

    /**
     * 完全销毁YIM，一般用于完全退出程序，调用resume将不能恢复
     *
     * @param context
     */
    public static void destroy() {

        YIMCacheManager.getInstance().putBoolean(YIMCacheManager.KEY_YIM_DESTROYED, true);

        Intent serviceIntent = new Intent();
        serviceIntent.setAction(ACTION_DESTORY);
        startService(serviceIntent);

    }

    /**
     * 重新恢复接收推送，重新连接服务端，并登录当前账号如果aotuBind == true
     *
     * @param context
     * @param aotuBind
     */
    public static void resume() {

        boolean isManualDestory = YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_YIM_DESTROYED);
        if (isManualDestory) {
            return;
        }

        autoBindDeviceId();
    }

    public static boolean isConnected() {
        return YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_YIM_CONNECTION_STATE);
    }

    public static int getState() {
        boolean isManualDestory = YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_YIM_DESTROYED);
        if (isManualDestory) {
            return STATE_DESTROYED;
        }

        boolean isManualStop = YIMCacheManager.getInstance().getBoolean(YIMCacheManager.KEY_MANUAL_STOP);
        if (isManualStop) {
            return STATE_STOPED;
        }

        return STATE_NORMAL;
    }


    public static String getClientVersion() {
        return System.getProperties().getProperty(YIMConstant.ConfigKey.CLIENT_VERSION);
    }

    public static String getAccount() {
        return System.getProperties().getProperty(YIMConstant.ConfigKey.CLIENT_ACCOUNT);
    }

    public static void setAccount(String account) {
        System.getProperties().put(YIMConstant.ConfigKey.CLIENT_ACCOUNT, account);
    }

    public static void setClientVersion(String version) {
        System.getProperties().put(YIMConstant.ConfigKey.CLIENT_VERSION, version);
    }

    private static String getDeviceId() {

        String deviceId = System.getProperties().getProperty(YIMConstant.ConfigKey.CLIENT_DEVICEID);

        if(deviceId == null) {
            deviceId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
            System.getProperties().put(YIMConstant.ConfigKey.CLIENT_DEVICEID, deviceId);
        }
        return deviceId;
    }
}
