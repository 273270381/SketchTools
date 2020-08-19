package ezviz.ezopensdk.been;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.RemoteViews;
import java.io.File;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.home.HomeActivity;
import ezviz.ezopensdk.utils.FTPutils;

/**
 * Created by hejunfeng on 2020/8/14 0014
 */
public class UpdateService extends Service {
    public static boolean isDownload = false;
    private String mTitle = "安装包下载中";
    private String saveFilePath = AppConfig.DEFAULT_SAVE_FILE_PATH;
    private String fileName;
    private NotificationManager mNotificationManager;
    private Notification.Builder builder;
    private String id = "channel_001";
    private String name = "update";
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    int rate = msg.arg1;
                    Log.d("TAG",String.valueOf(rate));
                    if (rate < 100) {
                        RemoteViews views = builder.getNotification().contentView;
                        views.setTextViewText(R.id.tv_download_progress, mTitle + "(" + rate + "%" + ")");
                        views.setProgressBar(R.id.pb_progress, 100, rate, false);
                    }else{
                        // 下载完毕后变换通知形式
                        builder.getNotification().flags = Notification.FLAG_AUTO_CANCEL;
                        RemoteViews views = builder.getNotification().contentView;
                        views.setTextViewText(R.id.tv_download_progress,"安装包下载完成");
                        views.setProgressBar(R.id.pb_progress,100,rate,true);
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.putExtra("completed", "yes");
                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(contentIntent);
                        stopSelf();
                        installApk();
                    }
                    mNotificationManager.notify(0, builder.getNotification());
                    break;

            }
        }
    };

    private void installApk() {
        File file = new File(saveFilePath + fileName);
        if (!file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(this, "ezviz.ezopensdk.fileprovider", file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isDownload = true;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fileName = intent.getStringExtra("fileName");
        File file = new File(saveFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        final File apkFile = new File(saveFilePath);
        setUpNotification();
        new Thread() {
            @Override
            public void run() {
                try {
                    downloadUpdateFile(apkFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void startService(Context context, String fileName) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra("fileName", fileName);
        context.startService(intent);
    }

    private void setUpNotification() {
        int icon = R.mipmap.paper;
        CharSequence tickerText = "开始下载";
        long when = System.currentTimeMillis();
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.layout_notification_view);
        contentView.setTextViewText(R.id.tv_download_progress, mTitle);
        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this,id)
                    .setSmallIcon(icon)
                    .setWhen(when)
                    .setContentTitle(tickerText)
                    .setCustomContentView(contentView)
                    .setContentIntent(contentIntent);
        }else{
            builder = new Notification.Builder(this)
                    .setSmallIcon(icon)
                    .setWhen(when)
                    .setContentTitle(tickerText)
                    .setContent(contentView)
                    .setContentIntent(contentIntent);
        }
        builder.getNotification().flags = Notification.FLAG_ONGOING_EVENT;
    }

    private void downloadUpdateFile(File saveFile) throws Exception {
        if (saveFile.exists()){
            saveFile.delete();
        }
        FTPutils ftPutils = new FTPutils();
        Boolean flag = ftPutils.connect(AlarmContant.ftp_ip,Integer.parseInt(AlarmContant.ftp_port),AlarmContant.name,AlarmContant.password);
        if (flag){
            try {
                ftPutils.downloadSingleFile(AlarmContant.apk_path + "/" + fileName, saveFilePath, fileName, new FTPutils.FtpProgressListener() {
                    @Override
                    public void onFtpProgress(int currentStatus, long process, File targetFile, long currentSize, long serverSize) {
                        if (currentStatus == Constant.FTP_FILE_NOTEXISTS){
                            if (ftPutils.isConnected()){
                                ftPutils.disconnect();
                            }
                        }else if(currentStatus ==Constant.LOCAL_FILE_AIREADY_COMPLETE){
                            mHandler.sendEmptyMessage(0);
                            isDownload = false;
                        }else{
                            if ((int)process > 0){
                                Message msg =mHandler.obtainMessage();
                                msg.what = 1;
                                msg.arg1 = (int)process;
                                mHandler.sendMessage(msg);
                            }
                        }
                    }
                });
            }catch (Exception e) {
                if (ftPutils.isConnected()){
                ftPutils.disconnect();
            }
        }
        }
    }
    public static String getServiceName(){
        return UpdateService.class.getName();
    }
    @Override
    public void onDestroy() {
        isDownload = false;
        super.onDestroy();
    }
}
