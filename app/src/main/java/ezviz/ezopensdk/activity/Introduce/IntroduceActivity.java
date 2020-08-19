package ezviz.ezopensdk.activity.Introduce;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import butterknife.BindView;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.been.base.BaseActivity;
import ezviz.ezopensdk.been.base.BasePresenter;
import ezviz.ezopensdk.utils.OSCSharedPreference;

/**
 * 介绍页
 * Created by huanghaibin on 2017/11/24.
 */

public class IntroduceActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    public static void show(Context context) {
        context.startActivity(new Intent(context, IntroduceActivity.class));
    }

    @Override
    public BasePresenter getPresenter() {
        return null;
    }

    @Override
    public void initPresenter() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_introduce;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        OSCSharedPreference.getInstance().putFirstInstall();
        setSwipeBackEnable(false);
        mViewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void showToast(String message) {

    }

    private class FragmentAdapter extends FragmentStatePagerAdapter {
        private FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return position == 0 ? OneFragment.newInstance() : TwoFragment.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
