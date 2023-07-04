package com.dilkw.studycodekotlin.threadsync;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadMain {

    public static void main(String[] args) {
        //sync();
        //System.out.println("java class");
        SyncDemo syncDemo = new SyncDemo();
        //syncDemo.threadVolatile();
        syncDemo.threadSync();
    }

    /**
     * 通过创建Thread对象重写run方法
     */
    private void thread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                System.out.println("thread Thread started");
            }
        };
        thread.start();
    }

    /**
     * 创建Runnable接口对象，重写run方法
     * 相比于直接创建Thread对象，这种可以方便于复用Runnable对象
     */
    private void threadRunnable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("runnable Thread started");
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * 线程工厂
     */
    ThreadFactory factory = new ThreadFactory() {
        int count = 0;
        @Override
        public Thread newThread(Runnable r) {
            count ++;
            return new Thread(r, "Thread-" + count);
        }
    };

    // 通过线程工厂方式创建
    private void threadWithFactory() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("runnable for thread factory");
            }
        };
        Thread thread = factory.newThread(runnable);
        thread.start();
    }

    /**
     * 线程同步
     */
    private void threadSync() {
        AtomicInteger a = new AtomicInteger(7);
        a.incrementAndGet();
    }

    static int count = 0;

    private static void in(int a) {
        count = a;
    }
    private static void sync() {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0 ; i < 100; i++) {
                    in(i);
                }
                System.out.println("thread1 count = " + count);
            }
        };

        Thread thread2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0 ; i < 100; i = i + 2) {
                    in(i);
                }
                System.out.println("thread2 count = " + count);
            }
        };
        thread1.start();
        thread2.start();
    }

    /**
     * 线程池
     */
    private static void threadWithExecutor() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("thread with executor");
            }
        };

        // 1.常用
        Executor executor = Executors.newCachedThreadPool();
        executor.execute(runnable);
        executor.execute(runnable );
        executor.execute(runnable );

        // 2.短时批量处理，短时间多个线程爆发处理
        ExecutorService executorFixed =
                Executors.newFixedThreadPool(20);
        List<Bitmap> bitmaps = new ArrayList<>();
        for (Bitmap bitmap : bitmaps) {
            executor.execute(bitmapProcessor(bitmap));
        }
        executorFixed.shutdown();

    }

    private static Runnable bitmapProcessor(Bitmap bitmap) {
        return new Runnable() {
            @Override
            public void run() {
                System.out.println("thread with fixedThreadPool");
            }
        };
    }

    /**
     * handler机制
     */

    private void threadHandler() {
        Handler handler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
            }
        };
    }

}
