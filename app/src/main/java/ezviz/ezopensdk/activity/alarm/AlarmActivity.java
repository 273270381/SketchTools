package ezviz.ezopensdk.activity.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.videogo.openapi.bean.EZCameraInfo;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.adapter.TitleWarningAdatter;
import ezviz.ezopensdk.activity.alarmdet.AlarmDetail;
import ezviz.ezopensdk.activity.arcmap.ArcMapActivity;
import ezviz.ezopensdk.been.AlarmMessage;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.been.db.AlarmReaded;
import ezviz.ezopensdk.been.db.DBManager;
import ezviz.ezopensdk.utils.OSCSharedPreference;
import ezviz.ezopensdk.utils.ToastNotRepeat;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by hejunfeng on 2020/7/22 0022
 */
public class AlarmActivity extends BaseActivity implements AlView{
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.spinner_1)
    Spinner spinner_time;
    @BindView(R.id.spinner_2)
    Spinner spinner_location;
    @BindView(R.id.query)
    ImageButton query;
    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.title)
    TextView title_text;
    @BindView(R.id.recyclerView)
    RecyclerView rv;

    private String userid;
    private int alarm_type;
    private int page_size = 30;
    private int list_size = 0;
    private int page = 1;
    private Boolean refreshType = true;
    private List<AlarmMessage> alarmMessageList = new ArrayList<>();
    private List<EZCameraInfo> cameraInfoList = new ArrayList<>();
    private List<String> read_list = new ArrayList<>();
    private AlarmPresenter alarmPresenter;
    private TitleWarningAdatter adatper;

    @Override
    public BasePresenter getPresenter() {
        return alarmPresenter;
    }

    @Override
    public void initPresenter() {
        alarmPresenter = new AlarmPresenter(new AlModelImpl());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_alarm;
    }

    @Override
    public void showToast(String message) {
        ToastNotRepeat.show(getApplicationContext(), message);
    }

    @Override
    protected void initData() {
        DBManager.getInstance().create(AlarmReaded.class);
        userid = OSCSharedPreference.getInstance().getUserId();
        alarm_type = getIntent().getIntExtra("type", 0);
        String str = getIntent().getStringExtra("title");
        cameraInfoList = getIntent().getParcelableArrayListExtra("camerainfo_list");
        title_text.setText(str);
        alarmPresenter.queryData(page_size, userid, alarm_type, 1);
        page++;
        DBManager.getInstance().create(AlarmReaded.class);
        alarmPresenter.queryReadId();
        LinearLayoutManager layoutManager = new WarnLinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);
        rv.addItemDecoration(CommItemDecoration.createVertical(getApplicationContext(), getResources().getColor(R.color.blue_bg), 4));
        rv.setItemAnimator(new DefaultItemAnimator());
        adatper = new TitleWarningAdatter(alarmMessageList ,cameraInfoList, getApplicationContext());
        rv.setAdapter(adatper);
        adatper.setSetOnItemClickListener(new TitleWarningAdatter.OnClickListener() {
            @Override
            public void OnItemClick(View view, int position, String address) {
                // TODO: 2020/8/12 页面跳转
                Intent intent = new Intent(getApplicationContext(), AlarmDetail.class);
                intent.putExtra("alarmMessage", alarmMessageList.get(position));
                intent.putExtra("address", address);
                Log.d("TAG","url="+alarmMessageList.get(position).getImgPath());
                startActivity(intent);
                updateRead(alarmMessageList.get(position).getId());
            }
        });
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == SCROLL_STATE_IDLE) {
                    adatper.setScrolling(false);
                    Log.d("TAG", "***********************************************************");
                    adatper.notifyDataSetChanged();
                } else {
                    adatper.setScrolling(true);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void updateRead(String userid) {
        if (!read_list.contains(userid)) {
            AlarmReaded alarmReaded = new AlarmReaded();
            alarmReaded.setType0(userid);
            DBManager.getInstance().insert(alarmReaded);
            read_list.add(userid);
            adatper.setRead_list(read_list);
            adatper.notifyDataSetChanged();
        }
    }

    @Override
    protected void initWidget() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                Log.d("TAG", "刷新");
                //刷新
                refreshType = true;
                page = 1;
                alarmPresenter.queryData(page_size, userid, alarm_type, page);
                refreshLayout.finishRefresh(1000);
                page++;
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshType = false;
                Log.d("refresh","alarm_type="+alarm_type);
                alarmPresenter.queryData(page_size, userid, alarm_type, page);
                if (list_size < page_size) {
                    ToastNotRepeat.show(getApplicationContext(), "暂无更多的数据啦");
                    refreshLayout.finishLoadMoreWithNoMoreData();
                    return;
                } else {
                    refreshLayout.setEnableLoadMore(true);
                    refreshLayout.finishLoadMore(1000);
                    page++;
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public static void show(Context context, String key, List ls){
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.putParcelableArrayListExtra(key, (ArrayList<? extends Parcelable>) ls);
        context.startActivity(intent);
    }

    @Override
    public void queryAlarmData(List<AlarmMessage> list) {
        list_size = list.size();
        if (refreshType) {
            //刷新
            if (alarmMessageList.size() != 0) {
                alarmMessageList.clear();
            }
            alarmMessageList.addAll(list);
            for (AlarmMessage alarmMessage : alarmMessageList){
                try {
                    alarmPresenter.queryLocation(alarmMessage);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        } else {
            //加载更多
            if (list_size>=page_size){
                for (AlarmMessage alarmMessage : list){
                    try {
                        alarmPresenter.queryLocation(alarmMessage);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
                alarmMessageList.addAll(list);
            }
        }
    }

    @Override
    public void refreshLocation() {
        if (refreshType) {
            adatper.notifyDataSetChanged();
        }else{
            if (list_size >= page_size) {
                adatper.notifyItemRangeInserted(alarmMessageList.size() - list_size, alarmMessageList.size());
                adatper.notifyItemRangeChanged(alarmMessageList.size() - list_size, alarmMessageList.size());
            }
        }
    }

    @Override
    public void queryId(List<String> ls) {
        read_list.clear();
        read_list.addAll(ls);
        adatper.setRead_list(read_list);
        adatper.notifyDataSetChanged();
    }
}
