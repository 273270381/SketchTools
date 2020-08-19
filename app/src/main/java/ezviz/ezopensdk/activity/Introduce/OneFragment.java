package ezviz.ezopensdk.activity.Introduce;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.been.base.BaseFragment;
import ezviz.ezopensdk.utils.Util;
import ezviz.ezopensdk.view.RatioLayout;


/**
 * 介绍页1
 * Created by huanghaibin on 2017/11/24.
 */

public class OneFragment extends BaseFragment {

    @BindView(R.id.ll_logo)
    LinearLayout mLinearLogo;
    @BindView(R.id.ratioLayout)
    RatioLayout mRatioLayout;

    static OneFragment newInstance() {
        return new OneFragment();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_one;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
            mLinearLogo.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLinearLogo.getLayoutParams();
                params.setMargins(0, (Util.getScreenHeight(mContext) - mRatioLayout.getRatioHeight()) / 4, 0, 0);
                mLinearLogo.setLayoutParams(params);
            }
        });
    }
}
