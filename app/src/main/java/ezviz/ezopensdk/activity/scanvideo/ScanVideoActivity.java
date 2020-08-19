package ezviz.ezopensdk.activity.scanvideo;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.videogo.openapi.bean.EZCameraInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.adapter.ScanVideoAdapter;
import ezviz.ezopensdk.activity.arcmap.ArcMapActivity;
import ezviz.ezopensdk.activity.scanvideo.brunch.VideoActivity;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;

/**
 * Created by hejunfeng on 2020/7/22 0022
 */
public class ScanVideoActivity extends BaseActivity {

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.back)
    ImageButton back;
    private ScanVideoAdapter adapter;
    private List<EZCameraInfo> list_ezCameras = new ArrayList<>();



    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_pre_video;
    }

    @Override
    public void showToast(String message) {

    }
    public static void show(Context context, String key, List ls){
        Intent intent = new Intent(context, ScanVideoActivity.class);
        intent.putParcelableArrayListExtra(key, (ArrayList<? extends Parcelable>) ls);
        context.startActivity(intent);
    }

    @Override
    protected void initData() {
        LinearLayoutManager layoutManager =new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(layoutManager);
        list_ezCameras = getIntent().getParcelableArrayListExtra("cameras_pic");
        EZCameraInfo info = new EZCameraInfo();
        info.setCameraName("最近");
        list_ezCameras.add(0,info);

        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(),DividerItemDecoration.VERTICAL));
        adapter = new ScanVideoAdapter(list_ezCameras);
        rv.setAdapter(adapter);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter.setOnItemClickListener(new ScanVideoAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent icamera = new Intent(v.getContext(), VideoActivity.class);
                icamera.putExtra("video",list_ezCameras.get(position).getCameraName());
                startActivity(icamera);
            }
        });
    }
}
