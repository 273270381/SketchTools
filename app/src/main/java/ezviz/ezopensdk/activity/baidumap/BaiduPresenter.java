package ezviz.ezopensdk.activity.baidumap;
import android.content.Context;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.model.LatLng;
import java.util.List;
import ezviz.ezopensdk.been.base.BasePresenter;

/**
 * Created by hejunfeng on 2020/7/23 0023
 */
public class BaiduPresenter extends BasePresenter<BdView> {
    private BdModelImpl bdModel;

    public BaiduPresenter(BdModelImpl bdModel) {
        this.bdModel = bdModel;
    }

    public void showInfo(Context context){
        if (!isViewAttached()) {
            return;
        }
        getView().showLoading();
        bdModel.addInfo(context, new BdCallBack() {

            @Override
            public void addLine(List<OverlayOptions> lineOptions,List<OverlayOptions> textOptions) {
                if (getView() != null){
                    getView().addLine(lineOptions,textOptions);
                }
            }

            @Override
            public void addMarker(List<OverlayOptions> options) {
                if (getView() != null){
                    getView().addMarker(options);
                }
            }

            @Override
            public void onSuccess(Object data) {
                if (getView() != null){
                    getView().hideLoading();
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    public void MeasureDistance(List<LatLng> points,LatLng latLng){
        if (!isViewAttached())
            return;
        bdModel.measure(points,latLng, new BdCallBack() {

            @Override
            public void setLineVisible(PolygonOptions mPolygonOptions) {
                if (getView() != null){
                    getView().setLineVisible(mPolygonOptions);
                }
            }

            @Override
            public void getDistance(double dis) {
                if (getView() != null){
                    getView().getDistance(dis);
                }
            }

            @Override
            public void addMeasureLineOption(OverlayOptions o) {
                if (getView() != null){
                    getView().addMeasureLineOption(o);
                }
            }

            @Override
            public void addMeasureOption(OverlayOptions options) {
                if (getView() != null){
                    getView().addMeasureOption(options);
                }
            }

            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}
