package cn.candrwow.clipui;

/**
 * Created by Candrwow on 2017/8/10.
 */

public class TimeUtils {
    /**
     * 将秒转换为5:11这种格式
     *
     * @param second
     * @return
     */
    public static String SecondToString(int second) {
        return (second / 60) + ":" + (second % 60);
    }
}
