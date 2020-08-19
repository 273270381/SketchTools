package ezviz.ezopensdk.been.base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

/**
 * Created by hejunfeng on 2020/7/17 0017
 */
public interface BaseView {

    /**
     * 显示下载进度对话框
     */
    ProgressDialog showUpdating(String s);

    void setProgress(Integer integer);

    /**
     * 隐藏下载进度对话框
     */
    void hideUpdating();

    /**
     * 显示确认对话框
     */
    AlertDialog showConfirmDialog(String msg, String title, DialogInterface.OnClickListener listener);

    /**
     * 隐藏确认对话框
     */
    void hideConfirmDialog();
    /**
     * 显示正在加载进度框
     */
    ProgressDialog showLoading();
    /**
     * 显示正在加载进度框
     */
    ProgressDialog showLoading(int resid);
    /**
     * 显示正在加载进度框
     */
    ProgressDialog showLoading(String msg);
    /**
     * 隐藏正在加载进度框
     */
    void hideLoading();
    /**
     * 显示提示
     */
    void showToast(String message, int icon, int gravity);

    void showToast(String message);

}
