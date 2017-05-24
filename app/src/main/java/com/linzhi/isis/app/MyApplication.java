package com.linzhi.isis.app;

import android.app.Application;

import com.linzhi.isis.utils.DebugUtil;
import com.linzhi.isis.http.HttpUtils;

/**
 * Created by sjy on 2017/4/18.
 */

public class MyApplication extends Application {

    private static MyApplication MyApplication;

    public static MyApplication getInstance() {
        return MyApplication;
    }

    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication = this;
        //正式打包，将DebugUtil.DEBUG值设为 false
        HttpUtils.getInstance().init(this, DebugUtil.DEBUG);
    }

}


