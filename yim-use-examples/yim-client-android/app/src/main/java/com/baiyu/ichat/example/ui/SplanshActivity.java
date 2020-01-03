package com.baiyu.ichat.example.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;
import com.baiyu.ichat.example.R;
import com.baiyu.ichat.example.app.Constant;
import com.baiyu.ichat.example.app.YIMMonitorActivity;
import com.baiyu.yim.sdk.android.YIMPushManager;

/**
 * @author baiyu
 * @data 2020-01-03 9:43
 */
public class SplanshActivity extends YIMMonitorActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        YIMPushManager.setLoggerEnable(this, BuildConfig.DEBUG);
        //连接服务端

        YIMPushManager.connect(SplanshActivity.this, Constant.YIM_SERVER_HOST, Constant.YIM_SERVER_PORT);


        final View view = View.inflate(this, R.layout.activity_splansh, null);
        setContentView(view);
        AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);


    }

    @Override
    public void onConnectionSuccessed(boolean autoBind) {

        Intent intent = new Intent(SplanshActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        finish();
        YIMPushManager.destroy(this);
    }

    @Override
    public void onConnectionFailed() {
        Toast.makeText(this,"连接服务器失败，请检查当前设备是否能连接上服务器IP和端口",Toast.LENGTH_LONG).show();
    }

}
