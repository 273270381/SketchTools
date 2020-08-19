package ezviz.ezopensdk.activity.arcmap;

import com.esri.core.map.Graphic;

import ezviz.ezopensdk.been.base.BaseView;

/**
 * Created by hejunfeng on 2020/8/13 0013
 */
public interface ArcView extends BaseView {
    void loadCamera(Graphic g1, Graphic g2);

    void loadinfo(Graphic g1, Graphic g2);
}
