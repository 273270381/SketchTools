package ezviz.ezopensdk.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.scanpic.PictureActivity;
import ezviz.ezopensdk.view.MyPaddingDecoration;

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.TitleHolder>{
    private Context context;
    private List<String> titile_list;
    private List<List<String>> file_list;
    private Boolean showCheck ;
    private Callback callback;
    private int width;
    //新增itemType
    public static final int ITEM_TYPE = 100;
    private ImageRecyclerAdapter imageRecyclerAdapter;
    public PicAdapter(Context context, List<String> titile_list, List<List<String>> file_list , int width , Boolean showCheck , Callback callback) {
        this.context = context;
        this.titile_list = titile_list;
        this.file_list = file_list;
        this.width = width;
        this.showCheck = showCheck;
        this.callback = callback;
    }
    @Override
    public TitleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_title,parent,false),imageRecyclerAdapter);
    }

    public boolean getCheck(){
        return showCheck;
    }


    public void setCheck(boolean showCheck){
        this.showCheck = showCheck;
    }

    @Override
    public int getItemViewType(int position) {
        return ITEM_TYPE;
    }


    @Override
    public void onBindViewHolder(TitleHolder holder, int position) {
        holder.title.setText(titile_list.get(position));
        if (holder.imageRecyclerAdapter == null){
            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.addItemDecoration(new MyPaddingDecoration(context,3));
            holder.recyclerView.setLayoutManager(new GridLayoutManager(context,width/350));
            holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            holder.imageRecyclerAdapter = new ImageRecyclerAdapter(context,file_list.get(position),showCheck);
            holder.recyclerView.setAdapter(holder.imageRecyclerAdapter);

        }
        if(!showCheck){
            holder.imageRecyclerAdapter.setShowChecked(true);
            holder.recyclerView.addItemDecoration(new MyPaddingDecoration(context,3));
            holder.imageRecyclerAdapter.notifyDataSetChanged();
        }else{
            holder.imageRecyclerAdapter.setShowChecked(false);
            holder.recyclerView.removeItemDecoration(holder.recyclerView.getItemDecorationAt(0));
            holder.imageRecyclerAdapter.notifyDataSetChanged();
        }

        holder.imageRecyclerAdapter.setItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            protected void click(View item, int p) {
                if (holder.imageRecyclerAdapter.getShowChecked()){
                    if (((CheckBox)item.getTag()).isChecked()){
                        ((CheckBox)item.getTag()).setChecked(false);
                        callback.removeStringPath(position,p);
                    }else{
                        ((CheckBox)item.getTag()).setChecked(true);
                        callback.addStringPath(position,p);
                    }
                }else{
                    startActivity(context, (ArrayList<String>)file_list.get(position),p);
                }
            }
        });
       holder.imageRecyclerAdapter.setItemLongClickListener(new OnRecyclerItemLongClickListener() {
           @Override
           protected void longClick(View item, int p) {
               showCheck = holder.imageRecyclerAdapter.getShowChecked();
               callback.callback(showCheck);
           }
       });
    }

    @Override
    public int getItemCount() {
        return titile_list.size();
    }

    public interface Callback{
        public void callback(boolean flag);
        public void addStringPath(int p1, int p2);
        public void removeStringPath(int p1, int p2);
    }
    public static class TitleHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private RecyclerView recyclerView;
        private ImageRecyclerAdapter imageRecyclerAdapter;
        public TitleHolder(View itemView, ImageRecyclerAdapter imageRecyclerAdapter) {
            super(itemView);
            title = itemView.findViewById(R.id.title_tv);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            this.imageRecyclerAdapter = imageRecyclerAdapter;
        }
    }
    public static void startActivity(Context context, ArrayList<String> list, int position){
        Intent intent = new Intent(context, PictureActivity.class);
        intent.putExtra("position",position);
        intent.putExtra("flag",false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putStringArrayListExtra("list", (ArrayList<String>) list);
        context.startActivity(intent);
    }
}
