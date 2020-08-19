package ezviz.ezopensdk.activity.arcmap;

import android.content.Context;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;

import java.util.concurrent.CountDownLatch;

/**
 * Created by hejunfeng on 2020/8/13 0013
 */
public class LoadKmlTask implements Runnable {

    private CountDownLatch endTaskLatch;
    private String url_1;
    private String url_2;
    private GraphicsLayer graphicsLayer;
    private GraphicsLayer graphicsLayer_camera;
    private GraphicsLayer graphicsLayer_info;
    private GraphicsLayer graphicsLayer_info_text;
    private MapView mapView;
    private ArcPresenter presenter;
    private Context context;

    public LoadKmlTask(CountDownLatch endTaskLatch, String url_1, String url_2, GraphicsLayer graphicsLayer, GraphicsLayer graphicsLayer_camera,
                       GraphicsLayer graphicsLayer_info, GraphicsLayer graphicsLayer_info_text, MapView mapView, ArcPresenter presenter, Context context) {
        this.endTaskLatch = endTaskLatch;
        this.url_1 = url_1;
        this.url_2 = url_2;
        this.graphicsLayer = graphicsLayer;
        this.graphicsLayer_camera = graphicsLayer_camera;
        this.graphicsLayer_info = graphicsLayer_info;
        this.graphicsLayer_info_text = graphicsLayer_info_text;
        this.mapView = mapView;
        this.presenter = presenter;
        this.context = context;
    }

    @Override
    public void run() {
        try {
            endTaskLatch.await();
            presenter.loadCamera(context, url_1);
            presenter.loadInfo(context, url_2);
            mapView.addLayer(graphicsLayer_camera);
            mapView.addLayer(graphicsLayer_info);
            mapView.addLayer(graphicsLayer_info_text);
            mapView.addLayer(graphicsLayer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
