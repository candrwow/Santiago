package candrwow.cn.santiago;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.candrwow.clipui.ClipLayout;

import static candrwow.cn.santiago.Matisse2Activity.INTENT_DATA_URL;

public class MainActivity extends AppCompatActivity {
    ClipLayout clipLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url = getIntent().getStringExtra(INTENT_DATA_URL);
        Log.d("MainActivity", url);
        clipLayout = (ClipLayout) findViewById(R.id.v);
        clipLayout.setVideo(url);
//        ValueAnimator valueAnimator = ValueAnimator.ofInt(8, 200);
//        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                clipLayout.setX((int) animation.getAnimatedValue());
//            }
//        });
//        valueAnimator.setDuration(6000);
//        valueAnimator.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        clipLayout.trigStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clipLayout.trigStop();
    }
}
