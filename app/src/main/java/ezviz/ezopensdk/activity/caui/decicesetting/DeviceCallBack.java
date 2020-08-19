package ezviz.ezopensdk.activity.caui.decicesetting;

import com.videogo.openapi.bean.EZDeviceVersion;

import ezviz.ezopensdk.been.base.BaseCallback;

/**
 * Created by hejunfeng on 2020/8/3 0003
 */
public abstract class DeviceCallBack implements BaseCallback {
    public void getVersionInfo(int code, String versionName, String fineName){}

    public void getDeviceInfo(EZDeviceVersion mDeviceVersion, Boolean b , int errorCode){}
}
