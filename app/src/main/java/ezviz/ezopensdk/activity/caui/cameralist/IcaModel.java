package ezviz.ezopensdk.activity.caui.cameralist;

import android.content.Context;

import com.videogo.openapi.bean.EZCameraInfo;

import java.util.List;

import ezviz.ezopensdk.view.spinner.LoBody;

/**
 * Created by hejunfeng on 2020/7/25 0025
 */
public interface IcaModel {
    void getCamersInfoList(Context context,boolean headerOrFooter,int count, CaCallBack caCallBack);

    void refresPic(List<EZCameraInfo> cameraInfoList , CaCallBack caCallBack);

    void sendTcp(String et_send, List<String> arrayList, List<LoBody> loBodyList,CaCallBack callBack);

    void getTcpresult(String data, CaCallBack callBack);
}
