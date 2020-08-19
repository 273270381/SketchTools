package ezviz.ezopensdk.activity.dataquery;

import java.util.List;

import ezviz.ezopensdk.been.Temp;
import ezviz.ezopensdk.been.base.BaseView;

/**
 * Created by hejunfeng on 2020/8/18 0018
 */
public interface MyView extends BaseView {
    void queryDataImp(List<Temp> list);

    void getCount(int count);

    void sendProcess(Long process);

    void sendDataList(List<Temp> list);
}
