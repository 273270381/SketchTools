package ezviz.ezopensdk.activity.Introduce;

import android.view.View;
import butterknife.OnClick;
import ezviz.ezopensdk.R;
import ezviz.ezopensdk.activity.login.LoginActivity;
import ezviz.ezopensdk.been.base.BaseFragment;

/**
 * 介绍页2
 * Created by huanghaibin on 2017/11/24.
 */

public class TwoFragment extends BaseFragment {

    static TwoFragment newInstance() {
        return new TwoFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_two;
    }

    @SuppressWarnings("unused")
    @OnClick({R.id.btn_introduce})
    public void onClick(View view) {
        LoginActivity.show(mContext);
        getActivity().finish();
    }
}
