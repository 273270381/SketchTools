package ezviz.ezopensdk.activity.baidumap;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

import ezviz.ezopensdk.been.base.BaseCallback;

/**
 * Created by hejunfeng on 2020/7/23 0023
 */
public abstract class BdCallBack implements BaseCallback {


    /**
     * 添加地块信息marker
     * @param options
     */
    public void addMarker(List<OverlayOptions> options){}


    /**
     * 添加地块信息线段
     * @param lineOptions
     * @param textOptions
     */
    public void addLine(List<OverlayOptions> lineOptions,List<OverlayOptions> textOptions){}


    /**
     * 添加测量marker
     * @param options
     */
    public void addMeasureOption(OverlayOptions options){}

    /**
     * 添加测量线段
     * @param o
     */
    public void addMeasureLineOption(OverlayOptions o){}

    /**
     * 距离
     * @param dis
     */
    public void getDistance(double dis){}

    /**
     * 隐藏线段
     */
    public void setLineVisible(PolygonOptions mPolygonOptions){}
}
