package candrwow.cn.santiago;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.List;

public class Matisse2Activity extends AppCompatActivity {
    static final int REQUEST_CODE_CHOOSE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matisse2);
        TextView textView = (TextView) findViewById(R.id.tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matisse.from(Matisse2Activity.this)
                        .choose(MimeType.of(MimeType.MP4))
                        .countable(true)
                        .maxSelectable(1)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });

    }

    public static final String INTENT_DATA_URL = "videoUrl";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Uri> mSelected = Matisse.obtainResult(data);
        if (mSelected != null && mSelected.size() != 0) {
            String s = FileUtils.getRealFilePath(this, mSelected.get(0));
            Intent intent = new Intent(Matisse2Activity.this, MainActivity.class);
            intent.putExtra(INTENT_DATA_URL, s);
            String url = Environment.getExternalStorageDirectory() + "/a.mp4";
            Log.d("Matisse2Activity", url);
            Log.d("Matisse2Activity", s);
            startActivity(intent);
        }
    }
}
