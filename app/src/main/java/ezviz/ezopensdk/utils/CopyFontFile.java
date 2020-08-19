package ezviz.ezopensdk.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CopyFontFile {
    //private static final String PHONE_PATH = "data/data/com.ezopensdk/databases/";
    private static final String PHONE_PATH = Environment.getExternalStorageDirectory().toString()+"/EZOpenSDK/Font/";
    public static final String FONT_PATH = PHONE_PATH + "DroidSansFallback.ttf";
    private Context mContext;

    public CopyFontFile(Context context){
        this.mContext = context;
    }

    public void DoCopy(){
        try{
            File dataFile = new File(FONT_PATH);
            if(dataFile.exists()){
                Log.i("CopyFontFile","qwer---> 文件已存在");
                return;
            }else{
                Log.i("CopyFontFile","qwer---> 文件不存在");
            }

            File file = new File(PHONE_PATH);
            if(!file.exists()){
                file.mkdir();
            }
            if(!(new File(FONT_PATH).exists())){
                InputStream mips = this.mContext.getResources().getAssets().open("DroidSansFallback.ttf");
                FileOutputStream mos = new FileOutputStream(FONT_PATH);
                byte[] buffer = new byte[1024*1024];
                int count = 0;

                while((count = mips.read(buffer)) >0){
                    mos.write(buffer,0,count);
                }
                mos.close();
                mips.close();
            }

        }catch(IOException e){
            e.printStackTrace();
        }

    }

}
