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
import ezviz.ezopensdk.been.Temp;

public class RightAdapter extends BaseAdapter {
    private List<Temp> tempList;
    private Context context;
    public static int  selectItem=-1;
    private String TAG = "DataQueryActivity";

    public RightAdapter(List<Temp> tempList, Context context) {
        this.tempList = tempList;
        this.context = context;
    }

    public void update(int index , ListView listview){
        //得到第一个可见item项的位置
        int visiblePosition = listview.getFirstVisiblePosition();
        //得到指定位置的视图，对listview的缓存机制不清楚的可以去了解下
        View view = listview.getChildAt(index - visiblePosition);
        RightHolder holder = (RightHolder) view.getTag();
        holder.li =  view.findViewById(R.id.li);
        holder.li.setBackgroundColor(context.getResources().getColor(R.color.auto_blue_text));
    }

    public  void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @Override
    public int getCount() {
        return tempList.size();
    }

    @Override
    public Object getItem(int i) {
        return tempList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final RightHolder holder;
        Temp temp = tempList.get(i);
        if (convertView==null){
            convertView= View.inflate(context, R.layout.table_right_item,null);
            holder=new RightHolder();
            holder.temp = convertView.findViewById(R.id.tv_table_content_right_item0);
            holder.Ph = convertView.findViewById(R.id.tv_table_content_right_item1);
            holder.Oxygen = convertView.findViewById(R.id.tv_table_content_right_item2);
            holder.Nitrogen = convertView.findViewById(R.id.tv_table_content_right_item3);
            holder.Permanganate = convertView.findViewById(R.id.tv_table_content_right_item4);
            holder.Phosphorus = convertView.findViewById(R.id.tv_table_content_right_item5);
            holder.Potential = convertView.findViewById(R.id.tv_table_content_right_item6);
            holder.Time = convertView.findViewById(R.id.tv_table_content_right_item7);
            holder.li = convertView.findViewById(R.id.li);
            convertView.setTag(holder);
        }else {
            holder= (RightHolder) convertView.getTag();
        }
        holder.temp.setText(temp.getTemp());
        holder.Ph.setText(temp.getPh());
        holder.Oxygen.setText(temp.getOxygen());
        holder.Nitrogen.setText(temp.getNitrogen());
        holder.Permanganate.setText(temp.getPermanganate());
        holder.Phosphorus.setText(temp.getPhosphorus());
        holder.Potential.setText(temp.getPotential());
        holder.Time.setText(temp.getTime().substring(0,temp.getTime().length()-7));

        int a1 = Double.compare(Double.parseDouble(temp.getPh()),6.00);
        int b1 = Double.compare(Double.parseDouble(temp.getPh()),9.00);
        if (a1<0||b1>0){
            holder.Ph.setTextColor(Color.RED);
        }else {
            holder.Ph.setTextColor(context.getResources().getColor(R.color.topBarText));
        }
        int a2 = Double.compare(Double.parseDouble(temp.getOxygen()),2.00);
        if (a2 < 0){
            holder.Oxygen.setTextColor(Color.RED);
        }else {
            holder.Oxygen.setTextColor(context.getResources().getColor(R.color.topBarText));
        }
        int a3 = Double.compare(Double.parseDouble(temp.getNitrogen()),2.00);
        if (a3 > 0 ){
            holder.Nitrogen.setTextColor(Color.RED);
        }else{
            holder.Nitrogen.setTextColor(context.getResources().getColor(R.color.topBarText));
        }
        int a4 = Double.compare(Double.parseDouble(temp.getPermanganate()),15.00);
        if (a4 > 0){
            holder.Permanganate.setTextColor(Color.RED);
        }else{
            holder.Permanganate.setTextColor(context.getResources().getColor(R.color.topBarText));
        }
        int a5 = Double.compare(Double.parseDouble(temp.getPhosphorus()),0.40);
        if (a5 > 0 ){
            holder.Phosphorus.setTextColor(Color.RED);
        }else{
            holder.Phosphorus.setTextColor(context.getResources().getColor(R.color.topBarText));
        }
        int a6 = Double.compare(Double.parseDouble(temp.getPotential()),50.00);
        if (a6 < 0 ){
            holder.Potential.setTextColor(Color.RED);
        }else {
            holder.Potential.setTextColor(context.getResources().getColor(R.color.topBarText));
        }

        if (i==selectItem){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.auto_blue_text));
        }else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }


    public static class RightHolder {
        private TextView temp;
        private TextView Ph;
        private TextView Oxygen;
        private TextView Nitrogen;
        private TextView Permanganate;
        private TextView Phosphorus;
        private TextView Potential;
        private TextView Time;
        private LinearLayout li;
    }
}
