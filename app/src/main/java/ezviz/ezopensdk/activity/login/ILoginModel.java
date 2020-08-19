package ezviz.ezopensdk.activity.login;

import java.util.Map;

public interface ILoginModel<T> {

    void login(String s, Map m, LoginCallBack<T> callback);

    void getAccessToken(String s, Map m,LoginCallBack<T> callback);
}
