package io.agora.api.example.common.server;

import android.os.Build;
import android.util.Log;
import io.agora.api.example.App;
import io.agora.api.example.common.model.report.EventData;
import io.agora.api.example.common.server.model.BaseResponse;
import io.agora.api.example.common.server.model.ReportResponseData;
import io.agora.api.example.common.server.model.User;
import io.agora.api.example.utils.GsonUtils;
import io.agora.api.example.utils.SystemUtil;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ServerHelper {
    public static void report(String event,CallBack<ReportResponseData> cb){
        EventData data=new EventData();
        long ts=System.currentTimeMillis();
        data.setTs(System.currentTimeMillis());
        data.setSrc("agora_labs");
        EventData.PTS pts=new EventData.PTS();
        EventData.PTS.LS ls=new EventData.PTS.LS();
        ls.setName("entryScene");
        ls.setPlatform("Android");
        ls.setVersion(SystemUtil.getVersionName(App.getInstance()));
        ls.setProject(event);
        ls.setModel(Build.MODEL);
        pts.setLs(ls);
        EventData.PTS.VS vs=new EventData.PTS.VS();
        vs.setCount(1);
        pts.setVs(vs);
        List<EventData.PTS> list=new ArrayList<>();
        list.add(pts);
        data.setPts(list);
        try {
            data.setSign(getMd5("src=agora_labs&ts="+ts));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            if(cb!=null) {
                cb.onFailed(e);
            }
            return;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            if(cb!=null) {
                cb.onFailed(e);
            }
            return;
        }
        Log.d("AgoraLab","------data:"+GsonUtils.getGson().toJson(data));
        RequestBody body=RequestBody.create(MediaType.parse("application/json"), GsonUtils.getGson().toJson(data));
        Observable<BaseResponse<ReportResponseData>> observable=ServiceHelper.getInstance().getService().report(body);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            new Observer<BaseResponse<ReportResponseData>>() {
                @Override public void onSubscribe(Disposable d) {

                }

                @Override public void onNext(BaseResponse<ReportResponseData> response) {
                    if(cb!=null){
                        cb.onSuccess(response.getData());
                    }
                }

                @Override public void onError(Throwable e) {
                    if(cb!=null){
                        cb.onFailed(e);
                    }
                }

                @Override public void onComplete() {

                }
            });
    }

    public static String getMd5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(text.getBytes(StandardCharsets.UTF_8));

        StringBuilder builder = new StringBuilder();

        for (byte aByte : bytes) {
            builder.append(Integer.toHexString((0x000000FF & aByte) | 0xFFFFFF00).substring(6));
        }
        return builder.toString();
    }




    public interface CallBack<T>{
        void onSuccess(T data);
        void onFailed(Throwable e);
    }
}
