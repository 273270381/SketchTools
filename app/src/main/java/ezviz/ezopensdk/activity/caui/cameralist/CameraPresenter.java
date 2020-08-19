package ezviz.ezopensdk.activity.caui.cameralist;

import android.content.Context;

import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;

import java.util.List;

import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.utils.TcpClient;
import ezviz.ezopensdk.view.spinner.LoBody;

/**
 * Created by hejunfeng on 2020/7/25 0025
 */
public class CameraPresenter extends BasePresenter<CameraView> {
    private CameraModelImpl model;

    public CameraPresenter(CameraModelImpl model) {
        this.model = model;
    }

    public void getCamersInfoListTask(Context context, boolean headerOrFooter, int count){
        if (!isViewAttached()){
            return;
        }
        getView().showLoading();
        model.getCamersInfoList(context, headerOrFooter, count, new CaCallBack() {

            @Override
            public void getCameraListInfo(List<EZDeviceInfo> result, Boolean b) {
                if (getView() != null){
                    getView().getCameraList(result, b);
                }
            }

            @Override
            public void sendErrorCode(int i) {
                if (getView() != null){
                    getView().onError(i);
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
                if (getView() != null){
                    getView().hideLoading();
                    getView().showToast(msg);
                }
            }
        });
    }

    public void refreshPic(List<EZCameraInfo> cameraInfoList){
        if (!isViewAttached()){
            return;
        }
        model.refresPic(cameraInfoList, new CaCallBack() {

            @Override
            public void refresh() {
                if (getView() != null){
                    getView().refresh();
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

    public void sendTcp(String et_send,List<String> arrayList,List<LoBody> loBodyList){
        if (!isViewAttached()){
            return;
        }
        model.sendTcp(et_send, arrayList, loBodyList, new CaCallBack() {

            @Override
            public void showLoading(TcpClient tcpClient, String no, String text) {
                if (getView() != null){
                    getView().showLoading();
                    getView().sendTcp(tcpClient,no,text);
                }
            }

            @Override
            public void hideLoading() {
                if (getView() != null){
                    getView().hideLoading();
                }
            }

            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailure(String msg) {
                if (getView() != null){
                    getView().showToast(msg);
                }
            }
        });
    }

    public void getTcpresult(String data){
        if (!isViewAttached()){
            return;
        }
        model.getTcpresult(data, new CaCallBack() {

            @Override
            public void tcpResult(boolean b) {
                if (getView() != null){
                    getView().tcpResult(b);
                }
            }

            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailure(String msg) {
                if (getView() != null){
                    getView().showToast(msg);
                }
            }

            @Override
            public void hideLoading() {
                if (getView() != null){
                    getView().hideLoading();
                }
            }
        });
    }
}
