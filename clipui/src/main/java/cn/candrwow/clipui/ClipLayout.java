package cn.candrwow.clipui;

import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

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
    ClipPositionListener clipPositionListener;

    public ClipPositionListener getClipPositionListener() {
        return clipPositionListener;
    }

    public void setClipPositionListener(ClipPositionListener clipPositionListener) {
        this.clipPositionListener = clipPositionListener;
    }

    public ClipLayout(Context context) {
        this(context, null);
    }


    public ClipLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);


    }

    public ClipLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {
        inflate(context, R.layout.layout_clip_scroll, this);
        llStartPos = findViewById(R.id.ll_start);
        llEndPos = findViewById(R.id.ll_end);
        svClip = findViewById(R.id.hsv_clip);
        llLeft = findViewById(R.id.ll_left);
        llCenter = findViewById(R.id.ll_center);
        llRight = findViewById(R.id.ll_right);
        llClip = findViewById(R.id.ll_clip);
    }

    float DownX = 0;
    //0是llStartPos,1是llEndPos,2是其他不用移动
    int nowTouchView = 2;
    int llLeftWidth = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int[] locStart = new int[2];
        int[] locEnd = new int[2];
        llStartPos.getLocationOnScreen(locStart);
        llEndPos.getLocationOnScreen(locEnd);
        RectF RectFStart = new RectF(locStart[0], locStart[1], locStart[0] + llStartPos.getMeasuredWidth(), locStart[1] + llStartPos.getMeasuredHeight());
        RectF RectFEnd = new RectF(locEnd[0], locEnd[1], locEnd[0] + llEndPos.getMeasuredWidth(), locEnd[1] + llEndPos.getMeasuredHeight());
        float X = ev.getRawX();
        float Y = ev.getRawY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (RectFStart.contains(X, Y)) {
                nowTouchView = 0;
                DownX = X;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLeft.getLayoutParams();
                llLeftWidth = params.width;
                return true;
            } else if (RectFEnd.contains(X, Y)) {
                nowTouchView = 1;
                DownX = X;
                return true;
            } else {
                nowTouchView = 2;
                DownX = 0;
                return false;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (nowTouchView == 0) {
                moveStartPos(ev.getRawX());
                return true;
            } else if (nowTouchView == 1) {
                moveEndPos(ev.getRawX());
                return true;
            } else {
                //设置重置防止意外
                nowTouchView = 2;
                DownX = 0;
                return false;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (clipPositionListener == null)
                return false;
            if (nowTouchView == 2)
                return false;
//            if (nowTouchView==0)
//                clipPositionListener.onStartPosChange();
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void moveStartPos(float moveX) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLeft.getLayoutParams();
        params.width = llLeftWidth + (int) (moveX - DownX);
        llLeft.setLayoutParams(params);
    }

    public void moveEndPos(float moveX) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llRight.getLayoutParams();
        params.width -= (moveX - DownX);
        llLeft.setLayoutParams(params);
    }

    public void setX(int i) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLeft.getLayoutParams();
        params.width = i;
        llLeft.setLayoutParams(params);
//        llClip.invalidate();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        super.onLayout(b, i, i1, i2, i3);
    }


}
