package ezviz.ezopensdk.activity.arcmap;

import android.os.Process;
import android.util.Log;

import com.esri.android.map.MapView;
import com.esri.android.map.RasterLayer;
import com.esri.core.raster.FileRasterSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hejunfeng on 2020/8/13 0013
 */
public class ImageTask implements Runnable{
    private CountDownLatch endTaskLatch;
    private String img_path;
    private int i;
    private MapView mMapview;
    private String str;
    private String TAG = "TAG";

    public ImageTask(CountDownLatch endTaskLatch, String img_path, int i, MapView mMapview, String str) {
        this.endTaskLatch = endTaskLatch;
        this.img_path = img_path;
        this.i = i;
        this.mMapview = mMapview;
        this.str = str;
    }

    @Override
    public void run() {
        FileRasterSource rasterSource = null;
        try {
            rasterSource = new FileRasterSource(img_path + str + i + ".TIF");
            rasterSource.project(mMapview.getSpatialReference());
            Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
            RasterLayer rasterLayer = new RasterLayer(rasterSource);
            mMapview.addLayer(rasterLayer);
            Log.d(TAG, "currentId="+Thread.currentThread().getId());
        }catch (FileNotFoundException e) {
            Log.d(TAG, "message = "+e.getMessage());
        } catch (IllegalArgumentException ie){
            Log.d(TAG, "message = "+ie.getMessage());
        } catch (RuntimeException re){
            Log.d(TAG, "message = "+re.getMessage());
            if (re.getMessage().contains("Failed to open raster dataset")){
                deleteFile();
            }
        }
        if (rasterSource!=null){
            rasterSource.dispose();
        }
        endTaskLatch.countDown();
    }

    private void deleteFile() {
        File file = new File(img_path+str+i+".TIF");
        File file2 = new File(img_path+str+i+".tfw");
        File file3 = new File(img_path+str+i+".TIF.ovr");
        File file4 = new File(img_path+str+i+".TIF.aux.xml");
        if (file.exists()){
            file.delete();
        }
        if (file2.exists()){
            file2.delete();
        }
        if (file3.exists()){
            file3.delete();
        }
        if (file4.exists()){
            file4.delete();
        }
    }
}
