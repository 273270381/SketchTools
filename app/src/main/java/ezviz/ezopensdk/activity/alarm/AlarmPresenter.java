package ezviz.ezopensdk.activity.alarm;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import ezviz.ezopensdk.been.AlarmMessage;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.been.base.BaseView;

/**
 * Created by hejunfeng on 2020/8/11 0011
 */
public class AlarmPresenter extends BasePresenter<AlView> {
    private AlModelImpl model;

    public AlarmPresenter(AlModelImpl model) {
        this.model = model;
    }

    public void queryData(int page_size, String userId, int type, int page){
        if (!isViewAttached()) {
            return;
        }
        if (page == 1 && getView() != null){
            getView().showLoading();
        }
        model.queryData(page_size, userId, type, page, new AlCallBack() {

            @Override
            public void queryData(List<AlarmMessage> alarmMessageList) {
                if (getView() != null){
                    getView().queryAlarmData(alarmMessageList);
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
                if (getView() != null){
                    getView().hideLoading();
                    getView().showToast(msg);
                }
            }
        });
    }

    public void queryLocation(AlarmMessage alarmMessage) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (!isViewAttached()) {
            return;
        }
        model.queryLocation(alarmMessage, new AlCallBack() {


            @Override
            public void refreshLocation() {
                if (getView() != null){
                    getView().refreshLocation();
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

    public void queryReadId(){
        if (!isViewAttached()) {
            return;
        }
        model.queryReadId(new AlCallBack() {

            @Override
            public void queryReadId(List<String> read_list) {
                if (getView() != null){
                    getView().queryId(read_list);
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
