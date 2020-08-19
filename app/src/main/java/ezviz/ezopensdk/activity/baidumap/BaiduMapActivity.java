package ezviz.ezopensdk.activity.baidumap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.DistanceUtil;
import com.esri.core.geometry.Point;
import com.videogo.constant.IntentConsts;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.OSCApplication;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.utils.OSCSharedPreference;

/**
 * Created by hejunfeng on 2020/7/22 0022
 */
public class BaiduMapActivity extends BaseActivity implements View.OnClickListener, BdView,
        BaiduMap.OnMapClickListener, BaiduMap.OnMarkerClickListener , BaiduMap.OnMapStatusChangeListener {

    @BindViews({R.id.change_ibtn, R.id.info_ibtn, R.id.robot_ibtn, R.id.measure_ibtn,
            R.id.measure_ibtn_sel, R.id.zoom_in_ibtn, R.id.zoom_out_ibtn,R.id.position_ibtn, R.id.position_ibtn_sel})
    List<ImageButton> buttonList;
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.result)
    TextView result;

    private BaiduMap mBaiduMap;
    private boolean isFirstLoc = true;
    private LocationService locationService;
    private String style;
    private List<EZDeviceInfo> listEzdevices;
    private MyOrientationListener myOrientationListener;
    private BDLocation location;
    private float angle;
    private String TAG = "baiduMapActivity";
    private BaiduPresenter bdPresenter;
    private List<OverlayOptions> markerOptions = new ArrayList<>();
    private List<OverlayOptions> lineOptions = new ArrayList<>();
    private List<OverlayOptions> textOptions = new ArrayList<>();
    private List<Overlay> markerOverlays = new ArrayList<>();
    private List<Overlay> lineOverlays = new ArrayList<Overlay>();
    private Overlay o;
    public final static int REQUEST_CODE = 100;
    private List<LatLng> points = new ArrayList<>();
    private Boolean showText = false;
    private Boolean showInfo = false;
    private Boolean showCamera = false;


    @Override
    public BasePresenter getPresenter() {
        return bdPresenter;
    }

    @Override
    public void initPresenter() {
        bdPresenter = new BaiduPresenter(new BdModelImpl());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_baidu_map;
    }

    @Override
    public void showToast(String message) {

    }
    public static void show(Context context, String key, List ls){
        Intent intent = new Intent(context, BaiduMapActivity.class);
        intent.putParcelableArrayListExtra(key, (ArrayList<? extends Parcelable>) ls);
        context.startActivity(intent);
    }

    @Override
    protected void initData() {
        style = OSCSharedPreference.getInstance().getMapStyle();
        listEzdevices = getIntent().getParcelableArrayListExtra("devices_baidu");
        initMap();
    }

    @Override
    protected void initWidget() {
        bdPresenter.showInfo(this);
    }

    private void initMap(){
        // 不显示缩放比例尺
        mapView.showZoomControls(false);
        // 不显示百度地图Logo
        mapView.removeViewAt(1);
        //初始化位置
        mBaiduMap = mapView.getMap();
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapStatusChangeListener(this);
        //打开交通图
        mBaiduMap.setTrafficEnabled(false);
        UiSettings settings = mBaiduMap.getUiSettings();
        settings.setCompassEnabled(false);
        settings.setOverlookingGesturesEnabled(false);
        if ("NORMAL".equals(style)||"".equals(style)||style==null){
            //标准地图
            style = "NORMAL";
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }else{
            //卫星地图
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        }
        mBaiduMap.setMyLocationEnabled(true);
        //初始位置
        LatLng cenpt = new LatLng(33.92287826538086, 118.19874572753906);
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(13).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);
        BaiduMapOptions optionsStatue = new BaiduMapOptions();
        optionsStatue.zoomGesturesEnabled(true);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        myOrientationListener = new MyOrientationListener(this);
        myOrientationListener.setmOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                angle = x;
            }
        });
        myOrientationListener.star();
        locationService = ((OSCApplication) getApplication()).locationService;
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }


    @OnClick({R.id.change_ibtn, R.id.info_ibtn, R.id.robot_ibtn, R.id.measure_ibtn,
            R.id.measure_ibtn_sel, R.id.zoom_in_ibtn, R.id.zoom_out_ibtn,R.id.position_ibtn, R.id.position_ibtn_sel})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_ibtn:
                selectfile();
                break;
            case R.id.info_ibtn:
                showInfo();
                break;
            case R.id.robot_ibtn:
                showRobot();
                break;
            case R.id.measure_ibtn:
                buttonList.get(3).setVisibility(View.GONE);
                buttonList.get(4).setVisibility(View.VISIBLE);
                result.setVisibility(View.VISIBLE);
                break;
            case R.id.measure_ibtn_sel:
                clearMeasure();
                break;
            case R.id.zoom_in_ibtn:
                zoomIn();
                break;
            case R.id.zoom_out_ibtn:
                zoomOut();
                break;
            case R.id.position_ibtn:
                startLocation();
                break;
            case R.id.position_ibtn_sel:
                stopLocation();
                break;
        }
    }

    private void showRobot() {
        if (showCamera){
            mBaiduMap.addOverlays(markerOptions);
            buttonList.get(2).setBackgroundResource(R.mipmap.jiqiren_sel);
            showCamera = false;
        }else{
            mBaiduMap.clear();
            clearMeasure();
            if (!showInfo){
                mBaiduMap.addOverlays(lineOptions);
            }
            if (showText){
                mBaiduMap.addOverlays(textOptions);
            }
            buttonList.get(2).setBackgroundResource(R.mipmap.jiqiren);
            showCamera = true;
        }
    }

    private void showInfo() {
        if (showInfo){
            mBaiduMap.addOverlays(lineOptions);
            mBaiduMap.addOverlays(textOptions);
            buttonList.get(1).setBackgroundResource(R.mipmap.xinxi_sel);
            showInfo = false;
            showText = true;
        }else{
            mBaiduMap.clear();
            clearMeasure();
            if (!showCamera){
                mBaiduMap.addOverlays(markerOptions);
            }
            buttonList.get(1).setBackgroundResource(R.mipmap.xinxi);
            showInfo = true;
            showText = false;
        }
    }

    private void clearMeasure(){
        for (Overlay overlay : markerOverlays){
            overlay.setVisible(false);
        }
        for (Overlay overlay :lineOverlays){
            overlay.setVisible(false);
        }
        buttonList.get(3).setVisibility(View.VISIBLE);
        buttonList.get(4).setVisibility(View.GONE);
        result.setVisibility(View.GONE);
        points.clear();
        markerOverlays.clear();
        lineOverlays.clear();
        result.setText("");
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        buttonList.get(7).setVisibility(View.GONE);
        buttonList.get(8).setVisibility(View.VISIBLE);
        if (isFirstLoc){
            locationService.requestLocation();
            locationService.start();
        }else{
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(new LatLng(location.getLatitude(),location.getLongitude())).build();
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    /**
     * 停止定位
     */
    private void stopLocation() {
        buttonList.get(7).setVisibility(View.VISIBLE);
        buttonList.get(8).setVisibility(View.GONE);
        MapStatus.Builder builder = new MapStatus.Builder();
        LatLng cenpt = new LatLng(33.935681, 118.289365);
        builder.target(cenpt).zoom(13).build();
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        location = locationService.getLastKnownLocation();
    }

    /**
     * 放大
     */
    private void zoomOut() {
        MapStatusUpdate zoomOut = MapStatusUpdateFactory.zoomOut();
        mBaiduMap.setMapStatus(zoomOut);
    }

    /**
     * 缩小
     */
    private void zoomIn() {
        MapStatusUpdate zoomIn = MapStatusUpdateFactory.zoomIn();
        mBaiduMap.setMapStatus(zoomIn);
    }

    /**
     *地图切换
     */
    private void selectfile() {
        if ("NORMAL".equals(style)){
            OSCSharedPreference.getInstance().putMapStyle("SATELLITE");
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            style = "SATELLITE";
        }else if ("SATELLITE".equals(style)){
            OSCSharedPreference.getInstance().putMapStyle("NORMAL");
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
            style = "NORMAL";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销掉监听
        locationService.unregisterListener(mListener); 
        locationService.stop(); //停止定位服务
        myOrientationListener.stop();
        mapView.onDestroy();
    }

    private BDAbstractLocationListener mListener = new BDAbstractLocationListener(){

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // 构造定位数据
            MyLocationData locdata = new MyLocationData.Builder()
                    .direction(angle)
                    .accuracy(100)
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locdata);
            //配置定位图层显示方式,三个参数的构造器
            MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,null);
            mBaiduMap.setMyLocationConfiguration(configuration);
            if(isFirstLoc){
                isFirstLoc = false;
                LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(status);
            }
        }
    };


    @Override
    public void addMarker(List<OverlayOptions> options) {
        markerOptions = options;
        mBaiduMap.addOverlays(markerOptions);
    }

    @Override
    public void addLine(List<OverlayOptions> lOptions,List<OverlayOptions> tOptions) {
        lineOptions = lOptions;
        textOptions = tOptions;
        mBaiduMap.addOverlays(lineOptions);
    }

    @Override
    public void getDistance(double dis) {
        result.setText("距离为:"+dis+"米");
    }

    @Override
    public void addMeasureLineOption(OverlayOptions mOverlayOptions) {
        Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);
        lineOverlays.add(mPolyline);
        //隐藏marker图标
        markerOverlays.get(0).setVisible(false);
        o.setVisible(false);
    }

    @Override
    public void addMeasureOption(OverlayOptions options) {
        Log.d(TAG,"points.size="+points.size());
        o = mBaiduMap.addOverlay(options);
        markerOverlays.add(o);
    }

    @Override
    public void setLineVisible(PolygonOptions mPolygonOptions) {

        lineOverlays.get(lineOverlays.size()-1).setVisible(false);
        Overlay mPolygon = mBaiduMap.addOverlay(mPolygonOptions);
        lineOverlays.add(mPolygon);
        Log.d(TAG,"lineOverlays.size="+lineOverlays.size());
        o.setVisible(false);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (buttonList.get(4).getVisibility() == View.VISIBLE){
            bdPresenter.MeasureDistance(points, latLng);
        }
    }

    @Override
    public void onMapPoiClick(MapPoi mapPoi) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle bundle = marker.getExtraInfo();
        if (bundle!=null){
            showDialog(bundle.getString("name"),bundle.getString("des"));
        }
        return false;
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        float zoom = mapStatus.zoom;
        if (zoom >= 16 && !showText){
            mBaiduMap.addOverlays(textOptions);
            showText = true;
        }else if (zoom < 16 && showText){
            mBaiduMap.clear();
            if (!showCamera){
                mBaiduMap.addOverlays(markerOptions);
            }
            if (!showInfo){
                mBaiduMap.addOverlays(lineOptions);
            }
            showText = false;
        }
    }

    /**
     * marker弹窗
     */
    private void showDialog(String name , String des){
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.marker_dialog,null);
        AlertDialog dialog = new AlertDialog.Builder(BaiduMapActivity.this).setTitle("").setView(linearLayout).show();
        TextView tvName = dialog.findViewById(R.id.name_tv);
        TextView tvDes = dialog.findViewById(R.id.des_tv);
        Button btnOpen = dialog.findViewById(R.id.open);
        tvName.setText(name);
        tvDes.setText(des);
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EZDeviceInfo ezDeviceInfo : listEzdevices){
                    for (EZCameraInfo ezCameraInfo : ezDeviceInfo.getCameraInfoList()){
                        if (ezCameraInfo == null){
                            return;
                        }else if(ezCameraInfo.getCameraName().equals(tvName.getText())){
//                            Intent intent = new Intent(BaiduMapActivity.this , EZRealPlayActivity.class);
//                            intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, ezCameraInfo);
//                            intent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, ezDeviceInfo);
//                            startActivityForResult(intent, REQUEST_CODE);
                            return;
                        }
                    }
                }
            }
        });
    }
}
