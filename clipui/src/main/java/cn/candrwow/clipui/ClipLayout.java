package cn.candrwow.clipui;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
import android.view.animation.LinearInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
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
    TextView tvClipTime;
    TextView tvPlayTime;
    VideoView videoView;
    ImageView ivFrame;
    RelativeLayout rlFrame;
    SeekBar seekBar;
    //所有的时长均按秒为单位
    //能够选取的最大视频时长，例如限定小视频10~20秒，用户可以选取一个40秒的视频来剪辑，40是最大选取时长，最大选取视频长度不能超过MaxAcceptTime*2
    int MaxShowTime = 480;
    //能够接受的最大视频时长，上述的20秒
    int MaxAcceptTime = 240;
    //能够接受的最小视频时长，上述的10秒
    int MinAcceptTime = 10;
    //左右滑块最短间距，需要根据VideoLength判断
    int MinWidth = 0;
    //视频裁剪ScrollView的宽度，屏幕宽-左右margin
    int ClipScreenLength = 0;
    //专门记录退出进入后台时的起始位置，当从后台再进入前台时，强制从选取的第一帧播放
    int startPos = 0;
    int endPos = 0;
    ValueAnimator seekAnimator;
    String playUrl = "";

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


    public void initClipPositionListener() {
        this.setClipPositionListener(new ClipPositionListener() {
            @Override
            public void onStartPosChange(final int startPos, final int endPos, String url) {
                rlFrame.setVisibility(VISIBLE);
                //ivFrame.setImageBitmap(mmr.getFrameAtTime(startPos * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                videoView.seekTo(startPos * 1000);
                seekBar.setMax((endPos - startPos) * 1000);
                seekBar.setProgress(0);
                seekAnimator = null;
                seekAnimator = ValueAnimator.ofInt(0, (endPos - startPos) * 1000);
                seekAnimator.setInterpolator(new LinearInterpolator());
                seekAnimator.setDuration((endPos - startPos) * 1000);
            }


            @Override
            public void onEndPosChange(final int startPos, final int endPos, String url) {
                rlFrame.setVisibility(VISIBLE);
                //ivFrame.setImageBitmap(mmr.getFrameAtTime(endPos * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                videoView.seekTo(startPos * 1000);
                seekBar.setMax((endPos - startPos) * 1000);
                seekBar.setProgress(0);
                seekAnimator = null;
                seekAnimator = ValueAnimator.ofInt(0, (endPos - startPos) * 1000);
                seekAnimator.setInterpolator(new LinearInterpolator());
                seekAnimator.setDuration((endPos - startPos) * 1000);
            }

            @Override
            public void onScrollPosChange(final int startPos, final int endPos, String url) {
                rlFrame.setVisibility(VISIBLE);
                //ivFrame.setImageBitmap(mmr.getFrameAtTime(startPos * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
                videoView.seekTo(startPos * 1000);
                seekBar.setMax((endPos - startPos) * 1000);
                seekBar.setProgress(0);
                seekAnimator = null;
                seekAnimator = ValueAnimator.ofInt(0, (endPos - startPos) * 1000);
                seekAnimator.setInterpolator(new LinearInterpolator());
                seekAnimator.setDuration((endPos - startPos) * 1000);

            }

            @Override
            public void onSureClip(int startPos, int endPos, String url) {

            }

            @Override
            public void onCancelClip() {

            }
        });
    }

    //当退出过后台时，标记此属性true。当此值false时，trigStart不执行，认为是第一次打开这个界面
    boolean isStop = false;
    //如果是退出导致的动画结束，不执行onAnimationEnd里的逻辑
    boolean isStopTrigAnimEnd = false;

    /**
     * 进入后台时保持状态并暂停video,触发onStop方法时调用
     */
    public void trigStop() {
        isStop = true;
        if (videoView != null) {
            videoView.pause();
            if (seekAnimator != null) {
                isStopTrigAnimEnd = true;
                seekAnimator.cancel();
                rlFrame.setVisibility(VISIBLE);
                seekBar.setVisibility(GONE);
                tvClipTime.setVisibility(VISIBLE);
                tvPlayTime.setVisibility(GONE);
                tvPlayTime.setText("0:00");
            }
        }
    }

    public void trigStart() {
        if (!isStop)
            return;
        try {
            if (videoView != null && playUrl != null) {
                videoView.setVideoPath(playUrl);
                videoView.seekTo(startPos * 1000);
                seekAnimator = null;
                seekAnimator = ValueAnimator.ofInt(0, (endPos - startPos) * 1000);
                seekAnimator.setInterpolator(new LinearInterpolator());
                seekAnimator.setDuration((endPos - startPos) * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            videoView.stopPlayback();
            seekAnimator = null;
        }
    }

    /**
     * 用于生成时间轴和视频的相关ui
     * 禁止传入的时间值小于minAcceptTime
     *
     * @param url
     */
    // TODO: 2017/8/8 传入时间禁止小于minAcceptTime
    public void initVideo(String url) {
        playUrl = url;
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(url);
        videoView.setVideoPath(url);
        videoView.seekTo(0);
        videoView.start();
        listImageView = new ArrayList<>();
        //extractMetaData默认单位是毫秒
        videoLength = Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000;
        ClipScreenLength = ViewUtils.getScreenPxWidth(getContext()) - ViewUtils.dp2px(18 * 2, getContext());
        //根据MaxAcceptTime，MaxShowTime，MinAcceptTime计算应该持有多少张封面和ScrollView的长度
        if (videoLength <= MaxAcceptTime) {
            frameNum = defaultFrameNum;
            MinWidth = (int) (ClipScreenLength * ((float) MinAcceptTime / (float) videoLength));
        } else if (videoLength <= MaxShowTime) {
            frameNum = (int) (defaultFrameNum * ((float) videoLength / (float) MaxAcceptTime));
            MinWidth = (int) (ClipScreenLength * ((float) MinAcceptTime / (float) MaxAcceptTime));
        }
        for (int i = 0; i < frameNum; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams((ViewUtils.getScreenPxWidth(getContext()) - ViewUtils.dp2px(18 * 2, getContext())) / defaultFrameNum, ViewGroup.LayoutParams.MATCH_PARENT));
            llSvClip.addView(imageView);
            listImageView.add(imageView);
        }
        for (int i = 0; i < frameNum; i++) {
            Observable.just(i)
                    .subscribeOn(Schedulers.io())
                    .map(new Func1<Integer, FrameIntoImageView>() {
                        @Override
                        public FrameIntoImageView call(Integer integer) {
                            //getFrameAtTime单位特殊是微秒
                            Bitmap bitmap = mmr.getFrameAtTime(((int) (videoLength * 1000 * 1000 * ((float) integer / (float) frameNum))), MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
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

    MediaMetadataRetriever mmr;
    List<ImageView> listImageView;
    //选取的视频时长
    int videoLength = 0;
    //截取帧数量,默认ScrollView一屏持有10帧，最大持有两屏宽度共计20帧
    int frameNum = 10;
    int defaultFrameNum = 10;

    public void setVideo(String url) {
        url = Environment.getExternalStorageDirectory() + "/a.mp4";
        initVideo(url);
        initClipPositionListener();
    }

    public void initView(Context context) {
        this.setOrientation(VERTICAL);
        inflate(context, R.layout.layout_preview_video, this);
        inflate(context, R.layout.layout_clip_scroll, this);
        inflate(context, R.layout.layout_clip_bar, this);
        tvClipTime = (TextView) findViewById(R.id.tv_clip_time);
        tvPlayTime = (TextView) findViewById(R.id.tv_play_time);
        llStartPos = (LinearLayout) findViewById(R.id.ll_start);
        llEndPos = (LinearLayout) findViewById(R.id.ll_end);
        svClip = (HorizontalScrollView) findViewById(R.id.hsv_clip);
        llLeft = (LinearLayout) findViewById(R.id.ll_left);
        llCenter = (LinearLayout) findViewById(R.id.ll_center);
        llRight = (LinearLayout) findViewById(R.id.ll_right);
        llClip = (LinearLayout) findViewById(R.id.ll_clip);
        llSvClip = (LinearLayout) findViewById(R.id.ll_sv_clip);
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
        videoView = (VideoView) findViewById(R.id.videoview);
        ivFrame = (ImageView) findViewById(R.id.iv_frame);
        rlFrame = (RelativeLayout) findViewById(R.id.rl_frame);
        rlFrame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoView != null && !videoView.isPlaying() && seekAnimator != null) {
                    seekBar.setVisibility(VISIBLE);
                    videoView.start();
                    final int currentPos = videoView.getCurrentPosition();
                    seekAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            seekBar.setProgress((int) valueAnimator.getAnimatedValue());
                            int playTime = ((int) valueAnimator.getAnimatedValue()) / 1000;
                            //在动画结束最后一帧，会出现时间不正确的问题，强制不能大于选取时长避免这一问题
                            if (playTime > getNowClipLength())
                                playTime = getNowClipLength();
                            tvPlayTime.setText(TimeUtils.SecondToString(playTime));
                            RelativeLayout.LayoutParams playTimeLayoutParams = (RelativeLayout.LayoutParams) tvPlayTime.getLayoutParams();
                            playTimeLayoutParams.setMargins((int) (llStartPos.getX() + llStartPos.getWidth() + (llEndPos.getX() - llStartPos.getX() - llStartPos.getWidth()) * (float) seekBar.getProgress() / (float) seekBar.getMax()) - tvPlayTime.getWidth() / 2, getResources().getDimensionPixelSize(R.dimen.clip_time_margin_top), 0, getResources().getDimensionPixelSize(R.dimen.clip_time_margin_bottom));
                            tvPlayTime.setLayoutParams(playTimeLayoutParams);
                        }
                    });
                    seekAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                            tvClipTime.setVisibility(INVISIBLE);
                            tvPlayTime.setVisibility(VISIBLE);
                            tvPlayTime.setText("0:00");
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (isStopTrigAnimEnd)
                                return;
                            videoView.pause();
                            //等待视频彻底停止，pause是个异步方法会出现音画进度不同步，延迟执行调整进度
                            Observable.timer(500, TimeUnit.MILLISECONDS)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<Long>() {
                                        @Override
                                        public void call(Long aLong) {
                                            videoView.seekTo(currentPos);
                                            rlFrame.setVisibility(VISIBLE);
                                            seekBar.setVisibility(GONE);
                                            tvClipTime.setVisibility(VISIBLE);
                                            tvPlayTime.setVisibility(GONE);
                                            tvPlayTime.setText("0:00");
                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {

                                        }
                                    });
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                }
                seekAnimator.start();
                rlFrame.setVisibility(GONE);
            }
        });
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setThumbOffset(0);
        setVideo("");
    }

    //滑块事件按下位置
    float DownX = 0;
    //0是llStartPos,1是llEndPos,2是scrollview,3是其他控件，4是没有触控
    int nowTouchView = 2;
    //分别记录左右遮罩的初始宽度
    int llLeftWidth = 0;
    int llRightWidth = 0;
    //记录当按下左右滑块时手指点距离滑块最左侧的距离
    int fingerOffset = 0;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float X = ev.getRawX();
        float Y = ev.getRawY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int[] locStart = new int[2];
            int[] locEnd = new int[2];
            int[] locSv = new int[2];
            llStartPos.getLocationOnScreen(locStart);
            llEndPos.getLocationOnScreen(locEnd);
            svClip.getLocationOnScreen(locSv);
            RectF RectFStart = new RectF(locStart[0], locStart[1], locStart[0] + llStartPos.getMeasuredWidth(), locStart[1] + llStartPos.getMeasuredHeight());
            RectF RectFEnd = new RectF(locEnd[0], locEnd[1], locEnd[0] + llEndPos.getMeasuredWidth(), locEnd[1] + llEndPos.getMeasuredHeight());
            RectF RectFSv = new RectF(locSv[0], locSv[1], locSv[0] + svClip.getMeasuredWidth(), locSv[1] + svClip.getMeasuredHeight());
            if (RectFStart.contains(X, Y)) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    tvClipTime.setVisibility(VISIBLE);
                    tvPlayTime.setVisibility(GONE);
                }
                nowTouchView = 0;
                DownX = X;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLeft.getLayoutParams();
                llLeftWidth = params.width;
                //记录手指响应的点距离滑块最左侧距离
                fingerOffset = (int) (X - locStart[0]);
                seekAnimator = null;
                seekBar.setVisibility(GONE);
            } else if (RectFEnd.contains(X, Y)) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    tvClipTime.setVisibility(VISIBLE);
                    tvPlayTime.setVisibility(GONE);
                }
                nowTouchView = 1;
                DownX = X;
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llRight.getLayoutParams();
                llRightWidth = params.width;
                fingerOffset = (int) (X - locEnd[0]);
                seekAnimator = null;
                seekBar.setVisibility(GONE);

            } else if (RectFSv.contains(X, Y)) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    tvClipTime.setVisibility(VISIBLE);
                    tvPlayTime.setVisibility(GONE);
                }
                //如果位置在控件的左右margin上不做任何处理，如果事件在遮罩上交给super.dispatchTouchEvent(ev)处理
                nowTouchView = 2;
                DownX = 0;
                fingerOffset = 0;
                seekAnimator = null;
                seekBar.setVisibility(GONE);
            } else {
                nowTouchView = 3;
                DownX = 0;
                fingerOffset = 0;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (nowTouchView == 0) {
//                moveStartPos(ev.getRawX());
                moveStartPos(ev.getRawX());
            } else if (nowTouchView == 1) {
                moveEndPos(ev.getRawX());
            } else {
                //设置重置防止意外
                DownX = 0;
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (nowTouchView == 0) {
                // TODO: 2017/8/7 调用事件监听
                if (clipPositionListener != null) {
                    //左滑块的位移
                    int llStartOffset = (int) llStartPos.getX();
                    //右滑块距离最左侧的位移,由于屏幕最左侧有Margin（等于滑块宽度），所以要减掉宽度
                    int llEndOffset = (int) llEndPos.getX() - llEndPos.getMeasuredWidth();
                    //当前滚动栏所滚动的视频百分比
                    float ScrollPercent = (float) svClip.getScrollX() / (float) llSvClip.getWidth();
                    //左滑块占总进度的位移百分比
                    float StartPercent = (float) (llStartOffset + svClip.getScrollX()) / (float) llSvClip.getWidth();
                    float EndPercent = (float) (llEndOffset + svClip.getScrollX()) / (float) llSvClip.getWidth();
                    startPos = (int) (videoLength * StartPercent);
                    endPos = (int) (videoLength * EndPercent);
                    clipPositionListener.onStartPosChange((int) (videoLength * StartPercent), (int) (videoLength * EndPercent), "");
                }
            } else if (nowTouchView == 1) {
                // TODO: 2017/8/7 调用结束按键事件监听
                //左滑块的位移
                int llStartOffset = (int) llStartPos.getX();
                //右滑块距离最左侧的位移,由于屏幕最左侧有Margin（等于滑块宽度），所以要减掉宽度
                int llEndOffset = (int) llEndPos.getX() - llEndPos.getMeasuredWidth();
                //当前滚动栏所滚动的视频百分比
                float ScrollPercent = (float) svClip.getScrollX() / (float) llSvClip.getWidth();
                //左滑块占总进度的位移百分比
                float StartPercent = (float) (llStartOffset + svClip.getScrollX()) / (float) llSvClip.getWidth();
                float EndPercent = (float) (llEndOffset + svClip.getScrollX()) / (float) llSvClip.getWidth();
                startPos = (int) (videoLength * StartPercent);
                endPos = (int) (videoLength * EndPercent);
                clipPositionListener.onEndPosChange((int) (videoLength * StartPercent), (int) (videoLength * EndPercent), "");
            } else if (nowTouchView == 2) {
                // TODO: 2017/8/8 滑动暂时不监听，交给ScrollView的ScrollListener处理
                //左滑块的位移
                int llStartOffset = (int) llStartPos.getX();
                //右滑块距离最左侧的位移,由于屏幕最左侧有Margin（等于滑块宽度），所以要减掉宽度
                int llEndOffset = (int) llEndPos.getX() - llEndPos.getMeasuredWidth();
                //当前滚动栏所滚动的视频百分比
                float ScrollPercent = (float) svClip.getScrollX() / (float) llSvClip.getWidth();
                //左滑块占总进度的位移百分比
                float StartPercent = (float) (llStartOffset + svClip.getScrollX()) / (float) llSvClip.getWidth();
                float EndPercent = (float) (llEndOffset + svClip.getScrollX()) / (float) llSvClip.getWidth();
                startPos = (int) (videoLength * StartPercent);
                endPos = (int) (videoLength * EndPercent);
                clipPositionListener.onScrollPosChange((int) (videoLength * StartPercent), (int) (videoLength * EndPercent), "");
            }

            nowTouchView = 4;
            DownX = 0;
            fingerOffset = 0;
        } else {
            return false;
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 控制滑块跟随手指移动，由于滑块和三个遮罩在LinearLayout中，控制任意一控件宽度，就可以调整其他控件位置
     * 事件的触发不能根据DownX去决定，因为可能DownX让左右滑块差距达到最小视频长度间隔，此时事件触发应当根据左右滑块间距和ScrollView的滚动距离判断
     *
     * @param moveX
     */
    public void moveStartPos(float moveX) {
        int[] locStartPos = new int[2];
        int[] locEndPos = new int[2];
        llStartPos.getLocationOnScreen(locStartPos);
        llEndPos.getLocationOnScreen(locEndPos);
        //如果手指触摸点超出最短间距，则强制moveX值为最小间距，这里需要考虑到fingerOffset,所有距离按照两个滑块的最左侧计算，所以假定手指触摸点是左滑块最左侧，如果不在，增加fingerOffset
        if ((moveX - fingerOffset) >= (locEndPos[0] - MinWidth - llStartPos.getMeasuredWidth())) {
            moveX = locEndPos[0] - MinWidth - llStartPos.getMeasuredWidth() + fingerOffset;
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llLeft.getLayoutParams();
        params.width = llLeftWidth + (int) (moveX - DownX);
        llLeft.setLayoutParams(params);
        tvClipTime.setText(TimeUtils.SecondToString(getNowClipLength()));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvClipTime.getLayoutParams();
        layoutParams.width = (int) (llEndPos.getX() - llStartPos.getX() + llEndPos.getWidth());
        layoutParams.setMargins(locStartPos[0], getResources().getDimensionPixelSize(R.dimen.clip_time_margin_top), locEndPos[1], getResources().getDimensionPixelSize(R.dimen.clip_time_margin_bottom));
        tvClipTime.setLayoutParams(layoutParams);
//        }
    }

    public void moveEndPos(float moveX) {
        int[] locStartPos = new int[2];
        int[] locEndPos = new int[2];
        llStartPos.getLocationOnScreen(locStartPos);
        llEndPos.getLocationOnScreen(locEndPos);
        if ((moveX - fingerOffset) <= (locStartPos[0] + MinWidth + llStartPos.getMeasuredWidth())) {
            moveX = locStartPos[0] + MinWidth + llStartPos.getMeasuredWidth() + fingerOffset;
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) llRight.getLayoutParams();
        params.width = llRightWidth - (int) (moveX - DownX);
        llRight.setLayoutParams(params);
        tvClipTime.setText(TimeUtils.SecondToString(getNowClipLength()));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tvClipTime.getLayoutParams();
        layoutParams.width = (int) (llEndPos.getX() - llStartPos.getX() + llEndPos.getWidth());
        layoutParams.setMargins(locStartPos[0], getResources().getDimensionPixelSize(R.dimen.clip_time_margin_top), locEndPos[1], getResources().getDimensionPixelSize(R.dimen.clip_time_margin_bottom));
        tvClipTime.setLayoutParams(layoutParams);
    }

    /**
     * 获取滑块滑动过程中或者结束滑动后，选取部分的时长
     */
    public int getNowClipLength() {
        int llStartOffset = (int) llStartPos.getX();
        //右滑块距离最左侧的位移,由于屏幕最左侧有Margin（等于滑块宽度），所以要减掉宽度
        int llEndOffset = (int) llEndPos.getX() - llEndPos.getMeasuredWidth();
        int nowClipLength = (int) (videoLength * (llEndOffset - llStartOffset + 0.0f) / (float) llSvClip.getWidth());
//        videoLength是秒数不需要除以1000转换
        return nowClipLength;
    }
}
