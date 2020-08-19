package ezviz.ezopensdk.activity.caui.cameralist;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.videogo.constant.Constant;
import com.videogo.constant.IntentConsts;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.util.DateTimeUtil;
import com.videogo.util.LogUtil;
import com.videogo.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.adapter.EZCameraListAdapter;
import ezviz.ezopensdk.activity.caui.decicesetting.EZDeviceSettingActivity;
import ezviz.ezopensdk.activity.caui.playback.PlayBackListActivity;
import ezviz.ezopensdk.activity.caui.playback.RemoteListContant;
import ezviz.ezopensdk.activity.caui.realpaly.EZRealPlayActivity;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.utils.ActivityUtils;
import ezviz.ezopensdk.utils.TcpClient;
import ezviz.ezopensdk.utils.ToastNotRepeat;
import ezviz.ezopensdk.view.pulltorefresh.IPullToRefresh;
import ezviz.ezopensdk.view.pulltorefresh.LoadingLayout;
import ezviz.ezopensdk.view.pulltorefresh.PullToRefreshBase;
import ezviz.ezopensdk.view.pulltorefresh.PullToRefreshFooter;
import ezviz.ezopensdk.view.pulltorefresh.PullToRefreshHeader;
import ezviz.ezopensdk.view.pulltorefresh.PullToRefreshListView;
import ezviz.ezopensdk.view.spinner.FuzzyMatchSpinner;
import ezviz.ezopensdk.view.spinner.LoBody;
import static ezviz.ezopensdk.activity.OSCApplication.getOpenSDK;
import static ezviz.ezopensdk.been.AlarmContant.modeArrayData;

/**
 * Created by hejunfeng on 2020/7/22 0022
 */
public class EZCameraListActivity extends BaseActivity implements View.OnClickListener, CameraView {

    @BindView(R.id.camera_listview)
    PullToRefreshListView mListView;
    @BindView(R.id.no_camera_tip_ly)
    LinearLayout mNoCameraTipLy;
    @BindView(R.id.get_camera_fail_tip_ly)
    LinearLayout mGetCameraFailTipLy;
    @BindView(R.id.text_my)
    TextView mMyDevice;
    @BindView(R.id.btn_add)
    Button mAddBtn;
    @BindView(R.id.btn_user)
    Button mUserBtn;
    @BindView(R.id.get_camera_list_fail_tv)
    TextView mCameraFailTipTv;
    @BindView(R.id.fuzzysp_mode_ddjdevwrite)
    FuzzyMatchSpinner spMode;
    @BindView(R.id.connect)
    Button connectTcp;



    private View mNoMoreView;
    private BroadcastReceiver mReceiver = null;
    private EZCameraListAdapter mAdapter = null;
    private CameraPresenter cameraPresenter;
    private String TAG = "EZCameraListActivity";
    private List<EZDeviceInfo> deviceInfoList = new ArrayList<>();
    private List<LoBody> loBodyList = new ArrayList<>();
    private List<String> arrayList = new ArrayList<>();
    private List<EZCameraInfo> cameraInfoList = new ArrayList<>();
    private TcpClient.OnDataReceiveListener dataReceiveListener;
    private boolean bIsFromSetting = false;
    private TcpClient tcpClient;
    public final static int TAG_CLICK_PLAY = 1;
    public final static int TAG_CLICK_REMOTE_PLAY_BACK = 2;
    public final static int TAG_CLICK_SET_DEVICE = 3;
    public final static int TAG_CLICK_ALARM_LIST = 4;
    private int mClickType;
    private String no = "0";
    private String send_tx;
    public final static int REQUEST_CODE = 100;
    public final static int RESULT_CODE = 101;


    @Override
    public BasePresenter getPresenter() {
        return cameraPresenter;
    }

    @Override
    public void initPresenter() {
        cameraPresenter = new CameraPresenter(new CameraModelImpl());
    }

    @Override
    protected int getContentView() {
        return R.layout.cameralist_page;
    }

    @Override
    public void showToast(String message) {
        ToastNotRepeat.show(getApplicationContext(),message);
    }

    public static void show(Context context){
        Intent intent = new Intent(context, EZCameraListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initData() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                LogUtil.debugLog(TAG, "onReceive:" + action);
                if (action.equals(Constant.ADD_DEVICE_SUCCESS_ACTION)) {
                    refreshButtonClicked();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ADD_DEVICE_SUCCESS_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void initWidget() {
        TcpClient.getInstance().setOnDataReceiveListener(dataReceiveListener);
        mNoMoreView = getLayoutInflater().inflate(R.layout.no_device_more_footer, null);
        mAdapter = new EZCameraListAdapter(getApplicationContext());
        setListView();
        mAdapter.setOnClickListener(new EZCameraListAdapter.OnClickListener() {
            @Override
            public void onPlayClick(BaseAdapter adapter, View view, int position) {
                mClickType = TAG_CLICK_PLAY;
                final EZCameraInfo cameraInfo = mAdapter.getItem(position);
                final EZDeviceInfo deviceInfo = mAdapter.getDeviceInfoItem(position);
                if (cameraInfo == null) {
                    LogUtil.d(TAG, "cameralist is null or cameralist size is 0");
                    return;
                }
                if (cameraInfo != null) {
                    Intent intent = new Intent(EZCameraListActivity.this, EZRealPlayActivity.class);
                    intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
                    intent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, deviceInfo);
                    intent.putExtra("titlename",cameraInfo.getCameraName());
                    startActivityForResult(intent, REQUEST_CODE);
                    return;
                }
            }

            @Override
            public void onDeleteClick(BaseAdapter adapter, View view, int position) {
                // TODO: 2020/7/27 0027  
            }

            @Override
            public void onAlarmListClick(BaseAdapter adapter, View view, int position) {
                // TODO: 2020/7/27 0027  
            }

            @Override
            public void onRemotePlayBackClick(BaseAdapter adapter, View view, int position) {
                mClickType = TAG_CLICK_REMOTE_PLAY_BACK;
                EZCameraInfo cameraInfo = mAdapter.getItem(position);
                if (cameraInfo == null) {
                    LogUtil.d(TAG, "cameralist is null or cameralist size is 0");
                    return;
                }
                if (cameraInfo != null) {
                    LogUtil.d(TAG, "cameralist have one camera");
                    Intent intent = new Intent(EZCameraListActivity.this, PlayBackListActivity.class);
                    intent.putExtra(RemoteListContant.QUERY_DATE_INTENT_KEY, DateTimeUtil.getNow());
                    intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
                    startActivity(intent);
                    return;
                }
            }

            @Override
            public void onSetDeviceClick(BaseAdapter adapter, View view, int position) {
                mClickType = TAG_CLICK_SET_DEVICE;
                EZDeviceInfo deviceInfo = mAdapter.getDeviceInfoItem(position);
                EZCameraInfo cameraInfo = mAdapter.getItem(position);
                Intent intent = new Intent(EZCameraListActivity.this, EZDeviceSettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(IntentConsts.EXTRA_DEVICE_INFO, deviceInfo);
                bundle.putParcelable(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
                intent.putExtra("Bundle", bundle);
                startActivity(intent);
                //bIsFromSetting = true;
            }

            @Override
            public void onDevicePictureClick(BaseAdapter adapter, View view, int position) {
                // TODO: 2020/7/27 0027  
            }

            @Override
            public void onDeviceVideoClick(BaseAdapter adapter, View view, int position) {
                // TODO: 2020/7/27 0027  
            }

            @Override
            public void onDeviceDefenceClick(BaseAdapter adapter, View view, int position) {
                // TODO: 2020/7/27 0027  
            }
        });
        dataReceiveListener = new TcpClient.OnDataReceiveListener() {
            @Override
            public void onConnectSuccess() {
                byte[] data = send_tx.getBytes();
                tcpClient.sendByteCmd(data,1001);
                Log.d("Socket","onDataReceive connect success");
            }

            @Override
            public void onConnectFail() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        Log.e("Socket","onDataReceive connect fail");
                        Toast.makeText(EZCameraListActivity.this,"尚未连接，请连接Socket",Toast.LENGTH_LONG).show();
                    }
                },2000);
            }

            @Override
            public void onDataReceive(byte[] buffer, int size, int requestCode) {
                //获取有效长度的数据
                byte[] data = new byte[size];
                System.arraycopy(buffer, 0, data, 0, size);
                final String oxValue = new String(data);
                tcpClient.disconnect();
                cameraPresenter.getTcpresult(oxValue);
            }
        };
    }

    private void setListView(){
        mListView.setLoadingLayoutCreator(new PullToRefreshBase.LoadingLayoutCreator() {

            @Override
            public LoadingLayout create(Context context, boolean headerOrFooter, PullToRefreshBase.Orientation orientation) {
                if (headerOrFooter) {
                    return new PullToRefreshHeader(context);
                } else {
                    return new PullToRefreshFooter(context, PullToRefreshFooter.Style.EMPTY_NO_MORE);
                }
            }
        });
        mListView.setMode(IPullToRefresh.Mode.BOTH);
        mListView.setOnRefreshListener(new IPullToRefresh.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView, boolean headerOrFooter) {
                int c = mAdapter.getCount();
                cameraPresenter.getCamersInfoListTask(getApplicationContext(), headerOrFooter, mAdapter.getCount());
            }
        });

        mListView.getRefreshableView().addFooterView(mNoMoreView);
        mListView.setAdapter(mAdapter);
        mListView.getRefreshableView().removeFooterView(mNoMoreView);


        List<String> datalist = Arrays.asList(modeArrayData);
        Gson gson = new Gson();
        List<JsonObject> list_objects = gson.fromJson(datalist.toString(),new TypeToken<List<JsonObject>>() {}.getType());
        for (JsonObject object : list_objects){
            LoBody loBody = gson.fromJson(object,LoBody.class);
            loBodyList.add(loBody);
            arrayList.add(loBody.getName());
        }
        ArrayAdapter modelAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,arrayList.toArray(new String[arrayList.size()]));
        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMode.setAdapter(modelAdapter,loBodyList);

    }

    /**
     * 刷新点击
     */
    private void refreshButtonClicked() {
        mListView.setVisibility(View.VISIBLE);
        mNoCameraTipLy.setVisibility(View.GONE);
        mGetCameraFailTipLy.setVisibility(View.GONE);
        mListView.setMode(IPullToRefresh.Mode.BOTH);
        mListView.setRefreshing();
    }


    @OnClick({R.id.btn_user, R.id.btn_add, R.id.fuzzysp_mode_ddjdevwrite, R.id.connect, R.id.camera_list_refresh_btn, R.id.no_camera_tip_ly})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connect:
                String et_send = spMode.getText();
                cameraPresenter.sendTcp(et_send, arrayList,loBodyList);
                break;
            case R.id.fuzzysp_mode_ddjdevwrite:
                if(spMode.getAlertDiag() == null) {
                    spMode.createAlertDialog();
                    Display defaultDisplay = getWindowManager().getDefaultDisplay();
                    Point point = new Point();
                    defaultDisplay.getSize(point);
                    int x = point.x;
                    int y = point.y;
                    spMode.setAlertDiag(x-100,y-300);
                } else if(!spMode.getAlertDiag().isShowing()) {
                    spMode.getAlertDiag().show();
                }
                break;
            case R.id.btn_user:
                finish();
                break;
            case R.id.btn_add:
                //Intent intent = new Intent(EZCameraListActivity.this, CaptureActivity.class);
                //startActivity(intent);
                break;
            case R.id.camera_list_refresh_btn:
            case R.id.no_camera_tip_ly:
                refreshButtonClicked();
                break;
        }
    }


    /**
     * 从服务器获取最新事件消息
     */
    @Override
    public void getCameraList(List<EZDeviceInfo> result, boolean mHeaderOrFooter) {
        mListView.onRefreshComplete();
        if (result != null) {
            if (mHeaderOrFooter) {
                CharSequence dateText = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
                for (LoadingLayout layout : mListView.getLoadingLayoutProxy(true, false).getLayouts()) {
                    ((PullToRefreshHeader) layout).setLastRefreshTime(":" + dateText);
                }
                mAdapter.clearItem();
            }
            if (mAdapter.getCount() == 0 && result.size() == 0) {
                mListView.setVisibility(View.GONE);
                mNoCameraTipLy.setVisibility(View.VISIBLE);
                mGetCameraFailTipLy.setVisibility(View.GONE);
                mListView.getRefreshableView().removeFooterView(mNoMoreView);
            } else if (result.size() < 10) {
                mListView.setFooterRefreshEnabled(false);
                mListView.getRefreshableView().addFooterView(mNoMoreView);
            } else if (mHeaderOrFooter) {
                mListView.setFooterRefreshEnabled(true);
                mListView.getRefreshableView().removeFooterView(mNoMoreView);
            }
            deviceInfoList.addAll(result);
            addCameraList(result);
            mAdapter.notifyDataSetChanged();
            cameraInfoList = mAdapter.getCameraInfoList();
            //更新封面
            cameraPresenter.refreshPic(cameraInfoList);
        }
    }

    @Override
    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(int errorCode) {
        switch (errorCode) {
            case ErrorCode.ERROR_WEB_SESSION_ERROR:
            case ErrorCode.ERROR_WEB_SESSION_EXPIRE:
                ActivityUtils.handleSessionException(EZCameraListActivity.this);
                break;
            default:
                if (mAdapter.getCount() == 0) {
                    mListView.setVisibility(View.GONE);
                    mNoCameraTipLy.setVisibility(View.GONE);
                    mCameraFailTipTv.setText(Utils.getErrorTip(getApplicationContext(), R.string.get_camera_list_fail, errorCode));
                    mGetCameraFailTipLy.setVisibility(View.VISIBLE);
                } else {
                    Utils.showToast(getApplicationContext(), R.string.get_camera_list_fail, errorCode);
                }
                break;
        }
    }

    @Override
    public void sendTcp(TcpClient tcpClient, String no, String text) {
        this.tcpClient = tcpClient;
        this.no = no;
        this.send_tx = text;
    }

    @Override
    public void tcpResult(Boolean b) {
        if (b){
            //Activity跳转
            EZCameraInfo cameraInfo = getCameraInfo(cameraInfoList,no);
            EZDeviceInfo deviceInfo = deviceInfoList.get(0);
            Log.d(TAG,"deviceInfoList.size="+deviceInfoList.size());
            Intent intent = new Intent(EZCameraListActivity.this, EZRealPlayActivity.class);
            intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
            intent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra("titlename",spMode.getText());
            startActivityForResult(intent, REQUEST_CODE);
        }
    }


    private EZCameraInfo getCameraInfo(List<EZCameraInfo> cameraInfos , String no){
        if (no!=null&&!no.equals("")){
            for (EZCameraInfo cameraInfo : cameraInfos){
                if (cameraInfo.getCameraNo() == Integer.parseInt(no)){
                    return cameraInfo;
                }
            }
        }
        return cameraInfoList.get(0);
    }

    private void addCameraList(List<EZDeviceInfo> result) {
        int count = result.size();
        EZDeviceInfo item = null;
        for (int i = 0; i < count; i++) {
            item = result.get(i);
            List<EZCameraInfo> CameraInfo_list = item.getCameraInfoList();
            for (EZCameraInfo cameraInfo : CameraInfo_list){
                mAdapter.addItem(cameraInfo,item);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bIsFromSetting || (mAdapter != null && mAdapter.getCount() == 0)) {
            refreshButtonClicked();
            bIsFromSetting = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.shutDownExecutorService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (tcpClient!=null){
            if (tcpClient.isConnect()){
                tcpClient.disconnect();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, R.string.update_exit).setIcon(R.drawable.exit_selector);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 得到被点击的item的itemId
        switch (item.getItemId()) {
            case 1:
                // 对应的ID就是在add方法中所设定的Id
                popLogoutDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 弹出登出对话框
     *
     * @see
     * @since V1.0
     */
    private void popLogoutDialog() {
        AlertDialog.Builder exitDialog = new AlertDialog.Builder(EZCameraListActivity.this);
        exitDialog.setTitle(R.string.exit);
        exitDialog.setMessage(R.string.exit_tip);
        exitDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new LogoutTask().execute();
            }
        });
        exitDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        exitDialog.show();
    }


    private class LogoutTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected Void doInBackground(Void... params) {
            getOpenSDK().logout();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            hideLoading();
            ActivityUtils.goToLoginAgain(EZCameraListActivity.this);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_CODE) {
            if (requestCode == REQUEST_CODE) {
                String deviceSerial = intent.getStringExtra(IntentConsts.EXTRA_DEVICE_ID);
                int cameraNo = intent.getIntExtra(IntentConsts.EXTRA_CAMERA_NO, -1);
                int videoLevel = intent.getIntExtra("video_level", -1);
                if (TextUtils.isEmpty(deviceSerial)) {
                    return;
                }
                if (videoLevel == -1 || cameraNo == -1) {
                    return;
                }

                if (mAdapter.getCameraInfoList() != null) {
                    for (EZCameraInfo cameraInfo : mAdapter.getCameraInfoList()) {
                        if (cameraInfo.getDeviceSerial().equals(deviceSerial)) {
                            if (cameraInfo != null) {
                                if (cameraInfo.getCameraNo() == cameraNo) {
                                    cameraInfo.setVideoLevel(videoLevel);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}
