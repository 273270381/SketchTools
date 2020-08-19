package ezviz.ezopensdk.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

public class MySpinner extends android.support.v7.widget.AppCompatSpinner {

    private int lastPosition = 0;

    public MySpinner(Context context) {
        super(context);
    }

    public MySpinner(Context context, int mode) {
        super(context, mode);
    }

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }

    @Override
    public void setSelection(int position, boolean animate) {
        super.setSelection(position, animate);
        if (position == lastPosition){
            getOnItemSelectedListener().onItemSelected(this,null,position,0);
        }
        lastPosition = position;
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (position == lastPosition){
            getOnItemSelectedListener().onItemSelected(this,null,position,0);
        }
        lastPosition = position;
    }
}
