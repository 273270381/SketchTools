package ezviz.ezopensdk.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class PackageUtils {
    public static String getVersionName(Context mContext){
        // 获取package管理者  需要上下文
        PackageManager packageManager = mContext.getPackageManager();
        //参数说明   参数一是获取哪一个包名的package的信息 (application包括了activity所以也会包括activity的信息)
        //参数二的信息就是设置是不是之后去
        //获取包名的方法
        String packageName = mContext.getPackageName();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            //获取里面的信息
            String versionName = packageInfo.versionName;
            return  versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        // 获取里面的信息
        // applicationInfo.

        return  null;

    }
    // 获取版本信息
    public static int  getVersionCode(Context mContext){
        // 获取package管理者  需要上下文
        PackageManager packageManager = mContext.getPackageManager();
        //参数说明   参数一是获取哪一个包名的package的信息 (application包括了activity所以也会包括activity的信息)
        //参数二的信息就是设置是不是获取其他的权限还是获取广播,设置为0只是获取简单的版本名称和版本信息
        //获取包名的方法
        String packageName = mContext.getPackageName();
        Log.d("PackageUtils","packageName="+packageName);
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            //获取里面的信息
            int versionCode = packageInfo.versionCode;
            Log.d("PackageUtils","versionCode="+versionCode);
            return  versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return  -1;

    }
}

