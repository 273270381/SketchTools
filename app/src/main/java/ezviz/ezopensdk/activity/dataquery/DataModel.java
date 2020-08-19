package ezviz.ezopensdk.activity.dataquery;

import java.util.Date;

/**
 * Created by hejunfeng on 2020/8/18 0018
 */
public interface DataModel {
    void queryData(String date, MyCallBack callBack);

    void getCount(MyCallBack callBack);

    void getData(int count, MyCallBack callBack);
}
