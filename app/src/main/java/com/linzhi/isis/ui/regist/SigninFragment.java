package com.linzhi.isis.ui.regist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import com.linzhi.isis.R;
import com.linzhi.isis.adapter.SigninAdapter;
import com.linzhi.isis.app.Constants;
import com.linzhi.isis.base.BaseFragment;
import com.linzhi.isis.base.baseadapter.OnItemClickListener;
import com.linzhi.isis.bean.BaseBean;
import com.linzhi.isis.bean.signin.SigninBeans;
import com.linzhi.isis.bean.signin.SigninDetailBean;
import com.linzhi.isis.databinding.FragmentSigninBinding;
import com.linzhi.isis.http.MyHttpService;
import com.linzhi.isis.http.cache.ACache;
import com.linzhi.isis.qrcode.encoding.EncodingHandler;
import com.linzhi.isis.ui.MainActivity;
import com.linzhi.isis.ui.QrcodeCaptureActivity;
import com.linzhi.isis.utils.DebugUtil;
import com.linzhi.isis.utils.DpUtils;
import com.linzhi.isis.utils.SPUtils;
import com.linzhi.isis.utils.TimeUtil;
import com.linzhi.isis.utils.ToastUtils;
import com.linzhi.isis.view.FloatActionButton;

import java.io.ByteArrayOutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 签到
 */

public class SigninFragment extends BaseFragment<FragmentSigninBinding> implements View.OnClickListener {
    private static final String TAG = "SigninFragment";
    private final static int SCANNIN_GREQUEST_CODE = 1;
    // 初始化完成后加载数据
    private boolean isPrepared = false;

    // 第一次显示时加载数据，第二次不显示
    private boolean isFirst = true;

    // 是否正在刷新（用于刷新数据时返回页面不再刷新）
    private boolean mIsLoading = false;

    private ACache aCache;
    private MainActivity activity;
    private SigninBeans registBean;
    private SigninAdapter signinAdapter;
    private String conferenceID;
    private String companyid;

    //二维码 参数
    private SigninDetailBean bean;
    private ImageView qrcodeImg;
    private Bitmap qrcodeBitmap;
    private String employeeid;
    private FloatActionButton floatActionButton;//小图标

    @Override
    public int setContent() {
        return R.layout.fragment_signin;
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
        registBean = (SigninBeans) aCache.getAsObject(Constants.SIGIN_TAG);
        isPrepared = true;

        initListener();
        loadSignData();
    }


    /**
     * 获取缓存数据，没有就加载
     */

    @Override
    protected void loadData() {

        //        if (!isPrepared || !mIsVisible) {
        //            return;
        //        }
        //
        //        // 获取one_data对应的value，没有默认为2016-11-26，即不是当天，则请求数据（正在请求时避免再次请求）
        //        String oneData = SPUtils.getString(Constants.ACACHE_DATA_SIGN, "2016-11-26");
        //
        //        if (!oneData.equals(TimeUtil.getData()) && !mIsLoading) {
        //            showLoading();
        //            /**延迟执行防止卡顿*/
        //            postDelayLoad();
        //        } else {
        //            // 为了正在刷新时不执行这部分
        //            if (mIsLoading) {
        //                return;
        //            }
        //            if (!isFirst) {
        //                return;
        //            }
        //
        //            showLoading();
        //
        //            if (registBean == null && !mIsLoading) {
        //                postDelayLoad();
        //            } else {
        //                bindingView.listOne.postDelayed(new Runnable() {
        //                    @Override
        //                    public void run() {
        //                        synchronized (this) {
        //                            setAdapter(registBean);
        //                            showContentView();
        //                        }
        //                    }
        //                }, 150);
        //                DebugUtil.error("----缓存: " + oneData);
        //            }
        //        }
        //        postDelayLoad();//databinding.FragmentSigninBinding.listOne' on a null object reference

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
                        loadSignData();
                    }
                }, 150);
            }
        }
    }

    private void setAdapter(SigninBeans signinBeans) {
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

        signinAdapter.clear();
        signinAdapter.addAll(signinBeans.getResult());
        signinAdapter.setOnItemClickListener(listener);//添加监听
        bindingView.listOne.setAdapter(signinAdapter);
        signinAdapter.notifyDataSetChanged();

        isFirst = false;
    }


    //
    private void loadSignData() {
        if (aCache == null) {
            aCache = ACache.get(activity);
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

        Subscription subscription = MyHttpService.Builder.getHttpServer()
                .GetSearchSigninList(companyid, conferenceID, "")//创建了被观察者Observable<>
                .subscribeOn(Schedulers.io())//事件产生的线程,无数量上限的线程池的调度器,比Schedulers.newThread()更效率
                .observeOn(AndroidSchedulers.mainThread())//消费的线程,指定的操作将在 Android 主线程运行
                .subscribe(new Observer<SigninBeans>() {//订阅观察者

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
                    public void onNext(SigninBeans registBean) {
                        if (registBean != null) {
                            aCache.remove(Constants.SIGIN_TAG);
                            // 保存12个小时
                            aCache.put(Constants.SIGIN_TAG, registBean, 43200);
                            setAdapter(registBean);
                            // 保存请求的日期
                            SPUtils.putString(Constants.ACACHE_DATA_SIGN, TimeUtil.getData());
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
        loadSignData();
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
        bindingView.btnTosend.setOnClickListener(this);
        bindingView.itemQrcode.setOnClickListener(this);
        bindingView.floatActionButton.setOnClickListener(this);

    }

    //监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_qrcode://关闭二维码
                bindingView.layoutQRcode.setVisibility(View.GONE);//二维码布局消失
                bindingView.scrollViewDetail.setVisibility(View.VISIBLE);//详细界面显示
                break;
            case R.id.btn_tosend:
                //获取参数
                RequestBody requestPhone = RequestBody.create(MediaType.parse("multipart/form-data"), bean.getTelephone());
                RequestBody requestCode = RequestBody.create(MediaType.parse("multipart/form-data"), bean.getEmployeeID());

                //bitmap转成二进制流

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                qrcodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // 创建MultipartBody.Part，用于封装文件数据
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), byteArray);//content-type为image/png
                MultipartBody.Part requestImgPart = MultipartBody.Part.createFormData("interactionFile", "qrcode.png", requestBody);

                MyHttpService.Builder.getHttpServer()
                        .sendMsg(requestPhone, requestCode, requestImgPart)//创建了被观察者Observable<>
                        .subscribeOn(Schedulers.io())//事件产生的线程,无数量上限的线程池的调度器,比Schedulers.newThread()更效率
                        .observeOn(AndroidSchedulers.mainThread())//消费的线程,指定的操作将在 Android 主线程运行
                        .subscribe(new Observer<BaseBean>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(BaseBean baseBean) {
                                if (baseBean.getCode().contains("1")) {
                                    Log.d(TAG, "onNext: 发送短信成功");
                                    ToastUtils.ShortToast(getActivity(), "发送短信成功");
                                } else {
                                    Log.d(TAG, "onNext: 发送短信失败");
                                    ToastUtils.ShortToast(getActivity(), "发送短信失败");
                                }
                            }
                        });

                break;

            case R.id.item_qrcode://查看二维码
                //用registDetailBean的companyid生成二维码
                if (bean != null) {
                    employeeid = bean.getEmployeeID();
                } else {
                    return;//防止误操作
                }

                if (!employeeid.equals("")) {
                    qrcodeImg = bindingView.imgQrcode;
                    //调用二维码代码
                    try {
                        qrcodeBitmap = EncodingHandler.createQRCode(employeeid, DpUtils.dp2px(activity, 300));//设置225dp

                    } catch (WriterException e) {
                        e.printStackTrace();
                        Log.d(TAG, "onClick: WriterException=" + e.getMessage());
                    }
                    //

                    qrcodeImg.setImageBitmap(qrcodeBitmap);

                    //切换布局
                    bindingView.layoutQRcode.setVisibility(View.VISIBLE);//二维码布局显示
                    bindingView.scrollViewDetail.setVisibility(View.GONE);//详细界面消失

                } else {
                    ToastUtils.ShortToast(activity, "没有获取字符串，请切换用户试一试");
                }
                break;

            case R.id.floatActionButton://二维码签到
                Intent intent = new Intent();
                intent.setClass(getActivity(), QrcodeCaptureActivity.class);//QrcodeCaptureActivity.class CodeInputActivity.class
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);

                break;
        }

    }

    //自定义recyclerView的监听
    OnItemClickListener<SigninDetailBean> listener = new OnItemClickListener<SigninDetailBean>() {
        @Override
        public void onClick(SigninDetailBean registDetailBean, int position) {

            bindingView.layoutQRcode.setVisibility(View.GONE);//二维码布局消失
            bindingView.scrollViewDetail.setVisibility(View.VISIBLE);//详细界面显示
            bean = registDetailBean;
            //数据绑定
            bindingView.setDetailBean(bean);
        }
    };
}

