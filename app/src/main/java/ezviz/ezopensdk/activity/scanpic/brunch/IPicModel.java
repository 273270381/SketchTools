package ezviz.ezopensdk.activity.scanpic.brunch;

import android.content.Context;

import java.util.List;

import ezviz.ezopensdk.been.db.PicFilePath;

/**
 * Created by hejunfeng on 2020/7/24 0024
 */
public interface IPicModel {
    void processData(Context context,String path, List<?> filePathList, String camera_name, PicCallBack callBack);

    void send(List<String> pathCheckedList,PicCallBack callBack);

    void delete(Context context,List<String> pathCheckedList,List<List<String>> fileList,List<?> filePathList,  PicCallBack callBack);
}
