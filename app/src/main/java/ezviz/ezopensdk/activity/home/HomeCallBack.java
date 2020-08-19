package ezviz.ezopensdk.activity.home;

import java.util.List;
import java.util.Map;

import ezviz.ezopensdk.been.base.BaseCallback;

/**
 * Created by hejunfeng on 2020/7/21 0021
 */
public abstract class HomeCallBack<T> implements BaseCallback {
    public void showBanner(List<Map<String, Object>> ls,List l ){ };

    public void getVersion(String s , String s2 , int version){};

    public void setdata(Object obj){};
}
