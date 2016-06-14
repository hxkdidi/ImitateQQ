package com.example.zuo.qq8.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by taojin on 2016/6/7.14:44
 */
public class ThreadUtils {

    private static Executor executor = Executors.newSingleThreadExecutor();

    public static  void runOnSubThread(Runnable runnable){
        executor.execute(runnable);
    }

}
