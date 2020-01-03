package com.baiyu.yim.sdk.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import com.baiyu.yim.sdk.android.coder.YIMLogger;
import com.baiyu.yim.sdk.android.constant.YIMConstant;
import com.baiyu.yim.sdk.android.model.SentBody;

import java.util.UUID;

/**
 * YIM 功能接口
 * @author baiyu
 * @data 2019-12-31 16:56
 */
public class YIMPushManager {

    protected static String ACTION_ACTIVATE_PUSH_SERVICE = "ACTION_ACTIVATE_PUSH_SERVICE";

    protected static String ACTION_CREATE_YIM_CONNECTION = "ACTION_CREATE_YIM_CONNECTION";

    protected static String ACTION_SEND_REQUEST_BODY = "ACTION_SEND_REQUEST_BODY";

    protected static String ACTION_CLOSE_YIM_CONNECTION = "ACTION_CLOSE_YIM_CONNECTION";

    protected static String ACTION_SET_LOGGER_EANABLE = "ACTION_SET_LOGGER_EANABLE";

    protected static String KEY_SEND_BODY = "KEY_SEND_BODY";

    protected static String KEY_YIM_CONNECTION_STATUS = "KEY_YIM_CONNECTION_STATUS";

    /**
     * 初始化,连接服务端，在程序启动页或者 在Application里调用
     *
     * @param context
     * @param ip
     * @param port
     */
    public static void connect(Context context, String host, int port) {

        if(TextUtils.isEmpty(host) || port == 0) {
            YIMLogger.getLogger().invalidHostPort(host, port);
            return;
        }


        YIMCacheManager.putString(context, YIMCacheManager.KEY_YIM_SERVIER_HOST, host);
        YIMCacheManager.putInt(context, YIMCacheManager.KEY_YIM_SERVIER_PORT, port);

        YIMCacheManager.putBoolean(context, YIMCacheManager.KEY_YIM_DESTROYED, false);
        YIMCacheManager.putBoolean(context, YIMCacheManager.KEY_MANUAL_STOP, false);

        YIMCacheManager.remove(context, YIMCacheManager.KEY_ACCOUNT);


        Intent serviceIntent = new Intent(context, YIMPushService.class);
        serviceIntent.setAction(ACTION_CREATE_YIM_CONNECTION);
        startService(context,serviceIntent);

    }

    public static void setLoggerEnable(Context context,boolean enable) {
        Intent serviceIntent = new Intent(context, YIMPushService.class);
        serviceIntent.putExtra(YIMPushService.KEY_LOGGER_ENABLE, enable);
        serviceIntent.setAction(ACTION_SET_LOGGER_EANABLE);
        startService(context,serviceIntent);
    }


    /**
     * 设置一个账号登录到服务端
     *
     * @param account
     *            用户唯一ID
     */
    public static void bindAccount(Context context, String account) {

        if (isDestoryed(context) || account == null || account.trim().length() == 0) {
            return;
        }

        sendBindRequest(context, account);

    }

    private static void sendBindRequest(Context context, String account) {

        YIMCacheManager.putBoolean(context, YIMCacheManager.KEY_MANUAL_STOP, false);
        YIMCacheManager.putString(context, YIMCacheManager.KEY_ACCOUNT, account);

        String deviceId = YIMCacheManager.getString(context, YIMCacheManager.KEY_DEVICE_ID);
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
            YIMCacheManager.putString(context, YIMCacheManager.KEY_DEVICE_ID, deviceId);
        }

        SentBody sent = new SentBody();
        sent.setKey(YIMConstant.RequestKey.CLIENT_BIND);
        sent.put("account", account);
        sent.put("deviceId", deviceId);
        sent.put("channel", "android");
        sent.put("device", android.os.Build.MODEL);
        sent.put("version", getVersionName(context));
        sent.put("osVersion", android.os.Build.VERSION.RELEASE);
        sent.put("packageName", context.getPackageName());
        sendRequest(context, sent);
    }

    protected static boolean autoBindAccount(Context context) {

        String account = YIMCacheManager.getString(context, YIMCacheManager.KEY_ACCOUNT);
        if (account == null || account.trim().length() == 0 || isDestoryed(context)) {
            return false;
        }

        sendBindRequest(context, account);

        return true;
    }

    /**
     * 发送一个YIM请求
     *
     * @param context
     * @body
     */
    public static void sendRequest(Context context, SentBody body) {

        if (isDestoryed(context) || isStoped(context)) {
            return;
        }

        Intent serviceIntent = new Intent(context, YIMPushService.class);
        serviceIntent.putExtra(KEY_SEND_BODY, body);
        serviceIntent.setAction(ACTION_SEND_REQUEST_BODY);
        startService(context,serviceIntent);

    }

    /**
     * 停止接受推送，将会退出当前账号登录，端口与服务端的连接
     *
     * @param context
     */
    public static void stop(Context context) {

        if (isDestoryed(context)) {
            return;
        }

        YIMCacheManager.putBoolean(context, YIMCacheManager.KEY_MANUAL_STOP, true);

        Intent serviceIntent = new Intent(context, YIMPushService.class);
        serviceIntent.setAction(ACTION_CLOSE_YIM_CONNECTION);
        startService(context,serviceIntent);

    }

    /**
     * 完全销毁YIM，一般用于完全退出程序，调用resume将不能恢复
     *
     * @param context
     */
    public static void destroy(Context context) {

        YIMCacheManager.putBoolean(context, YIMCacheManager.KEY_YIM_DESTROYED, true);
        YIMCacheManager.putString(context, YIMCacheManager.KEY_ACCOUNT, null);

        context.stopService(new Intent(context, YIMPushService.class));

    }

    /**
     * 重新恢复接收推送，重新连接服务端，并登录当前账号
     *
     * @param context
     */
    public static void resume(Context context) {

        if (isDestoryed(context)) {
            return;
        }

        autoBindAccount(context);
    }

    public static boolean isDestoryed(Context context) {
        return YIMCacheManager.getBoolean(context, YIMCacheManager.KEY_YIM_DESTROYED);
    }

    public static boolean isStoped(Context context) {
        return YIMCacheManager.getBoolean(context, YIMCacheManager.KEY_MANUAL_STOP);
    }

    public static boolean isConnected(Context context) {
        return YIMCacheManager.getBoolean(context, YIMCacheManager.KEY_YIM_CONNECTION_STATE);
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo networkInfo =  getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
    }


    public static void startService(Context context,Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }



    private static String getVersionName(Context context) {
        String versionName = null;
        try {
            PackageInfo mPackageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = mPackageInfo.versionName;
        } catch (PackageManager.NameNotFoundException ignore) {
        }
        return versionName;
    }
}
