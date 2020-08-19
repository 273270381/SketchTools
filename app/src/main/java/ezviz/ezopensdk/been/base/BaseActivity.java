package ezviz.ezopensdk.been.base;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.baidu.mobstat.StatService;

import java.text.ParseException;
import java.util.List;
import butterknife.ButterKnife;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.ui.CommonToast;
import ezviz.ezopensdk.utils.DialogHelper;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by hejunfeng on 2020/7/16 0016
 */
public abstract class BaseActivity extends SwipeBackActivity implements BaseView {
    private boolean mIsDestroy;
    public static boolean IS_ACTIVE = true;
    private ProgressDialog _waitDialog;
    private AlertDialog _comformDialog;
    private ProgressDialog _updating;
    private Fragment mFragment;
    public abstract BasePresenter getPresenter();
    public abstract void initPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSwipeBackEnable(true);
        setContentView(getContentView());
        initPresenter();
        if (getPresenter() != null){
            getPresenter().attachView(this);
        }
        initWindow();//设置透明状态栏
        ButterKnife.bind(this);
        initData();
        initWidget();
        StatService.setAuthorizedState(this,true);
        StatService.setDebugOn(true);
    }

    protected abstract int getContentView();

    protected void initWidget() {
    }

    protected void initData() {
    }


    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        IS_ACTIVE = isOnForeground();
    }

    @Override
    protected void onDestroy() {
        mIsDestroy = true;
        super.onDestroy();
        if (getPresenter() != null){
            getPresenter().detachView();
        }
    }

    public boolean isDestroy() {
        return mIsDestroy;
    }

    protected void initWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 是否在前台
     *
     * @return isOnForeground APP是否在前台
     */
    protected boolean isOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();

        assert activityManager != null;
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }



    @Override
    public ProgressDialog showLoading() {
        return showLoading(R.string.loading);
    }

    @Override
    public ProgressDialog showLoading(int resid) {
        return showLoading(getString(resid));
    }

    @Override
    public ProgressDialog showLoading(String message) {
        if (_waitDialog == null) {
            _waitDialog = DialogHelper.getProgressDialog(this, message);
        }
        if (_waitDialog != null) {
            _waitDialog.setMessage(message);
            _waitDialog.show();
        }
        return _waitDialog;
    }

    @Override
    public AlertDialog showConfirmDialog(String msg , String title, DialogInterface.OnClickListener listener) {
        if (_comformDialog == null){
            _comformDialog = DialogHelper.getConfirmDialog(this, msg, listener).create();
        }
        if (_comformDialog != null){
            _comformDialog.setTitle(title);
            _comformDialog.show();
        }
        return _comformDialog;
    }

    @Override
    public ProgressDialog showUpdating(String title){
        if (_updating == null){
            _updating = DialogHelper.getProgressDialog(this);
        }
        if (_updating != null){
            _updating.setTitle(title);
            _updating.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            _updating.setCancelable(false);// 设置允许取消
            _updating.show();
        }
        return _updating;
    }

    @Override
    public void setProgress(Integer integer) {
        if (_updating != null){
            _updating.setProgress(integer.intValue());
        }
    }

    @Override
    public void hideLoading() {
        if (_waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void hideUpdating() {
        if (_updating != null){
            try {
                _updating.dismiss();
                _updating = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void hideConfirmDialog() {
        if (_comformDialog != null){
            try {
                _comformDialog.dismiss();
                _comformDialog = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showToast(String message, int icon, int gravity) {
        CommonToast toast = new CommonToast(this);
        toast.setMessage(message);
        toast.setMessageIc(icon);
        toast.setLayoutGravity(gravity);
        toast.show();
    }

    protected void addFragment(int frameLayoutId, Fragment fragment){
        if (fragment != null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (fragment.isAdded()){
                if (mFragment != null){
                    transaction.hide(mFragment).show(fragment);
                }else{
                    transaction.show(fragment);
                }
            }else {
                if (mFragment != null){
                    transaction.hide(mFragment).add(frameLayoutId, fragment);
                }else {
                    transaction.add(frameLayoutId, fragment);
                }
            }
            mFragment = fragment;
            transaction.commit();
        }
    }

    protected void replaceFragment(int frameLayoutId, Fragment fragment){
        if (fragment != null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(frameLayoutId,fragment);
            transaction.commit();
        }
    }
}
