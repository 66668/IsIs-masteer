package com.linzhi.isis.http;


import com.linzhi.isis.bean.conference.ConferenceBean;
import com.linzhi.isis.bean.login.LoginBean;
import com.linzhi.isis.bean.login.UserInfoBean;
import com.linzhi.isis.bean.regist.RegistBean;
import com.linzhi.isis.bean.signin.SigninBean;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by jingbin on 16/11/21.
 * 网络请求类（一个接口一个方法）
 */
public interface MyHttpService {

    /**
     * 03-02 获取签到列表, 也包括查询
     */
    @FormUrlEncoded
    @POST("EmployeeSearch/GetEmployeeSearch")
    Observable<SigninBean> GetSearchSigninList(@Field("CompanyID") String CompanyID
            , @Field("ConferenceID") String ConferenceID
            , @Field("Telephone") String Telephone
            , @Field("EmpStore") String EmpStore
            , @Field("EmployeeName") String EmployeeName);

    /**
     * 03-01 获取注册列表, 也包括查询
     */
    @FormUrlEncoded
    @POST("EmployeeSearch/GetEmployeeSearch")
    Observable<RegistBean> GetSearchRegistList(@Field("CompanyID") String CompanyID
            , @Field("ConferenceID") String ConferenceID
            , @Field("Telephone") String Telephone
            , @Field("EmpStore") String EmpStore
            , @Field("EmployeeName") String EmployeeName);

    /**
     * 02 获取会议列表
     *
     * @param storeId
     * @return
     */
    @FormUrlEncoded
    @POST("ConferenceInfo/GetConferenceInfo")
    Observable<ConferenceBean> getConferenceList(@Field("storeID") String storeId);

    /**
     * 01 登录
     *
     * @param username
     * @param password
     * @return
     */
    @FormUrlEncoded
    @POST("User/LoginByPassword")
    //post
    Observable<LoginBean<UserInfoBean>> login(@Field("username") String username, @Field("password") String password);

    class Builder {

        /**
         * 登录
         *
         * @return
         */
        public static MyHttpService loginService() {
            return HttpUtils.getInstance().loginServer(MyHttpService.class);
        }

        /**
         * 会议
         */
        public static MyHttpService getConferenceService() {
            return HttpUtils.getInstance().getConferenceServer(MyHttpService.class);
        }

        /**
         *
         *
         */
        public static MyHttpService getRegistService() {
            return HttpUtils.getInstance().getRegistServer(MyHttpService.class);
        }

    }

}