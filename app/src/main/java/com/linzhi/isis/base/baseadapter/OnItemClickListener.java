package com.linzhi.isis.base.baseadapter;

/**
 * 自定义recylcerView的监听，recyclerView的缺点之一
 * Created by jingbin on 2016/3/2.
 */
public interface OnItemClickListener<T> {
    public void onClick(T t, int position);
}
