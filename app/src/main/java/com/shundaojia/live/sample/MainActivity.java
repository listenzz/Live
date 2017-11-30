package com.shundaojia.live.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.shundaojia.live.Live;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    Button button;

    Subject<String> subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text);
        button = findViewById(R.id.button);

        subject = PublishSubject.create();

        button.setOnClickListener( it -> {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivityForResult(intent, 100);
        });

        subject.compose(Live.bindLifecycle(this))
                .subscribe( it -> showFragment() );
    }

    private void showFragment() {
        // Can not perform this action after onSaveInstanceState will not happen again.
        getSupportFragmentManager().beginTransaction().add(R.id.content, new BlankFragment()).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            subject.onNext("show fragment");
        }
    }

}
