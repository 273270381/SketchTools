package ezviz.ezopensdk.activity.home;

import java.util.List;

import ezviz.ezopensdk.been.base.BaseView;

/**
 * Created by hejunfeng on 2020/7/21 0021
 */
public interface HomeView extends BaseView {
    /**
     * 添加广告栏
     */
    void addBanner(List ls, List ls2);


    /**
     * 下载APK
     */
    void showUpdateDialog(String s1, String s2);

    /**
     * 安装APK
     */
    void installApk(String s);

    /**
     * 发送数据
     * @param obj
     */
    void sendData(Object obj);

    void startDownLoadService(String fileName);
}
