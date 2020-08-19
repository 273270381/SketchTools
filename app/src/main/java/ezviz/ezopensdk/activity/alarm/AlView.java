package ezviz.ezopensdk.activity.alarm;

import java.util.List;

import ezviz.ezopensdk.been.AlarmMessage;
import ezviz.ezopensdk.been.base.BaseView;

/**
 * Created by hejunfeng on 2020/8/11 0011
 */
public interface AlView extends BaseView {
    void queryAlarmData(List<AlarmMessage> alarmMessageList);

    void refreshLocation();

    void queryId(List<String> ls);
}
