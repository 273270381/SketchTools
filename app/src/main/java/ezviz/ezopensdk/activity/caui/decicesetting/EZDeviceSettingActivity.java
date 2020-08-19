package ezviz.ezopensdk.activity.caui.decicesetting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.videogo.constant.IntentConsts;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.openapi.bean.EZDeviceVersion;
import com.videogo.widget.TitleBar;

import butterknife.BindView;
import butterknife.OnClick;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.utils.ActivityUtils;
import ezviz.ezopensdk.utils.PackageUtils;
import ezviz.ezopensdk.utils.ToastNotRepeat;

import static ezviz.ezopensdk.activity.OSCApplication.getOpenSDK;

/**
 * Created by hejunfeng on 2020/7/30 0030
 */
public class EZDeviceSettingActivity extends BaseActivity implements DeviceView, View.OnClickListener {

    @BindView(R.id.title_bar)
    TitleBar mTitleBar;
    @BindView(R.id.device_info_layout)
    ViewGroup mDeviceInfoLayout;
    @BindView(R.id.device_name)
    TextView mDeviceNameView;
    @BindView(R.id.device_type_sn)
    TextView mDeviceTypeSnView;
    @BindView(R.id.ez_device_serial_layout)
    ViewGroup mDeviceSNLayout;
    @BindView(R.id.defence_layout)
    ViewGroup mDefenceLayout;
    @BindView(R.id.defence)
    TextView mDefenceView;
    @BindView(R.id.defence_state)
    TextView mDefenceStateView;
    @BindView(R.id.defence_plan_parent_layout)
    ViewGroup mDefencePlanParentLayout;
    @BindView(R.id.defence_plan_arrow)
    View mDefencePlanArrowView;
    @BindView(R.id.defence_toggle_button)
    Button mDefenceToggleButton;
    @BindView(R.id.storage_layout)
    ViewGroup mStorageLayout;
    @BindView(R.id.storage_notice)
    View mStorageNoticeView;
    @BindView(R.id.version_layout)
    ViewGroup mVersionLayout;
    @BindView(R.id.version)
    TextView mVersionView;
    @BindView(R.id.version_newest)
    View mVersionNewestView;
    @BindView(R.id.version_notice)
    View mVersionNoticeView;
    @BindView(R.id.version_arrow)
    View mVersionArrowView;
    @BindView(R.id.current_version)
    TextView mCurrentVersionTextView;
    @BindView(R.id.encrypt_parent_layout)
    ViewGroup mEncryptParentLayout;
    @BindView(R.id.encrypt_button)
    Button mEncryptButton;
    @BindView(R.id.modify_password_layout)
    ViewGroup mModifyPasswordLayout;
    @BindView(R.id.device_delete)
    View mDeviceDeleteView;
    @BindView(R.id.ez_device_serial)
    TextView mDeviceSerialTextView;
    private EZDeviceInfo mEZDeviceInfo = null;
    private EZCameraInfo ezCameraInfo = null;
    private EZDeviceVersion mDeviceVersion = null;
    private int mCurrentVersionCode;
    private DevicePresenter devicePresenter;
    private int mErrorCode = 0;
    private String TAG = "EZDeviceSettingActivity";

    @Override
    public BasePresenter getPresenter() {
        return devicePresenter;
    }

    @Override
    public void initPresenter() {
        devicePresenter = new DevicePresenter(new DeviceModelImpl());
    }

    @Override
    protected int getContentView() {
        return R.layout.device_setting_page;
    }

    @Override
    public void showToast(String message) {
        ToastNotRepeat.show(getApplicationContext(), message);
    }

    @Override
    protected void initData() {
        devicePresenter.getVersionInfo();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("Bundle");
        mEZDeviceInfo = bundle.getParcelable(IntentConsts.EXTRA_DEVICE_INFO);
        ezCameraInfo = bundle.getParcelable(IntentConsts.EXTRA_CAMERA_INFO);
        if (mEZDeviceInfo == null) {
            showToast("设备未添加或已删除");
            finish();
        }
    }

    @Override
    protected void initWidget() {
        mTitleBar.setTitle(R.string.ez_setting_1);
        mTitleBar.setStyle(Color.rgb(0xff, 0xff, 0xff), getResources().getDrawable(R.color.blue_bg),
                getResources().getDrawable(R.drawable.message_back_selector_1));
        mTitleBar.addBackButton(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        devicePresenter.getDeviceInfo(mEZDeviceInfo);
    }


    @Override
    public void getVersion(int versionCode, String versionName, String fileName) {
        mVersionView.setText(versionName);
        mCurrentVersionCode = PackageUtils.getVersionCode(EZDeviceSettingActivity.this);
        if (mCurrentVersionCode >= versionCode) {
            mVersionNoticeView.setVisibility(View.GONE);
        } else {
            mVersionNoticeView.setVisibility(View.GONE);
        }
    }


    @Override
    public void getDeviceInfo(EZDeviceVersion deviceVersion, Boolean b, int errorCode) {
        mDeviceVersion = deviceVersion;
        if (b) {
            setupDeviceInfo();
        } else {
            switch (mErrorCode) {
                case ErrorCode.ERROR_WEB_NET_EXCEPTION:
                    break;
                case ErrorCode.ERROR_WEB_SESSION_ERROR:
                    ActivityUtils.handleSessionException(EZDeviceSettingActivity.this);
                    break;
                case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_ERROR:
                    ActivityUtils.handleSessionException(EZDeviceSettingActivity.this);
                    break;
                default:
                    break;
            }
        }
    }


    private void setupDeviceInfo() {
        if (mEZDeviceInfo != null) {
            String typeSn = ezCameraInfo.getCameraName();
            mDeviceSerialTextView.setText(mEZDeviceInfo.getDeviceSerial());
            mDeviceNameView.setText(TextUtils.isEmpty(typeSn) ? "" : typeSn);
            mDeviceTypeSnView.setVisibility(View.GONE);
            mDefencePlanParentLayout.setVisibility(View.GONE);
            boolean bSupportDefence = true;
            if (bSupportDefence) {
                mDefenceView.setText(R.string.detail_defend_c1_c2_f1);
                mDefenceStateView.setTextColor(getResources().getColorStateList(R.color.on_off_text_selector));
//                mDefenceStateView.setText(mDevice.isDefenceOn() ? R.string.on : R.string.off);
//                mDefenceStateView.setEnabled(mEZCameraInfo.getDefence() == 1);
                boolean isDefenceEnable = (mEZDeviceInfo.getDefence() != 0);
                mDefenceToggleButton.setBackgroundResource(isDefenceEnable ? R.drawable.autologin_on
                        : R.drawable.autologin_off);
                mDefenceLayout.setVisibility(View.VISIBLE);
//				mDefenceLayout.setTag(supportMode);
//				mDefenceLayout.setOnClickListener(mOnClickListener); // dont allow to click the list
            }
            // 存储状态部分
            mStorageNoticeView.setVisibility(View.VISIBLE);
            // TODO
            mStorageLayout.setVisibility(View.VISIBLE);
            // 版本部分
            if (mEZDeviceInfo.getStatus() == 1 && mDeviceVersion != null) {
                boolean bHasUpgrade = (mDeviceVersion.getIsNeedUpgrade() != 0);
                //mCurrentVersionTextView.setText(mDeviceVersion.getCurrentVersion());
                mCurrentVersionTextView.setText(PackageUtils.getVersionName(this));
                //mVersionView.setText(mDeviceVersion.getNewestVersion());
                if (bHasUpgrade) {
                    mVersionNewestView.setVisibility(View.VISIBLE);
                } else {
                    mVersionNewestView.setVisibility(View.GONE);
                }
//                bHasUpgrade = true;// TODO stub
                if (bHasUpgrade) {
                    //mVersionNoticeView.setVisibility(View.VISIBLE);
                    mVersionArrowView.setVisibility(View.VISIBLE);
                } else {
                    //mVersionNoticeView.setVisibility(View.GONE);
                    mVersionArrowView.setVisibility(View.GONE);
                    mVersionLayout.setOnClickListener(null);
                }
                mVersionLayout.setVisibility(View.VISIBLE);
            } else {
                mVersionLayout.setVisibility(View.GONE);
            }
            // 视频图片加密部分
            boolean bSupportEncrypt = true;
            if (!bSupportEncrypt) {
                mEncryptParentLayout.setVisibility(View.GONE);
            } else {
                mEncryptButton.setBackgroundResource((mEZDeviceInfo.getIsEncrypt() == 1) ? R.drawable.autologin_on : R.drawable.autologin_off);
                boolean bSupportChangePwd = false;
                if (!bSupportChangePwd) {
                    mModifyPasswordLayout.setVisibility(View.GONE);
                } else {
                    mModifyPasswordLayout.setVisibility(View.VISIBLE);
                }

                mEncryptParentLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        setupDeviceInfo();
    }

    @OnClick({R.id.version_notice, R.id.defence_toggle_button})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.version_notice:
                // TODO: 2020/8/3  版本更新
                break;
            default:
                break;
        }
    }
}
