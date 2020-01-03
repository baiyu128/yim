package com.baiyu.ichat.example.ui;

import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.baiyu.ichat.example.R;
import com.baiyu.ichat.example.adapter.SystemMsgListViewAdapter;
import com.baiyu.ichat.example.app.Constant;
import com.baiyu.ichat.example.app.YIMMonitorActivity;
import com.baiyu.yim.sdk.android.YIMPushManager;
import com.baiyu.yim.sdk.android.model.Message;

import java.util.ArrayList;

/**
 * @author baiyu
 * @data 2020-01-03 9:44
 */
public class SystemMessageActivity extends YIMMonitorActivity implements View.OnClickListener {

    protected ListView chatListView;
    protected SystemMsgListViewAdapter adapter;
    private ArrayList<Message> list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_chat);
        initViews();
    }

    public void initViews() {

        list = new ArrayList<Message>();

        chatListView = (ListView) findViewById(R.id.chat_list);
        findViewById(R.id.TOP_BACK_BUTTON).setOnClickListener(this);
        findViewById(R.id.TOP_BACK_BUTTON).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.TOP_BACK_BUTTON)).setText("登录");
        ((TextView) findViewById(R.id.TITLE_TEXT)).setText("系统消息");
        ((TextView) findViewById(R.id.account)).setText(this.getIntent().getStringExtra("account"));

        adapter = new SystemMsgListViewAdapter(this, list);
        chatListView.setAdapter(adapter);

        Toast.makeText(this,"登录成功，请通过后台页面发送消息吧^_^",Toast.LENGTH_LONG).show();

    }

    //收到消息
    @Override
    public void onMessageReceived(Message message) {

        if (message.getAction().equals(Constant.MessageAction.ACTION_999)) {
            //返回登录页面，停止接受消息
            YIMPushManager.stop(this);

            Toast.makeText(this,"你被系统强制下线!",Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            this.finish();
        } else {
            list.add(message);
            adapter.notifyDataSetChanged();
            chatListView.setSelection(chatListView.getTop());
        }

    }

    @Override
    public void onNetworkChanged(NetworkInfo info) {

        if (info == null) {
            Toast.makeText(this,"网络已断开!",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"网络已恢复，重新连接....",Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        //返回登录页面，停止接受消息
        YIMPushManager.stop(this);
        startActivity(new Intent(this, LoginActivity.class));
        super.onBackPressed();
    }
}
