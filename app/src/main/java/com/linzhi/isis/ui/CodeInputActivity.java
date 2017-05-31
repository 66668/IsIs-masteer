package com.linzhi.isis.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.linzhi.isis.R;
import com.linzhi.isis.bean.BaseBean;
import com.linzhi.isis.databinding.ActCodeInputBinding;
import com.linzhi.isis.http.MyHttpService;
import com.linzhi.isis.utils.ToastUtils;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjy on 2017/5/31.
 */

public class CodeInputActivity extends Activity {
    private static final String TAG = "CodeInputActivity";
    ActCodeInputBinding bindView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindView = DataBindingUtil.setContentView(CodeInputActivity.this, R.layout.act_code_input);
        initListener();

    }

    private void initListener() {
        //二维码签到
        bindView.tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CodeInputActivity.this, QrcodeCaptureActivity.class);
                startActivity(intent);
                CodeInputActivity.this.finish();
            }
        });

        //返回
        bindView.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodeInputActivity.this.finish();
            }
        });

        //签到
        bindView.btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = bindView.etInputCode.getText().toString();
                MyHttpService.Builder.getHttpServer()
                        .getQecode(code)//创建了被观察者Observable<>
                        .subscribeOn(Schedulers.io())//事件产生的线程,无数量上限的线程池的调度器,比Schedulers.newThread()更效率
                        .observeOn(AndroidSchedulers.mainThread())//消费的线程,指定的操作将在 Android 主线程运行
                        .subscribe(new Observer<BaseBean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: " + e.toString());
                                ToastUtils.ShortToast(CodeInputActivity.this, e.toString());
                            }

                            @Override
                            public void onNext(BaseBean baseBean) {
                                if (baseBean.getCode().contains("1")) {
                                    ToastUtils.ShortToast(CodeInputActivity.this, baseBean.getMessage());
                                    //                                QrcodeCaptureActivity.this.finish();
                                } else {
                                    ToastUtils.ShortToast(CodeInputActivity.this, baseBean.getMessage());
                                    //                                QrcodeCaptureActivity.this.finish();
                                }
                            }
                        });

            }
        });
    }
}
