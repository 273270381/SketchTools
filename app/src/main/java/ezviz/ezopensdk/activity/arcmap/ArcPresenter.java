package ezviz.ezopensdk.activity.arcmap;

import android.content.Context;

import com.esri.android.map.GraphicsLayer;
import com.esri.core.map.Graphic;

import ezviz.ezopensdk.been.base.BasePresenter;

/**
 * Created by hejunfeng on 2020/8/13 0013
 */
public class ArcPresenter extends BasePresenter<ArcView> {
    private ArcModel model;

    public ArcPresenter(ArcModel model) {
        this.model = model;
    }

    public void loadCamera(Context context, String url){
        model.loadCamera(context, url, new ArcCallBack() {
            @Override
            public void loadCamera(Graphic g1, Graphic g2) {
                if (getView() != null){
                    getView().loadCamera(g1 ,g2);
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

    public void loadInfo(Context context, String url){
        model.loadinfo(context, url, new ArcCallBack() {

            @Override
            public void loadinfo(Graphic g1, Graphic g2) {
                if (getView() != null){
                    getView().loadinfo(g1, g2);
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
