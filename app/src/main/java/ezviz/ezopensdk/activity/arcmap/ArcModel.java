package ezviz.ezopensdk.activity.arcmap;

import android.content.Context;

import com.esri.android.map.GraphicsLayer;

/**
 * Created by hejunfeng on 2020/8/13 0013
 */
public interface ArcModel {
    void loadCamera(Context context, String url, ArcCallBack callBack);

    void loadinfo(Context context, String url, ArcCallBack callBack);
}
