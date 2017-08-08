package cn.candrwow.clipui;

/**
 * Created by Candrwow on 2017/8/4.
 */

public interface ClipPositionListener {
    /**
     * 当滑动视频起始播放按钮结束后触发。将视频终止，并seekTo起始播放位置，封面采用startPos帧画面
     *
     * @param startPos 变动后的起始播放位置
     * @param endPos   变动后的结束播放位置
     * @param url      传递url避免获取不到视频地址或者视频地址不一致等可能遇到的问题，url应当是本地的路径地址
     */
    void onStartPosChange(int startPos, int endPos, String url);

    /**
     * @param startPos
     * @param endPos
     * @param url
     */
    void onEndPosChange(int startPos, int endPos, String url);

    /**
     * 当滑动帧截图ScrollView时进行处理
     *
     * @param startPos
     * @param endPos
     * @param url
     */
    void onScrollPosChange(int startPos, int endPos, String url);

    /**
     * 确定裁剪范围
     *
     * @param startPos
     * @param endPos
     * @param url
     */
    void onSureClip(int startPos, int endPos, String url);

    /**
     * 取消裁剪
     */
    void onCancelClip();

    public void onStart();

    /**
     * 所在ui恢复时调用
     */
    public void onResume();

    public void onPause();

    /**
     * 当所在ui不可见时，立刻销毁videoView
     */
    public void onStop();
}
