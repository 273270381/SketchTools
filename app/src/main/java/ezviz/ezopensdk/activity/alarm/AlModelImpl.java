package ezviz.ezopensdk.activity.alarm;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ezviz.ezopensdk.been.AlarmContant;
import ezviz.ezopensdk.been.AlarmMessage;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.SnCal;
import ezviz.ezopensdk.been.db.AlarmReaded;
import ezviz.ezopensdk.been.db.DBManager;
import ezviz.ezopensdk.utils.OkHttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hejunfeng on 2020/8/11 0011
 */
public class AlModelImpl implements IAlModel {
    @Override
    public void queryData(int page_size, String userId, int type, int page, AlCallBack callBack) {
        String url = AlarmContant.service_url + "api/getEarlyWarning";
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("type", String.valueOf(type));
        map.put("limit", String.valueOf(page_size));
        map.put("page", String.valueOf(page));
        map.put("isPush", "1");
        OkHttpUtil.post(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                AppOperator.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure("网络异常！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                List<AlarmMessage> alarmMessageList = new ArrayList<>();
                try {
                    JSONObject object = new JSONObject(responseBody);
                    String result = object.get("success").toString();
                    if (result.equals("true")) {
                        String data = object.get("data").toString();
                        JSONObject objectdata = new JSONObject(data);
                        String count = objectdata.get("count").toString();
                        if (Integer.parseInt(count)>0){
                            Gson gson = new Gson();
                            List<JsonObject> list_objects = gson.fromJson(objectdata.get("data").toString(), new TypeToken<List<JsonObject>>() {
                            }.getType());
                            for (JsonObject object1 : list_objects) {
                                AlarmMessage alarmMessage = gson.fromJson(object1, AlarmMessage.class);
                                alarmMessageList.add(alarmMessage);
                            }
                            AppOperator.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onSuccess(0);
                                    callBack.queryData(alarmMessageList);
                                }
                            });
                        }else{
                            AppOperator.runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.onSuccess(0);
                                    callBack.queryData(alarmMessageList);
                                }
                            });
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    AppOperator.runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onFailure("error");
                        }
                    });
                }
            }
        }, map);
    }

    @Override
    public void queryLocation(AlarmMessage alarmMessage, AlCallBack callBack) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String la = alarmMessage.getLatitude();
        String ln = alarmMessage.getLongitude();
        String url = AlarmContant.location_url;
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("location",la+","+ln);
        map.put("coordtype","wgs84ll");
        map.put("radius","500");
        map.put("extensions_poi","1");
        map.put("output","json");
        map.put("ak","KNAeq1kjoe2u24PTYfeL4kO0KvGaqNak");
        String sn = SnCal.getSnKry(map);
        OkHttpUtil.get(url, sn, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onFailure("error");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                String address = "";
                try {
                    JSONObject object = new JSONObject(responseBody);
                    String status = object.get("status").toString();
                    if (status.equals("0")){
                        String result = object.get("result").toString();
                        JSONObject objectdata = new JSONObject(result);
                        String formatted_address = objectdata.get("formatted_address").toString();
                        String sematic_description = objectdata.get("sematic_description").toString();
                        if (sematic_description==null || sematic_description.equals("")){
                            address = formatted_address;
                        } else {
                            address = formatted_address + "(" + sematic_description + ")";
                        }
                        if (address.equals("")||address==null){
                            alarmMessage.setAddress("未知");
                        }else{
                            alarmMessage.setAddress(address);
                        }
                        AppOperator.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.refreshLocation();
                            }
                        });
                    }else {
                        alarmMessage.setAddress("未知");
                        AppOperator.runOnMainThread(new Runnable() {
                            @Override
                            public void run() {
                                callBack.refreshLocation();
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    callBack.onFailure("error");
                }
            }
        }, map);
    }

    @Override
    public void queryReadId(AlCallBack callBack) {
        AppOperator.runOnThread(new Runnable() {
            @Override
            public void run() {
                List<AlarmReaded> ls = DBManager.getInstance().get(AlarmReaded.class);
                List<String> read_list = new ArrayList<>();
                for (AlarmReaded ar : ls){
                    read_list.add(ar.getType0());
                }
                callBack.queryReadId(read_list);
            }
        });
    }
}
