package com.linzhi.isis.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.linzhi.isis.R;
import com.linzhi.isis.base.baseadapter.BaseRecyclerViewAdapter;
import com.linzhi.isis.base.baseadapter.BaseRecyclerViewHolder;
import com.linzhi.isis.bean.regist.RegistDetailBean;
import com.linzhi.isis.databinding.ItemRegistBinding;
import com.linzhi.isis.utils.PerfectClickListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;


/**
 * Created by jingbin on 2016/11/25.
 */


public class RegistAdapter extends BaseRecyclerViewAdapter<RegistDetailBean> {

    private Activity activity;

    public RegistAdapter(Activity activity) {
        this.activity = activity;
    }

    //在这里把ViewHolder绑定Item的布局
    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent, R.layout.item_regist);
    }

    private class ViewHolder extends BaseRecyclerViewHolder<RegistDetailBean, ItemRegistBinding> {

        ViewHolder(ViewGroup context, int layoutId) {
            super(context, layoutId);
        }

        //在这里绑定数据到ViewHolder里面
        @Override
        public void onBindViewHolder(final RegistDetailBean positionData, final int position) {
            if (positionData != null) {
                binding.setRegistDetailBean(positionData);
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
                //                binding.viewColor.setBackgroundColor(CommonUtils.randomColor());

                ViewHelper.setScaleX(itemView, 0.8f);
                ViewHelper.setScaleY(itemView, 0.8f);
                ViewPropertyAnimator.animate(itemView).scaleX(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();
                ViewPropertyAnimator.animate(itemView).scaleY(1).setDuration(350).setInterpolator(new OvershootInterpolator()).start();

                binding.llOneItem.setOnClickListener(new PerfectClickListener() {

                    //监听回调
                    @Override
                    protected void onNoDoubleClick(View v) {
                        if (listener != null) {
                            listener.onClick(positionData, position);
                        }

                    }
                });
            }
        }
    }
}
