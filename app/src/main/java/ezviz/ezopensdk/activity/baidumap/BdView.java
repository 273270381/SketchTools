package ezviz.ezopensdk.activity.baidumap;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

import ezviz.ezopensdk.been.base.BaseView;

/**
 * Created by hejunfeng on 2020/7/23 0023
 */
public interface BdView extends BaseView {
    /**
     * 添加地块信息
     */
    void addMarker(List<OverlayOptions> options);

    void addLine(List<OverlayOptions> lineOptions,List<OverlayOptions> textOptions);

    void getDistance(double dis);

    void addMeasureLineOption(OverlayOptions o);

    void addMeasureOption(OverlayOptions options);

    void setLineVisible(PolygonOptions mPolygonOptions);


}
