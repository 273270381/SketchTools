package ezviz.ezopensdk.activity.alarm;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import java.io.File;
import java.util.HashMap;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.Constant;
import ezviz.ezopensdk.utils.FTPutils;

public class AsyncImageLoader {
    private String TAG = "GarbageActivity";

    public Drawable loadDrawable(final HashMap<String, String> map, ImageCallback imageCallback ) {
        //HashMap<String,String> map = DataUtils.getUrlResouse(imageUrl);
        //HashMap<String,String> map = DataUtils.getUrlResouses(imageUrl).get(0);
        String ip = map.get("ip");
        String port = map.get("port");
        String name = map.get("name");
        String password = map.get("password");
        String pic_name = map.get("pic_name");
        String server_name = map.get("server_name");
        //Log.d(TAG,"server_name: "+server_name);

        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                FTPutils ftPutils = new FTPutils();
                String localpath = Environment.getExternalStorageDirectory().toString()+"/EZOpenSDK/cash";
                Boolean flag = ftPutils.connect(ip, Integer.parseInt(port),name,password);
                if (flag){
                    try {
                        ftPutils.downloadSingleFile(server_name, localpath, pic_name, new FTPutils.FtpProgressListener() {
                            @Override
                            public void onFtpProgress(int currentStatus, long process, File targetFile, long currentSize, long serverSize) {
                                AppOperator.runOnMainThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (currentStatus == Constant.FTP_FILE_NOTEXISTS){
                                            imageCallback.imageLoadEmpty();
                                        }else {
                                            if (process == 100){
                                                imageCallback.imageLoaded();
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            imageCallback.imageLoadEmpty();
                        }
                    });
                }
            }
        });
        return null;
    }

    public interface ImageCallback {
        public void imageLoaded();
        public void imageLoadEmpty();
    }
}
