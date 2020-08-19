package ezviz.ezopensdk.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public  class OkHttpUtil {
    public static void post(String address, okhttp3.Callback callback, Map<String, String> map)
    {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).
        connectionPool(new ConnectionPool(5,3, TimeUnit.SECONDS)).build();
        FormBody.Builder builder = new FormBody.Builder();
        if (map!=null)
        {
            for (Map.Entry<String, String> entry:map.entrySet())
            {
                builder.add(entry.getKey(),entry.getValue());
            }
        }
        FormBody body = builder.build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
    public static void get(String address, String sn, okhttp3.Callback callback, LinkedHashMap<String, String> map) throws UnsupportedEncodingException {
        String paramsStr = ToQueryString(map);
        String requestUrl = String.format("%s?%s", address,  paramsStr);
        String totalUrl = requestUrl+"&sn="+sn;
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).readTimeout(3, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(5,1, TimeUnit.SECONDS)).build();
        Request request = new Request.Builder()
                .url(totalUrl)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }
    public static String ToQueryString(Map<?, ?> data)throws UnsupportedEncodingException {
        StringBuffer queryString = new StringBuffer();
        for (Map.Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            String ss[] = pair.getValue().toString().split(",");
            if(ss.length>1){
                for(String s:ss){
                    queryString.append(URLEncoder.encode(s,"UTF-8") + ",");
                }
                queryString.deleteCharAt(queryString.length()-1);queryString.append("&");
            }
            else{
                queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8") + "&");
            }
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }
}