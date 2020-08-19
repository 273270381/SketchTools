package ezviz.ezopensdk.activity.home;

import android.content.Context;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by hejunfeng on 2020/7/21 0021
 */
public interface  IHomeModel<T> {
    void addBanner(HomeCallBack callBack);

    void checkUpdate( HomeCallBack callBack);

    void asyDownLoadFile(String fileName, HomeCallBack callBack);

    void getDevices(HomeCallBack callBack);
}


