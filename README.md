##  Live
A Rx Transformer handling Android Lifecycle in the  same way with LiveData.

if you just want to take the stream until a specific lifecycle event happen, maybe <a href = "https://github.com/trello/RxLifecycle">RxLifecycle</a> is that you really want.

## Usage

```java

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {

        mObservable
                .compose(Live.bindLifecycle(this))
                .subscribe();

    }
}

```

## Installation

```groovy
allprojects {
    repositories {
        maven { url 'https://maven.google.com' }
        jcenter()
    }
}
```

```groovy
compile 'com.shundaojia:live:1.0.1'

//  using Support Library 26.1+
compile 'com.android.support:appcompat-v7:26.1.0'
compile 'com.android.support:support-v4:26.1.0'
compile 'com.android.support:design:26.1.0'

compile 'com.android.support.constraint:constraint-layout:1.0.2'

// RxJava
compile "io.reactivex.rxjava2:rxandroid:$rootProject.rxandroidVersion"
compile "io.reactivex.rxjava2:rxjava:$rootProject.rxjavaVersion"
```