package ezviz.ezopensdk.activity.baidumap;

import android.content.Context;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * Created by hejunfeng on 2020/7/23 0023
 */
public interface IBdModel<T>{

    void addInfo(Context context, BdCallBack callBack);

    void measure(List points, LatLng latLng, BdCallBack callBack);
}
