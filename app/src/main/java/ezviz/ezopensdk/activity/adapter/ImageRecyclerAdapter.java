package ezviz.ezopensdk.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.utils.MyVideoThumbLoader;
import ezviz.ezopensdk.utils.RoundTransform;
import ezviz.ezopensdk.view.MyImageView;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ImageHolder> {
    private List<String> dataList;
    private Context context;
    private LayoutInflater inflater;
    private Boolean showChecked ;
    private OnRecyclerItemClickListener itemClickListener;
    private OnRecyclerItemLongClickListener itemLongClickListener;
    private MyVideoThumbLoader mVideoThumbLoader;

    public ImageRecyclerAdapter(Context context, List<String> dataList, Boolean showChecked) {
        this.dataList = dataList;
        this.context = context;
        this.showChecked = showChecked;
        inflater = LayoutInflater.from(context);
        // 初始化缩略图载入方法
        mVideoThumbLoader = new MyVideoThumbLoader(context);
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(inflater.inflate(R.layout.item_image,parent,false),itemClickListener,itemLongClickListener);
    }


    public boolean getShowChecked(){
        return showChecked;
    }
    public void setShowChecked(boolean showChecked){
        this.showChecked = showChecked;
    }
    @Override
    public void onBindViewHolder(ImageHolder holder, int position) {
        String FileEnd = dataList.get(position).substring(dataList.get(position).lastIndexOf(".") + 1, dataList.get(position).length()).toLowerCase();
        if (FileEnd.equals("mp4")){
            //Bitmap bitmap = DataUtils.getVideoThumbnail(dataList.get(position),350,200, MediaStore.Video.Thumbnails.MINI_KIND);
            //holder.image.setImageBitmap(bitmap);
            //异步加载
            mVideoThumbLoader.showThumbByAsynctack(dataList.get(position),holder.image,350,200);
            holder.imgPlay.setVisibility(View.VISIBLE);
        }else {
            Picasso.with(context).load(new File(dataList.get(position))).resize(350,200).error(R.mipmap.load_fail).
                    transform(new RoundTransform(10)).into(holder.image);
        }
        if (showChecked){
            holder.image.setPadding(0,6,0,6);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(false);
        }else{
            holder.image.setPadding(0,0,0,0);
            holder.checkBox.setVisibility(View.GONE);
            holder.checkBox.setChecked(false);
        }
    }

    public void setItemClickListener(OnRecyclerItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
    public void setItemLongClickListener(OnRecyclerItemLongClickListener itemLongClickListener){
        this.itemLongClickListener = itemLongClickListener;
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private OnRecyclerItemClickListener itemClickListener;
        private OnRecyclerItemLongClickListener itemLongClickListener;

        @BindView(R.id.image) MyImageView image;
        @BindView(R.id.VideoFlag) ImageView imgPlay;
        @BindView(R.id.cb_item) CheckBox checkBox;

        public ImageHolder(View itemView, OnRecyclerItemClickListener itemClickListener, OnRecyclerItemLongClickListener itemLongClickListener) {
            super(itemView);
            ButterKnife.bind(this,itemView);


            this.itemClickListener = itemClickListener;
            this.itemLongClickListener = itemLongClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }
        @Override
        public void onClick(View view) {
            if(itemClickListener == null) {
                return;
            }
            itemView.setTag(checkBox);
            itemClickListener.click(itemView,getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if (itemLongClickListener == null) {
                return false;
            }
            itemLongClickListener.longClick(itemView,getAdapterPosition());
            return false;
        }
    }
}
