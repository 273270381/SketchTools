package ezviz.ezopensdk.view;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ezviz.ezopensdk.R;

public class MyImageButton extends LinearLayout {
    private TextView textView = null;
    private ImageView imageView = null;
    public MyImageButton(Context context, int imageResId, CharSequence textTesId, int width , int height) {
        super(context);
        imageView = new ImageView(context);
        textView = new TextView(context);
        setImageResource(imageResId);
        imageView.setPadding(0,0,0,0);
        textView.setTextColor(getResources().getColor(R.color.trans_bg_color));
        setImageSize(width,height);
        setText(textTesId);
        setTextSize(12);
        setTextPosition();
        setClickable(true);
        setFocusable(false);
        setBackgroundResource(R.color.transparent);
        setOrientation(LinearLayout.VERTICAL);
        addView(imageView);
        addView(textView);
    }
public void setText(int text){
    textView.setText(text);
}
public void setText(CharSequence charSequence){
        textView.setText(charSequence);
}
public void setImageResource(int resId){
    imageView.setImageResource(resId);
}
public void setImageSize(int width , int height){
        imageView.setMaxWidth(width);
        imageView.setMaxHeight(height);
}
public void setTextPosition(){
    textView.setGravity(Gravity.CENTER);
}
public void setTextSize(float a){
    textView.setTextSize(a);
}
}
