package ezviz.ezopensdk.activity.dataquery;

import java.util.Date;
import java.util.List;

import ezviz.ezopensdk.been.Temp;
import ezviz.ezopensdk.been.base.BasePresenter;

/**
 * Created by hejunfeng on 2020/8/18 0018
 */
public class MyPresenter extends BasePresenter<MyView> {
    private IDataModelImpl model;

    public MyPresenter(IDataModelImpl model) {
        this.model = model;
    }

    public void QueryData(String date){
        if (!isViewAttached()) {
            return;
        }
        getView().showLoading();
        model.queryData(date, new MyCallBack() {

            @Override
            public void queryData(List<Temp> list) {
                if (getView() != null){
                    getView().hideLoading();
                    getView().queryDataImp(list);
                }
            }

            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailure(String msg) {
                if (getView() != null){
                    getView().hideLoading();
                    getView().showToast(msg);
                }
            }
        });
    }

    public void getMyCount(){
        if (!isViewAttached()) {
            return;
        }
        getView().showLoading();
        model.getCount(new MyCallBack() {
            @Override
            public void getCount(int count) {
                if (getView() != null){
                    getView().hideLoading();
                    getView().getCount(count);
                }
            }

            @Override
            public void onSuccess(Object data) {

            }

            @Override
            public void onFailure(String msg) {
                if (getView() != null){
                    getView().hideLoading();
                    getView().showToast(msg);
                }
            }
        });
    }

    public void getData(int count){
        if (!isViewAttached()) {
            return;
        }
        getView().showLoading();
        model.getData(count, new MyCallBack() {

            @Override
            public void sendProcess(Long process) {
                if (getView() != null){
                    getView().hideLoading();
                    getView().sendProcess(process);
                }
            }

            @Override
            public void sendDataList(List<Temp> list) {
                if (getView() != null){
                    getView().sendDataList(list);
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
