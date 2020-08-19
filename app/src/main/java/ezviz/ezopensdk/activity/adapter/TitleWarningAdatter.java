package ezviz.ezopensdk.activity.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.videogo.openapi.bean.EZCameraInfo;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.alarm.AsyncImageLoader;
import ezviz.ezopensdk.been.AlarmMessage;
import ezviz.ezopensdk.utils.DataUtils;
import ezviz.ezopensdk.utils.RoundTransform;

/**
 * Created by hejunfeng on 2020/8/11 0011
 */
public class TitleWarningAdatter extends RecyclerView.Adapter<TitleWarningAdatter.MyViewHolder>{

    private OnClickListener OnClickListener;
    private boolean isSrolling = false;
    private List<String> read_list;
    private Context context;
    private List<AlarmMessage> alarmMessageList;
    private List<EZCameraInfo> cameraInfoList;
    private String address;
    private AsyncImageLoader asyncImageLoader;


    public TitleWarningAdatter(List<AlarmMessage> alarmMessageList,  List<EZCameraInfo> cameraInfos, Context context) {
        this.alarmMessageList = alarmMessageList;
        this.cameraInfoList = cameraInfos;
        this.context = context;
        this.asyncImageLoader = new AsyncImageLoader();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String path = alarmMessageList.get(position).getImgPath();
        holder.imageView.setTag(path);
        //加载图片
        if (path != null && !path.equals("")) {
            try {
                List<HashMap<String, String>> list = DataUtils.getUrlResouses(path);
                if (list == null) {
                    Picasso.with(context).load(R.mipmap.load_fail).transform(new RoundTransform(20)).resize(600, 300)
                            .error(context.getResources().getDrawable(R.mipmap.load_fail)).into(holder.imageView);
                }else {
                    String avatarTag = (String) holder.imageView.getTag();
                    HashMap<String, String> map = list.get(0);
                    String pic_name = map.get("pic_name");
                    String imagpath = Environment.getExternalStorageDirectory().toString() + "/EZOpenSDK/cash/" + pic_name;
                    File imgFile = new File(imagpath);
                    if (!imgFile.exists()) {
                        // TODO: 2020/8/12 图片ftp下载
                        asyncImageLoader.loadDrawable(map, new AsyncImageLoader.ImageCallback() {
                            @Override
                            public void imageLoaded() {
                                if (null == avatarTag || avatarTag.equals(holder.imageView.getTag())) {
                                    Picasso.with(context).load(imgFile).transform(new RoundTransform(20)).resize(600, 300)
                                            .error(context.getResources().getDrawable(R.mipmap.load_fail)).into(holder.imageView);
                                }
                            }

                            @Override
                            public void imageLoadEmpty() {
                                if (null == avatarTag || avatarTag.equals(holder.imageView.getTag())) {
                                    Picasso.with(context).load(R.mipmap.load_fail).transform(new RoundTransform(20)).resize(600, 300)
                                            .error(context.getResources().getDrawable(R.mipmap.load_fail)).into(holder.imageView);
                                }
                            }
                        });
                    }else{
                        if (null == avatarTag || avatarTag.equals(holder.imageView.getTag())) {
                            Picasso.with(context).load(imgFile).transform(new RoundTransform(20)).resize(600, 300)
                                    .error(context.getResources().getDrawable(R.mipmap.ic_launcher)).into(holder.imageView);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Picasso.with(context).load(R.mipmap.load_fail).transform(new RoundTransform(20)).resize(600, 300)
                    .error(context.getResources().getDrawable(R.mipmap.load_fail)).into(holder.imageView);
        }

        //地址
        if (alarmMessageList.get(position).getLatitude() != null || alarmMessageList.get(position).getLongitude() != null) {
            holder.address.setText(alarmMessageList.get(position).getAddress());
        } else {
            holder.address.setText("未知");
        }
        //设置已读
        String id = alarmMessageList.get(position).getId();
        if (read_list.contains(id)) {
            holder.camera_name.setTextColor(context.getResources().getColor(R.color.topBarText));
        } else {
            holder.camera_name.setTextColor(context.getResources().getColor(R.color.a1_blue_color));
        }
        String camera_name = getCameraInfo(cameraInfoList, alarmMessageList.get(position).getChannelNumber());
        holder.camera_name.setText(camera_name);
        holder.message_text.setText(alarmMessageList.get(position).getMessage());
        holder.time_creat.setText(alarmMessageList.get(position).getCreateTime());
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这里使用getTag方法获取position
                address = holder.address.getText().toString();
                OnClickListener.OnItemClick(view, (Integer) view.getTag(), address);
            }
        });

    }

    public void setScrolling(boolean scrolling){
        this.isSrolling = scrolling;
    }

    public void setRead_list(List<String> list){
        this.read_list = list;
    }


    private String getCameraInfo(List<EZCameraInfo> cameraInfos , String no){
        if (no!=null&&!no.equals("")){
            for (EZCameraInfo cameraInfo : cameraInfos){
                if (cameraInfo.getCameraNo() == Integer.parseInt(no)){
                    return cameraInfo.getCameraName();
                }
            }
        }
        return "Null";
    }


    @Override
    public int getItemCount() {
        return alarmMessageList.size();
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.img)
        ImageView imageView;
        @BindView(R.id.camera_name)
        TextView camera_name;
        @BindView(R.id.message)
        TextView message_text;
        @BindView(R.id.address)
        TextView address;
        @BindView(R.id.time_creat)
        TextView time_creat;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    public void setSetOnItemClickListener(OnClickListener onClickListener){
        this.OnClickListener = onClickListener;
    }



    public  interface OnClickListener{
        void OnItemClick(View view,int position , String address);
    }
}
