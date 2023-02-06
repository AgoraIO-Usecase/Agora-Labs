package io.agora.api.example.utils;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {
    private static final Handler uiHandler=new Handler(Looper.getMainLooper());
    private static ExecutorService executorService= Executors.newSingleThreadExecutor();

    public static void runOnNonUI(Runnable runnable){
        if(runnable==null){
            return;
        }
        executorService.submit(runnable);
    }

    public static void runOnUI(Runnable runnable){
        if(runnable==null){
            return;
        }
        if(Looper.myLooper()!=Looper.getMainLooper()){
            uiHandler.post(runnable);
        }else{
            runnable.run();
        }
    }

    public static void postRunOnUIDelayed(Runnable runnable,long delayMillis){
        if(runnable==null){
            return;
        }
        uiHandler.postDelayed(runnable,delayMillis);
    }

    public static void removeCallbacksAndMessagesOnUI(){
        uiHandler.removeCallbacksAndMessages(null);
    }
}
