package ezviz.ezopensdk.activity.alarm;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import com.github.lzyzsd.circleprogress.DonutProgress;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.Constant;
import ezviz.ezopensdk.utils.FTPutils;

public class AsyncImageLoaderPic {
    public static ImageCallback imageCallback;
    public Drawable loadDrawable(final HashMap<String, String> map, DonutProgress donutProgress , String str, ImageCallback imageCallback ) {
//        String imageUrl = "ftp://wh:wanghao@192.168.60.81/uftp/78b8998fe074fcfc708f8d91d93678aa.jpg";
        //HashMap<String,String> map = DataUtils.getUrlResouse(imageUrl);
        //HashMap<String,String> map = DataUtils.getUrlResouses(imageUrl).get(0);
        String ip = map.get("ip");
        String port = map.get("port");
        String name = map.get("name");
        String password = map.get("password");
        String pic_name = map.get("pic_name");
        String[] pic = pic_name.split("\\.");
        String file_name = pic[0]+str+"."+pic[1];
        Log.d("TAG","file_name: "+file_name);
        String server_name = map.get("server_name");
        String pic_sercer = server_name.substring(0,server_name.length()-4);
        String file_server_name = pic_sercer+str+"."+pic[1];
        Log.d("TAG","file_server_name: "+file_server_name);
        Handler handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what){
                    case 0:
                        Bundle bundle = message.getData();
                        String progress = bundle.getString("progress");
                        donutProgress.setProgress(Float.parseFloat(progress));
                        if (Float.parseFloat(progress)==1){
                            donutProgress.setVisibility(View.VISIBLE);
                        }else if(Float.parseFloat(progress)==100){
                            donutProgress.setVisibility(View.GONE);
                            imageCallback.imageLoaded();
                        }
                        break;
                    case 1:
                        imageCallback.imageLoadEmpty();
                        break;
                    case 3:
                        imageCallback.imageLoadLocal();
                        break;
                }
            }
        };

        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                FTPutils ftPutils = new FTPutils();
                String localpath = Environment.getExternalStorageDirectory().toString()+"/EZOpenSDK/cash";
                Boolean flag = ftPutils.connect(ip, Integer.parseInt(port),name,password);
                Log.d("TAG","flag="+flag);
                if (flag){
                    try {
                        ftPutils.downloadSingleFile2(file_server_name, localpath, file_name, new FTPutils.FtpProgressListener() {
                            @Override
                            public void onFtpProgress(int currentStatus, long process, File targetFile, long currentSize, long serverSize) {
                                //Log.d("TAG","currenstatus="+currentStatus);
                                Log.d("TAG","process="+process);
                                if (currentStatus == Constant.FTP_FILE_NOTEXISTS){
                                    Message message = Message.obtain();
                                    message.what = 1;
                                    handler.sendMessage(message);
                                }else if (currentStatus ==Constant.LOCAL_FILE_AIREADY_COMPLETE){
                                    Message message = Message.obtain();
                                    message.what = 3;
                                    handler.sendMessage(message);
                                } else {
                                    Message message = Message.obtain();
                                    message.what = 0;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("progress", String.valueOf(process));
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    Message message = Message.obtain();
                    message.what = 1;
                    handler.sendMessage(message);
                }
            }
        });
        return null;
    }

    public interface ImageCallback {
        public void imageLoaded();
        public void imageLoadEmpty();
        public void imageLoadLocal();
    }
}
