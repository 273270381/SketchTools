package ezviz.ezopensdk.been;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.arcmap.ArcMapActivity;
import ezviz.ezopensdk.utils.FTPutils;

import static ezviz.ezopensdk.been.AlarmContant.tifNum;

/**
 * Created by hejunfeng on 2020/8/15 0015
 */
public class DownLoadService extends Service {
    private String mTitle = "影像文件下载中";
    private String saveFilePath = AppConfig.DEFAULT_SAVE_FILE_PATH;
    private String localPath;
    private String url;
    private NotificationManager mNotificationManager;
    private Notification.Builder builder;
    private String id = "channel_002";
    private String name = "download";
    private String serverSize="0";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 6:
                    Bundle bundle2 = msg.getData();
                    serverSize = bundle2.getString("serverSize");
                    break;
                case 5:
                    Bundle bundle = msg.getData();
                    String progress = bundle.getString("progress");
                    String currentSize = bundle.getString("currentSize");
                    if (Integer.parseInt(progress) < 100){
                        RemoteViews views = builder.getNotification().contentView;
                        views.setTextViewText(R.id.tv_download_progress, mTitle +"("+Integer.parseInt(currentSize)/1048576
                                +"M/"+Integer.parseInt(serverSize)/1048576+"M)");
                        //views.setTextViewText(R.id.tv_download_progress, mTitle + "(" + progress + "%" + ")");
                        views.setProgressBar(R.id.pb_progress, 100, Integer.parseInt(progress), false);
                    }else{
                        // 下载完毕后变换通知形式
                        builder.getNotification().flags = Notification.FLAG_AUTO_CANCEL;
                        RemoteViews views = builder.getNotification().contentView;
                        views.setTextViewText(R.id.tv_download_progress,"影像文件下载完成");
                        views.setProgressBar(R.id.pb_progress,100,Integer.parseInt(progress),true);
                        Intent intent = new Intent(getApplicationContext(), ArcMapActivity.class);
                        intent.putExtra("completed", "yes");
                        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(contentIntent);
                        Intent i = new Intent();
                        i.setAction("ezviz.ezopensdk.MYRESTART");
                        sendBroadcast(i);
                        stopSelf();
                    }
                    mNotificationManager.notify(1, builder.getNotification());
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        localPath = intent.getStringExtra("localPath");
        url = intent.getStringExtra("url");
        File file = new File(saveFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        setUpNotification();
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                try {
                    downloadMapFile(localPath, url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadMapFile(String localPath, String url) {
        //下载地图文件
        List<String> serverPath_list = new ArrayList<>();
        List<String> filename_list = new ArrayList<>();
        for (int i = 0 ; i < tifNum ; i ++){
            serverPath_list.add("node/kaifaqu/map"+url+url+String.valueOf(i)+".TIF");
            serverPath_list.add("node/kaifaqu/map"+url+url+String.valueOf(i)+".TIF.aux.xml");
            serverPath_list.add("node/kaifaqu/map"+url+url+String.valueOf(i)+".TIF.ovr");
            serverPath_list.add("node/kaifaqu/map"+url+url+String.valueOf(i)+".tfw");
            filename_list.add(url.substring(1)+String.valueOf(i)+".TIF");
            filename_list.add(url.substring(1)+String.valueOf(i)+".TIF.aux.xml");
            filename_list.add(url.substring(1)+String.valueOf(i)+".TIF.ovr");
            filename_list.add(url.substring(1)+String.valueOf(i)+".tfw");
        }
        addPathList(serverPath_list, filename_list, url);
        FTPutils ftPutils = new FTPutils();
        Boolean flag = ftPutils.connect(AlarmContant.ftp_ip, Integer.parseInt(AlarmContant.ftp_port), AlarmContant.name, AlarmContant.password);
        if (flag) {
            try {
                ftPutils.downloadMap(serverPath_list, localPath, filename_list, new FTPutils.FtpProgressListener() {
                    @Override
                    public void onFtpProgress(int currentStatus, long process, File targetFile, long currentSize, long serverSize) {
                        if (currentStatus == Constant.FTP_DOWN_START){
                            Log.d("TAG","serverSize="+serverSize);
                            Message message = handler.obtainMessage();
                            message.what = 6;
                            Bundle bundle = new Bundle();
                            bundle.putString("serverSize", String.valueOf(serverSize));
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }else if((int)process > 0){
                            Message message = handler.obtainMessage();
                            message.what = 5;
                            Bundle bundle = new Bundle();
                            bundle.putString("progress", String.valueOf(process));
                            bundle.putString("currentSize", String.valueOf(currentSize));
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                });
            } catch (Exception e) {
                if (ftPutils.isConnected()){
                    ftPutils.disconnect();
                }
            }
        }
    }

    private void addPathList(List<String> serverPath_list, List<String> filename_list, String url) {
        serverPath_list.add("node/kaifaqu/map" + url + "/polyline.dbf");
        serverPath_list.add("node/kaifaqu/map" + url + "/polyline.prj");
        serverPath_list.add("node/kaifaqu/map" + url + "/polyline.sbn");
        serverPath_list.add("node/kaifaqu/map" + url + "/polyline.sbx");
        serverPath_list.add("node/kaifaqu/map" + url + "/polyline.shp");
        serverPath_list.add("node/kaifaqu/map" + url + "/polyline.shp.xml");
        serverPath_list.add("node/kaifaqu/map" + url + "/polyline.shx");
        serverPath_list.add("node/kaifaqu/map" + url + "/version.txt");
        filename_list.add("polyline.dbf");
        filename_list.add("polyline.prj");
        filename_list.add("polyline.sbn");
        filename_list.add("polyline.sbx");
        filename_list.add("polyline.shp");
        filename_list.add("polyline.shp.xml");
        filename_list.add("polyline.shx");
        filename_list.add("version.txt");
    }


    private void setUpNotification() {
        int icon = R.mipmap.paper;
        CharSequence tickerText = "开始下载";
        long when = System.currentTimeMillis();
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.layout_notification_view);
        contentView.setTextViewText(R.id.tv_download_progress, mTitle);
        Intent intent = new Intent(this, ArcMapActivity.class);
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


    public static void startService(Context context, String localPath, String url) {
        Intent intent = new Intent(context, DownLoadService.class);
        intent.putExtra("localPath", localPath);
        intent.putExtra("url", url);
        context.startService(intent);
    }

    public static String getServiceName(){
        return DownLoadService.class.getName();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
