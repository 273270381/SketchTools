package ezviz.ezopensdk.activity.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.videogo.openapi.bean.EZDeviceInfo;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.OnClick;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.arcmap.ArcMapActivity;
import ezviz.ezopensdk.activity.baidumap.BaiduMapActivity;
import ezviz.ezopensdk.activity.caui.cameralist.EZCameraListActivity;
import ezviz.ezopensdk.activity.scanpic.ScanPicActivity;
import ezviz.ezopensdk.activity.scanvideo.ScanVideoActivity;
import ezviz.ezopensdk.activity.warning.WarningActivity;
import ezviz.ezopensdk.been.AppConfig;
import ezviz.ezopensdk.been.AppContext;
import ezviz.ezopensdk.been.DownLoadService;
import ezviz.ezopensdk.been.UpdateService;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BaseApplication;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.jpush.TagAliasOperatorHelper;
import ezviz.ezopensdk.utils.OSCSharedPreference;
import ezviz.ezopensdk.utils.PackageUtils;
import ezviz.ezopensdk.utils.ServiceUtils;
import ezviz.ezopensdk.utils.ToastNotRepeat;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import static ezviz.ezopensdk.jpush.TagAliasOperatorHelper.sequence;

/**
 * Created by hejunfeng on 2020/7/20 0020
 */
public class HomeActivity extends BaseActivity implements HomeView, View.OnClickListener,
        EasyPermissions.PermissionCallbacks {

    @BindView(R.id.convenientBanner)
    ConvenientBanner convenientBanner;
    @BindView(R.id.gv_home)
    GridView homeGView;
    @BindView(R.id.update)
    TextView versionUpdate;
    @BindView(R.id.version_name)
    TextView versionName;

    private HomePresenter homePresenter;
    private int mCurrentVersionCode;
    private long mBackPressedTime;
    private static final int RC_EXTERNAL_STORAGE = 0x04;//存储权限
    private String TAG = "HomeActivity";
    private List<EZDeviceInfo> list_ezdevices = new ArrayList<>();
    private List<EZDeviceInfo> list_ezCamera = new ArrayList<>();
    private static String[] allpermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.REQUEST_INSTALL_PACKAGES,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    public BasePresenter getPresenter() {
        return homePresenter;
    }

    @Override
    public void initPresenter() {
        homePresenter = new HomePresenter(new HomeModelImpl());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_home;
    }


    @Override
    protected void initData() {
        requestPermission();
        homePresenter.addBanner();
        homePresenter.getDevices();
        AppContext.set(AppConfig.KEY_DOUBLE_CLICK_EXIT, true);
        //设置别名
        String userid = OSCSharedPreference.getInstance().getUserId();
        Boolean isSuccess = OSCSharedPreference.getInstance().getBooleanUserId();
        if (!isSuccess){
            TagAliasOperatorHelper.getInstance().handleAction(getApplicationContext(),sequence,userid);
        }
    }

    @Override
    protected void initWidget() {
        versionUpdate.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mCurrentVersionCode = PackageUtils.getVersionCode(HomeActivity.this);
        versionName.setText("版本号 "+PackageUtils.getVersionName(this));
        homeGView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView  img = (ImageView) view.findViewById(R.id.grid_icon);
                Animation animation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.item_img);
                img.startAnimation(animation);
                switch (position){
                    case 0:
                        if (list_ezdevices.size()!=0){
                            ArcMapActivity.show(view.getContext(),"devices_main",list_ezdevices);
                        }else{
                            ToastNotRepeat.show(view.getContext(),"请稍后重试！");
                        }
                        break;
                    case 1:
                        EZCameraListActivity.show(view.getContext());
                        break;
                    case 2:
                        if (list_ezdevices.size()!=0){
                            BaiduMapActivity.show(view.getContext(),"devices_baidu",list_ezdevices);
                        }else{
                            ToastNotRepeat.show(view.getContext(),"请稍后重试！");
                        }
                        break;
                    case 3:
                        if (list_ezCamera.size()!=0){
                            WarningActivity.show(view.getContext(), "cameras_pic", list_ezCamera);
                        }else{
                            ToastNotRepeat.show(view.getContext(),"请稍后重试！");
                        }
                        break;
                    case 4:
                        if (list_ezCamera.size()!=0){
                            ScanVideoActivity.show(view.getContext(), "cameras_pic", list_ezCamera);
                        }else{
                            ToastNotRepeat.show(view.getContext(),"请稍后重试！");
                        }
                        break;
                    case 5:
                        if (list_ezCamera.size()!=0){
                            ScanPicActivity.show(view.getContext(), "cameras_pic", list_ezCamera);
                        }else{
                            ToastNotRepeat.show(view.getContext(),"请稍后重试！");
                        }
                        break;
                }
            }
        });
    }

    /**
     * 设置广告栏
     */
    private void setConvenientBanner(List<Integer> imgs) {
        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new LocalImageHolderView();
            }
        },imgs).setPointViewVisible(true)//设置指示器是否可见
                .setPageIndicator(new int[]{R.mipmap.yuandianbantou,R.mipmap.yuandian});//设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
        convenientBanner.setManualPageable(true);//设置手动影响（设置了该项无法手动切换）
        convenientBanner.startTurning(2000);     //设置自动切换（同时设置了切换时间间隔）
        convenientBanner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);//设置指示器位置（左、中、右）
    }

    @Override
    public void addBanner(List ls, List l) {
        String[] from = {"icon", "iconName"};
        int[] to = {R.id.grid_icon, R.id.grid_iconName};
        setConvenientBanner(l);
        SimpleAdapter sim_adapter = new SimpleAdapter(this,ls, R.layout.home_grid_item, from, to);
        homeGView.setAdapter(sim_adapter);
    }


    @Override
    public void showUpdateDialog(String msg, String title) {
        boolean a = ServiceUtils.isServiceRunning(getApplicationContext(), UpdateService.getServiceName());
        if(a){
            showLoading("安装包下载中，请稍后...");
        }else{
            homePresenter.UpdateDialog(msg , title);
        }
    }

    @Override
    public void installApk(String updateFileName) {
        String file_path = Environment.getExternalStorageDirectory().toString()+"/EZOpenSDK/version/"+updateFileName;
        File imgFile = new File(file_path);
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(this, "ezviz.ezopensdk.fileprovider", imgFile);
            installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
        }else {
            installIntent.setDataAndType(Uri.fromFile(imgFile),"application/vnd.android.package-archive");
        }
        startActivity(installIntent);
        try {
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void sendData(Object obj) {
        list_ezdevices = (List<EZDeviceInfo>) ((Map)obj).get("device");
        list_ezCamera = (List<EZDeviceInfo>) ((Map)obj).get("camera");

    }

    @Override
    public void startDownLoadService(String fileName) {
        UpdateService.startService(this, fileName);
    }

    @Override
    public void showToast(String message) {
        ToastNotRepeat.show(getApplicationContext(),message);
    }

    @OnClick({R.id.update})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update:
                homePresenter.checkApk(mCurrentVersionCode);
                break;
        }
    }

    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    private void requestPermission(){
        if (EasyPermissions.hasPermissions(this, allpermissions)){
            Log.d(TAG, "onClick: 获取读写内存权限,Camera权限和wifi权限");
        }else{
            EasyPermissions.requestPermissions(this, "没有该权限，此应用程序可能无法正常工作，是否现在开启", RC_EXTERNAL_STORAGE, allpermissions);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    public class LocalImageHolderView implements Holder<Integer> {
        private ImageView imageView;
        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }
        @Override
        public void UpdateUI(Context context, int position, Integer data) {
            imageView.setImageResource(data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onBackPressed() {
        boolean isDoubleClick = BaseApplication.get(AppConfig.KEY_DOUBLE_CLICK_EXIT, true);
        Log.d(TAG,"diDoubleClick="+isDoubleClick);
        if (isDoubleClick) {
            long curTime = SystemClock.uptimeMillis();
            if ((curTime - mBackPressedTime) < (3 * 1000)) {
                finish();
            } else {
                mBackPressedTime = curTime;
                Toast.makeText(this, R.string.tip_double_click_exit, Toast.LENGTH_LONG).show();
            }
        } else {
            finish();
        }
    }
}
