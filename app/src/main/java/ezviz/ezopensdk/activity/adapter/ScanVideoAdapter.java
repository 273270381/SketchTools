package ezviz.ezopensdk.activity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.videogo.openapi.bean.EZCameraInfo;

import java.util.List;

import ezviz.ezopensdk.R;

public class ScanVideoAdapter extends RecyclerView.Adapter<ScanVideoAdapter.ViewHolder> {
    private List<EZCameraInfo> list_ezcameras;
    private ItemClickListener listener;

    public ScanVideoAdapter(List<EZCameraInfo> list_ezcameras) {
        this.list_ezcameras = list_ezcameras;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_video_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(list_ezcameras.get(position).getCameraName());
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
        return list_ezcameras.size();
    }

    public void setOnItemClickListener(ItemClickListener itemClickListener){
        this.listener = itemClickListener;
    }

    public interface ItemClickListener{
        void onItemClick(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv);
        }
    }
}
