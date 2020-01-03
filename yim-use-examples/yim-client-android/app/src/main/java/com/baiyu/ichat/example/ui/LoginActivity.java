package com.baiyu.ichat.example.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.baiyu.ichat.example.R;
import com.baiyu.ichat.example.app.Constant;
import com.baiyu.ichat.example.app.YIMMonitorActivity;
import com.baiyu.yim.sdk.android.YIMPushManager;
import com.baiyu.yim.sdk.android.constant.YIMConstant;
import com.baiyu.yim.sdk.android.model.ReplyBody;

/**
 * @author baiyu
 * @data 2020-01-03 9:41
 */
public class LoginActivity extends YIMMonitorActivity implements View.OnClickListener {

    EditText accountEdit;
    Button loginButton;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
    }

    private void initViews() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在登录，请稍候......");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        accountEdit = (EditText) this.findViewById(R.id.account);
        loginButton = (Button) this.findViewById(R.id.login);
        loginButton.setOnClickListener(this);

    }

    private void doLogin() {

        if (!"".equals(accountEdit.getText().toString().trim())) {
            progressDialog.show();
            if (YIMPushManager.isConnected(this)) {
                YIMPushManager.bindAccount(this, accountEdit.getText().toString().trim());
            } else {
                YIMPushManager.connect(this, Constant.YIM_SERVER_HOST, Constant.YIM_SERVER_PORT);
            }

        }
    }

    @Override
    public void onConnectionSuccessed(boolean autoBind) {
        if (!autoBind)
            YIMPushManager.bindAccount(this, accountEdit.getText().toString().trim());
    }


    @Override
    public void onReplyReceived(final ReplyBody reply) {
        progressDialog.dismiss();
        /*
         * 收到code为200的回应 账号绑定成功
         */
        if (reply.getKey().equals(YIMConstant.RequestKey.CLIENT_BIND) && reply.getCode().equals(YIMConstant.ReturnCode.CODE_200)) {
            Intent intent = new Intent(this, SystemMessageActivity.class);
            intent.putExtra("account", accountEdit.getText().toString().trim());
            startActivity(intent);
            this.finish();
        }
    }


    @Override
    public void onClick(View v) {
        doLogin();
    }

    @Override
    public void onBackPressed() {
        YIMPushManager.destroy(this);
        super.onBackPressed();
    }

}
