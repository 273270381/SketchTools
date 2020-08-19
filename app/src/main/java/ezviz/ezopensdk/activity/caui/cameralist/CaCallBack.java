package ezviz.ezopensdk.activity.caui.cameralist;

import com.videogo.openapi.bean.EZDeviceInfo;

import java.util.List;

import ezviz.ezopensdk.been.base.BaseCallback;
import ezviz.ezopensdk.utils.TcpClient;

/**
 * Created by hejunfeng on 2020/7/25 0025
 */
public abstract class CaCallBack implements BaseCallback {
    public void getCameraListInfo(List<EZDeviceInfo> result, Boolean b){}

    public void sendErrorCode(int i){}

    public void refresh(){}

    public void showLoading(TcpClient tcpClient, String no, String tx){}

    public void hideLoading(){}

    public void tcpResult(boolean b){}
}
