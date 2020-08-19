package ezviz.ezopensdk.activity.dataquery;

import java.util.List;

import ezviz.ezopensdk.been.Temp;
import ezviz.ezopensdk.been.base.BaseCallback;

/**
 * Created by hejunfeng on 2020/8/18 0018
 */
public abstract class MyCallBack implements BaseCallback {
    public void queryData(List<Temp> list){}

    public void getCount(int count){}

    public void sendProcess(Long process){}

    public void sendDataList(List<Temp> list){}
}
