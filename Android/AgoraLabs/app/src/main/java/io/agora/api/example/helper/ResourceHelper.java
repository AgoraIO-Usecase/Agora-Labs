package io.agora.api.example.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import io.agora.api.example.utils.SPUtils;

public class ResourceHelper implements CopyResourceTask.CopyResourceCallback {
    private static volatile ResourceHelper instance;

    private final Context context;

    public final static Byte[] locks = new Byte[0];

    public static ResourceHelper getInstance(Context context) {
        if (null == instance) {
            synchronized (locks) {
                if (instance == null) {
                    instance = new ResourceHelper(context);
                }
            }
        }
        return instance;
    }

    private ResourceHelper(Context context) {
        this.context = context;
    }


    public void copyResource() {
        if (!isResourceReady(getVersionCode())) {
            CopyResourceTask mTask = new CopyResourceTask(context.getApplicationContext(),this);
            mTask.execute();
        }
    }

    public int getVersionCode() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean isResourceReady(int versionCode) {
        boolean resourceReady = SPUtils.getInstance(context, "user").getBoolean("resource", false);
        int preVersioncode = SPUtils.getInstance(context, "user").getInt("versionCode", 0);
        return resourceReady && (versionCode == preVersioncode);
    }

    private final String TAG="AgoraLab";
    @Override
    public void onStartTask() {
        Log.d(TAG,"----onStartTask---");
    }

    @Override
    public void onEndTask(boolean result) {
        Log.d(TAG,"---onEndTask---:"+result);
        if (result) {
            SPUtils.getInstance(context, "user").put("resource", true);
            SPUtils.getInstance(context, "user").put("versionCode", getVersionCode());
        }
    }
}
