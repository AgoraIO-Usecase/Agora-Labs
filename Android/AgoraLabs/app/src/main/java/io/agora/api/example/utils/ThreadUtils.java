package io.agora.api.example.utils;

import android.os.Handler;
import android.os.Looper;

public class ThreadUtils {
    private static final Handler uiHandler=new Handler(Looper.getMainLooper());
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
