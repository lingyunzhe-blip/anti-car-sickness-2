package com.anticarsickness;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private TextView breathText;
    private final String[] tips = {"吸气…", "屏住…", "呼气…", "放松…"};
    private int index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        breathText = findViewById(R.id.breathText);
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                breathText.setText(tips[index]);
                index = (index + 1) % tips.length;
                handler.postDelayed(this, 4000);
            }
        };
        handler.post(runnable);
    }
}
