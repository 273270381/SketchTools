package ezviz.ezopensdk.activity.login;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Map;

import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.User;
import ezviz.ezopensdk.utils.OkHttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hejunfeng on 2020/7/20 0020
 */
public class LoginModelImpl implements ILoginModel {

    @Override
    public void login(String url,Map m ,LoginCallBack callback) {
        OkHttpUtil.post(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AppOperator.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure("网络异常");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONObject object = new JSONObject(responseBody);
                    String result = object.get("success").toString();
                    String data = object.get("data").toString();
                    Gson gson = new Gson();
                    User user = gson.fromJson(data,User.class);
                    callback.sendResult(user, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onSuccess(0);
            }
        },m);
    }

    @Override
    public void getAccessToken(String url, Map m, LoginCallBack callback) {
        OkHttpUtil.post(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure("onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                try {
                    JSONObject object = new JSONObject(responseBody);
                    String data = object.get("data").toString();
                    JSONObject obj = new JSONObject(data);
                    String accessToken = obj.get("accessToken").toString();
                    callback.sendAccessToken(accessToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },m);
    }
}
