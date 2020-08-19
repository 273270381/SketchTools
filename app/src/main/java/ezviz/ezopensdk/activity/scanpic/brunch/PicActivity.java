package ezviz.ezopensdk.activity.scanpic.brunch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.adapter.PicAdapter;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.been.db.DBManager;
import ezviz.ezopensdk.been.db.PicFilePath;
import ezviz.ezopensdk.utils.ToastNotRepeat;
import ezviz.ezopensdk.view.MyImageButton;
import static ezviz.ezopensdk.utils.DataUtils.getFileList;
import static ezviz.ezopensdk.utils.DataUtils.getFileTime;

/**
 * Created by hejunfeng on 2020/7/23 0023
 */
public class PicActivity extends BaseActivity implements PicView {

    @BindView(R.id.recyclerView)
    RecyclerView rv;
    @BindView(R.id.text)
    TextView tv;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.linear_1)
    LinearLayout linear_1;
    @BindView(R.id.linear_2)
    LinearLayout linear_2;
    private String camera_name;
    private PicPresenter picPresenter;
    private PicAdapter adapter;
    private MyImageButton myImageButton1 = null;
    private MyImageButton myImageButton2 = null;
    private List<String> pathCheckedList = new ArrayList<>();
    private List<List<String>> fileList = new ArrayList<>();
    private List<PicFilePath> filePathList = new ArrayList<>();
    private String excPath;
    private MyReceiver myReceiver;
    private String TAG = "PicActivity";


    @Override
    public BasePresenter getPresenter() {
        return picPresenter;
    }

    @Override
    public void initPresenter() {
        picPresenter = new PicPresenter(new PicModelLmpl());
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_camera_pic;
    }

    @Override
    public void showToast(String message) {
        ToastNotRepeat.show(getApplicationContext(), message);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        myImageButton1 = new MyImageButton(getApplicationContext(),R.mipmap.send,"发送",60,60);
        myImageButton2 = new MyImageButton(getApplicationContext(),R.mipmap.delate,"删除",60,60);
        linear_1.addView(myImageButton1);
        linear_2.addView(myImageButton2);
        //注册广播接收
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.refresh.pic");
        registerReceiver(myReceiver,filter);
        myImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送文件
                picPresenter.send(pathCheckedList);
            }
        });
        myImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除文件
                Log.d(TAG,"pathCheckedList="+pathCheckedList.toString());
                Log.d(TAG,"fileList="+fileList.toString());
                Log.d(TAG,"filePathList="+filePathList.toString());
                picPresenter.delete(getApplicationContext(),pathCheckedList,fileList,filePathList);
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        DBManager.getInstance().create(PicFilePath.class);
        DBManager.getInstance().alter(PicFilePath.class);
        filePathList = DBManager.getInstance().get(PicFilePath.class);
        camera_name = getIntent().getStringExtra("pic");
        //获取所有文件的路径
        excPath = Environment.getExternalStorageDirectory().toString()+"/EZOpenSDK/CapturePicture/"+camera_name;
        picPresenter.getData(getApplicationContext(),excPath, filePathList, camera_name);
    }


    @Override
    public void processData(List<String> dataList, List<String> titleList, List<List<String>> ls, int width) {
        if (adapter == null){
            fileList.addAll(ls);
            rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            adapter = new PicAdapter(getApplicationContext(), titleList, ls, width, true, new PicAdapter.Callback() {
                @Override
                public void callback(boolean flag) {
                    if (flag){
                        pathCheckedList.clear();
                        adapter.notifyDataSetChanged();
                        linearLayout.setVisibility(View.GONE);
                    }else{
                        pathCheckedList.clear();
                        adapter.notifyDataSetChanged();
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void addStringPath(int p1, int p2) {
                    pathCheckedList.add(fileList.get(p1).get(p2));
                }

                @Override
                public void removeStringPath(int p1, int p2) {
                    pathCheckedList.remove(fileList.get(p1).get(p2));
                }
            });
            rv.setAdapter(adapter);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showTextView(Boolean b) {
        rv.setVisibility(View.GONE);
        tv.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateView(Boolean b) {
        adapter.setCheck(true);
        adapter.notifyDataSetChanged();
        linearLayout.setVisibility(View.GONE);
    }

    @Override
    public void sendFiles(String str, ArrayList<Uri> file) {
        senfiles(str, file);
    }


    /**
     * 文件发送
     * @param dlgTitle
     * @param files
     */
    private void senfiles( String dlgTitle, ArrayList<Uri> files) {
        if (files.size() == 0) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        //Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        intent.setType("*/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // 设置弹出框标题
        // 自定义标题
        if (dlgTitle != null && !"".equals(dlgTitle)) {
            startActivity(Intent.createChooser(intent, dlgTitle));
        } else { // 系统默认标题
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String path = intent.getStringExtra("path");
            if (filePathList.size() != 0){
                List<String> datalist_db = getFileTime(getFileList(filePathList));
                if (datalist_db.contains(path)){
                    DBManager.getInstance().delete(PicFilePath.class,"path=?",new String[]{path});
                }
                filePathList = DBManager.getInstance().get(PicFilePath.class);
            }
            picPresenter.getData(getApplicationContext(),excPath, filePathList, camera_name);
        }
    }

}
