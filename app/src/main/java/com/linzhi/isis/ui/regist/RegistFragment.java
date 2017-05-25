package com.linzhi.isis.ui.regist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.linzhi.isis.R;
import com.linzhi.isis.adapter.RegistAdapter;
import com.linzhi.isis.app.Constants;
import com.linzhi.isis.base.BaseFragment;
import com.linzhi.isis.base.baseadapter.OnItemClickListener;
import com.linzhi.isis.bean.regist.RegistBean;
import com.linzhi.isis.bean.regist.RegistDetailBean;
import com.linzhi.isis.databinding.FragmentRegistBinding;
import com.linzhi.isis.http.MyHttpService;
import com.linzhi.isis.http.cache.ACache;
import com.linzhi.isis.ui.MainActivity;
import com.linzhi.isis.utils.DebugUtil;
import com.linzhi.isis.utils.SPUtils;
import com.linzhi.isis.utils.TimeUtil;
import com.linzhi.isis.utils.ToastUtils;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjy on 2017/5/8.
 */

public class RegistFragment extends BaseFragment<FragmentRegistBinding> implements View.OnClickListener {
    private static final String TAG = "SJY";

    // 初始化完成后加载数据
    private boolean isPrepared = false;

    // 第一次显示时加载数据，第二次不显示
    private boolean isFirst = true;

    // 是否正在刷新（用于刷新数据时返回页面不再刷新）
    private boolean mIsLoading = false;

    private ACache aCache;
    private MainActivity activity;
    private RegistBean registBean;
    private RegistAdapter registAdapter;
    private String conferenceID;
    private String companyid;
    private RegistDetailBean bean;
    @Override
    public int setContent() {
        return R.layout.fragment_regist;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showContentView();

        aCache = ACache.get(getActivity());
        registAdapter = new RegistAdapter(activity);
        registBean = (RegistBean) aCache.getAsObject(Constants.REGIST_TAG);
        isPrepared = true;
        //
        initListener();
    }

    private void initRxBus() {
        Log.d(TAG, "registfragment11: ");

    }

    /**
     * 获取缓存数据，没有就加载
     */
    @Override
    protected void loadData() {
        DebugUtil.error("------RegistFragment---loadData: ");

        if (!isPrepared || !mIsVisible) {
            return;
        }

        // 获取one_data对应的value，没有默认为2016-11-26，即不是当天，则请求数据（正在请求时避免再次请求）
        String oneData = SPUtils.getString("one_data", "2016-11-26");

        if (!oneData.equals(TimeUtil.getData()) && !mIsLoading) {
            showLoading();
            /**延迟执行防止卡顿*/
            postDelayLoad();
        } else {
            // 为了正在刷新时不执行这部分
            if (mIsLoading) {
                return;
            }
            if (!isFirst) {
                return;
            }

            showLoading();

            if (registBean == null && !mIsLoading) {
                postDelayLoad();
            } else {
                bindingView.listOne.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            setAdapter(registBean);
                            showContentView();
                        }
                    }
                }, 150);
                DebugUtil.error("----缓存: " + oneData);
            }
        }
    }

    /**
     * 延迟执行，避免卡顿
     * 加同步锁，避免重复加载
     */
    private void postDelayLoad() {
        synchronized (this) {
            if (!mIsLoading) {
                mIsLoading = true;
                bindingView.listOne.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadRegistData();
                    }
                }, 150);
            }
        }
    }


    //
    private void loadRegistData() {
        if (aCache == null) {
            aCache = ACache.get(getActivity());
        }
        if (TextUtils.isEmpty(companyid)) {
            companyid = aCache.getAsString(Constants.STORE_ID);
        }

        if (TextUtils.isEmpty(conferenceID)) {
            conferenceID = aCache.getAsString(Constants.CONFERENCE_ID);
        }

        /**
         * 后台线程取数据，主线程显示』的程序策略
         */

        Subscription subscription = MyHttpService.Builder.getRegistService()
                .GetSearchRegistList(companyid, conferenceID, "", "", "")//创建了被观察者Observable<>
                .subscribeOn(Schedulers.io())//事件产生的线程,无数量上限的线程池的调度器,比Schedulers.newThread()更效率
                .observeOn(AndroidSchedulers.mainThread())//消费的线程,指定的操作将在 Android 主线程运行
                .subscribe(new Observer<RegistBean>() {//订阅观察者

                    //不会再有新的 onNext() 发出时，需要触发
                    @Override
                    public void onCompleted() {
                        showContentView();
                    }

                    //异常
                    @Override
                    public void onError(Throwable e) {
                        showContentView();
                        if (registAdapter != null && registAdapter.getItemCount() == 0) {
                            showError();
                        }
                    }

                    //
                    @Override
                    public void onNext(RegistBean registBean) {
                        if (registBean != null) {
                            aCache.remove(Constants.REGIST_TAG);
                            // 保存12个小时
                            aCache.put(Constants.REGIST_TAG, registBean, 43200);
                            setAdapter(registBean);
                            // 保存请求的日期
                            SPUtils.putString("one_data", TimeUtil.getData());
                            // 刷新结束-
                            mIsLoading = false;
                        }

                        //构造器中，第一个参数表示列数或者行数，第二个参数表示滑动方向,瀑布流
                        //                        bindingContentView.listOne.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL));
                        // GridView
                        //                        bindingContentView.listOne.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                    }
                });
        addSubscription(subscription);//保存订阅

    }

    private void setAdapter(RegistBean registBean) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        bindingView.listOne.setLayoutManager(mLayoutManager);

        // 加上这两行代码，下拉出提示才不会产生出现刷新头的bug，不加拉不下来
        bindingView.listOne.setPullRefreshEnabled(false);
        bindingView.listOne.clearHeader();

        bindingView.listOne.setLoadingMoreEnabled(false);
        // 需加，不然滑动不流畅
        bindingView.listOne.setNestedScrollingEnabled(false);
        bindingView.listOne.setHasFixedSize(false);

        registAdapter.clear();
        registAdapter.addAll(registBean.getResult());
        registAdapter.setOnItemClickListener(listener);//添加监听
        bindingView.listOne.setAdapter(registAdapter);
        registAdapter.notifyDataSetChanged();

        isFirst = false;
    }

    @Override
    protected void onRefresh() {
        loadRegistData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy(); //取消订阅，释放内存
        DebugUtil.error("--RegistFragment   ----onDestroy");
    }

    /**
     * 从此页面新开activity界面返回此页面 走这里
     */
    @Override
    public void onResume() {
        super.onResume();
        DebugUtil.error("--RegistFragment   ----onResume");
    }

    private void initListener() {
        bindingView.closeQrcode.setOnClickListener(this);
        bindingView.btnLog.setOnClickListener(this);
        bindingView.btnTosend.setOnClickListener(this);
        bindingView.itemQrcode.setOnClickListener(this);
    }

    //监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_qrcode://关闭二维码
                bindingView.layoutQRcode.setVisibility(View.GONE);//二维码布局消失
                bindingView.scrollViewDetail.setVisibility(View.VISIBLE);//详细界面显示
                break;
            case R.id.btn_log:
                ToastUtils.ShortToast(activity, "打印");
                break;
            case R.id.btn_tosend:
                ToastUtils.ShortToast(activity, "发短信");
                break;
            case R.id.item_qrcode:
                bindingView.layoutQRcode.setVisibility(View.VISIBLE);//二维码布局显示
                bindingView.scrollViewDetail.setVisibility(View.GONE);//详细界面消失
                break;
        }

    }

    //自定义recyclerView的监听
    OnItemClickListener<RegistDetailBean> listener = new OnItemClickListener<RegistDetailBean>() {
        @Override
        public void onClick(RegistDetailBean registDetailBean, int position) {

            bindingView.layoutQRcode.setVisibility(View.GONE);//二维码布局消失
            bindingView.scrollViewDetail.setVisibility(View.VISIBLE);//详细界面显示
            bean = registDetailBean;
            //数据绑定
            bindingView.setDetailBean(bean);
        }
    };


}





























