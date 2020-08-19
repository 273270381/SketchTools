package ezviz.ezopensdk.activity.caui.cameralist;

import android.content.Context;
import android.os.Handler;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.util.ConnectionDetector;
import java.util.List;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.utils.TcpClient;
import ezviz.ezopensdk.view.spinner.LoBody;
import static ezviz.ezopensdk.activity.OSCApplication.getOpenSDK;

/**
 * Created by hejunfeng on 2020/7/25 0025
 */
public class CameraModelImpl implements  IcaModel{
    @Override
    public void getCamersInfoList(Context context,boolean headerOrFooter, int count,  CaCallBack callBack) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                int mErrorCode = 0;
                if (!ConnectionDetector.isNetworkAvailable(context)) {
                    mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
                    callBack.sendErrorCode(mErrorCode);
                    callBack.onFailure("网络连接失败");
                }
                try {
                    List<EZDeviceInfo> result = null;
                    if (headerOrFooter) {
                        result = getOpenSDK().getDeviceList(0, 20);
                    }else{
                        result = getOpenSDK().getSharedDeviceList((count / 20) + (count % 20 > 0 ? 1 : 0), 20);
                    }
                    List<EZDeviceInfo> finalResult = result;
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.getCameraListInfo(finalResult,headerOrFooter);
                            callBack.onSuccess(0);
                        }
                    });
                } catch (BaseException e) {
                    ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                    mErrorCode = errorInfo.errorCode;
                    if (mErrorCode != 0 ){
                        callBack.sendErrorCode(mErrorCode);
                    }
                    callBack.onFailure("查询失败");
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void refresPic(List<EZCameraInfo> cameraInfoList, CaCallBack caCallBack) {
        for (EZCameraInfo cameraInfo : cameraInfoList){
            AppOperator.runOnThread(new Runnable() {
                @Override
                public void run() {
                    String  url = null;
                    try {
                        url = getOpenSDK().captureCamera(cameraInfo.getDeviceSerial(),cameraInfo.getCameraNo());
                        cameraInfo.setCameraCover(url);
                        AppOperator.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                caCallBack.refresh();
                            }
                        });
                    } catch (BaseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void sendTcp(String et_send, List<String> arrayList, List<LoBody> loBodyList, CaCallBack callBack) {
        int pos = arrayList.lastIndexOf(et_send);
        String ip = loBodyList.get(pos).getIp();
        int port = Integer.parseInt(loBodyList.get(pos).getPort());
        String No = loBodyList.get(pos).getNo();
        String send_tx = "{'address': '"+loBodyList.get(pos).getAddress()+"', 'Preset': '"+loBodyList.get(pos).getPreset()+"'}.";
        TcpClient tcpClient = TcpClient.getInstance();
        if (!tcpClient.isConnect()){
            tcpClient.connect(ip,port);
        }else {
            byte[] data = send_tx.getBytes();
            tcpClient.sendByteCmd(data,1001);
        }
        callBack.showLoading(tcpClient, No, send_tx);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callBack.hideLoading();
                callBack.onFailure("无响应");
            }
        }, 4000);
    }

    @Override
    public void getTcpresult(String data, CaCallBack callBack) {
        if ("OK".equals(data)){
            AppOperator.runOnMainThreadDelayed(new Runnable() {
                @Override
                public void run() {
                    callBack.hideLoading();
                    callBack.tcpResult(true);
                }
            },2000);
        }else{
            AppOperator.runOnMainThreadDelayed(new Runnable() {
                @Override
                public void run() {
                    callBack.hideLoading();
                    callBack.onFailure("通道被占用，请稍等");
                }
            },2000);
        }
    }
}
