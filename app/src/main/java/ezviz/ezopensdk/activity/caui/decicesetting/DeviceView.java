package ezviz.ezopensdk.activity.caui.decicesetting;

import com.videogo.openapi.bean.EZDeviceVersion;

import ezviz.ezopensdk.been.base.BaseView;

/**
 * Created by hejunfeng on 2020/8/3 0003
 */
public interface DeviceView extends BaseView {
    void getVersion(int versionCode, String versionName, String fileName);

    void getDeviceInfo(EZDeviceVersion mDeviceVersion, Boolean b , int errorCode);
}
