package ezviz.ezopensdk.activity.baidumap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyOrientationListener implements SensorEventListener {
    private SensorManager msensorManager;
    private Context context;
    private Sensor msensor;
    private float lastX;
    private OnOrientationListener mOnOrientationListener;
    public void setmOnOrientationListener(OnOrientationListener mOnOrientationListener) {
        this.mOnOrientationListener = mOnOrientationListener;
    }


    public MyOrientationListener(Context context) {
        this.context = context;
    }
    public void star(){
        msensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (msensorManager!=null){
            //获得方向传感器
            msensor = msensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        if (msensor!=null){
            msensorManager.registerListener(this,msensor, SensorManager.SENSOR_DELAY_UI);
        }
    }
    public void stop(){
        //停止定位
        msensorManager.unregisterListener(this);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION){
            float x =event.values[SensorManager.DATA_X];
            if (Math.abs(x - lastX)>1.0){
                if (mOnOrientationListener != null){
                    mOnOrientationListener.onOrientationChanged(x);
                }
            }
            lastX = x;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public interface OnOrientationListener{
        void onOrientationChanged(float x);
    }
}
