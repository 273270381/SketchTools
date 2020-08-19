package ezviz.ezopensdk.activity.login;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.User;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.utils.ExampleUtil;
import ezviz.ezopensdk.utils.OSCSharedPreference;

import static ezviz.ezopensdk.been.AlarmContant.AppKey;
import static ezviz.ezopensdk.been.AlarmContant.Secret;

/**
 * Created by hejunfeng on 2020/7/20 0020
 */
public class LoginPresenter extends BasePresenter<LoginView> {
    private LoginModelImpl loginModel;

    public LoginPresenter(LoginModelImpl loginModel) {
        this.loginModel = loginModel;
    }

    public void LogIn(Context context, String strUsername, String strPassword, String url){
        if (!isViewAttached()){
            return;
        }
        Map<String,String> map = new HashMap<>();
        map.put("userName",strUsername);
        map.put("password",strPassword);
        if (!ExampleUtil.isConnected(context)){
            getView().showToast("确认网络是否断开");
        }else{
            if (strUsername.trim().equals("")){
                getView().showToast("请您输入用户名");
            }else{
                if (strPassword.trim().equals("")){
                    getView().showToast("请您输入密码");
                }else{
                    getView().showLoading();
                    getdata(map, url);
                }
            }
        }
    }
    private void getdata(Map map, String url){
        loginModel.login(url, map, new LoginCallBack() {
            @Override
            public void sendResult(User user, String s) {
                if (s.equals("true")){
                    getView().setUserType(user.getAccount());
                    OSCSharedPreference.getInstance().putUserId(user.getUserId());
                    OSCSharedPreference.getInstance().putUserName((String) map.get("userName"));
                    OSCSharedPreference.getInstance().putPassWord((String) map.get("password"));
                    String url = "https://open.ys7.com/api/lapp/token/get";
                    Map<String,String> map = new HashMap<>();
                    map.put("appKey",AppKey);
                    map.put("appSecret",Secret);
                    loginModel.getAccessToken(url, map, new LoginCallBack() {
                        @Override
                        public void sendResult(User user, String s) {

                        }

                        @Override
                        public void sendAccessToken(String s) {
                            getView().setAccessToken(s);
                        }

                        @Override
                        public void onSuccess(Object data) {

                        }

                        @Override
                        public void onFailure(String msg) {
                            AppOperator.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    getView().hideLoading();
                                    getView().showToast(msg);
                                }
                            });
                        }


                    });
                }else{
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            getView().hideLoading();
                            getView().showToast("密码错误");
                        }
                    });
                }
            }

            @Override
            public void sendAccessToken(String s) {

            }

            @Override
            public void onSuccess(Object data) {
                if (isViewAttached()) {
                    getView().hideLoading();
                }
            }

            @Override
            public void onFailure(String msg) {
                if (isViewAttached()) {
                    getView().hideLoading();
                    getView().showToast(msg);
                }
            }
        });
    }
}
