package com.linzhi.isis.http;


import com.linzhi.isis.bean.BaseBean;
import com.linzhi.isis.bean.conference.ConferenceBean;
import com.linzhi.isis.bean.login.LoginBean;
import com.linzhi.isis.bean.login.UserInfoBean;
import com.linzhi.isis.bean.regist.RegistBean;
import com.linzhi.isis.bean.signin.SigninBeans;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by jingbin on 16/11/21.
 * 网络请求类（一个接口一个方法）
 * 注:使用Observable<BaseBean<AddnewReturnBean>> AddnewEmployee(@Field("Obj") String bean);形式时出现gson解析bug,没发用泛型，后续解决
 */
public interface MyHttpService {
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
         * 获取注册-签到列表服务
         */
        public static MyHttpService getRegistService() {
            return HttpUtils.getInstance().getRegistServer(MyHttpService.class);
        }

        /**
         * 添加新人员
         *
         * @return
         */
        public static MyHttpService AddNewService() {
            return HttpUtils.getInstance().getRegistServer(MyHttpService.class);
        }


    }

    /**
     * 03-02 获取签到列表, 也包括查询
     */
    @FormUrlEncoded
    @POST("EmployeeSearch/GetEmployeeSearch")
    Observable<SigninBeans> GetSearchSigninList(@Field("CompanyID") String CompanyID
            , @Field("ConferenceID") String ConferenceID
            , @Field("NameorPhoneorStore") String Telephone);

    /**
     * 03-01-01 添加新人员
     */
    @FormUrlEncoded
    @POST("AddEmployee/AddEmployeePost")
    Observable<BaseBean<String>> AddnewEmployee(@Field("Obj") String bean);


    /**
     * 03-01 获取注册列表, 也包括查询
     */
    @FormUrlEncoded
    @POST("EmployeeSearch/GetEmployeeSearch")
    Observable<RegistBean> GetSearchRegistList(@Field("CompanyID") String CompanyID
            , @Field("ConferenceID") String ConferenceID
            , @Field("NameorPhoneorStore") String Telephone);

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


}