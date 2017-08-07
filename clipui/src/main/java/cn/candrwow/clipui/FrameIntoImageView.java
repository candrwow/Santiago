package cn.candrwow.clipui;

import android.widget.ImageView;

/**
 * Created by Candrwow on 2017/8/7.
 */

public class FrameIntoImageView {
    private ImageView imageView;
    private byte[] bytes;

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
