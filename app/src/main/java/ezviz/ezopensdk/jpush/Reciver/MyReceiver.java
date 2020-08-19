package ezviz.ezopensdk.jpush.Reciver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;
import cn.jpush.android.api.JPushInterface;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.been.AlarmMessage;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "TAG";

	@Override
	public void onReceive(Context context, Intent intent) {
//		try {
			Bundle bundle = intent.getExtras();
			Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				Log.i(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...i

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				Log.i(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				//processCustomMessage(context, bundle);
				String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
				Gson gson = new Gson();
				AlarmMessage alarmMessage = gson.fromJson(message,AlarmMessage.class);
				Log.d(TAG,"starttime="+alarmMessage.getStartTime());
//				sendNotification(context,context.getString(R.string.app_name) ,alarmMessage.getMessage()
//						,alarmMessage,"receive "+alarmMessage.getLatitude()+","+alarmMessage.getLongitude());
			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				Log.i(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				Log.i(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				Log.i(TAG, "[MyReceiver] 用户点击打开了通知");

				//打开自定义的Activity
//				Intent i = new Intent(context, TestActivity.class);
//				i.putExtras(bundle);
//				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//				context.startActivity(i);

			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				Log.i(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
				//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				Log.i(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else {
				Log.i(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
//		} catch (Exception e){
//			e.printStackTrace();
//		}

	}

	private static void saveData(AlarmMessage alarmMessage, SQLiteDatabase db, String table_name){
		//保存到数据库
		String sql = "insert into "+table_name+"(message,type,latitude,longitude,altitude,address,imgPath,videoPath,createTime,startTime,endTime,channelNumber) " +
				"values(?,?,?,?,?,?,?,?,?,?,?,?)";
		SQLiteStatement stat = db.compileStatement(sql);
		db.beginTransaction();
		stat.bindString(1,alarmMessage.getMessage());
		stat.bindString(2,alarmMessage.getType());
		stat.bindString(3,alarmMessage.getLatitude());
		stat.bindString(4,alarmMessage.getLongitude());
		if (alarmMessage.getAltitude()!=null){
			stat.bindString(5,alarmMessage.getAltitude());
		}
		if (alarmMessage.getAddress()!=null){
			stat.bindString(6,alarmMessage.getAddress());
		}
		if (alarmMessage.getImgPath()!=null){
			stat.bindString(7,alarmMessage.getImgPath());
		}
		if (alarmMessage.getVideoPath()!=null){
			stat.bindString(8,alarmMessage.getVideoPath());
		}
		if (alarmMessage.getCreateTime()!=null){
			stat.bindString(9,alarmMessage.getCreateTime());
		}
		if (alarmMessage.getStartTime()!=null){
			stat.bindString(10,alarmMessage.getStartTime());
		}
		if (alarmMessage.getEndTime()!=null){
			stat.bindString(11,alarmMessage.getEndTime());
		}
		if (alarmMessage.getChannelNumber()!=null){
			stat.bindString(12,alarmMessage.getChannelNumber());
		}
		stat.executeInsert();
		db.setTransactionSuccessful();
		db.endTransaction();
		Log.d(TAG,"insert success");
	}

//	private static void sendNotification(Context context, String contentTitle, String contentText, AlarmMessage alarmMessage, String address){
//		//获取系统通知服务
//		String mChannelId = "channelID";
//		NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
//		Intent resultIntent = new Intent(context, PlaybackActivity2.class);
//		resultIntent.putExtra("alarmMessage",alarmMessage);
//		resultIntent.putExtra("address",address);
//		PendingIntent resultPendingIntent = PendingIntent.getActivity(context,0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//			CharSequence name = context.getString(R.string.alarm_name);
//			NotificationChannel channel = new NotificationChannel(mChannelId, name, NotificationManager.IMPORTANCE_DEFAULT);
//			channel.enableVibration(true);
//			channel.setVibrationPattern(new long[]{0,500});
//			manager.createNotificationChannel(channel);
//
//		}
//		NotificationCompat.Builder bBuilder = new NotificationCompat.Builder(context, mChannelId)
//				.setSmallIcon(R.mipmap.paper)
//				.setContentText(contentText).setContentTitle(contentTitle).setAutoCancel(true)
//				.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.paper))
//				.setAutoCancel(true)
//				.setContentIntent(resultPendingIntent);
//		Notification notification = bBuilder.build();
//		manager.notify(1, notification);
//	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.i(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.get(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to MainActivity
//	private void processCustomMessage(Context context, Bundle bundle) {
//		if (MainActivity.isForeground) {
//			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//			if (!ExampleUtil.isEmpty(extras)) {
//				try {
//					JSONObject extraJson = new JSONObject(extras);
//					if (extraJson.length() > 0) {
//						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//					}
//				} catch (JSONException e) {
//
//				}
//
//			}
//			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
//		}
//	}
}
