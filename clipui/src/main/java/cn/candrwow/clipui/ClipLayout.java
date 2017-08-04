package cn.candrwow.clipui;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by Candrwow on 2017/8/4.
 */

public class ClipLayout extends ViewGroup {
    /**
     * 起始滑块，终止滑块，视频滑动ScrollView
     */
    LinearLayout llStartPos, llEndPos;
    HorizontalScrollView svClip;
    //起始滑块左侧，滑块中央，终止滑块右侧
    LinearLayout llLeft, llCenter, llRight, llClip;

    public ClipLayout(Context context) {
        this(context, null);
    }


    public ClipLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.layout_clip_scroll, this);
        llStartPos = findViewById(R.id.ll_start);
        llEndPos = findViewById(R.id.ll_end);
        svClip = findViewById(R.id.hsv_clip);
        llLeft = findViewById(R.id.ll_left);
        llCenter = findViewById(R.id.ll_center);
        llRight = findViewById(R.id.ll_right);
        llClip = findViewById(R.id.ll_clip);
    }

    public void setX() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLeft.getLayoutParams();
        params.width = 100;
        llLeft.setLayoutParams(params);
        llClip.invalidate();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        this.getChildAt(0).layout(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
    }


}
