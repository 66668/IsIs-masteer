package com.linzhi.isis.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.linzhi.isis.R;
import com.linzhi.isis.app.Constants;
import com.linzhi.isis.base.BaseActivity2;
import com.linzhi.isis.bean.BaseBean;
import com.linzhi.isis.bean.regist.AddNewEvent;
import com.linzhi.isis.bean.regist.AddnewBean;
import com.linzhi.isis.databinding.ActAddnewBinding;
import com.linzhi.isis.http.MyHttpService;
import com.linzhi.isis.http.cache.ACache;
import com.linzhi.isis.utils.ToastUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjy on 2017/5/26.
 */

public class AddNewActivity extends BaseActivity2<ActAddnewBinding> implements View.OnClickListener {
    private static final String TAG = "AddNewActivity";
    AddnewBean bean;
    AddNewEvent event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_addnew);
        showContentView();

        initData();
        initListener();
    }

    private void initListener() {
        bindingView.layout.tvRight.setOnClickListener(this);
        bindingView.layout.layoutBack.setOnClickListener(this);
    }

    //绑定数据
    private void initData() {
        bean = new AddnewBean();
        bindingView.setAddNewBean(bean);

        //绑定输入监听，获取bean值
        event = new AddNewEvent(bean);
        bindingView.setAddEvent(event);

    }

    //监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right://提交

                if (isEmpoty() == false) {
                    return;
                }
                commintData();
                break;
            case R.id.layout_back://返回
                this.finish();
                break;
        }
    }

    /**
     * 非空判断
     */
    private boolean isEmpoty() {

        if (TextUtils.isEmpty(bean.getEmployeeName())) {
            ToastUtils.ShortToast(this, "请输入姓名！");
            return false;
        }
        if (TextUtils.isEmpty(bean.getTelephone())) {
            ToastUtils.ShortToast(this, "请输入手机号！");
            return false;
        }
        //        if (TextUtils.isEmpty(bean.getEmployeeName())) {
        //            ToastUtils.ShortToast(this, "请输入姓名！");
        //            return;
        //        }
        //        if (TextUtils.isEmpty(bean.getEmployeeName())) {
        //            ToastUtils.ShortToast(this, "请输入姓名！");
        //            return;
        //        }
        return true;
    }

    /**
     * 提交
     */
    private void commintData() {
        //需要传值 storeId conferenceId
        ACache aCache = ACache.get(this);
        bean.setCompanyID(aCache.getAsString(Constants.STORE_ID));
        bean.setConferenceID(aCache.getAsString(Constants.CONFERENCE_ID));

        //
        String str = (new Gson()).toJson(bean).toString();
        Log.d(TAG, "commintData: json=" + str);
        Subscription subscription = MyHttpService.Builder.getHttpServer().AddnewEmployee(str)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e.toString());
                        ToastUtils.ShortToast(AddNewActivity.this, "出现异常，请排查！");
                    }

                    @Override
                    public void onNext(BaseBean<String> stringBaseBean) {
                        Log.d(TAG, "onNext: =" + stringBaseBean.getCode());

                        if (stringBaseBean.getCode().contains("1")) {
                            ToastUtils.ShortToast(AddNewActivity.this, "注册成功！");
                            AddNewActivity.this.finish();
                        }
                    }


                });
    }
}