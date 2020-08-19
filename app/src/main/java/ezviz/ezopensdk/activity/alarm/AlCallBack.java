package ezviz.ezopensdk.activity.alarm;

import java.util.List;

import ezviz.ezopensdk.been.AlarmMessage;
import ezviz.ezopensdk.been.base.BaseCallback;

/**
 * Created by hejunfeng on 2020/8/11 0011
 */
public abstract class AlCallBack implements BaseCallback {
    public void queryData(List<AlarmMessage> alarmMessageList){}

    public void refreshLocation(){}

    public void queryReadId(List<String> read_list){}
}
