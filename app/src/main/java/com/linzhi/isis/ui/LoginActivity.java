package com.linzhi.isis.ui;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.linzhi.isis.R;
import com.linzhi.isis.app.Constants;
import com.linzhi.isis.base.BaseActivity2;
import com.linzhi.isis.bean.login.LoginBtnListener;
import com.linzhi.isis.bean.login.LoginEvent;
import com.linzhi.isis.bean.login.User;
import com.linzhi.isis.databinding.ActLoginPasswordBinding;
import com.linzhi.isis.utils.SPUtils;


public class LoginActivity extends BaseActivity2<ActLoginPasswordBinding> {

    private static final String TAG = "LoginActivity";
    //变量
    //    ActbindingView bindingView;
    ProgressDialog progressDialog;
    LoginBtnListener btnListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_login_password);
        showContentView();
        //        bindingView = DataBindingUtil.setContentView(this, R.layout.act_login_password);

        //绑定相关类
        User user = new User();
        //判断是否有缓存值
        if (!TextUtils.isEmpty(SPUtils.getString(Constants.USRENAME))) {
            Log.d(TAG, "onCreate: " + SPUtils.getString(Constants.USRENAME));
            user.setUserName(SPUtils.getString(Constants.USRENAME));
        }
        if (!TextUtils.isEmpty(SPUtils.getString(Constants.PASSWORD))) {
            Log.d(TAG, "onCreate: " + SPUtils.getString(Constants.PASSWORD));
            user.setPassword(SPUtils.getString(Constants.PASSWORD));
        }
        bindingView.setUser(user);//缓存显示

        //更改监听
        LoginEvent event = new LoginEvent(user);
        bindingView.setLoginEvent(event);

        //
        progressDialog = new ProgressDialog(this);
        //登录响应在该类中完成
        btnListener = new LoginBtnListener(user, this, progressDialog);
        bindingView.setLoginBtnListener(btnListener);

    }
}
