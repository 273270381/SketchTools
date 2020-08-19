package ezviz.ezopensdk.activity.baidumap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.esri.core.geometry.Point;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ezviz.ezopensdk.R;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.StyleId;
import ezviz.ezopensdk.been.StyleMap;
import ezviz.ezopensdk.utils.ReadKml;

/**
 * Created by hejunfeng on 2020/7/23 0023
 */
public class BdModelImpl implements IBdModel{
    @Override
    public void addInfo(Context context, BdCallBack callBack) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                List<OverlayOptions> markerOptions = new ArrayList<>();
                ReadKml readKml = new ReadKml("camera.kml",context);
                readKml.parseKml2();
                for (int i  = 0 ; i < readKml.getList_point().size() ; i++){
                    //坐标转换
                    CoordinateConverter converter  = new CoordinateConverter().from(CoordinateConverter.CoordType.GPS).
                            coord(new LatLng(readKml.getList_point().get(i).getY(),readKml.getList_point().get(i).getX()));
                    LatLng latLng = converter.convert();
                    Bundle bundle = new Bundle();
                    bundle.putString("name",readKml.getList_name().get(i+2));
                    bundle.putString("des",readKml.getList_des().get(i));
                    OverlayOptions marker_option = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)).extraInfo(bundle);
                    OverlayOptions text_option = new TextOptions().text(readKml.getList_name().get(i+2)).fontSize(25).position(latLng).fontColor(Color.GREEN);
                    markerOptions.add(marker_option);
                    markerOptions.add(text_option);
                }
                callBack.addMarker(markerOptions);
            }
        });
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                ReadKml readKml = new ReadKml("info.kml",context);
                readKml.parseKml();
                List<List<Point>> list_collection = readKml.getList_collection();
                List<String> list_style_url = readKml.getList_style_url();
                List<StyleMap> list_stylemap = readKml.getList_stylemap();
                List<StyleId> list_styleid = readKml.getList_styleid();
                List<String> list_des_info = readKml.getList_des();
                List<OverlayOptions> lineOptions = new ArrayList<>();
                List<OverlayOptions> textOptions = new ArrayList<>();
                for (int i = 0 ; i < list_collection.size() ; i++){
                    List<LatLng> points = new ArrayList<LatLng>();
                    for (int j = 0 ; j < list_collection.get(i).size() ; j++){
                        //坐标转换
                        CoordinateConverter converter  = new CoordinateConverter().from(CoordinateConverter.CoordType.GPS).
                                 coord(new LatLng(list_collection.get(i).get(j).getY(),list_collection.get(i).get(j).getX()));
                        LatLng latLng = converter.convert();
                        points.add(latLng);
                    }
                    String url = list_style_url.get(i);
                    String linecolor="";
                    String linewidth="";
                    for (StyleMap styleMap : list_stylemap){
                        if (styleMap.getId().equals(url)){
                            String stylemapUrl = styleMap.getStyleUrl();
                            for (StyleId styleid : list_styleid){
                                if (styleid.getId().equals(stylemapUrl)){
                                    linecolor = styleid.getLineColor();
                                    linewidth = styleid.getLineWidth();
                                }
                            }
                        }
                    }
                    OverlayOptions mOverlayOptions = new PolylineOptions().width(Integer.parseInt(linewidth)).
                            color(Color.parseColor("#"+linecolor)).points(points);
                    lineOptions.add(mOverlayOptions);

                    String des_info = list_des_info.get(i);
                    BitmapDescriptor bitmapDescriptor = stringToBitmapDescriptor(des_info, context);
                    OverlayOptions option = new MarkerOptions().icon(bitmapDescriptor).position(getInterPosition(points));
                    textOptions.add(option);
                }
                callBack.addLine(lineOptions,textOptions);
                callBack.onSuccess(0);
            }
        });
    }

    /**
     * 测量
     */
    @Override
    public void measure(List points, LatLng latLng, BdCallBack callBack) {
        points.add(latLng);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.point1);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap);
        callBack.addMeasureOption(option);
        if (points.size() == 2){
            //添加线元素
            OverlayOptions mOverlayOptions = new PolylineOptions().width(3).color(Color.BLACK).points(points);
            callBack.addMeasureLineOption(mOverlayOptions);
            //计算距离
            double distance = DistanceUtil. getDistance((LatLng) points.get(0), (LatLng) points.get(1));
            BigDecimal b = new BigDecimal(distance);
            //保留小数点后两位
            double distances = b.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
            callBack.getDistance(distances);
        }else if(points.size()>2){
            //添加面元素
            PolygonOptions mPolygonOptions = new PolygonOptions().points(points).fillColor(Color.argb(75,255,255,0)).stroke(new Stroke(0, R.color.simple_fill_color)); //边框宽度和颜色
            callBack.setLineVisible(mPolygonOptions);
            //计算面积
            String area = measure_area(points);
            double mu = Double.parseDouble(area)*0.0015;
            BigDecimal b = new BigDecimal(mu);
            //保留小数点后两位
            double mu1 = b.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
            callBack.getDistance(mu1);
        }
    }


    /**
     * String to Bitmap
     * @param string
     * @return
     */
    public BitmapDescriptor stringToBitmapDescriptor(String string,Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(10);
        textView.setTextColor(Color.BLACK);
        textView.setShadowLayer(0, 0, 0, Color.BLACK);
        textView.setText(string);
        textView.destroyDrawingCache();
        textView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        textView.setDrawingCacheEnabled(true);
        Bitmap bitmapText = textView.getDrawingCache(true);
        BitmapDescriptor bd = BitmapDescriptorFactory.fromBitmap(bitmapText);
        return bd;
    }

    /**
     * 获取Points集合中心点
     * @param points
     * @return
     */
    private LatLng getInterPosition(List<LatLng> points){
        double x = 0.0, y = 0.0;
        for (int  i = 0 ; i < points.size() ; i++){
            x += points.get(i).latitude;
            y += points.get(i).longitude;
        }
        LatLng latLng = new LatLng(x/points.size(),y/points.size());
        return latLng;
    }

    /**
     *计算面积
     */
    public String measure_area(List<LatLng> points){
        DecimalFormat df = new DecimalFormat("0.000");
        List<double[]> list_point = new ArrayList<double[]>();
        double earthRadiusMeters = 6378137.0;
        double metersPerDegree = 2.0 * Math.PI * earthRadiusMeters / 360.0;
        double radiansPerDegree = Math.PI / 180.0;
        String pt = "";
        //获取图形，并提示
        for ( int i = 0 ; i < points.size() ; i++){
            if ((i+1) >= points.size()){
                if ((i+1) == points.size()){
                    pt = pt +points.get(i).latitude + "," +points.get(i).longitude +",";
                }
            }else{
                if (points.get(i).latitude == points.get(i+1).latitude && points.get(i).longitude == points.get(i+1).longitude){

                }else{
                    pt = pt + points.get(i).latitude + "," + points.get(i).longitude + ",";
                }
            }
        }
        String pp = pt.substring(0, pt.length() - 1);
        String[] pp1 = pp.split(";");
        for (String ppap : pp1) {
            String[] temp = ppap.split(",");
            for (int i = 0; i < temp.length; ) {
                double[] point = {Double.parseDouble(temp[i]), Double.parseDouble(temp[i + 1])};
                list_point.add(point);
                i = i + 2;
            }
        }
        //经纬度计算多边形面积
        double a = 0.0;
        for (int i = 0; i < list_point.size(); ++i) {
            int j = (i + 1) % list_point.size();
            double xi = list_point.get(i)[0] * metersPerDegree * Math.cos(list_point.get(i)[1] * radiansPerDegree);
            double yi = list_point.get(i)[1] * metersPerDegree;
            double xj = list_point.get(j)[0] * metersPerDegree * Math.cos(list_point.get(j)[1] * radiansPerDegree);
            double yj = list_point.get(j)[1] * metersPerDegree;
            a += xi * yj - xj * yi;
        }
        double s = Math.abs(a / 2.0);
        return df.format(s);
    }
}
