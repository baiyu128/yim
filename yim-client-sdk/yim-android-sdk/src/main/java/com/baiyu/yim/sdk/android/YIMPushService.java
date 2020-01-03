package com.baiyu.yim.sdk.android;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import com.baiyu.yim.sdk.android.coder.YIMLogger;
import com.baiyu.yim.sdk.android.constant.YIMConstant;
import com.baiyu.yim.sdk.android.model.SentBody;

/**
 * 与服务端连接服务
 * @author baiyu
 * @data 2019-12-31 15:50
 */
public class YIMPushService extends Service {

    public final static String KEY_DELAYED_TIME = "KEY_DELAYED_TIME";
    public final static String KEY_LOGGER_ENABLE = "KEY_LOGGER_ENABLE";

    private final static int NOTIFICATION_ID = Integer.MAX_VALUE;

    private YIMConnectorManager manager;
    private KeepAliveBroadcastReceiver keepAliveReceiver;
    private ConnectivityManager connectivityManager;
    @Override
    public void onCreate() {
        manager = YIMConnectorManager.getManager(this.getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            keepAliveReceiver = new KeepAliveBroadcastReceiver();
            registerReceiver(keepAliveReceiver, keepAliveReceiver.getIntentFilter());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            connectivityManager.registerDefaultNetworkCallback(networkCallback);

        }
    }

    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            Intent intent = new Intent();
            intent.setPackage(getPackageName());
            intent.setAction(YIMConstant.IntentAction.ACTION_NETWORK_CHANGED);
            sendBroadcast(intent);
        }
        @Override
        public void onUnavailable() {
            Intent intent = new Intent();
            intent.setPackage(getPackageName());
            intent.setAction(YIMConstant.IntentAction.ACTION_NETWORK_CHANGED);
            sendBroadcast(intent);
        }

    };

    Handler connectHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message message) {
            connect();
        }
    };

    Handler notificationHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message message) {
            stopForeground(true);
        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(getClass().getSimpleName(),getClass().getSimpleName(), NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this, channel.getId())
                    .setContentTitle("Push service")
                    .setContentText("Push service is running")
                    .build();
            startForeground(NOTIFICATION_ID,notification);
        }

        intent = (intent == null ? new Intent(YIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE) : intent);

        String action = intent.getAction();

        if (YIMPushManager.ACTION_CREATE_YIM_CONNECTION.equals(action)) {
            connect(intent.getLongExtra(KEY_DELAYED_TIME, 0));
        }

        if (YIMPushManager.ACTION_SEND_REQUEST_BODY.equals(action)) {
            manager.send((SentBody) intent.getSerializableExtra(YIMPushManager.KEY_SEND_BODY));
        }

        if (YIMPushManager.ACTION_CLOSE_YIM_CONNECTION.equals(action)) {
            manager.closeSession();
        }

        if (YIMPushManager.ACTION_ACTIVATE_PUSH_SERVICE.equals(action)) {
            handleKeepAlive();
        }

        if (YIMPushManager.ACTION_SET_LOGGER_EANABLE.equals(action)) {
            boolean enable = intent.getBooleanExtra(KEY_LOGGER_ENABLE, true);
            YIMLogger.getLogger().debugMode(enable);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationHandler.sendEmptyMessageDelayed(0, 1000);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void connect(long delayMillis) {

        if(delayMillis <= 0) {
            connect();
            return;
        }

        connectHandler.sendEmptyMessageDelayed(0, delayMillis);

    }

    private void connect() {

        if(YIMPushManager.isDestoryed(this) || YIMPushManager.isStoped(this)) {
            return;
        }

        String host = YIMCacheManager.getString(this, YIMCacheManager.KEY_YIM_SERVIER_HOST);
        int port = YIMCacheManager.getInt(this, YIMCacheManager.KEY_YIM_SERVIER_PORT);

        if(host == null || host.trim().length() == 0 || port <= 0) {
            Log.e(this.getClass().getSimpleName(), "Invalid hostname or port. host:" + host  + " port:" + port);
            return;
        }

        manager.connect(host, port);

    }

    private void handleKeepAlive() {

        if (manager.isConnected()) {
            YIMLogger.getLogger().connectState(true);
            return;
        }

        connect();

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.destroy();
        connectHandler.removeMessages(0);
        notificationHandler.removeMessages(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            unregisterReceiver(keepAliveReceiver);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    public class KeepAliveBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            handleKeepAlive();
        }

        public IntentFilter getIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            return intentFilter;
        }

    }
}
