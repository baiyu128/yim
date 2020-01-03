package com.baiyu.ichat.example.app;

import android.app.Activity;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.baiyu.yim.sdk.android.YIMEventListener;
import com.baiyu.yim.sdk.android.YIMListenerManager;
import com.baiyu.yim.sdk.android.model.Message;
import com.baiyu.yim.sdk.android.model.ReplyBody;
import com.baiyu.yim.sdk.android.model.SentBody;

/**
 * @author baiyu
 * @data 2020-01-03 9:38
 */
public class YIMMonitorActivity extends Activity implements YIMEventListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YIMListenerManager.registerMessageListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        YIMListenerManager.removeMessageListener(this);

    }

    @Override
    public void onRestart() {
        super.onRestart();
        YIMListenerManager.registerMessageListener(this);
    }

    @Override
    public void onMessageReceived(Message arg0) {
    }

    @Override
    public void onNetworkChanged(NetworkInfo info) {
    }

    /**
     * 与服务端断开连接时回调,不要在里面做连接服务端的操作
     */
    @Override
    public void onConnectionClosed() {
    }

    @Override
    public void onConnectionFailed() {

    }

    @Override
    public int getEventDispatchOrder() {
        return 0;
    }

    /**
     * 连接服务端成功时回调
     */

    @Override
    public void onConnectionSuccessed(boolean arg0) {
    }


    @Override
    public void onReplyReceived(ReplyBody arg0) {
    }

    @Override
    public void onSentSuccessed(SentBody sentBody) {

    }
}
