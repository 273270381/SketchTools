package ezviz.ezopensdk.activity.scanpic.brunch;

import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import ezviz.ezopensdk.been.base.BaseCallback;

/**
 * Created by hejunfeng on 2020/7/24 0024
 */
public abstract class PicCallBack implements BaseCallback {
    public void getDada(List<String> dataList,List<String> titleList,List<List<String>> fileList, int width){}

    public void showTextView(Boolean b){}

    public void updateView(Boolean b){}

    public void sendFiles(String str, ArrayList<Uri> file){}
}
