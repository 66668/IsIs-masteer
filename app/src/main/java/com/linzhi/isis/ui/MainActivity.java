package com.linzhi.isis.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linzhi.isis.R;
import com.linzhi.isis.app.Constants;
import com.linzhi.isis.app.rx.RxBus;
import com.linzhi.isis.base.BaseActivity2;
import com.linzhi.isis.databinding.ActivityMainBinding;
import com.linzhi.isis.http.cache.ACache;
import com.linzhi.isis.ui.regist.RegistFragment;
import com.linzhi.isis.ui.regist.SigninFragment;
import com.linzhi.isis.utils.ToastUtils;
import com.linzhi.isis.view.MyFragmentPagerAdapter;

import java.util.ArrayList;

import rx.functions.Action1;

public class MainActivity extends BaseActivity2<ActivityMainBinding> implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String TAG = "SJY";
    ArrayList<Fragment> mFragmentList;
    private ViewPager vpContent;
    private Toolbar toolbar;
    private RelativeLayout relativeLayout;
    ACache aCache;
    // 一定需要对应的bean
    //    private ActivityMainBinding bindingView;
    private TextView titlebarReg;
    private TextView titlebarSign;
    private ImageView main_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showContentView();
        //        bindingView = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initView();

        initContentFragment();
        initListener();

        RxBus.getInstance().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                Log.d(TAG, "call: main");
            }
        });
    }

    //设置状态栏
    //    private void initStatusView() {
    //        ViewGroup.LayoutParams layoutParams = bindingView.viewStatus.getLayoutParams();
    //        layoutParams.height = StatusBarUtil.getStatusBarHeight(this);
    //        bindingView.viewStatus.setLayoutParams(layoutParams);
    //    }

    //初始化控件，获取跳转传值
    private void initView() {
        vpContent = bindingView.vpContent;
        toolbar = bindingView.toolbar;
        relativeLayout = bindingView.relativeLayout;

        titlebarReg = bindingView.ivTitleRegist;
        titlebarSign = bindingView.ivTitleSign;
        main_logo = bindingView.mainLogo;
    }

    /**
     * 加载fragment模块
     */

    private void initContentFragment() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new SigninFragment());
        mFragmentList.add(new RegistFragment());

        // 注意使用的是：getSupportFragmentManager
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        vpContent.setAdapter(adapter);
        // 设置ViewPager最大缓存的页面个数(cpu消耗少)
        vpContent.setOffscreenPageLimit(2);
        vpContent.addOnPageChangeListener(this);

        //设置默认显示
        bindingView.ivTitleSign.setSelected(true);
        vpContent.setCurrentItem(0);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //去除默认Title显示
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    //监听
    private void initListener() {
        bindingView.ivTitleRegist.setOnClickListener(this);
        bindingView.ivTitleSign.setOnClickListener(this);

        bindingView.imgBack.setOnClickListener(this);
        bindingView.tvQuit.setOnClickListener(this);
        main_logo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_sign:
                if (vpContent.getCurrentItem() != 0) {//不然cpu会有损耗
                    titlebarReg.setSelected(false);
                    titlebarSign.setSelected(true);
                    vpContent.setCurrentItem(0);
                }
                break;
            case R.id.iv_title_regist:
                if (vpContent.getCurrentItem() != 1) {//不然cpu会有损耗
                    titlebarReg.setSelected(true);
                    titlebarSign.setSelected(false);
                    vpContent.setCurrentItem(1);
                }
                break;


            case R.id.img_back:
                //清空缓存中保存的会议id
                aCache = ACache.get(this);
                aCache.remove(Constants.CONFERENCE_ID);

                this.finish();
                break;

            case R.id.main_logo:
                ToastUtils.ShortToast(this, "点我干嘛，我是logo");
                break;
            case R.id.tv_quit://退出
                //清除 acache缓存

                //调用基类退出广播
                Intent intent = new Intent();
                intent.setAction(EXIT_APP_ACTION);
                sendBroadcast(intent);//发送退出的广播
                this.finish();
                break;
        }

    }

    //ViewPager的三个监听
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                titlebarReg.setSelected(true);
                titlebarSign.setSelected(false);
                break;
            case 1:
                titlebarReg.setSelected(false);
                titlebarSign.setSelected(true);
                vpContent.setCurrentItem(1);
                break;
            case 2:
                titlebarReg.setSelected(false);
                titlebarSign.setSelected(false);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
