package cn.candrwow.clipui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
    LinearLayout llLeft, llCenter, llRight, llClip, llSvClip;
    ClipPositionListener clipPositionListener;
    //能够选取的最大视频时长，例如限定小视频10~20秒，用户可以选取一个40秒的视频来剪辑，40是最大选取时长
    int MaxShowTime = 0;
    //能够接受的最大视频时长，上述的20秒
    int MaxAcceptTime = 0;
    //能够接受的最小视频时长，上述的10秒
    int MinAcceptTime = 0;

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

    /**
     * 设置视频帧列表
     *
     * @param frameList
     */
    public void setFrameList(List<Bitmap> frameList) {

    }

    public void setVideo(String url) {
        final MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String str = Environment.getExternalStorageDirectory() + "/a.mp4";
        mmr.setDataSource(str);
        final List<ImageView> listImageView = new ArrayList<>();
        for (int i = 0; i <= 30; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams((ViewUtils.getScreenPxWidth(getContext()) - ViewUtils.dp2px(8 * 2, getContext())) / 10, ViewGroup.LayoutParams.MATCH_PARENT));
            llSvClip.addView(imageView);
            listImageView.add(imageView);
        }
        for (int i = 0; i < Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000 && i <= 30; i++) {
            rx.Observable.just(i)
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<Integer, FrameIntoImageView>() {
                        @Override
                        public FrameIntoImageView call(Integer integer) {
                            Bitmap bitmap = mmr.getFrameAtTime(integer * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            byte[] bytes = bos.toByteArray();
                            ImageView imageView = listImageView.get(integer);
                            FrameIntoImageView f = new FrameIntoImageView();
                            f.setBytes(bytes);
                            f.setImageView(imageView);
                            return f;
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<FrameIntoImageView>() {
                        @Override
                        public void call(FrameIntoImageView frameIntoImageView) {
                            Glide.with(getContext()).load(frameIntoImageView.getBytes()).into(frameIntoImageView.getImageView());
                        }
                    });
        }
    }

    public void initView(Context context) {
        this.setOrientation(VERTICAL);
        inflate(context, R.layout.layout_clip_scroll, this);
        inflate(context, R.layout.layout_clip_bar, this);
        llStartPos = findViewById(R.id.ll_start);
        llEndPos = findViewById(R.id.ll_end);
        svClip = findViewById(R.id.hsv_clip);
        llLeft = findViewById(R.id.ll_left);
        llCenter = findViewById(R.id.ll_center);
        llRight = findViewById(R.id.ll_right);
        llClip = findViewById(R.id.ll_clip);
        llSvClip = findViewById(R.id.ll_sv_clip);
        //设置遮罩与滑动按钮，滑动按钮拦截分发事件不再向scrollview传递，遮罩向下传递
        llStartPos.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        llEndPos.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        llLeft.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        llCenter.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        llRight.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        setVideo("");
    }

    //滑块事件按下位置
    float DownX = 0;
    //0是llStartPos,1是llEndPos,2是scrollview
    int nowTouchView = 2;
    //分别记录左右遮罩的初始宽度
    int llLeftWidth = 0;
    int llRightWidth = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float X = ev.getRawX();
        float Y = ev.getRawY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int[] locStart = new int[2];
            int[] locEnd = new int[2];
            llStartPos.getLocationOnScreen(locStart);
            llEndPos.getLocationOnScreen(locEnd);
            RectF RectFStart = new RectF(locStart[0], locStart[1], locStart[0] + llStartPos.getMeasuredWidth(), locStart[1] + llStartPos.getMeasuredHeight());
            RectF RectFEnd = new RectF(locEnd[0], locEnd[1], locEnd[0] + llEndPos.getMeasuredWidth(), locEnd[1] + llEndPos.getMeasuredHeight());
            if (RectFStart.contains(X, Y)) {
                nowTouchView = 0;
                DownX = X;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLeft.getLayoutParams();
                llLeftWidth = params.width;
            } else if (RectFEnd.contains(X, Y)) {
                nowTouchView = 1;
                DownX = X;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llRight.getLayoutParams();
                llRightWidth = params.width;
            } else {
                //如果位置在控件的左右margin上不做任何处理，如果事件在遮罩上交给super.dispatchTouchEvent(ev)处理
                nowTouchView = 2;
                DownX = 0;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (nowTouchView == 0) {
                moveStartPos(ev.getRawX());
            } else if (nowTouchView == 1) {
                moveEndPos(ev.getRawX());
            } else {
                //设置重置防止意外
                nowTouchView = 2;
                DownX = 0;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (nowTouchView == 0) {
                // TODO: 2017/8/7 调用事件监听
                if (clipPositionListener != null)
                    clipPositionListener.onStartPosChange(1, 1, "");
            } else if (nowTouchView == 1) {
                // TODO: 2017/8/7 调用结束按键事件监听
            }
            nowTouchView = 2;
            DownX = 0;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 控制滑块跟随手指移动，由于滑块和三个遮罩在LinearLayout中，控制任意一控件宽度，就可以调整其他控件位置
     *
     * @param moveX
     */
    public void moveStartPos(float moveX) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLeft.getLayoutParams();
        params.width = llLeftWidth + (int) (moveX - DownX);
        llLeft.setLayoutParams(params);
    }

    public void moveEndPos(float moveX) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llRight.getLayoutParams();
        params.width = llRightWidth - (int) (moveX - DownX);
        llRight.setLayoutParams(params);
    }

}
