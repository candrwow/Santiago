package cn.candrwow.clipui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by Candrwow on 2017/8/9.
 * 禁止滚动SeekBar，传入起始终止
 */

public class ClipSeekBar extends SeekBar {
    public ClipSeekBar(Context context) {
        super(context);
    }

    public ClipSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
}
