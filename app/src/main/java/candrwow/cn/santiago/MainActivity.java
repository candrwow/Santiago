package candrwow.cn.santiago;

import android.animation.ValueAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.Toast;

import cn.candrwow.clipui.ClipLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ClipLayout clipLayout = (ClipLayout) findViewById(R.id.v);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(8, 200);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                clipLayout.setX((int) animation.getAnimatedValue());
            }
        });
        valueAnimator.setDuration(6000);
        valueAnimator.start();

    }
}
