package cn.candrwow.clipui;

import android.content.Context;

/**
 * Created by Candrwow on 2017/8/7.
 */

public class ViewUtils {
    public static int dp2px(final float dpValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(final float pxValue, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenPxWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
