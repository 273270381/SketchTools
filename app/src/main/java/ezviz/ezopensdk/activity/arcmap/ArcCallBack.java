package ezviz.ezopensdk.activity.arcmap;

import com.esri.core.map.Graphic;

import ezviz.ezopensdk.been.base.BaseCallback;

/**
 * Created by hejunfeng on 2020/8/13 0013
 */
public abstract class ArcCallBack implements BaseCallback {
    public void loadCamera(Graphic g1, Graphic g2){}

    public void loadinfo(Graphic g1, Graphic g2){}
}
