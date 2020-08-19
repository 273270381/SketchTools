package ezviz.ezopensdk.activity.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ezviz.ezopensdk.R;

public class LeftAdapter extends BaseAdapter {
    private List<Integer> sizeList;
    private Context context;
    public static int  selectItem=-1;

    public LeftAdapter(List<Integer> sizeList, Context context) {
        this.sizeList = sizeList;
        this.context = context;
    }

    public void update(int index , ListView listview){
        //得到第一个可见item项的位置
        int visiblePosition = listview.getFirstVisiblePosition();
        //得到指定位置的视图，对listview的缓存机制不清楚的可以去了解下
        View view = listview.getChildAt(index - visiblePosition);
        LeftHolder holder = (LeftHolder) view.getTag();
        holder.li1 =  view.findViewById(R.id.li1);
        holder.li1.setBackgroundColor(context.getResources().getColor(R.color.auto_blue_text));
    }
    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @Override
    public int getCount() {
        return sizeList.size();
    }

    @Override
    public Object getItem(int i) {
        return sizeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final LeftHolder holder;
        if (convertView==null){
            convertView= View.inflate(context, R.layout.table_left_item,null);
            holder=new LeftHolder();
            holder.tv= (TextView) convertView.findViewById(R.id.tv_table_content_item_left);
            holder.li1 = convertView.findViewById(R.id.li1);
            convertView.setTag(holder);
        }else {
            holder= (LeftHolder) convertView.getTag();
        }
        holder.tv.setText(String.valueOf(sizeList.get(i)));
        if (i==selectItem){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.auto_blue_text));
        }else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }


    public static class LeftHolder {
        private TextView tv;
        private LinearLayout li1;
    }
}
