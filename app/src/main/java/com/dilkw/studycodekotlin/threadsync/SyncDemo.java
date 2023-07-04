package com.dilkw.studycodekotlin.threadsync;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public class SyncDemo {

    // volatile
    int a = 0;
    volatile int b = 0;

    /**
     * 当两个不同的线程在同一个时间内访问同一个变量时，不能确保对资源访问的同步性
     * 出错的情况：   1.当线程1对a进行加一时，线程2同时对a进行加一时，两个线程拿到的a的值都是3，加一后得到的值就是4，
     *                  线程是将外部共同的变量值拷贝一份到自己独立的内存区域，更新后再将变量更新回去共同的内存区域
     *                  比如：a的原始值为1，当线程1进行了加一操作，未来得及将a的值更新（线程1内部独立的内存区a的值应该为2，但未将新值更新回去共同区域中的变量值去），
     *                       切换到线程2，而线程2拿回来的值是线程1加一操作后未重新赋值的原始值即为1，线程2进行加一操作后得到的值仍然是2，当两个线程都计算完成后最终得到a的值为2
     *              2.但是实际情况是a应该为5，因为两个线程都分别对a进行了加一操作，及总共进行了两次加一操作，所以结果应该为5
     */
    public void threadVolatile() {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    a = a + 1;
                    // b = b + 1 属于原子性操作，因为b变量使用了volatile修饰
                    // 而 b++ 不属于原子性操作，实际上等于  temp = b + 1; b = temp; 当temp = b + 1时很有可能会切换到其他线程
                    b = b + 1;
                    System.out.println("thread1 a = " + a + "   b = " + b);
                }
            }
        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    a = a + 1;
                    b = b + 1;
                    System.out.println("thread2 a = " + a + "   b = " + b);
                }
            }
        };
        thread1.start();
        thread2.start();
    }

    // synchronized 同步锁
    // 本质上是创建了一个监视器monitor

    //Atomic类可以保证同步性和原子性
    AtomicInteger countAtomic = new AtomicInteger(0);
    private void addCountAtomic() {
        countAtomic.incrementAndGet();
    }

    // 通过synchronized修饰符修饰方法，给方法上锁写法
    private int countSynchronized = 0;
    private synchronized void addCountSynchronized() {
        countSynchronized++;
    }
    //上面的方法等同于下面这种写法，因为通过synchronized修饰方法，默认创建的监视器monitor是this，即当前类本身的实例对象
    //    private void addCountSynchronized() {
    //        synchronized (this) {
    //            countSynchronized++;
    //        }
    //    }

    /**
     *  同时我们可以创建自定义监视器monitor，监视器其实是作为一个上锁标记，可以是任务一个类的实例对象
     *  创建不同的监视器，实现对锁的管理
     */

    private final Object monitor1 = new Object();
    private final Object monitor2 = new Object();
    private void monitor1Method() {
        synchronized (monitor1) {
            countSynchronized++;
        }
    }
    private void monitor2Method() {
        synchronized (monitor2) {
            countSynchronized++;
        }
    }

    // 同时开启两个线程，调用addCount()方法
    public void threadSync() {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    addCountAtomic();
                    addCountSynchronized();
                }
                System.out.println("thread1 count = " + countAtomic.get() +
                        "\n countSynchronized = " + countSynchronized);
            }
        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 100000; i++) {
                    addCountAtomic();
                    addCountSynchronized();
                }
                System.out.println("thread2 countAtomic = " + countAtomic.get() +
                                    "\n countSynchronized = " + countSynchronized);
            }
        };
        thread1.start();
        thread2.start();
    }

}

