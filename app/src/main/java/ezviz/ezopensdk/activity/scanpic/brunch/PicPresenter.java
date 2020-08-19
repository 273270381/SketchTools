package ezviz.ezopensdk.activity.scanpic.brunch;

import android.content.Context;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import ezviz.ezopensdk.been.base.BasePresenter;

/**
 * Created by hejunfeng on 2020/7/24 0024
 */
public class PicPresenter extends BasePresenter<PicView> {
    private PicModelLmpl modelLmpl;

    public PicPresenter(PicModelLmpl modelLmpl) {
        this.modelLmpl = modelLmpl;
    }

    public void getData(Context context,String path, List<?> filePathList, String camera_name){
        if (!isViewAttached()){
            return;
        }
        getView().showLoading();
        modelLmpl.processData(context, path, filePathList, camera_name, new PicCallBack() {

            @Override
            public void getDada(List<String> dataList, List<String> titleList, List<List<String>> fileList, int width) {
                super.getDada(dataList, titleList, fileList, width);
                if (getView() != null){
                    getView().processData(dataList, titleList, fileList, width);
                }
            }

            @Override
            public void showTextView(Boolean b) {
                super.showTextView(b);
                if (getView() != null){
                    getView().showTextView(b);
                }
            }

            @Override
            public void onSuccess(Object data) {
                getView().hideLoading();
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    /**
     * 发送文件
     */
    public void send(List<String> pathCheckedList){
        if (!isViewAttached()){
            return;
        }
        getView().showLoading();
        modelLmpl.send(pathCheckedList,new PicCallBack() {

            @Override
            public void sendFiles(String str, ArrayList<Uri> file) {
                super.sendFiles(str, file);
            }

            @Override
            public void updateView(Boolean b) {
                super.updateView(b);
                if (getView() != null){
                    getView().updateView(b);
                }
            }

            @Override
            public void onSuccess(Object data) {
                if (getView() != null){
                    getView().hideLoading();
                    getView().showToast((String)data);
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
    /**
     * 删除文件
     */
    public void delete(Context context,List<String> pathCheckedList, List<List<String>> fileList, List<?> filePathList){
        if (!isViewAttached()){
            return;
        }
        getView().showLoading();
        modelLmpl.delete(context, pathCheckedList, fileList,  filePathList,  new PicCallBack() {

            @Override
            public void updateView(Boolean b) {
                super.updateView(b);
                if (getView() != null){
                    getView().updateView(b);
                }
            }

            @Override
            public void onSuccess(Object data) {
                if (getView() != null){
                    getView().hideLoading();
                    getView().showToast((String)data);
                }
            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}
