package main;

import java.util.concurrent.*;

public class ThreadMain {
    public static void main(String[] args) {
        // 模拟10个用户请求
        int requestCount = 10;

        // 方式1：不使用线程池（不推荐）
        System.out.println("=== 不使用线程池 ===");
        long start1 = System.currentTimeMillis();
        for (int i = 1; i <= requestCount; i++) {
            final int requestId = i;
            new Thread(() -> {
                handleRequest(requestId);
            }).start();
        }
        // 主线程等待一下观察效果（实际中可以用CountDownLatch）
        sleep(5000);
        long end1 = System.currentTimeMillis();
        System.out.println("不使用线程池总耗时约: " + (end1 - start1) + "ms\n");

        // 方式2：使用线程池（推荐）
        System.out.println("=== 使用固定大小线程池（4个线程） ===");
        // 创建一个固定4个线程的线程池
        ExecutorService pool = Executors.newFixedThreadPool(4);

        long start2 = System.currentTimeMillis();
        for (int i = 1; i <= requestCount; i++) {
            final int requestId = i;
            pool.submit(() -> {
                handleRequest(requestId);
            });
        }

        // 关闭线程池（不再接受新任务，等待已有任务完成）
        pool.shutdown();
        // 等待所有任务完成
        try {
            pool.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end2 = System.currentTimeMillis();
        System.out.println("使用线程池总耗时约: " + (end2 - start2) + "ms");
    }

    // 模拟处理一个请求（耗时2秒）
    private static void handleRequest(int requestId) {
        System.out.println("[" + Thread.currentThread().getName() +
                "] 开始处理请求 " + requestId + "，时间: " + System.currentTimeMillis());
        try {
            Thread.sleep(2000);  // 模拟耗时操作（如查数据库）
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("[" + Thread.currentThread().getName() +
                "] 完成请求 " + requestId);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
