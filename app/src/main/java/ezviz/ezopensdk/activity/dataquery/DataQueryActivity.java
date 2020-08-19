package ezviz.ezopensdk.activity.dataquery;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.videogo.util.LogUtil;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.adapter.LeftAdapter;
import ezviz.ezopensdk.activity.adapter.RightAdapter;
import ezviz.ezopensdk.been.Temp;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.utils.ToChineseNumUtill;
import ezviz.ezopensdk.utils.ToastNotRepeat;
import ezviz.ezopensdk.view.MyListView;
import ezviz.ezopensdk.view.MySpinner;
import ezviz.ezopensdk.view.SyncHorizontalScrollView;

/**
 * Created by hejunfeng on 2020/8/18 0018
 */
public class DataQueryActivity extends BaseActivity implements View.OnClickListener, MyView {
    @BindView(R.id.tv_table_title_left)
    TextView tv_table_title_left;
    @BindView(R.id.right_title_container)
    LinearLayout right_title_container;
    @BindView(R.id.left_container_listview)
    MyListView leftlistView;
    @BindView(R.id.right_container_listview)
    MyListView rightlistView;
    @BindView(R.id.title_horsv)
    SyncHorizontalScrollView titleHorScv;
    @BindView(R.id.content_horsv)
    SyncHorizontalScrollView contentHorScv;
    @BindView(R.id.spinner)
    MySpinner spinner;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.donut_progress)
    DonutProgress donutProgress;
    @BindView(R.id.query)
    ImageButton query;
    @BindView(R.id.back)
    ImageButton back;

    private Date queryDate = null;
    private MyPresenter presenter;
    private List<Temp> tempList = new ArrayList<>();
    private List<Integer> integerList = new ArrayList<>();
    private List<Temp> totalList = new ArrayList<>();
    private int pagesize = 200;
    private int count = 0;
    private String TAG = "DataQueryActivity";
    private String[] mItems;
    private LeftAdapter leftListAdapter;
    private RightAdapter rightlistAdapter;

    @Override
    public BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void initPresenter() {
        presenter = new MyPresenter(new IDataModelImpl());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_query_data;
    }

    @Override
    public void showToast(String message) {
        ToastNotRepeat.show(getApplicationContext(),message);
    }

    @Override
    protected void initData()  {
        tv_table_title_left.setText("序号");
        getLayoutInflater().inflate(R.layout.table_right_title, right_title_container);
        // 设置两个水平控件的联动
        titleHorScv.setScrollView(contentHorScv);
        contentHorScv.setScrollView(titleHorScv);
        long systime = System.currentTimeMillis();
        try {
            queryDate = longToDate(systime,"yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date.setText(dateToString(queryDate,"yyyy-MM-dd"));
    }

    @Override
    protected void initWidget() {
        presenter.getMyCount();
    }

    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    @OnClick({R.id.date,R.id.back,R.id.query})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.date:
                goToCalendar();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.query:
                presenter.QueryData(date.getText().toString());
                break;
        }
    }

    private void goToCalendar() {
        if (getMinDate() != null && new Date().before(getMinDate())) {
            ToastNotRepeat.show(getApplicationContext(),"请先将日期设置到2012/01/01之后");
            return;
        }
        showDatePicker();
    }

    private Date getMinDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse("2012-01-01");
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showDatePicker() {
        Calendar nowCalendar = Calendar.getInstance();
        nowCalendar.setTime(queryDate);
        DatePickerDialog dpd = new DatePickerDialog(this, null, nowCalendar.get(Calendar.YEAR),
                nowCalendar.get(Calendar.MONTH), nowCalendar.get(Calendar.DAY_OF_MONTH));

        dpd.setCancelable(true);
        dpd.setTitle(R.string.select_date);
        dpd.setCanceledOnTouchOutside(true);
        dpd.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.certain),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dg, int which) {
                        DatePicker dp = null;
                        Field[] fields = dg.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            field.setAccessible(true);
                            if (field.getName().equals("mDatePicker")) {
                                try {
                                    dp = (DatePicker) field.get(dg);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        dp.clearFocus();
                        Calendar selectCalendar = Calendar.getInstance();
                        selectCalendar.set(Calendar.YEAR, dp.getYear());
                        selectCalendar.set(Calendar.MONTH, dp.getMonth());
                        selectCalendar.set(Calendar.DAY_OF_MONTH, dp.getDayOfMonth());
                        queryDate = (Date) selectCalendar.getTime();
                        date.setText(dateToString(queryDate,"yyyy-MM-dd"));
                    }
                });
        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtil.debugLog("Picker", "Cancel!");
                        if (!isFinishing()) {
                            dialog.dismiss();
                        }

                    }
                });

        dpd.show();
    }

    private void getInt(int count){
        integerList.clear();
        for (int i = 1 ; i <= count ; i ++){
            integerList.add(i);
        }
    }

    @Override
    public void queryDataImp(List<Temp> list) {
        tempList.clear();
        tempList.addAll(list);
        getInt(tempList.size());
        leftListAdapter.notifyDataSetChanged();
        rightlistAdapter.notifyDataSetChanged();
    }

    @Override
    public void getCount(int count) {
        this.count = count;
        getInt(pagesize);
        donutProgress.setVisibility(View.VISIBLE);
        presenter.getData(count);
    }

    @Override
    public void sendProcess(Long progress) {
        donutProgress.setProgress(progress);
        if (progress >= 100){
            donutProgress.setVisibility(View.GONE);
        }
        Log.d(TAG,"progress="+progress);
    }

    @Override
    public void sendDataList(List<Temp> list) {
        totalList = list;
        List<String> arrayList = new ArrayList<>();
        arrayList = getSpinerData();
        mItems = arrayList.toArray(new String[arrayList.size()]);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.spinner_item, mItems);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        //绑定 Adapter到控件
        spinner .setAdapter(adapter);
        tempList.addAll(totalList.subList(0,pagesize));
        leftListAdapter = new LeftAdapter(integerList,getApplicationContext());
        rightlistAdapter = new RightAdapter(tempList,getApplicationContext());
        leftlistView.setAdapter(leftListAdapter);
        rightlistView.setAdapter(rightlistAdapter);
        leftlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setSelected(i);
            }
        });
        rightlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                setSelected(i);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tempList.clear();
                tempList.addAll(totalList.subList(position*pagesize,(position+1)*pagesize));
                getInt(pagesize);
                rightlistAdapter.notifyDataSetChanged();
                leftListAdapter.notifyDataSetChanged();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG,"noSelected");
            }
        });

    }


    private void setSelected(int i){
        rightlistAdapter.update(i,rightlistView);
        leftListAdapter.update(i,leftlistView);
    }

    private List<String> getSpinerData(){
        int a = count/pagesize;
        List<String> list = new ArrayList<>();
        for (int i = 1 ;i <= a ; i++){
            list.add("第"+ ToChineseNumUtill.numberToChinese(i) +"页");
        }
        return list;
    }
}
