package ezviz.ezopensdk.jpush;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.helper.Logger;
import ezviz.ezopensdk.utils.ExampleUtil;

/**
 * 处理tagalias相关的逻辑
 * */
public class TagAliasOperatorHelper {
    public String TAG = "TagAliasOperatorHelper";
    public static int sequence = 1;
    private String userid;
    private Context context;
    public static final int DELAY_SEND_ACTION = 1;
    private static TagAliasOperatorHelper mInstance;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private TagAliasOperatorHelper(){
    }
    public static TagAliasOperatorHelper getInstance(){
        if(mInstance == null){
            synchronized (TagAliasOperatorHelper.class){
                if(mInstance == null){
                    mInstance = new TagAliasOperatorHelper();
                }
            }
        }
        return mInstance;
    }
    public void init(Context context){
        if(context != null) {
            this.context = context.getApplicationContext();
            editor =  context.getSharedPreferences("alias",context.MODE_PRIVATE).edit();
            sharedPreferences = context.getSharedPreferences("userid",context.MODE_PRIVATE);
        }
    }
    private SparseArray<String> setActionCache = new SparseArray<String>();

    public String get(int sequence){
        return setActionCache.get(sequence);
    }
    public void put(int sequence, String tagAliasBean){
        setActionCache.put(sequence,tagAliasBean);
    }
    private Handler delaySendHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DELAY_SEND_ACTION:
                    sequence++;
                    String alias = msg.getData().getString("alias");
                    setActionCache.put(sequence,alias );
                    if(context!=null) {
                        handleAction(context, sequence, alias);
                    }else{
                        Logger.e(TAG,"#unexcepted - context was null");
                    }
                    break;
            }
        }
    };
    public void handleAction(Context context, int sequence, String alias){
        init(context);
        if (alias == null){
            return;
        }
        put(sequence,alias);
        JPushInterface.setAlias(context,sequence,alias);
    }

    private boolean RetryActionIfNeeded(int errorCode, String alias){
        if(!ExampleUtil.isConnected(context)){
            Log.d(TAG,"确认网络是否断开！");
            return false;
        }
        //返回的错误码为6002 超时,6014 服务器繁忙,都建议延迟重试
        if(errorCode == 6002 || errorCode == 6014){
            Logger.d(TAG,"need retry");
            if(alias!=null){
                Message message = new Message();
                message.what = DELAY_SEND_ACTION;
                Bundle bundle = new Bundle();
                bundle.putString("alias",alias);
                message.setData(bundle);
                delaySendHandler.sendMessageDelayed(message,1000*60);
                String logs ="错误码为6002 超时,6014 服务器繁忙,都建议延迟重试";
                //ExampleUtil.showToast(logs, context);
                return true;
            }
        }
        return false;
    }
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        int sequence = jPushMessage.getSequence();
        Logger.i(TAG,"action - onAliasOperatorResult, sequence:"+sequence+",alias:"+jPushMessage.getAlias());
        init(context);
        //根据sequence从之前操作缓存中获取缓存记录
        String alias = (String) setActionCache.get(sequence);
        if(alias == null){
            ExampleUtil.showToast("获取缓存记录失败", context);
            return ;
        }
        if(jPushMessage.getErrorCode() == 0){
            Logger.i(TAG,"action - modify alias Success,sequence:"+sequence);
            setActionCache.remove(sequence);
            userid = sharedPreferences.getString("id","1");
            editor.putBoolean(userid,true);
            editor.commit();
            String logs = "set alias success";
            Logger.i(TAG,logs);
            ExampleUtil.showToast(logs, context);
        }else{
            String logs = "Failed to set alias, errorCode:" + jPushMessage.getErrorCode();
            Logger.e(TAG, logs);
            if(!RetryActionIfNeeded(jPushMessage.getErrorCode(),alias)) {
                //ExampleUtil.showToast(logs, context);
            }
            editor.putBoolean("isSuccess",false);
            editor.commit();
        }
    }
}
