package ezviz.ezopensdk.activity.alarmdet;

import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.squareup.picasso.Picasso;
import com.videogo.constant.Constant;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.exception.InnerException;
import com.videogo.openapi.EZConstants;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.remoteplayback.RemoteFileInfo;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.LocalInfo;
import com.videogo.util.LogUtil;
import com.videogo.util.MediaScanner;
import com.videogo.util.RotateViewUtil;
import com.videogo.util.SDCardUtil;
import com.videogo.util.Utils;
import com.videogo.widget.CheckTextButton;
import com.videogo.widget.CustomRect;
import com.videogo.widget.CustomTouchListener;
import com.videogo.widget.TitleBar;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.adapter.ImageViewRecyclerAdapter;
import ezviz.ezopensdk.activity.alarm.AsyncImageLoaderPic;
import ezviz.ezopensdk.activity.caui.playback.RemoteListContant;
import ezviz.ezopensdk.activity.caui.playback.RootActivity;
import ezviz.ezopensdk.activity.caui.playback.WaitDialog;
import ezviz.ezopensdk.activity.common.ScreenOrientationHelper;
import ezviz.ezopensdk.activity.scanpic.PictureActivity;
import ezviz.ezopensdk.been.AlarmContant;
import ezviz.ezopensdk.been.AlarmMessage;
import ezviz.ezopensdk.been.SnCal;
import ezviz.ezopensdk.been.db.AlarmReaded;
import ezviz.ezopensdk.been.db.DBManager;
import ezviz.ezopensdk.been.db.PicFilePath;
import ezviz.ezopensdk.been.db.Verifcode;
import ezviz.ezopensdk.been.db.VideoFilePath;
import ezviz.ezopensdk.utils.AudioPlayUtil;
import ezviz.ezopensdk.utils.DataUtils;
import ezviz.ezopensdk.utils.EZUtils;
import ezviz.ezopensdk.utils.ImageUtil;
import ezviz.ezopensdk.utils.OkHttpUtil;
import ezviz.ezopensdk.utils.RemoteListUtil;
import ezviz.ezopensdk.utils.RoundTransform;
import ezviz.ezopensdk.utils.VerifyCodeInput;
import ezviz.ezopensdk.view.loadtextview.LoadingTextView;
import ezviz.ezopensdk.view.loadtextview.LoadingView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static ezviz.ezopensdk.activity.OSCApplication.getOpenSDK;
import static ezviz.ezopensdk.been.AlarmContant.short_str;

/**
 * Created by hejunfeng on 2020/8/12 0012
 */
public class AlarmDetail extends RootActivity implements SurfaceHolder.Callback , View.OnTouchListener ,
        View.OnClickListener , VerifyCodeInput.VerifyCodeInputListener {
    // 输入法管理类
    private InputMethodManager imm;
    private WaitDialog mWaitDlg = null;
    private AlarmMessage alarmMessage = null;
    private String mVerifyCode;
    private String ChanneNumber;
    private String CameraName = "";
    private TextView tx_message;
    private TextView tx_address;
    private TextView tx_creattime;
    private ImageView imageView;
    private DonutProgress donut_progress;
    private String address = "";
    public String imgpath;
    public HashMap<String,String> map;
    private AsyncImageLoaderPic asyncImageLoaderPic;
    private ImageViewRecyclerAdapter imageViewRecyclerAdapter;
    private List<HashMap<String,String>> url_list = new ArrayList<>();
    // 本地播放文件
    private RemoteFileInfo fileInfo;
    // 显示数据网络提示
    private boolean mShowNetworkTip = true;
    private List<EZCameraInfo> cameraInfoList = new ArrayList<>();
    private Handler handler ;
    // 本地信息
    private LocalInfo localInfo = null;
    private int mCaptureDisplaySec = 0;
    // 音频播放
    private AudioPlayUtil mAudioPlayUtil = null;

    private RotateViewUtil mRecordRotateViewUtil = null;
    private RelativeLayout remoteListPage = null;
    private Rect mRemotePlayBackRect = null;
    // 控制栏时间值
    private int mControlDisplaySec = 0;
    // 标题
    private TitleBar mTitleBar;
    // 定时器
    private Timer mUpdateTimer = null;
    // 定时器执行的任务
    private TimerTask mUpdateTimerTask = null;
    // 加载进度圈
    private LoadingTextView loadingBar;
    // 播放缓冲百分比
    private TextView remoteLoadingBufferTv, touchLoadingBufferTv;
    // 播放区域
    private RelativeLayout remotePlayBackArea;
    // 结束时间文本
    private TextView endTimeTV = null;
    // 关闭播放区域按钮
    private ImageButton exitBtn;
    // 左上角返回按钮
    private Button backBtn;
    // 播放界面SurfaceView
    private SurfaceView surfaceView = null;
    private TextView mRemotePlayBackRatioTv = null;
    private CustomTouchListener mRemotePlayBackTouchListener = null;
    // 是否显示播放控制区，默认为没有显示
    private boolean notShowControlArea = true;
    // 播放比例
    private float mPlayScale = 1;
    // 播放状态
    private int status = RemoteListContant.STATUS_INIT;
    // 流量限定提示框
    private AlertDialog mLimitFlowDialog = null;
    private int mCountDown = 10;
    private LinearLayout mRemotePlayBackRecordLy = null;
    // 播放进度条
    private SeekBar progressSeekbar = null;
    private ProgressBar progressBar = null;
    private ImageView matteImage;
    // 开始时间文本
    private TextView beginTimeTV = null;
    // 错误信息显示
    private TextView errorInfoTV;
    // 错误重播按钮
    private ImageButton errorReplay;
    // 播放控制区域
    private LinearLayout controlArea = null;
    private LinearLayout progressArea = null;
    // 拍照
    private ImageButton captureBtn = null;
    // 录像
    private ImageButton videoRecordingBtn = null;
    // 停止录像
    private ImageButton videoRecordingBtn_end = null;
    private View mRealPlayRecordContainer = null;
    // 下载按钮
    private LinearLayout downloadBtn = null;
    // 文件大小文本
    private TextView fileSizeText;
    // Loading图片
    private LoadingView loadingImgView;
    private LinearLayout loadingPbLayout;
    private boolean  bIsRecording = false;
    private String mRecordTime = null;
    // 流量统计
    private TextView flowTV = null;
    // 屏幕方向
    private int mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    // loading时停止出现的播放按钮
    private ImageButton loadingPlayBtn;
    // 暂停/播放按钮
    private ImageButton pauseBtn = null;
    // 声音按钮
    private ImageButton soundBtn = null;
    private EZPlayer mPlayer = null;
    // 重播和下一个播放 控制区域
    private LinearLayout replayAndNextArea = null;
    // 录像标记点
    private ImageView mRemotePlayBackRecordIv = null;
    // 录像时间
    private int mRecordSecond = 0;
    // 播放时间
    private TextView mRemotePlayBackRecordTv = null;
    // 重播按钮
    private ImageButton replayBtn;
    private long beginTime;
    private long endTime;
    private long sustainTime = 20*1000;
    private long forwardTime = 1*20*60*1000;
    // 下一个播放按钮
    private ImageButton nextPlayBtn;
    // 进度条拖动时的进度圈
    private LinearLayout touchProgressLayout;
    // 全屏按钮
    private CheckTextButton mFullscreenButton;
    private ScreenOrientationHelper mScreenOrientationHelper;
    // 是否暂停播放，默认为没有暂停
    private boolean notPause = true;
    // 当前流量 */
    private int mRealFlow = 0;
    // 存放上一次的流量 */
    private long mStreamFlow = 0;
    private RelativeLayout mControlBarRL;
    private TitleBar mLandscapeTitleBar = null;
    private Context context;
    private static String TAG= "PlaybackActivity2";
    private MyReceiver myReceiver;
    // 播放分辨率
    private float mRealRatio = Constant.LIVE_VIEW_RATIO;
    private Handler playBackHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // 片段播放完毕
                // 380061即开始时间>=结束时间，播放完成
                case ErrorCode.ERROR_CAS_RECORD_SEARCH_START_TIME_ERROR:
                    //Log.d(TAG, "ERROR_CAS_RECORD_SEARCH_START_TIME_ERROR");
                    handlePlaySegmentOver();
                    break;
                case EZConstants.EZPlaybackConstants.MSG_REMOTEPLAYBACK_PLAY_FINISH:
                    //Log.d(TAG, "MSG_REMOTEPLAYBACK_PLAY_FINISH");
                    loadingPlayBtn.setVisibility(View.VISIBLE);
                    handlePlaySegmentOver();
                    break;
                // 画面显示第一帧
                case EZConstants.EZPlaybackConstants.MSG_REMOTEPLAYBACK_PLAY_SUCCUSS:
                    handleFirstFrame(msg);
                    break;
                case EZConstants.EZPlaybackConstants.MSG_REMOTEPLAYBACK_STOP_SUCCESS:
                    handleStopPlayback();
                    break;
                case EZConstants.EZPlaybackConstants.MSG_REMOTEPLAYBACK_PLAY_FAIL:
                    ErrorInfo errorInfo = (ErrorInfo) msg.obj;
                    handlePlayFail(errorInfo);
                    break;
                // 处理播放链接异常
                case RemoteListContant.MSG_REMOTELIST_CONNECTION_EXCEPTION:
                    if (msg.arg1 == ErrorCode.ERROR_CAS_RECORD_SEARCH_START_TIME_ERROR) {
                        handlePlaySegmentOver();
                    } else {
                        String detail = (msg.obj != null ? msg.obj.toString() : "");
                        //handleConnectionException(msg.arg1, detail);
                    }
                    break;
                case RemoteListContant.MSG_REMOTELIST_UI_UPDATE:
                    updateRemotePlayUI();
                    break;
                case RemoteListContant.MSG_REMOTELIST_STREAM_TIMEOUT:
                    //handleStreamTimeOut();
                    break;
                case 301:
                    tx_address.setText("未知");
                    break;
                case 302:
                    Bundle bundle = msg.getData();
                    String str = bundle.getString("address");
                    tx_address.setText(str);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playback_page);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // 保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mWaitDlg = new WaitDialog(AlarmDetail.this, android.R.style.Theme_Translucent_NoTitleBar);
        mWaitDlg.setCancelable(false);
        getData();

        //注册广播接收
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.delate.pic");
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver,filter);
        try {
            initUi();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        onQueryExceptionLayoutTouched();
        initRemoteListPlayer();
        fakePerformClickUI();
        initEzPlayer();
        MyTask myTask = new MyTask(AlarmDetail.this);
        myTask.execute();
        initListener();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 256:
                        CameraName = getCameraInfo(cameraInfoList,ChanneNumber);
                        mLandscapeTitleBar.setTitle(CameraName);
                        break;
                }
            }
        };
    }

    private void initUi() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        mRecordRotateViewUtil = new RotateViewUtil();

        remoteListPage = (RelativeLayout) findViewById(R.id.remote_list_page);
        mTitleBar = (TitleBar) findViewById(R.id.title);
        /** 测量状态栏高度 **/
        ViewTreeObserver viewTreeObserver = remoteListPage.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mRemotePlayBackRect == null) {
                    // 获取状况栏高度
                    mRemotePlayBackRect = new Rect();
                    getWindow().getDecorView().getWindowVisibleDisplayFrame(mRemotePlayBackRect);
                }
            }
        });

        loadingBar = (LoadingTextView) findViewById(R.id.loadingTextView);
        loadingBar.setText(R.string.loading_text_default);
        remoteLoadingBufferTv = (TextView) findViewById(R.id.remote_loading_buffer_tv);
        touchLoadingBufferTv = (TextView) findViewById(R.id.touch_loading_buffer_tv);
        remotePlayBackArea = (RelativeLayout) findViewById(R.id.remote_playback_area);
        endTimeTV = (TextView) findViewById(R.id.end_time_tv);
        exitBtn = (ImageButton) findViewById(R.id.exit_btn);
        surfaceView = (SurfaceView) findViewById(R.id.remote_playback_wnd_sv);
        surfaceView.getHolder().addCallback(this);
        mRemotePlayBackRatioTv = (TextView) findViewById(R.id.remoteplayback_ratio_tv);
        tx_message = findViewById(R.id.message);
        tx_address = findViewById(R.id.address);
        tx_creattime = findViewById(R.id.creattime);
        imageView = findViewById(R.id.img);
        donut_progress = findViewById(R.id.donut_progress);
        ImageUtil.setImageSize(getApplicationContext(),imageView);
        asyncImageLoaderPic = new AsyncImageLoaderPic();
        if (address.contains("receive")){
            String str = address.substring(8,address.length());
            String la = str.substring(0,str.indexOf(","));
            String ln = str.substring(str.indexOf(",")+1,str.length());
            queryLocation(tx_address,la,ln);
        }else{
            tx_address.setText(address);
        }
        if (alarmMessage.getMessage()!=null){
            tx_message.setText(alarmMessage.getMessage());
        }

        if (alarmMessage.getCreateTime()!=null){
            if (alarmMessage.getCreateTime().contains("-")){
                Log.d(TAG,"creattime="+alarmMessage.getCreateTime());
                tx_creattime.setText(alarmMessage.getCreateTime());
            }else{
                String time = DataUtils.timeStamp2Date(Long.parseLong(alarmMessage.getCreateTime()),"yyyy-MM-dd HH:mm:ss");
                Log.d(TAG,"creattime="+time);
                tx_creattime.setText(time);
            }
        }
        if (alarmMessage.getImgPath()!=null&&!alarmMessage.getImgPath().equals("")){
            Log.d(TAG,"path="+alarmMessage.getImgPath());
            List<String> list = new ArrayList<>();
            url_list = DataUtils.getUrlResouses(alarmMessage.getImgPath());
            if (url_list!=null){
                try {
                    map = url_list.get(0);
                    String pic_name = map.get("pic_name");
                    Log.d(TAG,"pic_name="+pic_name);
                    String[] pic = pic_name.split("\\.");
                    String name = pic[0]+short_str+"."+pic[1];
                    imgpath = Environment.getExternalStorageDirectory().toString()+"/EZOpenSDK/cash/"+name;
                    list.add(imgpath);
                    File imgFile = new File(imgpath);
                    asyncImageLoaderPic.loadDrawable(map, donut_progress, short_str, new AsyncImageLoaderPic.ImageCallback() {
                        @Override
                        public void imageLoaded() {
                            Picasso.with(context).load(imgFile).transform(new RoundTransform(20))
                                    .error(context.getResources().getDrawable(R.mipmap.load_fail)).into(imageView);
                        }

                        @Override
                        public void imageLoadEmpty() {
                            File imgFile = new File(Environment.getExternalStorageDirectory().toString() + "/EZOpenSDK/cash/" + pic_name);
                            Log.d(TAG, "img=" + imgFile.toString());
                            Picasso.with(context).load(imgFile).transform(new RoundTransform(20))
                                    .error(context.getResources().getDrawable(R.mipmap.load_fail2)).into(imageView);
                        }

                        @Override
                        public void imageLoadLocal() {
                            Log.d(TAG, "图片存在!");
                            Picasso.with(context).load(imgFile).transform(new RoundTransform(20))
                                    .error(context.getResources().getDrawable(R.mipmap.load_fail)).into(imageView);
                        }
                    });
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), PictureActivity.class);
                            intent.putExtra("position", "0");
                            intent.putExtra("flag", true);
                            intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                            startActivity(intent);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        mRemotePlayBackTouchListener = new CustomTouchListener() {

            @Override
            public boolean canZoom(float scale) {

                return false;
            }

            @Override
            public boolean canDrag(int direction) {
                if (mPlayScale != 1) {
                    return true;
                }
                return false;
            }

            @Override
            public void onSingleClick() {
                onPlayAreaTouched();
            }

            @Override
            public void onDoubleClick(MotionEvent e) {
            }

            @Override
            public void onZoom(float scale) {
            }

            @Override
            public void onDrag(int direction, float distance, float rate) {
                LogUtil.debugLog(TAG, "onDrag:" + direction);
            }

            @Override
            public void onEnd(int mode) {
                LogUtil.debugLog(TAG, "onEnd:" + mode);
            }

            @Override
            public void onZoomChange(float scale, CustomRect oRect, CustomRect curRect) {
                LogUtil.debugLog(TAG, "onZoomChange:" + scale);
                if (status == RemoteListContant.STATUS_PLAYING) {
                    if (scale > 1.0f && scale < 1.1f) {
                        scale = 1.1f;
                    }
                    setPlayScaleUI(scale, oRect, curRect);
                }
            }
        };
        surfaceView.setOnTouchListener(mRemotePlayBackTouchListener);

        setRemoteListSvLayout();

        mRemotePlayBackRecordLy = (LinearLayout) findViewById(R.id.remoteplayback_record_ly);
        progressSeekbar = (SeekBar) findViewById(R.id.progress_seekbar);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        beginTimeTV = (TextView) findViewById(R.id.begin_time_tv);
        controlArea = (LinearLayout) findViewById(R.id.control_area);
        progressArea = (LinearLayout) findViewById(R.id.progress_area);
        captureBtn = (ImageButton) findViewById(R.id.remote_playback_capture_btn);
        videoRecordingBtn = (ImageButton) findViewById(R.id.remote_playback_video_recording_btn);
        videoRecordingBtn_end = findViewById(R.id.remote_playback_video_recording_btn_end);
        mRealPlayRecordContainer = findViewById(R.id.playback_video_frame);
        downloadBtn = (LinearLayout) findViewById(R.id.remote_playback_download_btn);
        fileSizeText = (TextView) findViewById(R.id.file_size_text);
        measure(downloadBtn);
        measure(controlArea);
        loadingImgView = (LoadingView) findViewById(R.id.remote_loading_iv);
        loadingPbLayout = (LinearLayout) findViewById(R.id.loading_pb_ly);
        flowTV = (TextView) findViewById(R.id.remote_playback_flow_tv);

        errorInfoTV = (TextView) findViewById(R.id.error_info_tv);
        errorReplay = (ImageButton) findViewById(R.id.error_replay_btn);
        loadingPlayBtn = (ImageButton) findViewById(R.id.loading_play_btn);
        pauseBtn = (ImageButton) findViewById(R.id.remote_playback_pause_btn);
        soundBtn = (ImageButton) findViewById(R.id.remote_playback_sound_btn);
        replayAndNextArea = (LinearLayout) findViewById(R.id.re_next_area);
        mRemotePlayBackRecordIv = (ImageView) findViewById(R.id.remoteplayback_record_iv);
        mRemotePlayBackRecordTv = (TextView) findViewById(R.id.remoteplayback_record_tv);
        replayBtn = (ImageButton) findViewById(R.id.replay_btn);
        nextPlayBtn = (ImageButton) findViewById(R.id.next_play_btn);
        progressSeekbar.setMax(RemoteListContant.PROGRESS_MAX_VALUE);
        progressBar.setMax(RemoteListContant.PROGRESS_MAX_VALUE);
        matteImage = (ImageView) findViewById(R.id.matte_image);


        touchProgressLayout = (LinearLayout) findViewById(R.id.touch_progress_layout);

        mFullscreenButton = (CheckTextButton) findViewById(R.id.fullscreen_button);
        mScreenOrientationHelper = new ScreenOrientationHelper(this, mFullscreenButton);
        notPause = true;
        mControlBarRL = (RelativeLayout) findViewById(R.id.flow_area);

        mLandscapeTitleBar = (TitleBar) findViewById(R.id.pb_title_bar_landscape);
        mLandscapeTitleBar.setStyle(Color.rgb(0xff, 0xff, 0xff), getResources().getDrawable(R.color.dark_bg_70p),
                getResources().getDrawable(R.drawable.message_back_selector_1));
        mLandscapeTitleBar.setOnTouchListener(this);
        mLandscapeTitleBar.addBackButton(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void initEzPlayer() {
        newPlayInit(true, true);
        showControlArea(true);
        timeBucketUIInit(beginTime, endTime);
        if (alarmMessage.getChannelNumber()!=null && !alarmMessage.getChannelNumber().equals("")){
            mPlayer.setHandler(playBackHandler);
            mPlayer.setSurfaceHold(surfaceView.getHolder());
            Calendar begin = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            begin.setTime(new Date(beginTime));
            end.setTime(new Date(endTime));
            mPlayer.startPlayback(begin,end);
        }
    }

    private int getAndroidOSVersion() {
        int osVersion;
        try {
            osVersion = Integer.parseInt(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            osVersion = 0;
        }
        return osVersion;
    }
    private void onQueryExceptionLayoutTouched() {
        mTitleBar.setTitle("报警信息");

    }
    private void initListener() {
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.blue_bg));
        mTitleBar.setStyle(Color.rgb(0xff, 0xff, 0xff),getResources().getDrawable(R.color.blue_bg),
                getResources().getDrawable(R.drawable.message_back_selector_1));
        backBtn = mTitleBar.addBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExitCurrentPage();
                finish();
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onPlayExitBtnOnClick();
            }
        });
        // loading继续播放按钮
        loadingPlayBtn.setOnClickListener(this);
        // 重播按钮事件
        replayBtn.setOnClickListener(this);
        errorReplay.setOnClickListener(this);
        // 播放下一片段按钮事件
        nextPlayBtn.setOnClickListener(this);
        // 回放区域touch事件
        remotePlayBackArea.setOnTouchListener(this);
        // 控制区域touch事件
        controlArea.setOnTouchListener(this);
        controlArea.setOnClickListener(this);
        // 暂停播放按钮事件
        pauseBtn.setOnClickListener(this);
        // 声音按钮事件
        soundBtn.setOnClickListener(this);
        // 退出播放按钮事件
        exitBtn.setOnClickListener(this);
        // 抓图按钮事件
        captureBtn.setOnClickListener(this);
        // 录像按钮事件
        videoRecordingBtn.setOnClickListener(this);
        // 抓图/录像形成图片区域点击事件

        progressSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 拖动条停止拖动的时候调用
             */
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                int progress = arg0.getProgress();
                if (progress == RemoteListContant.PROGRESS_MAX_VALUE) {
                    stopRemoteListPlayer();
                    handlePlaySegmentOver();
                    return;
                }
                if (CameraName != null) {
                    long avg = (endTime - beginTime) / RemoteListContant.PROGRESS_MAX_VALUE;
                    long trackTime = beginTime + (progress * avg);
                    seekInit(true, false);
                    progressBar.setProgress(progress);
                    LogUtil.i(TAG, "onSeekBarStopTracking, begin time:"
                            + beginTime + " endtime:" + endTime
                            + " avg:" + avg + " MAX:"
                            + RemoteListContant.PROGRESS_MAX_VALUE
                            + " tracktime:" + trackTime);
                    if (mPlayer != null) {
                        Calendar seekTime = Calendar.getInstance();
                        seekTime.setTime(new Date(trackTime));
                        Calendar stopTime = Calendar.getInstance();
                        stopTime.setTime(new Date(endTime));
                        mPlayer.stopPlayback();
                        mPlayer.startPlayback(seekTime,stopTime);
                    }
                }
            }

            /**
             * 拖动条开始拖动的时候调用
             */
            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            /**
             * 拖动条进度改变的时候调用
             */
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                if (CameraName != null) {
                    long time = endTime-beginTime;
                    int diffSeconds = (int) (time * arg1 / 1000) / 1000;
                    String convToUIDuration = RemoteListUtil.convToUIDuration(diffSeconds);
                    beginTimeTV.setText(convToUIDuration);
                }
            }
        });
    }
    // 退出播放按钮事件处理
    private void onPlayExitBtnOnClick() {
        stopRemoteListPlayer();
        //remotePlayBackArea.setVisibility(View.GONE);
        // 不允许旋转屏幕
        mScreenOrientationHelper.disableSensorOrientation();
        controlArea.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        mControlDisplaySec = 0;
        loadingImgView.setVisibility(View.GONE);
        loadingPbLayout.setVisibility(View.GONE);
        touchProgressLayout.setVisibility(View.GONE);
        status = RemoteListContant.STATUS_STOP;
        notShowControlArea = true;
        notPause = false;
    }
    private void stopRemoteListPlayer() {
        try {
            if(mPlayer != null) {
                mPlayer.stopPlayback();
                LogUtil.i(TAG, "stop");
                mPlayer.stopLocalRecord();
            }
            mRealFlow = localInfo.getLimitFlow();
            mStreamFlow = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initRemoteListPlayer() {
        stopRemoteListPlayer();
        if (status != RemoteListContant.STATUS_DECRYPT) {
            status = RemoteListContant.STATUS_INIT;
        }
    }
    private void fakePerformClickUI() {
        fileSizeText.setText("");
        downloadBtn.setPadding(0, 0, 0, 0);
        remotePlayBackArea.setVisibility(View.VISIBLE);
        errorReplay.setVisibility(View.GONE);
        loadingPlayBtn.setVisibility(View.GONE);
        hideControlArea();
    }
    private void handlePlaySegmentOver() {
        LogUtil.errorLog(TAG, "handlePlaySegmentOver");
        stopRemoteListPlayer();
        stopRemotePlayBackRecord();

        if (mOrientation != Configuration.ORIENTATION_PORTRAIT) {
            setRemoteListSvLayout();
        }
        controlArea.setVisibility(View.GONE);
        mControlDisplaySec = 0;
        exitBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        beginTimeTV.setText(endTimeTV.getText());
        notShowControlArea = true;
        status = RemoteListContant.STATUS_STOP;
        loadingPbLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.debugLog(TAG, "onResume()");

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(surfaceView.getWindowToken(), 0);
            }
        }, 200);

        // 判断是否处理暂停状态
        if (notPause || status == RemoteListContant.STATUS_DECRYPT) {
            surfaceView.setVisibility(View.VISIBLE);
            startUpdateTimer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closePlayBack();
        unregisterReceiver(myReceiver);
        if (mPlayer != null) {
           getOpenSDK().releasePlayer(mPlayer);
        }
        removeHandler(playBackHandler);
        removeHandler(handler);
        if (mUpdateTimer != null){
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }
        if (mUpdateTimerTask != null){
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
        mScreenOrientationHelper.postOnStart();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mScreenOrientationHelper.postOnStop();
        LogUtil.debugLog(TAG, "onStop():" + notPause + " status:" + status);

        if (status == RemoteListContant.STATUS_PLAY || status == RemoteListContant.STATUS_PLAYING
                || status == RemoteListContant.STATUS_PAUSE) {
        }

        if (notPause) {
            closePlayBack();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initEzPlayer();
        Log.d(TAG,"onrestart");
    }

    @Override
    public void onBackPressed() {
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            mScreenOrientationHelper.portrait();
            return;
        }
        if (backBtn != null && backBtn.getVisibility() == View.GONE) {
        } else {
            onExitCurrentPage();
            finish();
        }
    }
    /**
     * 启动定时器
     *
     * @see
     * @since V1.0
     */
    private void startUpdateTimer() {
        stopUpdateTimer();
        // 开始录像计时
        mUpdateTimer = new Timer();
        mUpdateTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (controlArea.getVisibility() == View.VISIBLE && mControlDisplaySec < 5
                        && status != RemoteListContant.STATUS_INIT) {
                    mControlDisplaySec++;
                }
                // 流量提醒
                if (mLimitFlowDialog != null && mLimitFlowDialog.isShowing() && mCountDown > 0) {
                    mCountDown--;
                }
                // 录像显示
                if (bIsRecording) {
                    // 更新录像时间
                    Calendar OSDTime = null;
                    if(mPlayer != null)
                        OSDTime = mPlayer.getOSDTime();
                    if (OSDTime != null) {
                        String playtime = Utils.OSD2Time(OSDTime);
                        if (!playtime.equals(mRecordTime)) {
                            mRecordSecond++;
                            mRecordTime = playtime;
                        }
                    }
                }
                sendMessage(RemoteListContant.MSG_REMOTELIST_UI_UPDATE, 0, 0);
            }
        };
        // 延时1000ms后执行，1000ms执行一次
        mUpdateTimer.schedule(mUpdateTimerTask, 0, 1000);
    }
    private void sendMessage(int message, int arg1, int arg2) {
        if (playBackHandler != null) {
            Message msg = playBackHandler.obtainMessage();
            msg.what = message;
            msg.arg1 = arg1;
            msg.arg2 = arg2;
            playBackHandler.sendMessage(msg);
        }
    }
    private void updateRemotePlayUI() {
        if (mControlDisplaySec == 5) {
            mControlDisplaySec = 0;
            if (status != RemoteListContant.STATUS_INIT) {
                hideControlArea();
            }
        }
        if (mLimitFlowDialog != null && mLimitFlowDialog.isShowing()) {
            if (mCountDown == 0) {
                dismissPopDialog(mLimitFlowDialog);
                mLimitFlowDialog = null;
                // 流量大于限定时，停止播放
                if (status != RemoteListContant.STATUS_STOP) {
                    onPlayExitBtnOnClick();
                }
            }
        }
        if (bIsRecording) {
            updateRecordTime();
        }
        if (mPlayer != null && status == RemoteListContant.STATUS_PLAYING) {
            Calendar osd = mPlayer.getOSDTime();
            if(osd != null) {
                handlePlayProgress(osd);
            }
        }
    }
    private void handlePlayProgress(Calendar osdTime) {
        long osd = osdTime.getTimeInMillis();
        double x = ((osd - beginTime) * RemoteListContant.PROGRESS_MAX_VALUE) / (double) (endTime - beginTime);
        int progress = (int) x;
        progressSeekbar.setProgress(progress);
        progressBar.setProgress(progress);
        int beginTimeClock = (int) ((osd - beginTime) / 1000);
        updateTimeBucketBeginTime(beginTimeClock);
    }
    private void updateTimeBucketBeginTime(int beginTimeClock) {
        String convToUIDuration = RemoteListUtil.convToUIDuration(beginTimeClock);
        beginTimeTV.setText(convToUIDuration);
    }
    private void dismissPopDialog(AlertDialog popDialog) {
        if (popDialog != null && popDialog.isShowing() && !isFinishing()) {
            try {
                popDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // 停止播放
    private void stopRemotePlayBackRecord() {
        if (!bIsRecording) {
            return;
        }
        mAudioPlayUtil.playAudioFile(AudioPlayUtil.RECORD_SOUND);
        showToast(getResources().getString(R.string.already_saved_to_volume));
        if(mPlayer != null) {
            mPlayer.stopLocalRecord();
        }
        // 计时按钮不可见
        mRemotePlayBackRecordLy.setVisibility(View.GONE);
        mRecordRotateViewUtil.applyRotation(mRealPlayRecordContainer, videoRecordingBtn_end, videoRecordingBtn, 0, 90);
        mCaptureDisplaySec = 0;
    }
    // 更新录像时间
    private void updateRecordTime() {
        if (mRemotePlayBackRecordIv.getVisibility() == View.VISIBLE) {
            mRemotePlayBackRecordIv.setVisibility(View.INVISIBLE);
        } else {
            mRemotePlayBackRecordIv.setVisibility(View.VISIBLE);
        }
        // 计算分秒
        int leftSecond = mRecordSecond % 3600;
        int minitue = leftSecond / 60;
        int second = leftSecond % 60;

        // 显示录像时间
        String recordTime = String.format("%02d:%02d", minitue, second);
        mRemotePlayBackRecordTv.setText(recordTime);
    }
    private void handleFirstFrame(Message msg) {
        if (msg.arg1 != 0) {
            mRealRatio = (float) msg.arg2 / msg.arg1;
        }
        status = RemoteListContant.STATUS_PLAYING;
        notShowControlArea = true;
        controlArea.setVisibility(View.VISIBLE);
        progressArea.setVisibility(View.VISIBLE);
        mControlDisplaySec = 0;
        captureBtn.setEnabled(true);
        videoRecordingBtn.setEnabled(true);
        setRemoteListSvLayout();
        mScreenOrientationHelper.enableSensorOrientation();
        loadingImgView.setVisibility(View.GONE);
        loadingPbLayout.setVisibility(View.GONE);
        touchProgressLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        errorInfoTV.setVisibility(View.GONE);
        errorReplay.setVisibility(View.GONE);
        flowTV.setText("0k/s 0MB");
        downloadBtn.setPadding(Utils.dip2px(getApplicationContext(), 5), 0, Utils.dip2px(getApplicationContext(), 5), 0);
        if (localInfo.isSoundOpen()) {
            // 打开声音
            if(mPlayer != null) {
                mPlayer.openSound();
            }
        } else {
            // 关闭声音
            if(mPlayer != null) {
                mPlayer.closeSound();
            }
        }
    }
    // 收到停止回放成功的消息后处理
    private void handleStopPlayback() {
        LogUtil.debugLog(TAG, "stop playback success");
    }
    // 播放失败处理
    private void handlePlayFail(ErrorInfo errorInfo) {

        LogUtil.debugLog(TAG, "handlePlayFail. Playback failed. error info is " + errorInfo.toString());
        status = RemoteListContant.STATUS_STOP;
        stopRemoteListPlayer();

        int errorCode = errorInfo.errorCode;

        switch (errorCode) {
            case ErrorCode.ERROR_TRANSF_ACCESSTOKEN_ERROR:{
            }
            // 收到这两个错误码，可以弹出对话框，让用户输入密码后，重新取流预览
            case ErrorCode.ERROR_INNER_VERIFYCODE_NEED:
            case ErrorCode.ERROR_INNER_VERIFYCODE_ERROR:{
                showTipDialog("");
                VerifyCodeInput.VerifyCodeInputDialog(getApplicationContext(),this).show();
            }
            break;
            default: {
                String txt = null;
                if (errorCode == ErrorCode.ERROR_CAS_CONNECT_FAILED) {
                    txt = getString(R.string.remoteplayback_connect_server_error);
                } else if (errorCode == 2004/*VideoGoNetSDKException.VIDEOGONETSDK_DEVICE_EXCEPTION*/) {
                    txt = getString(R.string.realplay_fail_connect_device);
                }  else if (errorCode == InnerException.INNER_DEVICE_NOT_EXIST) {
                    // 提示播放失败
                    txt = getString(R.string.camera_not_online);
                } else {
                    if (errorCode!=0){
                        //txt = getErrorTip(R.string.remoteplayback_fail, errorCode);
                        txt = errorInfo.description;
                        if (txt.equals("回放在不到录像文件")){
                            txt = "回放找不到录像文件";
                        }
                    }
                }

                int errorId = 0; //getErrorId(errorCode);
                showTipDialog(errorId != 0 ? getString(errorId) : txt);

                if (errorCode == ErrorCode.ERROR_CAS_STREAM_RECV_ERROR
                        || errorCode == ErrorCode.ERROR_TRANSF_DEVICE_OFFLINE
                        || errorCode == ErrorCode.ERROR_CAS_PLATFORM_CLIENT_REQUEST_NO_PU_FOUNDED
                        || errorCode == ErrorCode.ERROR_CAS_MSG_PU_NO_RESOURCE) {
                }
            }
        }
    }
    private void showTipDialog(String txt) {
        loadingImgView.setVisibility(View.GONE);
        loadingPbLayout.setVisibility(View.GONE);
        touchProgressLayout.setVisibility(View.GONE);
        controlArea.setVisibility(View.GONE);
        mControlDisplaySec = 0;
        errorInfoTV.setVisibility(View.VISIBLE);
        errorInfoTV.setText(txt);
    }
    private void seekInit(boolean resetPause, boolean resetProgress) {
        newSeekPlayUIInit();

        if (resetPause) {
            resetPauseBtnUI();
        }
        if (resetProgress) {
            progressBar.setProgress(0);
            progressSeekbar.setProgress(0);
        }
        if (localInfo.isSoundOpen()) {
            soundBtn.setBackgroundResource(R.drawable.remote_list_soundon_btn_selector);
        } else {
            soundBtn.setBackgroundResource(R.drawable.remote_list_soundoff_btn_selector);
        }
    }
    // 重置暂停按钮 UI和状态值
    private void resetPauseBtnUI() {
        notPause = true;
        pauseBtn.setBackgroundResource(R.drawable.ez_remote_list_pause_btn_selector);
    }
    private void newSeekPlayUIInit() {
        touchProgressLayout.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
        exitBtn.setVisibility(View.GONE);
        replayAndNextArea.setVisibility(View.GONE);
        errorInfoTV.setVisibility(View.GONE);
        errorReplay.setVisibility(View.GONE);
        // 加载百分比重置
        remoteLoadingBufferTv.setText("0%");
        touchLoadingBufferTv.setText("0%");
        notShowControlArea = false;
        controlArea.setVisibility(View.VISIBLE);
        progressArea.setVisibility(View.GONE);
        mControlDisplaySec = 0;

        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            captureBtn.setVisibility(View.GONE);
            videoRecordingBtn.setVisibility(View.VISIBLE);
        } else {
            captureBtn.setVisibility(View.VISIBLE);
            videoRecordingBtn.setVisibility(View.VISIBLE);
            captureBtn.setEnabled(false);
            videoRecordingBtn.setEnabled(false);
        }
        loadingPlayBtn.setVisibility(View.GONE);
    }
    /**
     * <p>
     * 退出该页面
     * </p>
     *
     * @author hanlieng 2014-8-4 上午9:04:24
     */
    private void onExitCurrentPage() {
        notPause = true;
        closePlayBack();
    }
    private void closePlayBack() {
        if (status == RemoteListContant.STATUS_EXIT_PAGE) {
            return;
        }
        LogUtil.debugLog(TAG, "停止运行.........");
        stopRemoteListPlayer();

        onActivityStopUI();
        stopUpdateTimer();
        status = RemoteListContant.STATUS_EXIT_PAGE;
    }
    // 页面不可见时UI
    private void onActivityStopUI() {
        if(exitBtn != null) {
            exitBtn.setVisibility(View.GONE);
        }
        if(controlArea != null) {
            controlArea.setVisibility(View.GONE);
        }
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        mControlDisplaySec = 0;
        notShowControlArea = true;
    }
    // 停止定时器
    private void stopUpdateTimer() {
        mControlDisplaySec = 0;
        // 停止录像计时
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }

        if (mUpdateTimerTask != null) {
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }
    private void measure(View view) {
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(width, height);
    }
    private void setRemoteListSvLayout() {
        // 设置播放窗口位置
        final int screenWidth = localInfo.getScreenWidth();
        final int screenHeight = (mOrientation == Configuration.ORIENTATION_PORTRAIT) ? (localInfo.getScreenHeight() - localInfo
                .getNavigationBarHeight()) : localInfo.getScreenHeight();
        final RelativeLayout.LayoutParams realPlaySvlp = Utils.getPlayViewLp(mRealRatio, mOrientation,
                localInfo.getScreenWidth(), (int) (localInfo.getScreenWidth() * Constant.LIVE_VIEW_RATIO), screenWidth,
                screenHeight);
        RelativeLayout.LayoutParams svLp = new RelativeLayout.LayoutParams(realPlaySvlp.width, realPlaySvlp.height);
        svLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceView.setLayoutParams(svLp);
        mRemotePlayBackTouchListener.setSacaleRect(Constant.MAX_SCALE, 0, 0, realPlaySvlp.width, realPlaySvlp.height);
        setPlayScaleUI(1, null, null);
    }
    private EZCameraInfo getmCameraInfo(List<EZCameraInfo> cameraInfos,String channeNumber){
        if (channeNumber!=null&&!channeNumber.equals("")){
            for (EZCameraInfo cameraInfo : cameraInfos){
                if (cameraInfo.getCameraNo() == Integer.parseInt(channeNumber)){
                    return cameraInfo;
                }
            }
        }
        return cameraInfos.get(1);
    }

    private void onPlayAreaTouched() {
        if (status == RemoteListContant.STATUS_PLAYING || status == RemoteListContant.STATUS_PAUSE) {
            if (notShowControlArea) {
                showControlArea(true);
            } else {
                hideControlArea();
            }
        }
    }
    private void hideControlArea() {
        controlArea.setVisibility(View.GONE);
        mControlDisplaySec = 0;
        exitBtn.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        notShowControlArea = true;
        mLandscapeTitleBar.setVisibility(View.GONE);
    }
    private void showControlArea(boolean show) {
        if(!show) {
            controlArea.setVisibility(View.GONE);
            return;
        }
        controlArea.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        mControlDisplaySec = 0;
        notShowControlArea = false;
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            captureBtn.setVisibility(View.VISIBLE);
            videoRecordingBtn.setVisibility(View.VISIBLE);
        } else {
            exitBtn.setVisibility(View.GONE);
            captureBtn.setVisibility(View.VISIBLE);
            videoRecordingBtn.setVisibility(View.VISIBLE);
            mLandscapeTitleBar.setVisibility(View.VISIBLE);
        }
    }
    private void setPlayScaleUI(float scale, CustomRect oRect, CustomRect curRect) {
        boolean bDisableZoom = true;
        if(bDisableZoom) {
            return;
        }
        if (scale == 1) {
            if (mPlayScale == scale) {
                return;
            }
            mRemotePlayBackRatioTv.setVisibility(View.GONE);
            try {
                if(mPlayer != null) {
                    mPlayer.setDisplayRegion(false, null, null);
                }
            } catch (BaseException e) {
                e.printStackTrace();
            }
        } else {
            if (mPlayScale == scale) {
                try {
                    if(mPlayer != null) {
                        mPlayer.setDisplayRegion(true, oRect, curRect);
                    }
                } catch (BaseException e) {
                    e.printStackTrace();
                }
                return;
            }
            RelativeLayout.LayoutParams realPlayRatioTvLp = (RelativeLayout.LayoutParams) mRemotePlayBackRatioTv
                    .getLayoutParams();
            if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                realPlayRatioTvLp.setMargins(Utils.dip2px(getApplicationContext(), 10), Utils.dip2px(getApplicationContext(), 10), 0, 0);
            } else {
                realPlayRatioTvLp.setMargins(Utils.dip2px(getApplicationContext(), 70), Utils.dip2px(getApplicationContext(), 20), 0, 0);
            }
            mRemotePlayBackRatioTv.setLayoutParams(realPlayRatioTvLp);
            String sacleStr = String.valueOf(scale);
            mRemotePlayBackRatioTv.setText(sacleStr.subSequence(0, Math.min(3, sacleStr.length())) + "X");
            mRemotePlayBackRatioTv.setVisibility(View.VISIBLE);
            notShowControlArea = false;
            onPlayAreaTouched();
            try {
                if(mPlayer != null) {
                    mPlayer.setDisplayRegion(true, oRect, curRect);
                }
            } catch (BaseException e) {
                e.printStackTrace();
            }
        }
        mPlayScale = scale;
    }
    private void getData() {
        context = getApplicationContext();
        localInfo = LocalInfo.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            alarmMessage = getIntent().getParcelableExtra("alarmMessage");
            address = getIntent().getStringExtra("address");
            ChanneNumber = alarmMessage.getChannelNumber();
            String time = alarmMessage.getCreateTime();
            Log.d(TAG,"time="+time);
            if (!time.equals("")){
                try {
                    if (time.contains("-")){
                        beginTime = Long.parseLong(DataUtils.date2TimeStamp(time,"yyyy-MM-dd HH:mm:ss"))-2000;
                        Log.d(TAG,"beginTime="+beginTime);
                        endTime = beginTime+sustainTime;
                    }else{
                        beginTime = Long.parseLong(time)-2000;
                        endTime = beginTime+sustainTime;
                    }
                    Log.d(TAG,"beginTime2="+beginTime);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        Application application = (Application) getApplication();
        mAudioPlayUtil = AudioPlayUtil.getInstance(application);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        localInfo.setScreenWidthHeight(metric.widthPixels, metric.heightPixels);
        localInfo.setNavigationBarHeight((int) Math.ceil(25 * getResources().getDisplayMetrics().density));
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mPlayer != null) {
            mPlayer.setSurfaceHold(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mPlayer != null) {
            mPlayer.setSurfaceHold(null);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.remote_playback_area:
                onPlayAreaTouched();
                break;
            case R.id.control_area:
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // TODO
            case R.id.query_exception_ly:
                onQueryExceptionLayoutTouched();
                break;
            case R.id.loading_play_btn:
                loadingPlayBtn.setVisibility(View.GONE);
                onReplayBtnClick();
                break;
            case R.id.error_replay_btn:
            case R.id.replay_btn:
                break;
            case R.id.next_play_btn:
                break;
            case R.id.remote_playback_pause_btn:
                onPlayPauseBtnClick();
                break;
            case R.id.remote_playback_sound_btn:
                onSoundBtnClick();
                break;
            case R.id.remote_playback_capture_btn:
                onCapturePicBtnClick();
                break;
            case R.id.remote_playback_video_recording_btn:
            case R.id.remote_playback_video_recording_btn_end:
                onRecordBtnClick();
                break;
            case R.id.exit_btn:
                break;
            case R.id.control_area:
                break;
            default:
                break;
        }
    }
    // 重播当前录像片段
    private void onReplayBtnClick() {
        newPlayInit(true, true);
        showControlArea(true);
        timeBucketUIInit(beginTime, endTime);
        Calendar begin = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        begin.setTime(new Date(beginTime));
        end.setTime(new Date(endTime));
        mPlayer.startPlayback(begin,end);
    }
    private void timeBucketUIInit(long beginTime, long endTime) {
        int diffSeconds = (int) (endTime - beginTime) / 1000;
        String convToUIDuration = RemoteListUtil.convToUIDuration(diffSeconds);
        beginTimeTV.setText(RemoteListContant.VIDEO_DUAR_BEGIN_INIT);
        endTimeTV.setText(convToUIDuration);
    }
    // 暂停按钮事件处理
    private void onPlayPauseBtnClick() {
        if (notPause) {
            // 暂停播放
            notPause = false;
            pauseBtn.setBackgroundResource(R.drawable.remote_list_play_btn_selector);
            if (status != RemoteListContant.STATUS_PLAYING) {
                pauseStop();
            } else {
                status = RemoteListContant.STATUS_PAUSE;
                if(mPlayer != null) {
                    // 停止录像
                    stopRemotePlayBackRecord();
                    mPlayer.pausePlayback();
                }
            }
        } else {
            notPause = true;
            pauseBtn.setBackgroundResource(R.drawable.ez_remote_list_pause_btn_selector);
            if (status != RemoteListContant.STATUS_PAUSE) {
                pausePlay();
            } else {
                // 继续播放
                if(mPlayer != null) {
                    mPlayer.resumePlayback();
                }
                mScreenOrientationHelper.enableSensorOrientation();
                status = RemoteListContant.STATUS_PLAYING;
            }
        }
    }
    // 声音按钮
    private void onSoundBtnClick() {
        if(mPlayer == null) {
            return;
        }

        if (localInfo.isSoundOpen()) {
            // 关闭声音
            localInfo.setSoundOpen(false);
            mPlayer.closeSound();
            soundBtn.setBackgroundResource(R.drawable.remote_list_soundoff_btn_selector);
        } else {
            // 打开声音
            localInfo.setSoundOpen(true);
            mPlayer.openSound();
            soundBtn.setBackgroundResource(R.drawable.remote_list_soundon_btn_selector);
        }
    }
    // 开始录像
    private void onRecordBtnClick() {
        mControlDisplaySec = 0;
        if (bIsRecording) {
            stopRemotePlayBackRecord();
            mRemotePlayBackRecordLy.setVisibility(View.GONE);
            bIsRecording = !bIsRecording;
            mRemotePlayBackRecordTv.setText("00:00");
            mRecordSecond = 0;
            return;
        }

        bIsRecording = !bIsRecording;
        if (!SDCardUtil.isSDCardUseable()) {
            mRemotePlayBackRecordLy.setVisibility(View.GONE);
            mRemotePlayBackRecordTv.setText("00:00");
            mRecordSecond = 0;
            // 提示SD卡不可用
            showToast(R.string.remoteplayback_SDCard_disable_use);
            return;
        }

        if (SDCardUtil.getSDCardRemainSize() < SDCardUtil.PIC_MIN_MEM_SPACE) {
            mRemotePlayBackRecordLy.setVisibility(View.GONE);
            mRemotePlayBackRecordTv.setText("00:00");
            mRecordSecond = 0;
            // 提示内存不足
            showToast(R.string.remoteplayback_record_fail_for_memory);
            return;
        }
        mRemotePlayBackRecordLy.setVisibility(View.VISIBLE);
        mAudioPlayUtil.playAudioFile(AudioPlayUtil.RECORD_SOUND);
        if(mPlayer != null) {
            // 可以采用deviceSerial+时间作为文件命名，demo中简化，只用时间命名
            Date date = new Date();
            String strRecordFile = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/CaptureVideo/" + CameraName+"/"
                    + String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) + ".mp4";
            mPlayer.startLocalRecordWithFile(strRecordFile);

            //保存路径
            List<VideoFilePath> files = DBManager.getInstance().get(VideoFilePath.class);
            if (files.size()>=200){
                DBManager.getInstance().delete(VideoFilePath.class,"path=",new String[]{files.get(0).getPath()});
            }
            String name = String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) +".jpg";
            DBManager.getInstance().insert(new VideoFilePath(strRecordFile, name));
            mRecordRotateViewUtil.applyRotation(mRealPlayRecordContainer, videoRecordingBtn,
                    videoRecordingBtn_end, 0, 90);
        }
    }
    // 抓拍按钮响应函数
    private void onCapturePicBtnClick() {
        java.util.Date date = new java.util.Date();
        String path = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/CapturePicture/" +CameraName+"/"
                + String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) +".jpg";
        mControlDisplaySec = 0;
        if (!SDCardUtil.isSDCardUseable()) {
            // 提示SD卡不可用
            showToast(R.string.remoteplayback_SDCard_disable_use);
            return;
        }
        if (SDCardUtil.getSDCardRemainSize() < SDCardUtil.PIC_MIN_MEM_SPACE) {
            // 提示内存不足
            showToast(R.string.remoteplayback_capture_fail_for_memory);
            return;
        }
        mCaptureDisplaySec = 4;
        Thread thr = new Thread() {
            @Override
            public void run() {
                if (mPlayer == null) {
                    return;
                }
                Bitmap bmp = mPlayer.capturePicture();
                if(bmp != null) {
                    try {
                        mAudioPlayUtil.playAudioFile(AudioPlayUtil.CAPTURE_SOUND);
                        if (TextUtils.isEmpty(path)) {
                            bmp.recycle();
                            bmp = null;
                            return;
                        }
                        EZUtils.saveCapturePictrue(path, bmp);
                        //保存路径
                        List<PicFilePath> files = DBManager.getInstance().get(PicFilePath.class);
                        if (files.size()>=200){
                            DBManager.getInstance().delete(PicFilePath.class, "path=?", new String[]{files.get(0).getPath()});
                        }
                        String name = String.format("%tH", date) + String.format("%tM", date) + String.format("%tS", date) + String.format("%tL", date) +".jpg";
                        DBManager.getInstance().insert(new PicFilePath(path, name));
                        MediaScanner mMediaScanner = new MediaScanner(getApplicationContext());
                        mMediaScanner.scanFile(path, "jpg");
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.already_saved_to_volume), Toast.LENGTH_SHORT).show();
                            }});
                    } catch (InnerException e) {
                        e.printStackTrace();
                    } finally {
                        if(bmp != null){
                            bmp.recycle();
                            bmp = null;
                            return;
                        }
                    }
                }
                super.run();
            }};
        thr.start();
    }
    // 暂停按钮实现停止
    private void pauseStop() {
        status = RemoteListContant.STATUS_STOP;
        stopRemoteListPlayer();
        loadingImgView.setVisibility(View.GONE);
        loadingPbLayout.setVisibility(View.GONE);

        loadingPlayBtn.setVisibility(View.VISIBLE);
    }
    private void pausePlay() {
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            // 不允许选择屏幕
            mScreenOrientationHelper.disableSensorOrientation();
        }
        Calendar seekTime = getTimeBarSeekTime();
        Calendar osdTime = null;
        if(mPlayer != null) {
            osdTime = mPlayer.getOSDTime();
        }
        Calendar startTime = Calendar.getInstance();
        long playTime = 0L;
        if (osdTime != null) {
            playTime = osdTime.getTimeInMillis();
        } else {
            playTime = seekTime.getTimeInMillis();
        }
        startTime.setTimeInMillis(playTime);
        LogUtil.infoLog(TAG, "pausePlay:" + startTime);
        if (CameraName != null) {
            reConnectPlay(startTime);
        }

    }
    private Calendar getTimeBarSeekTime() {
        if (CameraName != null) {
            int progress = progressSeekbar.getProgress();
            long seekTime = (((endTime - beginTime) * progress) / RemoteListContant.PROGRESS_MAX_VALUE) + beginTime;
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(seekTime);
            return c;
        }
        return null;
    }
    // 重播
    private void reConnectPlay(Calendar uiPlayTimeOnStop) {
        newPlayInit(false, false);
        RemoteFileInfo fileInfo1 = this.fileInfo.copy();
        fileInfo1.setStartTime(uiPlayTimeOnStop);
    }
    private void newPlayInit(boolean resetPause, boolean resetProgress) {
        if (mShowNetworkTip) {
            mShowNetworkTip = false;
        }
        initEZPlayer();
        newPlayUIInit();

        if (resetPause) {
            resetPauseBtnUI();
        }
        if (resetProgress) {
            progressBar.setProgress(0);
            progressSeekbar.setProgress(0);
        }
        if (localInfo.isSoundOpen()) {
            soundBtn.setBackgroundResource(R.drawable.remote_list_soundon_btn_selector);
        } else {
            soundBtn.setBackgroundResource(R.drawable.remote_list_soundoff_btn_selector);
        }
    }

    private void initEZPlayer() {
        if(mPlayer != null) {
            // 停止录像
            mPlayer.stopLocalRecord();
            // 停止播放
            mPlayer.stopPlayback();
        } else {
            if (alarmMessage.getChannelNumber()!=null && !alarmMessage.getChannelNumber().equals("")) {
                mPlayer = getOpenSDK().createPlayer(AlarmContant.DEVICE_SERIAL_NUM, Integer.parseInt(alarmMessage.getChannelNumber()));
                String name = AlarmContant.DEVICE_SERIAL_NUM + String.valueOf(ChanneNumber);
                List<Verifcode> verifcodeList =  DBManager.getInstance().get(Verifcode.class);
                if (verifcodeList.size() != 0){
                    for (Verifcode verifcode : verifcodeList){
                        if (verifcode.getName().equals(name)){
                            mVerifyCode = verifcode.getCode();
                        }
                    }
                }
                mPlayer.setPlayVerifyCode(mVerifyCode);
            }
        }
    }
    // 新的播放UI初始化
    private void newPlayUIInit() {
        remotePlayBackArea.setVisibility(View.VISIBLE);
        surfaceView.setVisibility(View.INVISIBLE);
        surfaceView.setVisibility(View.VISIBLE);
        loadingImgView.setVisibility(View.VISIBLE);
        loadingPbLayout.setVisibility(View.VISIBLE);
        touchProgressLayout.setVisibility(View.GONE);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);
        exitBtn.setVisibility(View.GONE);
        replayAndNextArea.setVisibility(View.GONE);
        errorInfoTV.setVisibility(View.GONE);
        errorReplay.setVisibility(View.GONE);
        // 加载百分比重置
        remoteLoadingBufferTv.setText("0%");
        touchLoadingBufferTv.setText("0%");
        notShowControlArea = false;
        controlArea.setVisibility(View.VISIBLE);
        progressArea.setVisibility(View.GONE);
        mControlDisplaySec = 0;
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            captureBtn.setVisibility(View.GONE);
            videoRecordingBtn.setVisibility(View.VISIBLE);
            mControlBarRL.setVisibility(View.VISIBLE);
        } else {
            captureBtn.setVisibility(View.VISIBLE);
            videoRecordingBtn.setVisibility(View.VISIBLE);
            captureBtn.setEnabled(false);
            videoRecordingBtn.setEnabled(false);
            mControlBarRL.setVisibility(View.GONE);
        }
        loadingPlayBtn.setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mOrientation = newConfig.orientation;
        onOrientationChanged();
        super.onConfigurationChanged(newConfig);
    }

    private void onOrientationChanged() {
        setRemoteListSvLayout();
        if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            // 显示状态栏
            fullScreen(false);
            if (status != RemoteListContant.STATUS_PLAYING) {
                // 不允许选择屏幕
                mScreenOrientationHelper.disableSensorOrientation();
            }
            // 竖屏处理
            remoteListPage.setBackgroundColor(getResources().getColor(R.color.white));
            mTitleBar.setVisibility(View.VISIBLE);
            if (controlArea.getVisibility() == View.VISIBLE) {
                captureBtn.setVisibility(View.GONE);
                videoRecordingBtn.setVisibility(View.VISIBLE);
            }
            mControlBarRL.setVisibility(View.VISIBLE);
            mLandscapeTitleBar.setVisibility(View.GONE);
        } else {
            // 横屏处理
            // 隐藏状态栏
            fullScreen(true);
            remoteListPage.setBackgroundColor(getResources().getColor(R.color.black_bg));
            mTitleBar.setVisibility(View.GONE);
            exitBtn.setVisibility(View.GONE);
            captureBtn.setVisibility(View.VISIBLE);
            videoRecordingBtn.setVisibility(View.VISIBLE);
            mControlBarRL.setVisibility(View.GONE);
            mLandscapeTitleBar.setVisibility(View.VISIBLE);
        }
    }
    private void fullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }


    @Override
    public void onInputVerifyCode(String verifyCode) {
        if (mPlayer != null) {
            String name = AlarmContant.DEVICE_SERIAL_NUM+String.valueOf(ChanneNumber);
            if (mVerifyCode == null){
                DBManager.getInstance().insert(new Verifcode(name, verifyCode));
            }else{
                DBManager.getInstance().update(new Verifcode(name, verifyCode),"name=?", new String[]{name});
            }

            newPlayUIInit();
            showControlArea(true);
            mVerifyCode = verifyCode;
            if (mPlayer != null){
                LogUtil.debugLog(TAG, "verify code is " + verifyCode);
                mPlayer.setPlayVerifyCode(mVerifyCode);
            }
            Calendar begin = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            begin.setTime(new Date(beginTime));
            end.setTime(new Date(endTime));
            mPlayer.startPlayback(begin,end);
            Log.d("TAG","******mPlayer.startPlayback********");
        }
    }

    /**
     * 获取事件消息任务
     */
    private  class MyTask extends AsyncTask<Void, Void, List<EZDeviceInfo>> {
        private int mErrorCode = 0;
        private WeakReference<AlarmDetail> activityReference;

        MyTask(AlarmDetail context) {
            activityReference = new WeakReference<>(context);
        }


        @Override
        protected List<EZDeviceInfo> doInBackground(Void... voids) {
            if (!ConnectionDetector.isNetworkAvailable(getApplicationContext())){
                mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
                return null;
            }
            try {
                List<EZDeviceInfo> result = null;
                result = getOpenSDK().getDeviceList(0, 30);
                return result;
            }catch (BaseException e){
                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                mErrorCode = errorInfo.errorCode;
                Log.i("TAG","eooro = "+errorInfo.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<EZDeviceInfo> result) {
            AlarmDetail activity2 = activityReference.get();
            if (activity2 == null || activity2.isFinishing() || activity2.isDestroyed()){
                return;
            }
            if (result!=null){
                for (EZDeviceInfo ezDeviceInfo : result){
                    for (EZCameraInfo cameraInfo : ezDeviceInfo.getCameraInfoList()){
                        cameraInfoList.add(cameraInfo);
                        Message msg = new Message();
                        msg.what = 256;
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("cameralist", (ArrayList<? extends Parcelable>) cameraInfoList);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }
            }
        }
    }
    private String getCameraInfo(List<EZCameraInfo> cameraInfos , String no){
        if (no!=null&&!no.equals("")){
            for (EZCameraInfo cameraInfo : cameraInfos){
                if (cameraInfo.getCameraNo() == Integer.parseInt(no)){
                    Log.d("TAG","no="+cameraInfo.getDeviceSerial());
                    return cameraInfo.getCameraName();
                }
            }
        }
        return "Null";
    }


    public void queryLocation(TextView textView , String la, String ln) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String url = AlarmContant.location_url;
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("location",la+","+ln);
        map.put("coordtype","wgs84ll");
        map.put("radius","500");
        map.put("extensions_poi","1");
        map.put("output","json");
        map.put("ak","KNAeq1kjoe2u24PTYfeL4kO0KvGaqNak");
        String sn = SnCal.getSnKry(map);
        OkHttpUtil.get(url, sn,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: ",e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                String address = "";
                try {
                    JSONObject object = new JSONObject(responseBody);
                    String status = object.get("status").toString();
                    if (status.equals("0")){
                        String result = object.get("result").toString();
                        JSONObject objectdata = new JSONObject(result);
                        String formatted_address = objectdata.get("formatted_address").toString();
                        String sematic_description = objectdata.get("sematic_description").toString();
                        if (sematic_description==null || sematic_description.equals("")){
                            address = formatted_address;
                        }else{
                            address = formatted_address+"("+sematic_description+")";
                        }
                        if (address.equals("")){
                            Message message = Message.obtain();
                            message.what = 301;
                            playBackHandler.sendMessage(message);
                        }else{
                            Message message = Message.obtain();
                            message.what = 302;
                            Bundle bundle = new Bundle();
                            bundle.putString("address",address);
                            message.setData(bundle);
                            playBackHandler.sendMessage(message);
                        }
                    }else{
                        Message message = Message.obtain();
                        message.what = 301;
                        playBackHandler.sendMessage(message);
                    }
                    Log.d("TAG","address="+address);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },map);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Picasso.with(context).invalidate(new File(imgpath));
            imageView.setImageDrawable(null);
            //donut_progress.setVisibility(View.VISIBLE);
            //下载图片
            String pic_name = map.get("pic_name");
            String[] pic = pic_name.split("\\.");
            String name = pic[0]+short_str+"."+pic[1];
            imgpath = Environment.getExternalStorageDirectory().toString()+"/EZOpenSDK/cash/"+name;
            File imgFile = new File(imgpath);
            asyncImageLoaderPic.loadDrawable(map,donut_progress , short_str,new AsyncImageLoaderPic.ImageCallback() {
                @Override
                public void imageLoaded() {
                    Picasso.with(context).load(imgFile).transform(new RoundTransform(20))
                            .error(context.getResources().getDrawable(R.mipmap.load_fail)).into(imageView);
                }

                @Override
                public void imageLoadEmpty() {
                    File imgFile = new File(Environment.getExternalStorageDirectory().toString()+"/EZOpenSDK/cash/"+pic_name);
                    Log.d(TAG,"img="+imgFile.toString());
                    Picasso.with(context).load(imgFile).transform(new RoundTransform(20))
                            .error(context.getResources().getDrawable(R.mipmap.load_fail2)).into(imageView);
                }

                @Override
                public void imageLoadLocal() {

                }
            });
        }
    }
}
