package ezviz.ezopensdk.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ezviz.ezopensdk.been.db.PicFilePath;
import ezviz.ezopensdk.been.db.VideoFilePath;

public class DataUtils {
    /**
     * Url字符串截取
     * @param path
     * @return
     */
    public static List<HashMap<String, String>> getUrlResouse(String path){
        try {
            List<HashMap<String, String>> hashMapList = new ArrayList<>();
            String[] urls = path.split(";");
            for (int i = 0 ; i < urls.length ; i++){
                String url = urls[i];
                HashMap<String, String> map = new HashMap<>();
                String[] rils = url.split("/");
                String[]ips = rils[2].split("@");
                String ip = ips[1];
                map.put("ip",ip);
                map.put("port","21");
                String[]pas = ips[0].split(":");
                String name = pas[0];
                map.put("name",name);
                String password = pas[1];
                map.put("password",password);
                String pic_name = rils[rils.length-1];
                map.put("pic_name",pic_name);
                String serverPath = url.substring(rils[0].length()+2+rils[2].length()+1,url.length());
                map.put("server_name",serverPath);
                String dir_name = url.substring(7+rils[2].length(),url.length()-pic_name.length()-1);
                map.put("dir_name",dir_name);
                hashMapList.add(map);
                return hashMapList;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<HashMap<String, String>> getUrlResouses(String path){

        try {
            List<HashMap<String, String>> hashMapList = new ArrayList<>();
            String[] urls = path.split(";");
            for (int i = 0 ; i < urls.length ; i++){
                String url = urls[i];
                //url = ftp://ubuntu:rushi12345@183.208.120.226:3301/node/kaifaqu/data/Suchness197/picture/2019/12/25/10_55_48_1.jpg
                HashMap<String, String> map = new HashMap<>();
                String[] rils = url.split("/");
                if (rils[2].contains("@")){
                    String[]ips = rils[2].split("@");
                    String[] ip_port = ips[1].split(":");
                    String ip = ip_port[0];
                    String port = ip_port[1];
                    map.put("ip",ip);
                    map.put("port",port);
                    String[]pas = ips[0].split(":");
                    String name = pas[0];
                    map.put("name",name);
                    String password = pas[1];
                    map.put("password",password);
                    String pic_name = rils[rils.length-1];
                    map.put("pic_name",pic_name);
                    String dir_name = url.substring(7+rils[2].length(),url.length()-pic_name.length()-1);
                    String serverPath = url.substring(rils[0].length()+2+rils[2].length()+1,url.length());
                    map.put("server_name",serverPath);
                    map.put("dir_name",dir_name);
                    hashMapList.add(map);
                    return hashMapList;
                }else {
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getImagePathFromSD(String path){
        List<String> urlList = new ArrayList<>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        //String filePath = Environment.getExternalStorageState().toString()+ File.separator+"Pictures";
        // 得到该路径文件夹下所有的文件
        //File fileAll = new File(filePath.toString()+"/Camera");
        File fileAll = new File(path);
        if (!fileAll.exists()){
            fileAll.mkdir();
        }
        File[] files = fileAll.listFiles();
        if (files!=null){
            orderByDate(files);
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath())) {
                    urlList.add(file.getPath());
                }
            }
        }
        return urlList;
    }
    /**
     * 检查扩展名，得到图片格式的文件
     * @param fName  文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg")|| FileEnd.equals("bmp")||FileEnd.equals("mp4") ) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    /**
     * 根据日期递增排序
     * @param files
     */
    public static void orderByDate(File[] files){
        Arrays.sort(files, new Comparator<File>(){
            @Override
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return -1;
                else if (diff == 0)
                    return 0;
                else
                    return 1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
            }
            public boolean equals(Object obj) {
                return true;
            }
        });
    }
    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @param width 指定输出视频缩略图的宽度
     * @param height 指定输出视频缩略图的高度度
     * @param kind 参照MediaStore.Images(Video).Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind); //調用ThumbnailUtils類的靜態方法createVideoThumbnail獲取視頻的截圖；
        if(bitmap!= null){
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);//調用ThumbnailUtils類的靜態方法extractThumbnail將原圖片（即上方截取的圖片）轉化為指定大小；
        }
        return bitmap;
    }

    //日期格式字符串转换时间戳
    public static String date2TimeStamp(String date , String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String timeStamp2Date(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 获取file时间
     * @return
     */
    public static List<String> getFileTime(List<String> datalist){
        List<String> list = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0 ; i < datalist.size() ; i++){
            long time = new File(datalist.get(i)).lastModified();
            String date = format.format(time);
            list.add(date);
        }
        return list;
    }


    public static List<String>getFileList(List<?> ls){
        List<String> list = new ArrayList<>();
        if (ls.size() != 0){
            for (int i = 0 ; i < ls.size() ; i ++){
                if (PicFilePath.class.isInstance(ls.get(i))){
                    list.add(((PicFilePath)ls.get(i)).getPath());
                }else{
                    list.add(((VideoFilePath)ls.get(i)).getPath());
                }
            }
            return list;
        }
        return null;
    }
}
