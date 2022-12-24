package io.agora.api.example.task;

import android.content.Context;
import android.os.AsyncTask;
import io.agora.api.example.utils.FileUtils;
import io.agora.api.example.utils.ResourceUtils;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class CopyResourceTask extends AsyncTask<Void,Void,Boolean> {

    private final Context context;
    public interface CopyResourceCallback {

        void onStartTask();

        void onEndTask(boolean result);
    }

    private final WeakReference<CopyResourceCallback> mCallback;

    public CopyResourceTask(Context context,CopyResourceCallback callback) {
        this.context=context.getApplicationContext();
        mCallback = new WeakReference<>(callback);
    }


    @Override
    protected Boolean doInBackground(Void... voids) {
        File dir =context.getExternalFilesDir("blur");
        if (dir.exists()) {
            deleteFile(dir);
        }
        FileUtils.copyFilesFromAssets(context, "blur", context.getExternalFilesDir("blur").toString());
        FileUtils.copyFilesFromAssets(context,"faceunity",context.getExternalFilesDir("assets")+"/faceunity");
        FileUtils.copyFilesFromAssets(context,"bytedance",context.getExternalFilesDir("assets")+"/bytedance");

        return true;
    }

    public static void deleteFile(File file){
        if (file == null || !file.exists()){
            return;
        }
        File[] files = file.listFiles();
        if(files==null||files.length<1){
            return;
        }
        for (File f: files){
            if (f.isDirectory()){
                deleteFile(f);
            }else {
                f.delete();
            }
        }
        file.delete();
    }


    @Override
    protected void onPreExecute() {
        mCallback.get().onStartTask();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        mCallback.get().onEndTask(result);
    }
}
