package com.shundaojia.live.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.shundaojia.live.Live;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";

    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textView = findViewById(R.id.text);
        button = findViewById(R.id.button);

        button.setOnClickListener(
                it -> {
                    setResult(RESULT_OK);
                    finish();
                });

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(() -> Log.w(TAG, "do dispose"))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(Live.bindLifecycle(this))
                .subscribe(
                        it -> {
                            Log.w(TAG, String.valueOf(it));
                            textView.setText(String.valueOf(it));
                        },
                        it -> Log.e(TAG, "", it),
                        () -> Log.w(TAG, "complete")
                );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "onDestroy");
    }

}
