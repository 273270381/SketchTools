package ezviz.ezopensdk.activity.home;

import android.content.DialogInterface;

import com.videogo.openapi.bean.EZDeviceInfo;

import java.util.List;
import java.util.Map;

import ezviz.ezopensdk.been.DownLoadService;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.utils.ServiceUtils;

/**
 * Created by hejunfeng on 2020/7/21 0021
 */
public class HomePresenter extends BasePresenter<HomeView> {
    private HomeModelImpl homeModel;

    public HomePresenter(HomeModelImpl homeModel) {
        this.homeModel = homeModel;
    }


    /**
     * 检查更新
     * @param a
     */
    public void checkApk(int a){
        if (!isViewAttached()) {
            return;
        }
        getView().showLoading();
        homeModel.checkUpdate(new HomeCallBack() {

            @Override
            public void getVersion(String versionName, String fileName, int versionCode) {
                if (getView() != null){
                    getView().hideLoading();
                    if (a >= versionCode){
                        getView().showToast("当前已经是最新版本");
                    }else{
                        //更新apk
                        getView().showUpdateDialog(versionName,fileName);
                    }
                }
            }

            @Override
            public void onSuccess(Object data) {

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

    /**
     * 下载apk
     * @param versionName
     * @param fileName
     */
    public void UpdateDialog(String versionName, String fileName) {
        if (!isViewAttached()) {
            return;
        }
        getView().showConfirmDialog("最新版本："+versionName, "版本更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getView() != null){
                    getView().startDownLoadService(fileName);
                }
                //getView().showUpdating("APK文件下载中，请稍候...");
                //开始下载任务
//                homeModel.asyDownLoadFile(fileName, new HomeCallBack() {
//
//                    @Override
//                    public void setdata(Object obj) {
//                        getView().setProgress(((Long) obj).intValue());
//                        if (((Long) obj).intValue() == 100){
//                            getView().hideUpdating();
//                            getView().showToast("下载完成");
//                            getView().installApk(fileName);
//                        }
//                    }
//
//                    @Override
//                    public void onSuccess(Object data) {
//                        getView().hideUpdating();
//                        getView().showToast("下载完成");
//                        getView().installApk(fileName);
//                    }
//
//                    @Override
//                    public void onFailure(String msg) {
//                        getView().hideUpdating();
//                        getView().showToast(msg);
//                    }
//
//                });
            }
        });

    }

    /**
     * 添加广告栏
     */
    public void addBanner(){
        if (!isViewAttached()){
            return;
        }
        homeModel.addBanner(new HomeCallBack() {

            @Override
            public void showBanner(List ls, List l) {
                if (getView() != null){
                    getView().addBanner(ls, l);
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

    public void getDevices(){
        if (!isViewAttached()){
            return;
        }
        homeModel.getDevices(new HomeCallBack() {
            @Override
            public void setdata(Object obj) {
                if (getView() != null){
                    getView().sendData(obj);
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
