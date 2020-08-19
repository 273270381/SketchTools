package ezviz.ezopensdk.activity.scanpic.brunch;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import ezviz.ezopensdk.been.base.BaseView;

/**
 * Created by hejunfeng on 2020/7/24 0024
 */
public interface PicView extends BaseView {

    void processData(List<String> dataList, List<String> titleList, List<List<String>> fileList, int width);

    void showTextView(Boolean b);

    void updateView(Boolean b);

    void sendFiles(String str, ArrayList<Uri> file);
}
