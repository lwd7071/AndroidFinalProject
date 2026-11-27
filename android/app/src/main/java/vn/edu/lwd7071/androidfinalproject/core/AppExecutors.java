package vn.edu.lwd7071.androidfinalproject.core;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class AppExecutors {
    private final Executor diskIO = Executors.newSingleThreadExecutor();
    private final Executor networkIO = Executors.newFixedThreadPool(3);
    private final Executor mainThread = new Handler(Looper.getMainLooper())::post;

    public Executor diskIO() { return diskIO; }
    public Executor networkIO() { return networkIO; }
    public Executor mainThread() { return mainThread; }
}
