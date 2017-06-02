package com.linzhi.isis.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.linzhi.isis.R;
import com.linzhi.isis.app.Constants;
import com.linzhi.isis.base.baseadapter.BaseRecyclerViewAdapter;
import com.linzhi.isis.base.baseadapter.BaseRecyclerViewHolder;
import com.linzhi.isis.bean.conference.ConferenceDetailBean;
import com.linzhi.isis.databinding.ItemConferencBinding;
import com.linzhi.isis.http.cache.ACache;
import com.linzhi.isis.ui.MainActivity;
import com.linzhi.isis.utils.PerfectClickListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;


/**
 * 会议列表 适配
 */

public class ConferencesAdapter extends BaseRecyclerViewAdapter<ConferenceDetailBean> {
    private static final String TAG = "ConferencesAdapter";
    private Activity activity;
    private ACache acache;

    public ConferencesAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_conferenc);
    }

    private class ViewHolder extends BaseRecyclerViewHolder<ConferenceDetailBean, ItemConferencBinding> {

        ViewHolder(ViewGroup context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindViewHolder(final ConferenceDetailBean bean, final int position) {
            if (bean != null) {
                binding.setBean(bean);
                // 图片
                //                ImgLoadUtil.displayEspImage(bean.getImages().getLarge(), binding.ivOnePhoto,0);
                // 导演
                //                binding.tvOneDirectors.setText(StringFormatUtil.formatName(bean.getDirectors()));
                // 主演
                //                binding.tvOneCasts.setText(StringFormatUtil.formatName(bean.getCasts()));
                // 类型
                //                binding.tvOneGenres.setText("类型：" + StringFormatUtil.formatGenres(bean.getGenres()));
                // 评分
                //                binding.tvOneRatingRate.setText("评分：" + String.valueOf(bean.getRating().getAverage()));
                // 分割线颜色

                ViewHelper.setScaleX(itemView, 0.8f);
                ViewHelper.setScaleY(itemView, 0.8f);
                ViewPropertyAnimator.animate(itemView).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
                ViewPropertyAnimator.animate(itemView).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();


                //item监听
                binding.llItemTop.setOnClickListener(new PerfectClickListener() {
                    @Override
                    protected void onNoDoubleClick(View v) {

                        //传递信息
                        if (acache == null) {
                            acache = ACache.get(activity);
                        }
                        Log.d(TAG, "onNoDoubleClick: 选中会议跳转 =" + bean.getConferenceID());
                        acache.put(Constants.CONFERENCE_ID, bean.getConferenceID());

                        // 页面跳转
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);


                    }
                });
            }
        }
    }
}
