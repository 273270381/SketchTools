package ezviz.ezopensdk.been.base;

public interface BaseCallback<T> {
    /**
     * 数据请求成功
     * @param data 请求到的数据
     */
    void onSuccess(T data);

    /**
     * 使用网络API接口请求方式时，虽然已经请求成功但是由
     *  于{@code msg}的原因无法正常返回数据。
     * @param msg
     */
    void onFailure(String msg);

}
