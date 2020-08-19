package ezviz.ezopensdk.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReadUtils {
    public static String ReadTxtFile(String strFilePath, Context context){
        String path = strFilePath;
        Log.d("TAG","path="+path);
        //打开文件
        File file = new File(path);
        StringBuffer sb = new StringBuffer("");
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (!file.exists()){
            ToastNotRepeat.show(context,"网络连接错误，请稍后重试!");
        }else{
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null){
                    InputStreamReader inputreader = new InputStreamReader(instream,"gbk");
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line ;
                    //分行读取
                    while (( line = buffreader.readLine()) != null){
                        sb.append(line);
                    }
                    instream.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
