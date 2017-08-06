package cn.candrwow.clipui;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by Candrwow on 2017/8/4.
 */

public class ClipLayout extends LinearLayout {
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
//        View view = LayoutInflater.from(context).inflate(R.layout.layout_clip_scroll, this,true);
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate(context, R.layout.layout_clip_scroll, this);
//        View.inflate(context, R.layout.layout_clip_scroll, this);
//        this.addView(view);
        llStartPos = findViewById(R.id.ll_start);
        llEndPos = findViewById(R.id.ll_end);
        svClip = findViewById(R.id.hsv_clip);
        llLeft = findViewById(R.id.ll_left);
        llCenter = findViewById(R.id.ll_center);
        llRight = findViewById(R.id.ll_right);
        llClip = findViewById(R.id.ll_clip);
    }

    public void setX(int i) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLeft.getLayoutParams();
        params.width = i;
        llLeft.setLayoutParams(params);
        llClip.invalidate();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        Log.d("ClipLayout", "getChildCount():" + getChildCount());
        super.onLayout(b, i, i1, i2, i3);
    }


}
