package ezviz.ezopensdk.activity.splash;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;

import java.io.File;

import butterknife.BindView;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.Introduce.IntroduceActivity;
import ezviz.ezopensdk.activity.login.LoginActivity;
import ezviz.ezopensdk.activity.OSCApplication;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.Launcher;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.utils.CacheManager;
import ezviz.ezopensdk.utils.OSCSharedPreference;

/**
 * @description 
 * @author hejunfeng
 * @time 2020/7/17 0017 
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.frameSplash)
    FrameLayout mFlameSplash;

    @BindView(R.id.fl_content)
    FrameLayout mFlameContent;

    private boolean isShowAd;


    public static void show(Context context){
        context.startActivity(new Intent(context, SplashActivity.class));
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void initData() {
        super.initData();
        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1, false);
        StatService.start(this);
        Launcher launcher = CacheManager.readJson(OSCApplication.getInstance(), "Launcher", Launcher.class);
        String savePath = OSCApplication.getInstance().getCacheDir()+"/launcher";
        File file = new File(savePath);
        if (launcher != null && !launcher.isExpired() && file.exists()){
            isShowAd = true;
            mFlameSplash.setVisibility(View.GONE);
            mFlameContent.setVisibility(View.VISIBLE);
            addFragment(R.id.fl_content, AdFragment.newInstance(launcher));
        }
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 完成后进行跳转操作
                redirectTo();
            }
        });
    }

    private void redirectTo() {
        if (OSCSharedPreference.getInstance().isFirstInstall()) {
            IntroduceActivity.show(this);
        } else {
            LoginActivity.show(this);
        }
        finish();
    }

    @Override
    public void showToast(String message) {

    }
}
