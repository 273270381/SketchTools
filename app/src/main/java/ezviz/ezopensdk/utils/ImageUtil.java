package ezviz.ezopensdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//make circle image
public class ImageUtil {
    public static Bitmap createCircleImage(Bitmap source, int min) {
        // init a paint
        final Paint paint = new Paint();
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        /**
         * create a canvas same as big as up
         */
        Canvas canvas = new Canvas(target);
        /**
         * first paint circle
         */
        canvas.drawCircle(min/2,min/2,min/2,paint);
        /**
         * use SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * paint photo
         */
        canvas.drawBitmap(source,0,0,paint);
        return target;
    }
    public static void setImageSize(Context context , ImageView imageView){
        //计算图片左右间距之和
        int padding = 15;
        int spacePx = (int) (UiUtil.dp2px(context, padding) * 2);
        //计算图片宽度
        int imageWidth = UiUtil.getScreenWidth(context) - spacePx;
        //计算宽高比，注意数字后面要加上f表示浮点型数字
        float scale = 16f / 9f;
        //根据图片宽度和比例计算图片高度
        int imageHeight = (int) (imageWidth / scale);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( imageWidth,imageHeight);
        //设置左右边距
        params.leftMargin = (int) UiUtil.dp2px(context, padding);
        params.rightMargin = (int) UiUtil.dp2px(context, padding);
        imageView.setLayoutParams(params);
    }
}
