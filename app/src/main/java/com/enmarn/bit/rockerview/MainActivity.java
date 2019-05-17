package com.enmarn.bit.rockerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    RockerView rockerView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rockerView = findViewById(R.id.rocker);
        textView = findViewById(R.id.debuger);

        rockerView.setOnTouchListener(new RockerView.onTouchListener() {
            @Override
            public void onTouch(RockerView.RockerEvent event) {
                String string = "type " + event.type + " rad " + event.rad + " offset " + event.offset;
                textView.setText(string);
            }
        });
    }
}
