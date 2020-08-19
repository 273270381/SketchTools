package ezviz.ezopensdk.activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.videogo.openapi.bean.EZCameraInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ezviz.ezopensdk.R;

public class ScanPicAdapter extends RecyclerView.Adapter<ScanPicAdapter.ViewHolder> {
    private List<EZCameraInfo> list_ezCameras;
    private ItemClickListener listener;

    public ScanPicAdapter(List<EZCameraInfo> list_ezCameras) {
        this.list_ezCameras = list_ezCameras;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_pic_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(list_ezCameras.get(position).getCameraName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onItemClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_ezCameras.size();
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.listener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv) TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
