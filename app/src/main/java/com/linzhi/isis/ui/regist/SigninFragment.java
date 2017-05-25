package com.linzhi.isis.ui.regist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import com.linzhi.isis.R;
import com.linzhi.isis.adapter.SigninAdapter;
import com.linzhi.isis.app.Constants;
import com.linzhi.isis.base.BaseFragment;
import com.linzhi.isis.bean.signin.SigninBean;
import com.linzhi.isis.databinding.FragmentSignBinding;
import com.linzhi.isis.http.MyHttpService;
import com.linzhi.isis.http.cache.ACache;
import com.linzhi.isis.ui.MainActivity;
import com.linzhi.isis.utils.DebugUtil;
import com.linzhi.isis.utils.SPUtils;
import com.linzhi.isis.utils.TimeUtil;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sjy on 2017/5/8.
 */

public class SigninFragment extends BaseFragment<FragmentSignBinding> {
    private static final String TAG = "SJY";

    // 初始化完成后加载数据
    private boolean isPrepared = false;

    // 第一次显示时加载数据，第二次不显示
    private boolean isFirst = true;

    // 是否正在刷新（用于刷新数据时返回页面不再刷新）
    private boolean mIsLoading = false;

    private ACache aCache;
    private MainActivity activity;
    private SigninBean signinBean;
    private SigninAdapter signinAdapter;
    private String conferenceID;
    private String companyid;


    /**
     * 设置布局
     *
     * @return
     */
    @Override
    public int setContent() {
        return R.layout.fragment_sign;
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
        signinAdapter = new SigninAdapter(activity);
        signinBean = (SigninBean) aCache.getAsObject(Constants.GIGNIN_FRAGMENT_TAG);
        isPrepared = true;

    }


    /**
     * 获取缓存数据，没有就加载
     */

    @Override
    protected void loadData() {

        if (!isPrepared || !mIsVisible) {
            return;
        }

        // 获取one_data对应的value，没有默认为2016-11-26，即不是当天，则请求数据（正在请求时避免再次请求）
        String oneData = SPUtils.getString("one_data", "2016-11-25");

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

            if (signinBean == null && !mIsLoading) {
                postDelayLoad();
            } else {
                bindingView.listSign.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this) {
                            setAdapter(signinBean);
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
                bindingView.listSign.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadRegistData();
                    }
                }, 150);
            }
        }
    }

    private void setAdapter(SigninBean signinBean) {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        bindingView.listSign.setLayoutManager(mLayoutManager);

        // 加上这两行代码，下拉出提示才不会产生出现刷新头的bug，不加拉不下来
        bindingView.listSign.setPullRefreshEnabled(false);
        bindingView.listSign.clearHeader();

        bindingView.listSign.setLoadingMoreEnabled(false);
        // 需加，不然滑动不流畅
        bindingView.listSign.setNestedScrollingEnabled(false);
        bindingView.listSign.setHasFixedSize(false);

        signinAdapter.clear();
        signinAdapter.addAll(signinBean.getResult());
        bindingView.listSign.setAdapter(signinAdapter);
        signinAdapter.notifyDataSetChanged();

        isFirst = false;
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
                .GetSearchSigninList(companyid, conferenceID, "", "", "")//创建了被观察者Observable<>
                .subscribeOn(Schedulers.io())//事件产生的线程,无数量上限的线程池的调度器,比Schedulers.newThread()更效率
                .observeOn(AndroidSchedulers.mainThread())//消费的线程,指定的操作将在 Android 主线程运行
                .subscribe(new Observer<SigninBean>() {//订阅观察者

                    //不会再有新的 onNext() 发出时，需要触发
                    @Override
                    public void onCompleted() {
                        showContentView();
                    }

                    //异常
                    @Override
                    public void onError(Throwable e) {
                        showContentView();
                        if (signinAdapter != null && signinAdapter.getItemCount() == 0) {
                            showError();
                        }
                    }

                    //
                    @Override
                    public void onNext(SigninBean signinBean) {
                        if (signinBean != null) {
                            aCache.remove(Constants.GIGNIN_FRAGMENT_TAG);
                            // 保存12个小时
                            aCache.put(Constants.GIGNIN_FRAGMENT_TAG, signinBean, 43200);
                            setAdapter(signinBean);
                            // 保存请求的日期
                            SPUtils.putString("one_data", TimeUtil.getData());
                            // 刷新结束
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
}





























