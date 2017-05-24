package com.linzhi.isis.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.linzhi.isis.R;
import com.linzhi.isis.databinding.ActWelcomeBinding;
import com.linzhi.isis.utils.ResourceUtils;

/**
 * 启动页 欢迎页
 * Created by sjy on 2017/4/18.
 */

public class WelcomeActivity extends AppCompatActivity {

    private ActWelcomeBinding mBinding;
    private boolean isIn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.act_welcome);
        //显示默认图
        mBinding.activityTransition.setBackground(ResourceUtils.getDrawable(R.mipmap.welcome_bg));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toLoginAct();
            }
        },800);
    }
    private void toLoginAct() {
        if (isIn) {
            return;
        }
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        //自定义 两个 Activity 切换时的动画
        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
        finish();
        isIn = true;

    }
}
