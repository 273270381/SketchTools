package ezviz.ezopensdk.activity.caui.decicesetting;

import android.util.Log;

import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.openapi.bean.EZDeviceVersion;

import org.apache.commons.net.ftp.FTPFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ezviz.ezopensdk.activity.OSCApplication;
import ezviz.ezopensdk.been.AlarmContant;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.utils.FTPutils;

import static ezviz.ezopensdk.activity.OSCApplication.getOpenSDK;

/**
 * Created by hejunfeng on 2020/8/3 0003
 */
public class DeviceModelImpl implements IDeviceModel{
    @Override
    public void getVersionInfo(DeviceCallBack callBack) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                FTPutils ftPutils = new FTPutils();
                List<Integer> integerList = new ArrayList<>();
                Boolean flag = ftPutils.connect(AlarmContant.ftp_ip,Integer.parseInt(AlarmContant.ftp_port),AlarmContant.name,AlarmContant.password);
                if (flag){
                    try {
                        FTPFile[] files = ftPutils.listName(AlarmContant.apk_path);
                        for (int i = 0 ; i < files.length ; i ++){
                            String[] strings = files[i].getName().split("_");
                            String versionCode = strings[1].substring(1);
                            integerList.add(Integer.parseInt(versionCode));
                        }
                        int versionCode = Collections.max(integerList);
                        String versionName = files[integerList.indexOf(versionCode)].getName().split("_")[2];
                        String fileName = files[integerList.indexOf(versionCode)].getName();
                        if (versionName!=null){
                            AppOperator.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.getVersionInfo(versionCode,versionName, fileName);
                                    callBack.onSuccess(0);
                                    Log.d("TAG","versionName="+versionName);
                                }
                            });
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void getDeviceInfo(EZDeviceInfo mEZDeviceInfo, DeviceCallBack callBack) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                Boolean b = true;
                EZDeviceVersion mDeviceVersion = null;
                try {
                    mDeviceVersion = getOpenSDK().getDeviceVersion(mEZDeviceInfo.getDeviceSerial());
                    b = true;
                    int mErrorCode = 0;
                    EZDeviceVersion finalMDeviceVersion = mDeviceVersion;
                    Boolean finalB = b;
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.getDeviceInfo(finalMDeviceVersion, finalB, mErrorCode);
                        }
                    });
                } catch (BaseException e) {
                    ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                    int mErrorCode = errorInfo.errorCode;
                    b = false;
                    e.printStackTrace();
                    EZDeviceVersion finalMDeviceVersion1 = mDeviceVersion;
                    Boolean finalB1 = b;
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.getDeviceInfo(finalMDeviceVersion1, finalB1, mErrorCode);
                        }
                    });
                }
            }
        });
    }
}
