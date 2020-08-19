package ezviz.ezopensdk.activity.login;

import ezviz.ezopensdk.been.User;
import ezviz.ezopensdk.been.base.BaseCallback;

/**
 * Created by hejunfeng on 2020/7/20 0020
 */
public interface LoginCallBack<T> extends BaseCallback {

    void sendResult(User user , String s);

    void sendAccessToken(String s);
}
