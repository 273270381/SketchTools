package ezviz.ezopensdk.activity.caui.decicesetting;

import com.videogo.openapi.bean.EZDeviceInfo;

/**
 * Created by hejunfeng on 2020/8/3 0003
 */
public interface IDeviceModel {
    void getVersionInfo(DeviceCallBack callBack);

    void getDeviceInfo(EZDeviceInfo mEZDeviceInfo, DeviceCallBack callBack);
}
