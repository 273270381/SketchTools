package ezviz.ezopensdk.activity.home;

import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.videogo.exception.BaseException;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.OSCApplication;
import ezviz.ezopensdk.been.AlarmContant;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.Constant;
import ezviz.ezopensdk.utils.FTPutils;

/**
 * Created by hejunfeng on 2020/7/21 0021
 */
public class HomeModelImpl implements IHomeModel {
    private List<Integer> imgs=new ArrayList<>();
    private int[] icon = { R.mipmap.home_icon_real_map,R.mipmap.home_icon_preview,R.mipmap.home_icon_baidu_map,
            R.mipmap.home_icon_alarm_information,R.mipmap.home_icon_show_video,R.mipmap.home_icon__show_picture };
    private String[] iconName = { "实景地图", "画面预览", "百度地图", "报警信息", "视频查看", "图片查看"};
    @Override
    public void addBanner(HomeCallBack callBack) {
        imgs.add(R.mipmap.bg_home_1);
        imgs.add(R.mipmap.bg_home_2);
        imgs.add(R.mipmap.bg_home_3);
        List<Map<String, Object>> data_list = getData();
        callBack.showBanner(data_list, imgs);
    }
    public List<Map<String, Object>> getData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        List<Map<String, Object>> data_list = new ArrayList<>();
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("icon", icon[i]);
            map.put("iconName", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }

    @Override
    public void checkUpdate( HomeCallBack callBack){
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
                                    callBack.getVersion(versionName, fileName, versionCode);
                                }
                            });
                        }
                    }catch (Exception e) {
                        AppOperator.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFailure("error");
                            }
                        });
                        e.printStackTrace();
                    }
                }else {
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure("网络异常");
                        }
                    });
                }
            }
        });
    }


    @Override
    public void asyDownLoadFile(String fileName, HomeCallBack callBack){
        String file_path = Environment.getExternalStorageDirectory().toString()+"/EZOpenSDK/version/"+fileName;
        File imgFile = new File(file_path);
        if (imgFile.exists()){
            imgFile.delete();
        }
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                FTPutils ftPutils = new FTPutils();
                String localpath = Environment.getExternalStorageDirectory().toString()+"/EZOpenSDK/version";
                Boolean flag = ftPutils.connect(AlarmContant.ftp_ip,Integer.parseInt(AlarmContant.ftp_port),AlarmContant.name,AlarmContant.password);
                if (flag){
                    try {
                        ftPutils.downloadSingleFile(AlarmContant.apk_path + "/" + fileName, localpath, fileName, new FTPutils.FtpProgressListener() {
                            @Override
                            public void onFtpProgress(int currentStatus, long process, File targetFile, long currentSize, long serverSize) {
                                AppOperator.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (currentStatus == Constant.FTP_FILE_NOTEXISTS){
                                            callBack.onFailure("下载失败");
                                        }else if(currentStatus ==Constant.LOCAL_FILE_AIREADY_COMPLETE){
                                            callBack.onSuccess(0);
                                        }else{
                                            callBack.setdata(process);
                                        }
                                    }
                                });
                            }
                        });
                    }catch (Exception e) {
                        AppOperator.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.onFailure("error");
                            }
                        });
                    }
                }else{
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure("下载失败");
                        }
                    });
                }
            }
        });
    }


    @Override
    public void getDevices(HomeCallBack callBack){
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<EZCameraInfo> list_ezCamera = new ArrayList<>();
                    List<EZDeviceInfo> list_ezdevices = OSCApplication.getOpenSDK().getDeviceList(0,40);
                    if (list_ezdevices.size() != 0 ){
                        for (EZDeviceInfo ezDeviceInfo : list_ezdevices){
                            for (EZCameraInfo cameraInfo : ezDeviceInfo.getCameraInfoList()){
                                list_ezCamera.add(cameraInfo);
                            }
                        }
                    }
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            Map<String,List> m = new HashMap<>();
                            m.put("device", list_ezdevices);
                            m.put("camera", list_ezCamera);
                            callBack.setdata(m);
                        }
                    });
                } catch (BaseException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
