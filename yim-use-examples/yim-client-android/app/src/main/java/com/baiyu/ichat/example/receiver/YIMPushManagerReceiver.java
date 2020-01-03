package com.baiyu.ichat.example.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import com.baiyu.ichat.example.R;
import com.baiyu.yim.sdk.android.YIMEventBroadcastReceiver;
import com.baiyu.yim.sdk.android.YIMListenerManager;
import com.baiyu.yim.sdk.android.model.Message;
import com.baiyu.yim.sdk.android.model.ReplyBody;

/**
 * 消息入口，所有消息都会经过这里
 * @author baiyu
 * @data 2020-01-03 9:40
 */
public final class YIMPushManagerReceiver extends YIMEventBroadcastReceiver {

    //当收到消息时，会执行onMessageReceived，这里是消息第一入口

    @Override
    public void onMessageReceived(Message message, Intent intent) {

        //调用分发消息监听
        YIMListenerManager.notifyOnMessageReceived(message);

        //以开头的为动作消息，无须显示,如被强行下线消息Constant.ACTION_999
        if (message.getAction().startsWith("9")) {
            return;
        }

        showNotify(context, message);
    }


    private void showNotify(Context context, Message msg) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channelId = "system";
            NotificationChannel channel = new NotificationChannel(channelId, "message", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点   
            notificationManager.createNotificationChannel(channel);
        }

        String title = "系统消息";
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, new Intent(context, SystemMessageActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setWhen(msg.getTimestamp());
        builder.setSmallIcon(R.drawable.icon);
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(msg.getContent());
        builder.setDefaults(Notification.DEFAULT_LIGHTS);
        builder.setContentIntent(contentIntent);
        final Notification notification = builder.build();


        notificationManager.notify(R.drawable.icon, notification);

    }


    @Override
    public void onNetworkChanged(NetworkInfo info) {
        YIMListenerManager.notifyOnNetworkChanged(info);
    }


    @Override
    public void onConnectionSuccessed(boolean hasAutoBind) {
        YIMListenerManager.notifyOnConnectionSuccessed(hasAutoBind);
    }

    @Override
    public void onConnectionClosed() {
        YIMListenerManager.notifyOnConnectionClosed();
    }


    @Override
    public void onReplyReceived(ReplyBody body) {
        YIMListenerManager.notifyOnReplyReceived(body);
    }


    @Override
    public void onConnectionFailed() {
        // TODO Auto-generated method stub
        YIMListenerManager.notifyOnConnectionFailed();
    }
}
