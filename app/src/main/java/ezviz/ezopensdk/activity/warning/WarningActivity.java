package ezviz.ezopensdk.activity.warning;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.videogo.openapi.bean.EZCameraInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.OSCApplication;
import ezviz.ezopensdk.activity.adapter.WarningAdapter;
import ezviz.ezopensdk.activity.alarm.AlarmActivity;
import ezviz.ezopensdk.activity.dataquery.DataQueryActivity;
import ezviz.ezopensdk.been.AlarmContant;
import ezviz.ezopensdk.been.AppOperator;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.utils.OSCSharedPreference;

import static ezviz.ezopensdk.been.AlarmContant.gettype;

/**
 * Created by hejunfeng on 2020/8/11 0011
 */
public class WarningActivity extends BaseActivity {

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.back)
    ImageButton back;
    private int user_type;
    private int alarm_type;
    private List<String> list;
    private List<EZCameraInfo> cameraInfoList = new ArrayList<>();
    private List<Integer> type_list = new ArrayList<>();
    private String userid;
    private WarningAdapter adapter;
    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_warning;
    }

    @Override
    public void showToast(String message) {

    }

    @Override
    protected void initData() {
        user_type = OSCApplication.user_type;
        cameraInfoList = getIntent().getParcelableArrayListExtra("cameras_pic");
        userid = OSCSharedPreference.getInstance().getUserId();
        switch (user_type){
            case AlarmContant.USER_TYPE_CHENGGUAN:
                list = AlarmContant.getList_chengguan();
                break;
            case AlarmContant.USER_TYPE_SHIWUJU:
                list = AlarmContant.getList_shiwuju();
                break;
            case AlarmContant.USER_TYPE_HUANBAOJU:
                list = AlarmContant.getList_huanbaoju();
                break;
            case AlarmContant.USER_TYPE_ZHIFAJU:
                list = AlarmContant.getList_zhifaju();
                break;
            case AlarmContant.USER_TYPE_FAZHANJU:
                list = AlarmContant.getList_fazhanju();
                break;
            case AlarmContant.USER_TYPE_SUPER:
                list = AlarmContant.getList_super();
                break;
        }
        for (int i = 0 ; i < list.size() ; i++){
            int type= gettype(list.get(i));
            type_list.add(type);
        }
        LinearLayoutManager layoutManager =new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);
        //添加分割线
        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));
        adapter = new WarningAdapter(getApplicationContext(), list, new WarningAdapter.setOnclick() {
            @Override
            public void onClick(View view, int p, int size_url) {
                if (p == 6){
                    // TODO: 2020/8/11 水质监测站
                    Intent i = new Intent(view.getContext(), DataQueryActivity.class);
                    startActivity(i);
                }else {
                    alarm_type = gettype(list.get(p));
                    Log.d("TAG","alarm_type="+alarm_type);
                    String title = list.get(p);
                    Intent i1 = new Intent(view.getContext(), AlarmActivity.class);
                    i1.putExtra("type",alarm_type);
                    i1.putExtra("title",title);
                    i1.putParcelableArrayListExtra("camerainfo_list", (ArrayList<? extends Parcelable>) cameraInfoList);
                    startActivity(i1);
                }
            }
        });
        rv.setAdapter(adapter);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public static void show(Context context, String key, List ls){
        Intent intent = new Intent(context, WarningActivity.class);
        intent.putParcelableArrayListExtra(key, (ArrayList<? extends Parcelable>) ls);
        context.startActivity(intent);
    }
}
