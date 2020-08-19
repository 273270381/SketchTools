package ezviz.ezopensdk.activity.scanpic;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.adapter.PhotoPagerAdapter;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;

/**
 * Created by hejunfeng on 2020/7/24 0024
 */
public class PictureActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.share)
    ImageButton shareBtn;
    @BindView(R.id.delate)
    ImageButton delateBtn;
    @BindView(R.id.refresh)
    ImageButton refresh;
    private ArrayList<String> urlList = new ArrayList<>();
    private int position;
    private Boolean flag = false;
    private Uri uri;
    private String path;


    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_picture;
    }

    @Override
    public void showToast(String message) {

    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        urlList = intent.getStringArrayListExtra("list");
        position = intent.getIntExtra("position",0);
        flag = intent.getBooleanExtra("flag",false);
        PhotoPagerAdapter viewPagerAdapter = new PhotoPagerAdapter(getSupportFragmentManager(), urlList);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);
        uri = Uri.parse(urlList.get(position));
        if (!flag){
            delateBtn.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
        }else{
            delateBtn.setVisibility(View.GONE);
            refresh.setVisibility(View.VISIBLE);
        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                uri = Uri.parse(urlList.get(position));
                path = urlList.get(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @OnClick({R.id.share,R.id.refresh,R.id.delate})
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share:
                shareImg("分享","分享","分享",uri);
                break;
            case R.id.refresh:
                reFresh();
                break;
            case R.id.delate:
                delete(path);
                break;
        }
    }

    private void delete(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
            Toast.makeText(PictureActivity.this, "文件已删除!", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent();
            intent1.putExtra("path",path);
            intent1.setAction("com.refresh.pic");
            sendBroadcast(intent1);
            finish();
        }
    }

    private void reFresh() {
        String path = urlList.get(0);
        File file = new File(path);
        if (file.exists()&&file.isFile()){
            file.delete();
            //Toast.makeText(PictureActivity.this,"文件已删除!", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent();
            intent1.setAction("com.delate.pic");
            sendBroadcast(intent1);
            finish();
        } else {
            String[] str = path.split("\\.");
            String s1 = str[0].substring(0, str[0].length() - 5);
            String path2 = s1 + "." + str[1];
            File file2 = new File(path2);
            if (file2.exists()&&file2.isFile()){
                file2.delete();
                //Toast.makeText(PictureActivity.this,"文件已删除!", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent();
                intent1.setAction("com.delate.pic");
                sendBroadcast(intent1);
                finish();
            }
        }
    }

    /**
     * 分享图片和文字内容
     *
     * @param dlgTitle
     *            分享对话框标题
     * @param subject
     *            主题
     * @param content
     *            分享内容（文字）
     * @param uri
     *            图片资源URI
     */
    private void shareImg(String dlgTitle, String subject, String content, Uri uri) {
        if (uri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        if (subject != null && !"".equals(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (content != null && !"".equals(content)) {
            intent.putExtra(Intent.EXTRA_TEXT, content);
        }
        // 设置弹出框标题
        if (dlgTitle != null && !"".equals(dlgTitle)) { // 自定义标题
            startActivity(Intent.createChooser(intent, dlgTitle));
        } else { // 系统默认标题
            startActivity(intent);
        }
    }

}
