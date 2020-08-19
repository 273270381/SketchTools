package ezviz.ezopensdk.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ezviz.ezopensdk.R;

public class WarningAdapter extends RecyclerView.Adapter<WarningAdapter.WarningHolder> {
    private Context context;
    private List<String> list;
    private setOnclick onclick;
    private List<String> type_size;
    private List<Map<String, Integer>> type_size_url;
    public WarningAdapter(Context context, List<String> list , setOnclick onclick) {
        this.context = context;
        this.list = list;
        this.onclick = onclick;
    }

    @Override
    public WarningHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WarningHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.warning_item,parent,false));
    }

    @Override
    public void onBindViewHolder(WarningHolder holder, int position) {
        String type = list.get(position);
        int size_url = 0;
        holder.tv.setTextColor(context.getResources().getColor(R.color.topBarText));
        holder.tv.setText(list.get(position));
        int finalSize_url = size_url;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick.onClick(v,position, finalSize_url);
            }
        });
    }

    public void setType_size_url(List<Map<String, Integer>> mapList){
        this.type_size_url = mapList;
    }

    public void setType_size(List<String> size){
        this.type_size = size;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface setOnclick{
        void onClick(View view, int p, int size_url);
    }

    public static class WarningHolder extends RecyclerView.ViewHolder{
        private TextView tv;
        public WarningHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
        }
    }
}
