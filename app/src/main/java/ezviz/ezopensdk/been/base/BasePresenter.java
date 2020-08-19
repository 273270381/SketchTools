package ezviz.ezopensdk.been.base;

/**
 * Created by hejunfeng on 2020/7/20 0020
 */
public class BasePresenter  <V extends BaseView>{
    /**
     * 绑定的view
     */
    private V view;
    /**
     * 绑定view，一般在初始化中调用该方法
     */
    public void attachView(V view){
        this.view = view;
    }
    /**
     * 断开view，一般在onDestroy中调用
     */
    public void detachView(){
        this.view = null;
    }
    /**
     * 是否与View建立连接
     * 每次调用业务请求的时候都要出先调用方法检查是否与View建立连接
     */
    public boolean isViewAttached(){
        return view != null;
    }

    public V getView(){
        return view;
    }
}
