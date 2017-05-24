package com.linzhi.isis.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 获取json数据基类
 * {"code":0,"message":"","result":""}
 */

public class BaseResponseBean<T> implements Serializable {
    @SerializedName("code")
    public int code;

    @SerializedName("message")
    public String message;

    @SerializedName("result")
    public T result;
}
