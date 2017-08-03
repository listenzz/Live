package com.shundaojia.live;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Looper;
import android.support.annotation.MainThread;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * A Rx Transformer handle Android Lifecycle as same as LiveData.<p>
 * if you just want to take the stream until a specific lifecycle event happen,
 * maybe <a href = "https://github.com/trello/RxLifecycle">RxLifecycle</a> is that you really want.
 * <p>
 * Created by listen on 2017/8/1.
 */

public final class Live<T> implements ObservableTransformer<T, T>, LifecycleObserver {

    public static <T> ObservableTransformer<T, T> bindLifecycle(LifecycleOwner owner) {
        return new Live<>(owner);
    }

    private final PublishSubject<T> mSubject = PublishSubject.create();

    private final LifecycleOwner mLifecycleOwner;

    private Disposable mDisposable;

    private T mData;

    private boolean mActive;

    private int mVersion = -1;

    private int mLastVersion = -1;

    private Live(LifecycleOwner owner) {
        mLifecycleOwner = owner;
    }

    @MainThread
    @Override
    public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
        assertMainThread();
        if (mLifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.DESTROYED) {

            mLifecycleOwner.getLifecycle().addObserver(this);

            upstream.subscribe(new Observer<T>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    assertMainThread();
                    mDisposable = d;
                }

                @Override
                public void onNext(@NonNull T t) {
                    assertMainThread();
                    ++ mVersion;
                    mData = t;
                    considerNotify();
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    assertMainThread();
                    if (!mDisposable.isDisposed()) {
                        mSubject.onError(e);
                    }
                }

                @Override
                public void onComplete() {
                    assertMainThread();
                    if (!mDisposable.isDisposed()) {
                        mSubject.onComplete();
                    }
                }
            });
            return mSubject;
        } else {
            return Observable.empty();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onStateChange() {
        if (this.mLifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            if (mDisposable != null && !mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
            mLifecycleOwner.getLifecycle().removeObserver(this);
        } else {
            this.activeStateChanged(Live.isActiveState(mLifecycleOwner.getLifecycle().getCurrentState()));
        }
    }

    void activeStateChanged(boolean newActive) {
        if (newActive != mActive) {
            mActive = newActive;
            considerNotify();
        }
    }

    void considerNotify() {
        if (mActive) {
            if (isActiveState(mLifecycleOwner.getLifecycle().getCurrentState())) {
                if (mLastVersion < mVersion) {
                    mLastVersion = mVersion;
                    if (mDisposable != null && !mDisposable.isDisposed()) {
                        mSubject.onNext(mData);
                    }
                }
            }
        }
    }

    private void assertMainThread() {
        if (!isMainThread()) {
            throw new IllegalStateException("You should not use the Live Transformer at a background thread.");
        }
    }

    static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    static boolean isActiveState(Lifecycle.State state) {
        return state.isAtLeast(Lifecycle.State.STARTED);
    }

}
