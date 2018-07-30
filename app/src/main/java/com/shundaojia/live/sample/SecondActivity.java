package com.shundaojia.live.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.shundaojia.live.Live;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

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
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long it) {
                        Log.w(TAG, String.valueOf(it));
                        textView.setText(String.valueOf(it));
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.w(TAG, "complete");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w(TAG, "onDestroy");
    }

}
