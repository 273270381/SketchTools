package ezviz.ezopensdk.activity.scanpic;

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
import butterknife.OnClick;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.adapter.ScanPicAdapter;
import ezviz.ezopensdk.activity.scanpic.brunch.PicActivity;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;

/**
 * Created by hejunfeng on 2020/7/22 0022
 */
public class ScanPicActivity extends BaseActivity implements View.OnClickListener{

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.back)
    ImageButton back;
    private List<EZCameraInfo> list_ezCameras = new ArrayList<>();
    private ScanPicAdapter adapter;
    private String TAG = "ScanPicActivity";

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void initPresenter() {

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
        adapter = new ScanPicAdapter(list_ezCameras);
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new ScanPicAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent icamera = new Intent(v.getContext(), PicActivity.class);
                icamera.putExtra("pic",list_ezCameras.get(position).getCameraName());
                startActivity(icamera);
            }
        });

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_pre_pic;
    }

    @Override
    public void showToast(String message) {

    }

    public static void show(Context context, String key, List ls){
        Intent intent = new Intent(context, ScanPicActivity.class);
        intent.putParcelableArrayListExtra(key, (ArrayList<? extends Parcelable>) ls);
        context.startActivity(intent);
    }


    @OnClick({R.id.back})
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            finish();
        }
    }
}
