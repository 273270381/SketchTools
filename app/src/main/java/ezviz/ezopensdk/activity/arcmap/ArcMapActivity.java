package ezviz.ezopensdk.activity.arcmap;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geodatabase.ShapefileFeatureTable;
import com.esri.core.geometry.AreaUnit;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.LinearUnit;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.videogo.constant.IntentConsts;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.caui.realpaly.EZRealPlayActivity;
import ezviz.ezopensdk.been.AlarmContant;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.ComPareService;
import ezviz.ezopensdk.been.Constant;
import ezviz.ezopensdk.been.DownLoadService;
import ezviz.ezopensdk.been.UpdateFileService;
import ezviz.ezopensdk.been.UpdateService;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.utils.CopyFontFile;
import ezviz.ezopensdk.utils.FTPutils;
import ezviz.ezopensdk.utils.OSCSharedPreference;
import ezviz.ezopensdk.utils.ReadUtils;
import ezviz.ezopensdk.utils.ServiceUtils;
import ezviz.ezopensdk.utils.ToastNotRepeat;

import static ezviz.ezopensdk.been.AlarmContant.fileNum;
import static ezviz.ezopensdk.been.AlarmContant.tifNum;

/**
 * Created by hejunfeng on 2020/7/22 0022
 */
public class ArcMapActivity extends BaseActivity implements ArcView, View.OnClickListener, OnSingleTapListener, OnZoomListener {
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.change_ibtn)
    ImageButton change;
    @BindView(R.id.info_ibtn)
    ImageButton info;
    @BindView(R.id.robot_ibtn)
    ImageButton robot;
    @BindView(R.id.measure_ibtn)
    ImageButton measure;
    @BindView(R.id.measure_ibtn_sel)
    ImageButton measure_sel;
    @BindView(R.id.zoom_in_ibtn)
    ImageButton zoom_in;
    @BindView(R.id.zoom_out_ibtn)
    ImageButton zoom_out;
    @BindView(R.id.position_ibtn)
    ImageButton position;
    @BindView(R.id.position_ibtn_sel)
    ImageButton position_sel;
    @BindView(R.id.result)
    TextView result;
    @BindView(R.id.title_tv)
    TextView title;
    @BindView(R.id.update)
    TextView update;

    public CountDownLatch TaskLatch;
    private String path;
    private List<EZDeviceInfo> list_ezDevices;
    private LocationDisplayManager locationDisplayManager = null;
    private GraphicsLayer graphicsLayer_info;
    private GraphicsLayer graphicsLayer_info_text;
    private GraphicsLayer graphicsLayer_camera;
    private GraphicsLayer graphicsLayer;
    private List<Point> pointList = new ArrayList<>();
    private List<Graphic> list_graphic = new ArrayList<>();
    private String url_3 = "/west";
    private ArcPresenter presenter;
    private String TAG = "ArcMapActivity";
    public final static int REQUEST_CODE = 100;
    private String local_path = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/map";
    private MyReceiver receiver;
    private Boolean hasMap = false;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    hideLoading();
                    //1.txt解析
                    String s = getTxtContent("/EZOpenSDK/map/version.txt");
                    String local_s = getTxtContent("/EZOpenSDK/map"+url_3+"/version.txt");
                    //2.与本地txt比对
                    String[] num = s.substring(0, s.lastIndexOf(";")).split(",");
                    String[] local_num = local_s.substring(0, local_s.lastIndexOf(";")).split(",");
                    List<String> name_list = Arrays.asList(num);
                    List<String> name_local_list = Arrays.asList(local_num);
                    try {
                        if (name_list.size() != name_local_list.size()){
                            if (name_local_list.size() > 0) {
                                for (int i = 0; i < name_local_list.size(); i++) {
                                    name_list.remove(name_local_list.get(i));
                                }
                            }
                            updateMap(name_list);
                        }else{
                            ToastNotRepeat.show(getApplicationContext(),"当前版本已是最新版本！");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        //updateMap(name_list);
                    }

                    break;
                case 1:
                    hideLoading();
                    ToastNotRepeat.show(getApplicationContext(),"网络连接错误，请稍后重试...");
                    break;
            }
        }
    };

    private void updateMap(List<String> name_list) {
        List<String> serverPath_list = new ArrayList<>();
        List<String> filename_list = new ArrayList<>();
        for (String str : name_list){
            serverPath_list.add("node/kaifaqu/map"+url_3+url_3+str+".TIF");
            serverPath_list.add("node/kaifaqu/map"+url_3+url_3+str+".TIF.aux.xml");
            serverPath_list.add("node/kaifaqu/map"+url_3+url_3+str+".TIF.ovr");
            serverPath_list.add("node/kaifaqu/map"+url_3+url_3+str+".tfw");
            filename_list.add(url_3.substring(1)+str+".TIF");
            filename_list.add(url_3.substring(1)+str+".TIF.aux.xml");
            filename_list.add(url_3.substring(1)+str+".TIF.ovr");
            filename_list.add(url_3.substring(1)+str+".tfw");
        }
        if (ServiceUtils.isServiceRunning(getApplicationContext(), UpdateFileService.getServiceName())){
            showLoading("影像文件下载中，请稍后...");
        }else{
            //3.根据比对结果下载地图文件
            showConfirmDialog("是否更新？", "提示", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showLoading("影像文件更新中，请稍后...");
                    // TODO: 2020/8/15  更新影像
                    String server_path = "node/kaifaqu/map"+url_3+"/version.txt";
                    String file_name = "version.txt";
                    UpdateFileService.startService(getApplicationContext(), local_path+url_3, filename_list, serverPath_list, server_path, file_name);
                }
            });
        }
    }


    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void initPresenter() {
        presenter = new ArcPresenter(new ArcModelImpl());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_arcmap;
    }

    @Override
    public void showToast(String message) {

    }

    public static void show(Context context, String key, List ls){
        Intent intent = new Intent(context,ArcMapActivity.class);
        intent.putParcelableArrayListExtra(key, (ArrayList<? extends Parcelable>) ls);
        context.startActivity(intent);
    }

    @Override
    protected void initData() {
        //设置授权
        ArcGISRuntime.setClientId("Gxw2gDOFkkdudimV");
        mapView = (MapView) findViewById(R.id.map);
        update.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        TaskLatch = new CountDownLatch(1);
        CopyFontFile mCopyData_File = new CopyFontFile(this);
        mCopyData_File.DoCopy();
        mapView.setEsriLogoVisible(false);
        mapView.setOnSingleTapListener(this);
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("ezviz.ezopensdk.MYRESTART");
        registerReceiver(receiver,filter);
        path = OSCSharedPreference.getInstance().getPath();
        list_ezDevices = new ArrayList<>();
        list_ezDevices = getIntent().getParcelableArrayListExtra("devices_main");
        if (path.equals("") || path == null) {
            path = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/map/west";
        }
        //加载tif
        locationDisplayManager = mapView.getLocationDisplayManager();
        try {
            locationDisplayManager.setAllowNetworkLocation(true);
            locationDisplayManager.setAccuracyCircleOn(true);
            locationDisplayManager.setShowLocation(true);
            locationDisplayManager.setAccuracySymbol(new SimpleFillSymbol(Color.GREEN).setAlpha(20));
            locationDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.OFF);
        } catch (Exception e) {
            e.printStackTrace();
        }
        graphicsLayer = new GraphicsLayer();
        graphicsLayer_camera = new GraphicsLayer();
        graphicsLayer_info = new GraphicsLayer();
        graphicsLayer_info_text = new GraphicsLayer();
        graphicsLayer_info_text.setVisible(false);
        loadlayer(path);
    }

    private void loadlayer(String path) {
        showLoading();
        update.setVisibility(View.GONE);
        File file = new File(path);
        String url_2 = "西区.kml";
        if ((path.substring(path.lastIndexOf("/") + 1)).equals("west")) {
            title.setText("西区");
            url_2 = "西区.kml";
            url_3 = "/west";
        } else {
            title.setText("南区");
            url_2 = "南区.kml";
            url_3 = "/south";
        }
        if (!file.exists()){
            hideLoading();
            boolean a = ServiceUtils.isServiceRunning(getApplicationContext(),DownLoadService.getServiceName());
            if(a){
                showLoading("影像文件下载中，请稍后...");
            }else{
                showConfirmDialog("缺少影像文件，是否下载？", "提示", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 2020/8/13 下载影像文件
                        showLoading("影像文件下载中，请稍后...");
                        DownLoadService.startService(getApplicationContext(),path,url_3);
                    }
                });
            }
        }else{
            //判断文件是否完整
            File[] files = file.listFiles();
            if (files.length<fileNum ){
                List<String> l = new ArrayList<>();
                for (File sfile : files){
                    l.add(sfile.getName());
                }
                // TODO: 2020/8/13 文件比对
                if (ServiceUtils.isServiceRunning(getApplicationContext(),ComPareService.getServiceName())){
                    showLoading("影像文件下载中，请稍后...");
                }else{
                    showConfirmDialog("缺少影像文件，是否下载？", "提示", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showLoading("影像文件下载中，请稍后...");
                            ComPareService.startService(getApplicationContext(),path,url_3,l);
                        }
                    });
                }
            }else{
                hideLoading();
                hasMap = true;
                update.setVisibility(View.VISIBLE);
                //加载shp
                try {
                    ShapefileFeatureTable shapefileFeatureTable = new ShapefileFeatureTable(path + "/polyline.shp");
                    FeatureLayer featureLayer = new FeatureLayer(shapefileFeatureTable);
                    featureLayer.setRenderer(new SimpleRenderer(new SimpleFillSymbol(Color.RED)));
                    mapView.addLayer(featureLayer);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                //加载tif
                for (int i = 0; i < tifNum; i++) {
                    Log.d(TAG,String.valueOf(i));
                    AppOperator.runOnMainThread(new ImageTask(TaskLatch, path, i, mapView, url_3));
                }
                //加载kml
                LoadKmlTask loadKmlTask = new LoadKmlTask(TaskLatch, "camera.kml", url_2, graphicsLayer, graphicsLayer_camera,
                        graphicsLayer_info, graphicsLayer_info_text, mapView, presenter, getApplicationContext());
                AppOperator.runOnMainThread(loadKmlTask);
            }
        }
    }


    @OnClick({R.id.change_ibtn,R.id.info_ibtn,R.id.robot_ibtn,R.id.measure_ibtn,R.id.measure_ibtn_sel,R.id.zoom_in_ibtn,
            R.id.zoom_out_ibtn,R.id.position_ibtn,R.id.position_ibtn_sel,R.id.update})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_ibtn:
                if (hasMap){
                    selectfile();
                }
                break;
            case R.id.info_ibtn:
                if (hasMap){
                    if (graphicsLayer_info.isVisible()) {
                        graphicsLayer_info.setVisible(false);
                        if (mapView.getScale() < 30000) {
                            graphicsLayer_info_text.setVisible(false);
                        }
                        info.setBackgroundResource(R.mipmap.xinxi);
                    } else {
                        graphicsLayer_info.setVisible(true);
                        if (mapView.getScale() < 30000) {
                            graphicsLayer_info_text.setVisible(true);
                        }
                        info.setBackgroundResource(R.mipmap.xinxi_sel);
                    }
                }
                break;
            case R.id.robot_ibtn:
                if (hasMap){
                    if (graphicsLayer_camera.isVisible()) {
                        graphicsLayer_camera.setVisible(false);
                        robot.setBackgroundResource(R.mipmap.jiqiren);
                    } else {
                        graphicsLayer_camera.setVisible(true);
                        robot.setBackgroundResource(R.mipmap.jiqiren_sel);
                    }
                }
                break;
            case R.id.measure_ibtn:
                if (hasMap){
                    measure.setVisibility(View.GONE);
                    measure_sel.setVisibility(View.VISIBLE);
                    result.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    graphicsLayer.removeAll();
                    pointList.clear();
                }
                break;
            case R.id.measure_ibtn_sel:
                if (hasMap){
                    measure.setVisibility(View.VISIBLE);
                    measure_sel.setVisibility(View.GONE);
                    result.setVisibility(View.GONE);
                    title.setVisibility(View.VISIBLE);
                    list_graphic.clear();
                    result.setText("");
                    graphicsLayer.removeAll();
                    pointList.clear();
                }
                break;
            case R.id.zoom_in_ibtn:
                if (hasMap){
                    mapView.zoomin();
                }
                break;
            case R.id.zoom_out_ibtn:
                if (hasMap){
                    mapView.zoomout();
                }
                break;
            case R.id.position_ibtn:
                if (hasMap){
                    if (!locationDisplayManager.isStarted()) {
                        locationDisplayManager.start();
                        position.setVisibility(View.GONE);
                        position_sel.setVisibility(View.VISIBLE);
                        AppOperator.runOnMainThreadDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    double la = locationDisplayManager.getLocation().getLatitude();
                                    double ln = locationDisplayManager.getLocation().getLongitude();
                                    Point p = new Point(ln, la);
                                    Envelope e = mapView.getMaxExtent();
                                    if (!e.contains(p)) {
                                        locationDisplayManager.stop();
                                        position.setVisibility(View.VISIBLE);
                                        position_sel.setVisibility(View.GONE);
                                        ToastNotRepeat.show(getApplicationContext(), "超出地图范围！");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        },1500);
                    }
                }
                break;
            case R.id.position_ibtn_sel:
                if (hasMap){
                    if (locationDisplayManager.isStarted()) {
                        locationDisplayManager.stop();
                        position.setVisibility(View.VISIBLE);
                        position_sel.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.update:
                if (hasMap){
                    map_update();
                }
                break;
        }
    }

    /**
     * 影像更新
     */
    private void map_update() {
        // TODO: 2020/8/13 地图下载
        String server_path = "node/kaifaqu/map"+url_3+"/version.txt";
        String file_name = "version.txt";
        showLoading();
        FTPutils.FtpProgressListener listener = new FTPutils.FtpProgressListener() {
            @Override
            public void onFtpProgress(int currentStatus, long process, File targetFile,long currentSize,long serverSize) {
                if (currentStatus == Constant.FTP_DOWN_SUCCESS) {
                    Message message = Message.obtain();
                    message.what = 0;
                    handler.sendMessage(message);
                }
            }
        };
        File file = new File(local_path + "/" + file_name);
        if (file.exists()) {
            file.delete();
        }
        UpdateTask updateTask = new UpdateTask(local_path, server_path, file_name, listener);
        AppOperator.runOnThread(updateTask);
    }

    private void selectfile() {
        if (path.equals("") || path == null || (path.substring(path.lastIndexOf("/") + 1)).equals("south")) {
            path = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/map/west";
        } else if ((path.substring(path.lastIndexOf("/") + 1)).equals("west")) {
            path = Environment.getExternalStorageDirectory().getPath() + "/EZOpenSDK/map/south";
        }
        OSCSharedPreference.getInstance().putPath(path);
        recreate();
    }

    private String getTxtContent(String s2) {
        String path = Environment.getExternalStorageDirectory().getPath() + s2;
        String content = ReadUtils.ReadTxtFile(path, ArcMapActivity.this);
        String[] str = content.split(":");
        String s = str[str.length - 1];
        return s;
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume");
        super.onResume();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG,"onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (locationDisplayManager.isStarted()) {
            locationDisplayManager.stop();
        }
    }

    @Override
    public void onSingleTap(float v, float v1) {
        Log.d(TAG,"onSingleTap");
        if (hasMap){
            if (measure_sel.getVisibility() == View.VISIBLE){
                Point p = mapView.toMapPoint(v, v1);
                pointList.add(p);
                //点，线，面样式
                SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(Color.BLACK, 6, SimpleMarkerSymbol.STYLE.CIRCLE);
                SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(Color.BLACK, 2);
                SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.YELLOW);
                simpleFillSymbol.setAlpha(90);
                simpleFillSymbol.setOutline(new SimpleLineSymbol(Color.argb(0, 0, 0, 0), 1));
                if (pointList.size() == 1) {
                    Graphic point = new Graphic(p, simpleMarkerSymbol);
                    graphicsLayer.addGraphic(point);
                }else if (pointList.size() == 2) {
                    graphicsLayer.removeAll();
                    Polyline polyline = new Polyline();
                    polyline.startPath(pointList.get(0));
                    polyline.lineTo(p);
                    Graphic line = new Graphic(polyline, simpleLineSymbol);
                    graphicsLayer.addGraphic(line);
                    double distance = GeometryEngine.geodesicDistance(pointList.get(0), pointList.get(1), mapView.getSpatialReference(), new LinearUnit(LinearUnit.Code.METER));
                    double distance_2 = new BigDecimal(distance).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    result.setText("距离为:" + distance_2 + "米");
                }else if (pointList.size() > 2) {
                    graphicsLayer.removeAll();
                    Polygon polygon = new Polygon();
                    polygon.startPath(pointList.get(0));
                    for (int i = 1; i < pointList.size(); i++) {
                        polygon.lineTo(pointList.get(i));
                    }
                    Graphic gon = new Graphic(polygon, simpleFillSymbol);
                    graphicsLayer.addGraphic(gon);
                    double area = GeometryEngine.geodesicArea(polygon, mapView.getSpatialReference(), new AreaUnit(AreaUnit.Code.SQUARE_METER));
                    double area_2 = new BigDecimal(area).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    double mu = area * 0.0015;
                    BigDecimal b = new BigDecimal(mu);
                    //保留小数点后两位
                    double mu_2 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    result.setText("面积为:" + area_2 + "平方米/" + mu_2 + "亩");
                }
            }else{
                int[] objectIds = graphicsLayer_camera.getGraphicIDs(v, v1, 20);
                if (objectIds != null && objectIds.length > 0) {
                    for (int i = 0; i < objectIds.length; i++) {
                        Graphic graphic = graphicsLayer_camera.getGraphic(objectIds[i]);
                        if (graphic.getAttributes().get("style").equals("marker")) {
                            showDialog(graphic);
                        }
                    }
                }
            }
        }
    }
    /**
     * marker弹窗
     */
    private void showDialog(Graphic graphic) {
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.marker_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(ArcMapActivity.this).setTitle("").setView(linearLayout).show();
        TextView tv_name = dialog.findViewById(R.id.name_tv);
        TextView tv_des = dialog.findViewById(R.id.des_tv);
        Button btn_open = dialog.findViewById(R.id.open);
        tv_name.setText(graphic.getAttributes().get("name").toString());
        tv_des.setText(graphic.getAttributes().get("des").toString());
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (EZDeviceInfo ezDeviceInfo : list_ezDevices) {
                    for (EZCameraInfo ezCameraInfo : ezDeviceInfo.getCameraInfoList()) {
                        if (ezCameraInfo == null) {
                            return;
                        } else if (ezCameraInfo.getCameraName().equals(tv_name.getText())) {
                            Intent intent = new Intent(ArcMapActivity.this, EZRealPlayActivity.class);
                            intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, ezCameraInfo);
                            intent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, ezDeviceInfo);
                            intent.putExtra("titlename",tv_name.getText());
                            startActivityForResult(intent, REQUEST_CODE);
                            return;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void preAction(float v, float v1, double v2) {
        //定义地图默认缩放处理之前的操作。
    }

    @Override
    public void postAction(float v, float v1, double v2) {
        if (hasMap){
            //定义地图默认缩放处理后的操作。
            if (mapView.getScale() > 30000) {
                graphicsLayer_info_text.setVisible(false);
            } else {
                graphicsLayer_info_text.setVisible(true);
            }
        }
    }

    @Override
    public void loadCamera(Graphic g1, Graphic g2) {
        graphicsLayer_camera.addGraphic(g1);
        graphicsLayer_camera.addGraphic(g2);
    }

    @Override
    public void loadinfo(Graphic g1, Graphic g2) {
        graphicsLayer_info.addGraphic(g1);
        graphicsLayer_info_text.addGraphic(g2);
    }
    /**
     * 检查更新
     */
    private class UpdateTask implements Runnable {
        private String localPath;
        private String serverPath;
        private String fileName;
        private FTPutils.FtpProgressListener listener;

        public UpdateTask(String localPath, String serverPath, String fileName, FTPutils.FtpProgressListener listener) {
            this.localPath = localPath;
            this.serverPath = serverPath;
            this.fileName = fileName;
            this.listener = listener;
        }

        @Override
        public void run() {
            FTPutils ftPutils = new FTPutils();
            Boolean flag = ftPutils.connect(AlarmContant.ftp_ip, Integer.parseInt(AlarmContant.ftp_port), AlarmContant.name, AlarmContant.password);
            if (flag) {
                try {
                    ftPutils.downloadSingleFile2(serverPath, localPath, fileName, listener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Message message = Message.obtain();
                message.what = 1;
                handler.sendMessage(message);
            }
        }
    }

    public class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            recreate();
        }
    }
}
