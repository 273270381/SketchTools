package ezviz.ezopensdk.activity.alarm;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import ezviz.ezopensdk.been.AlarmMessage;

/**
 * Created by hejunfeng on 2020/8/11 0011
 */
public interface IAlModel {
    void queryData(int page_size, String userId, int type, int page, AlCallBack callBack);

    void queryLocation(AlarmMessage alarmMessage, AlCallBack callBack) throws UnsupportedEncodingException, NoSuchAlgorithmException;

    void queryReadId(AlCallBack callBack);
}
