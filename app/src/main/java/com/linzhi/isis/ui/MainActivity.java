package com.linzhi.isis.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.linzhi.isis.R;
import com.linzhi.isis.app.rx.RxBus;
import com.linzhi.isis.databinding.ActivityMainBinding;
import com.linzhi.isis.ui.regist.RegistFragment;
import com.linzhi.isis.view.MyFragmentPagerAdapter;

import java.util.ArrayList;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private static final String TAG = "SJY";
    ArrayList<Fragment> mFragmentList;
    private ViewPager vpContent;
    private Toolbar toolbar;
    private RelativeLayout linearLayout;
    // 一定需要对应的bean
    private ActivityMainBinding mBinding;
    private ImageView titlebarReg;
    private ImageView titlebarSign;
    private ImageView titlebarThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //        initStatusView();
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
    //        ViewGroup.LayoutParams layoutParams = mBinding.viewStatus.getLayoutParams();
    //        layoutParams.height = StatusBarUtil.getStatusBarHeight(this);
    //        mBinding.viewStatus.setLayoutParams(layoutParams);
    //    }

    //初始化控件，获取跳转传值
    private void initView() {
        linearLayout = mBinding.linearlayout;
        toolbar = mBinding.toolbar;
        vpContent = mBinding.vpContent;

        titlebarReg = mBinding.ivTitleRegist;
        titlebarSign = mBinding.ivTitleSign;
        titlebarThree = mBinding.ivTitleThree;
    }

    //加载fragment模块
    private void initContentFragment() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new RegistFragment());
        mFragmentList.add(new RegistFragment());
        mFragmentList.add(new RegistFragment());

        // 注意使用的是：getSupportFragmentManager
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        vpContent.setAdapter(adapter);
        // 设置ViewPager最大缓存的页面个数(cpu消耗少)
        vpContent.setOffscreenPageLimit(2);
        vpContent.addOnPageChangeListener(this);

        //设置默认显示
        mBinding.ivTitleRegist.setSelected(true);
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
        mBinding.ivTitleRegist.setOnClickListener(this);
        mBinding.ivTitleSign.setOnClickListener(this);
        mBinding.ivTitleThree.setOnClickListener(this);

        mBinding.imgBack.setOnClickListener(this);
        mBinding.tvQuit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_title_regist:
                if (vpContent.getCurrentItem() != 0) {//不然cpu会有损耗
                    titlebarReg.setSelected(true);
                    titlebarSign.setSelected(false);
                    titlebarThree.setSelected(false);
                    vpContent.setCurrentItem(0);
                }
                break;

            case R.id.iv_title_sign:
                if (vpContent.getCurrentItem() != 1) {//不然cpu会有损耗
                    titlebarReg.setSelected(false);
                    titlebarSign.setSelected(true);
                    titlebarThree.setSelected(false);
                    vpContent.setCurrentItem(1);
                }
                break;

            case R.id.iv_title_three:
                if (vpContent.getCurrentItem() != 2) {//不然cpu会有损耗
                    titlebarReg.setSelected(false);
                    titlebarSign.setSelected(false);
                    titlebarThree.setSelected(true);
                    vpContent.setCurrentItem(2);
                }
                break;
            case R.id.img_back:
                this.finish();
                break;
            case R.id.tv_quit:
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
                titlebarThree.setSelected(false);
                break;
            case 1:
                titlebarReg.setSelected(false);
                titlebarSign.setSelected(true);
                titlebarThree.setSelected(false);
                vpContent.setCurrentItem(1);
                break;
            case 2:
                titlebarReg.setSelected(false);
                titlebarSign.setSelected(false);
                titlebarThree.setSelected(true);
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
