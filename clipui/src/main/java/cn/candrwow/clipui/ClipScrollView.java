package cn.candrwow.clipui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

/**
 * Created by Candrwow on 2017/8/9.
 */

public class ClipScrollView extends HorizontalScrollView {

    public ClipScrollView(Context context) {
        this(context, null);
    }

    public ClipScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void fling(int velocityX) {
        //禁止惯性滚动
        super.fling(10000);
    }
}
