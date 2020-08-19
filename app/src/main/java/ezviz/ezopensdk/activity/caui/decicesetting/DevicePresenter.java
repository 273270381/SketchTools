package ezviz.ezopensdk.activity.caui.decicesetting;

import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.openapi.bean.EZDeviceVersion;

import ezviz.ezopensdk.been.base.BasePresenter;

/**
 * Created by hejunfeng on 2020/8/3 0003
 */
public class DevicePresenter extends BasePresenter<DeviceView> {
    private DeviceModelImpl model;

    public DevicePresenter(DeviceModelImpl model) {
        this.model = model;
    }

    public void getVersionInfo(){
        if (!isViewAttached()){
            return;
        }
        model.getVersionInfo(new DeviceCallBack() {
            @Override
            public void getVersionInfo(int code, String versionName, String fineName) {
                if (getView() != null){
                    getView().getVersion(code,versionName,fineName);
                }
            }

            @Override
            public void onSuccess(Object data) {
                if (getView() != null){
                    getView().hideLoading();
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });

    }

    public void getDeviceInfo(EZDeviceInfo mEZDeviceInfo){
        if (!isViewAttached()){
            return;
        }
        model.getDeviceInfo(mEZDeviceInfo, new DeviceCallBack() {

            @Override
            public void getDeviceInfo(EZDeviceVersion mDeviceVersion, Boolean b, int errorCode) {
                if (getView() != null){
                    getView().getDeviceInfo(mDeviceVersion, b, errorCode);
                }
            }

            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}
