package ezviz.ezopensdk.activity.splash;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.UUID;
import butterknife.BindView;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.login.LoginActivity;
import ezviz.ezopensdk.activity.OSCApplication;
import ezviz.ezopensdk.been.Launcher;
import ezviz.ezopensdk.been.base.BaseFragment;
import ezviz.ezopensdk.view.CountDownView;

/**
 * 首页启动广告
 * Created by hejunfeng on 2020/7/17 0017
 */

public class AdFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.countDownView)
    CountDownView mCountDownView;
    private Launcher mLauncher;

    @BindView(R.id.iv_ad)
    ImageView mImageAd;

    private static boolean isClickAd;

    public static AdFragment newInstance(Launcher launcher) {

        AdFragment fragment = new AdFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("launcher", launcher);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ad;
    }


    @Override
    protected void initBundle(Bundle bundle) {
        super.initBundle(bundle);
        mLauncher = (Launcher) bundle.getSerializable("launcher");
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        isClickAd = false;
        Glide.with(mContext)
                .load(OSCApplication.getInstance().getCacheDir() + "/launcher")
                .signature(new StringSignature(UUID.randomUUID().toString()))
                .fitCenter()
                .into(mImageAd);
        mCountDownView.setListener(new CountDownView.OnProgressListener() {
            @Override
            public void onFinish() {
                if (isClickAd || mContext == null)
                    return;
                LoginActivity.show(mContext);
                mCountDownView.cancel();
                getActivity().finish();
            }
        });
        mCountDownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickAd || mContext == null)
                    return;
                mCountDownView.cancel();
                LoginActivity.show(mContext);
                getActivity().finish();
            }
        });
        mCountDownView.start();
    }

    @Override
    public void onClick(View v) {

    }

//    @OnClick({R.id.iv_ad, R.id.iv_logo})
//    @Override
//    public void onClick(View v) {
//        if (TextUtils.isEmpty(mLauncher.getHref())) {
//            return;
//        }
//        isClickAd = true;
//        mCountDownView.cancel();
//        UIHelper.showUrlRedirect(mContext, mLauncher.getHref());
//        getActivity().finish();
//    }
}
