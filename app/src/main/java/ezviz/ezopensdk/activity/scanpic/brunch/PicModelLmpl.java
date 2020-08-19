package ezviz.ezopensdk.activity.scanpic.brunch;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.db.DBManager;
import ezviz.ezopensdk.been.db.PicFilePath;
import ezviz.ezopensdk.utils.DataUtils;
import static ezviz.ezopensdk.utils.DataUtils.getFileList;
import static ezviz.ezopensdk.utils.DataUtils.getFileTime;

/**
 * Created by hejunfeng on 2020/7/24 0024
 */
public class PicModelLmpl implements IPicModel{
    @Override
    public void processData(Context context,String path, List<?> filePathList,String camera_name, PicCallBack callBack) {
        AppOperator.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                List<String> dataList = new ArrayList<>();
                List<String> titleList = new ArrayList<>();
                List<List<String>> fileList = new ArrayList<>();
                List<String> timeList = new ArrayList<>();
                List<Integer> indexList = new ArrayList<>();
                if (!"最近".equals(camera_name)){
                    dataList.addAll(DataUtils.getImagePathFromSD(path));
                    if (dataList.size() != 0){
                        //获取所有文件最后修改时间
                        timeList.addAll(getFileTime(dataList));
                        //获取时间相同的文件的下标
                        indexList.addAll(getIndex(timeList));
                        //根据下标，截取出时间相同的文件集合
                        fileList.addAll(getData(dataList,indexList));
                        for (int i = 0 ; i < indexList.size() ; i++){
                            titleList.add(timeList.get(indexList.get(i)));
                        }
                        int width = getScreenProperty(context);
                        callBack.getDada(dataList,titleList,fileList,width);
                        callBack.onSuccess(0);
                    }else{
                        callBack.showTextView(true);
                        callBack.onSuccess(0);
                    }
                }else{
                    if (filePathList.size() != 0){
                        timeList = getFileTime(getFileList((List<Object>) filePathList));
                        indexList = getIndex(timeList);
                        fileList.addAll(getData(dataList,indexList));
                        for (int i = 0 ; i < indexList.size() ; i++){
                            titleList.add(timeList.get(indexList.get(i)));
                        }
                        callBack.getDada(dataList,titleList,fileList,getScreenProperty(context));
                        callBack.onSuccess(0);
                    }else{
                        callBack.showTextView(true);
                        callBack.onSuccess(0);
                    }
                }
            }
        });
    }



    @Override
    public void send( List<String> pathCheckedList,PicCallBack callBack) {
        ArrayList <Uri> files = new ArrayList<>();
        //发送
        for (int i = 0 ; i < pathCheckedList.size() ; i++){
            //Uri uri = FileProvider.getUriForFile(CameraPicActivity.this,"ezviz.ezopensdk.fileprovider",new File(path_checked_list.get(i)));
            Uri uri = Uri.parse(pathCheckedList.get(i));
            files.add(uri);
        }
        callBack.sendFiles("分享",files);
        pathCheckedList.clear();
        callBack.updateView(true);
        callBack.onSuccess("发送成功");
    }

    @Override
    public void delete(Context context, List<String> pathCheckedList, List<List<String>> fileList,List<?> filePathList, PicCallBack callBack) {
        for (int i = 0 ; i < pathCheckedList.size() ; i++){
            File file = new File(pathCheckedList.get(i));
            if (file.exists()){
                file.delete();
            }
            if (filePathList.size() != 0){
                List<String> datalist_db = getFileTime(getFileList(filePathList));
                if (datalist_db.contains(pathCheckedList.get(i))){
                    DBManager.getInstance().delete(PicFilePath.class, "path=?", new String[]{pathCheckedList.get(i)});
                }
            }
            //更新数据
            Iterator ia = fileList.iterator();
            while (ia.hasNext()){
                List<String> s = (List<String>) ia.next();
                Iterator ib = s.iterator();
                while (ib.hasNext()){
                    String str = (String) ib.next();
                    for (int m = 0 ; m < pathCheckedList.size() ; m++){
                        if (str.equals(pathCheckedList.get(m))){
                            ib.remove();
                        }
                    }
                }
            }
            pathCheckedList.clear();
            callBack.updateView(true);
            callBack.onSuccess("删除成功");
        }
    }





    private int getScreenProperty(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;  //屏幕宽度(像素)
        float density = dm.density;  //屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        return width;
    }





    /**
     * 获取相同元素的下标
     * @param time_list
     * @return
     */
    private List<Integer> getIndex(List<String> time_list){
        int index = 0;
        int a = 0;
        List<Integer> list_index = new ArrayList<>();
        while (index <= time_list.size()-1){
            if (a < time_list.size()){
                a = time_list.lastIndexOf(time_list.get(index));
                index = a+1;
                list_index.add(a);
            }
        }
        return list_index;
    }

    /**
     * 集合截取
     * @param datalist
     * @param index_list
     * @return
     */
    private List<List<String>> getData( List<String> datalist,List<Integer> index_list){
        List<List<String>> list = new ArrayList<>();
        for (int  i = 0 ; i < index_list.size() ; i++){
            List<String> list_a = new ArrayList<>();
            if (i == 0 ){
                list_a.addAll(datalist.subList(0,index_list.get(i)+1));
                list.add(list_a);
            }else if (i > 0 ){
                list_a.addAll(datalist.subList(index_list.get(i-1)+1,index_list.get(i)+1));
                list.add(list_a);
            }
        }
        return list;
    }
}
