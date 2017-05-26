package com.linzhi.isis.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.LinearLayout;

import com.linzhi.isis.R;
import com.linzhi.isis.adapter.ConferencesAdapter;
import com.linzhi.isis.app.Constants;
import com.linzhi.isis.base.BaseActivity2;
import com.linzhi.isis.bean.conference.ConferenceBean;
import com.linzhi.isis.databinding.ActConferenceBinding;
import com.linzhi.isis.http.MyHttpService;
import com.linzhi.isis.http.cache.ACache;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 会议选择 选中一个会议，跳转主界面
 */

public class ConferenceActivity extends BaseActivity2<ActConferenceBinding> {
    //
    private static final String TAG = "ConferenceActivity";

    //
    LinearLayout scrl_Conference;
    RecyclerView xrv_conference;

    ActConferenceBinding actConferenceBinding;
    private GridLayoutManager mLayoutManager;
    private ConferencesAdapter conferenceAdapter;
    private ACache aCache;
    // 是否正在刷新（用于刷新数据时返回页面不再刷新）
    private boolean mIsLoading = false;
    private boolean mIsPrepared;
    private boolean mIsFirst = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//全屏

        setContentView(R.layout.act_conference);//由于继承的BaseActivity有xml绑定databinding的操作，就不用再多写绑定了
        showContentView();
        initID();//初始化控件
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);//设置四列
        xrv_conference.setLayoutManager(mLayoutManager);//GridLayoutManager关联recycleView
        /**
         * 因为启动时先走loadData()再走onActivityCreated，
         * 所以此处要额外调用load(),不然最初不会加载内容
         */
        loadData();
    }

    private void initID() {
        // 准备就绪
        mIsPrepared = true;
        scrl_Conference = bindingView.scrlConference;
        xrv_conference = bindingView.xrvConference;
    }

    private void loadData() {

        if (!mIsPrepared || !mIsFirst) {
            return;
        }

        scrl_Conference.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: onCreate--loadData-loadConferenceData");
                loadConferenceData();
            }
        }, 500);
    }

    private void loadConferenceData() {
        //获取缓存
        if (aCache == null) {
            aCache = ACache.get(ConferenceActivity.this);
        }

        Subscription get = MyHttpService.Builder.getConferenceService()
                .getConferenceList(aCache.getAsString(Constants.STORE_ID))//"fa3df134-7fc7-4623-879e-24165d286568"
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ConferenceBean>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                        showContentView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError:--- " + e.getMessage());
                        showContentView();
                    }

                    @Override
                    public void onNext(ConferenceBean conferenceBean) {
                        if (conferenceBean.getCode().equals("1")) {

                            //保存所有json信息 保存12个小时
                            aCache.put(Constants.CONFERENCE_LIST, conferenceBean, 43200);
                            //设置显示
                            setAdapter(conferenceBean);
                            // 刷新结束
                            mIsLoading = false;
                            mIsFirst = false;
                        }

                    }
                });
        //
        addSubscription(get);
    }

    //设置显示adapter
    private void setAdapter(ConferenceBean bean) {
        if (bean != null && bean.getResult() != null && bean.getResult().size() > 0) {
            if (conferenceAdapter == null) {
                conferenceAdapter = new ConferencesAdapter(ConferenceActivity.this);
            }
            conferenceAdapter.addAll(bean.getResult());
            conferenceAdapter.notifyDataSetChanged();
            xrv_conference.setAdapter(conferenceAdapter);

        }
        //        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 不退出程序，进入后台
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
