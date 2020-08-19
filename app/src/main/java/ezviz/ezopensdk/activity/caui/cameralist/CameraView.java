package ezviz.ezopensdk.activity.caui.cameralist;

import com.videogo.openapi.bean.EZDeviceInfo;

import java.util.List;

import ezviz.ezopensdk.been.base.BaseView;
import ezviz.ezopensdk.utils.TcpClient;

/**
 * Created by hejunfeng on 2020/7/25 0025
 */
public interface CameraView extends BaseView {

    void getCameraList(List<EZDeviceInfo> result, boolean b);

    void refresh();

    void onError(int errorCode);

    void sendTcp(TcpClient tcpClient, String no, String text);

    void tcpResult(Boolean b);
}
