package com.linzhi.isis.ui;


import android.app.ProgressDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.linzhi.isis.R;
import com.linzhi.isis.bean.login.LoginBtnListener;
import com.linzhi.isis.bean.login.LoginEvent;
import com.linzhi.isis.bean.login.User;
import com.linzhi.isis.databinding.ActLoginPasswordBinding;


public class LoginActivity extends AppCompatActivity {

    //变量
    ActLoginPasswordBinding loginPasswordBinding;
    ProgressDialog progressDialog;
    LoginBtnListener btnListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        loginPasswordBinding = DataBindingUtil.setContentView(this, R.layout.act_login_password);
        //绑定相关类
        User user = new User();
        LoginEvent event = new LoginEvent(user);
        loginPasswordBinding.setLoginEvent(event);

        //
        progressDialog = new ProgressDialog(this);
        //登录响应在该类中完成
        btnListener = new LoginBtnListener(user, this, progressDialog);
        loginPasswordBinding.setLoginBtnListener(btnListener);

    }
}
