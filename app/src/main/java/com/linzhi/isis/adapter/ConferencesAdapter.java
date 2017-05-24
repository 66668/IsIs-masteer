package com.linzhi.isis.adapter;

import android.app.Activity;
import android.content.Intent;
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
        public void onBindViewHolder(final ConferenceDetailBean positionData, final int position) {
            if (positionData != null) {
                binding.setBean(positionData);
                // 图片
                //                ImgLoadUtil.displayEspImage(positionData.getImages().getLarge(), binding.ivOnePhoto,0);
                // 导演
                //                binding.tvOneDirectors.setText(StringFormatUtil.formatName(positionData.getDirectors()));
                // 主演
                //                binding.tvOneCasts.setText(StringFormatUtil.formatName(positionData.getCasts()));
                // 类型
                //                binding.tvOneGenres.setText("类型：" + StringFormatUtil.formatGenres(positionData.getGenres()));
                // 评分
                //                binding.tvOneRatingRate.setText("评分：" + String.valueOf(positionData.getRating().getAverage()));
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
                        acache.put(Constants.CONFERENCE_ID, positionData.getConferenceID());

                        // 页面跳转
                        Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);

                        //                        OneMovieDetailActivity.start(activity, positionData, binding.ivOnePhoto);

                        //                        if (position % 2 == 0) {

                        //                            SlideScrollViewActivity.start(activity, positionData, binding.ivOnePhoto);

                        //                            MovieDetailActivity.start(activity, positionData, binding.ivOnePhoto);
                        //                            OneMovieDetailActivity.start(activity, positionData, binding.ivOnePhoto);

                        //                            TestActivity.start(activity, positionData, binding.ivOnePhoto);
                        //                            activity.overridePendingTransition(R.anim.push_fade_out, R.anim.push_fade_in);
                        //                        } else {
                        //                            SlideScrollViewActivity.start(activity, positionData, binding.ivOnePhoto);
                        //                            SlideShadeViewActivity.start(activity, positionData, binding.ivOnePhoto);
                        //                            OneMovieDetailActivity.start(activity, positionData, binding.ivOnePhoto);
                        //                        }

                        // 这个可以
                        //                        SlideScrollViewActivity.start(activity, positionData, binding.ivOnePhoto);
                        //                        TestActivity.start(activity,positionData,binding.ivOnePhoto);
                        //                        v.getContext().startActivity(new Intent(v.getContext(), SlideScrollViewActivity.class));

                        //                        SlideShadeViewActivity.start(activity, positionData, binding.ivOnePhoto);

                    }
                });
            }
        }
    }
}
