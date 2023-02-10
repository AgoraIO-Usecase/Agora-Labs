package io.agora.api.example.common.server;

import io.agora.api.example.common.server.model.BaseResponse;
import io.agora.api.example.common.server.model.User;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ServerHelper {
    public static void requestSendVerCode(String phone,CallBack<String> cb){
        Observable<BaseResponse<String>> observable=ServiceHelper.getInstance().getService().requestSendVerCode(phone);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            new Observer<BaseResponse<String>>() {
                @Override public void onSubscribe(Disposable d) {

                }

                @Override public void onNext(BaseResponse<String> response) {
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

    public static void requestLogin(String phone,String code,CallBack<User> cb){
        Observable<BaseResponse<User>> observable=ServiceHelper.getInstance().getService().requestLogin(phone,code);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            new Observer<BaseResponse<User>>() {
                @Override public void onSubscribe(Disposable d) {

                }

                @Override public void onNext(BaseResponse<User> response) {
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

    public static void requestCancellation(String userNo,CallBack<String> cb){
        Observable<BaseResponse<String>> observable=ServiceHelper.getInstance().getService().requestCancellation(userNo);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            new Observer<BaseResponse<String>>() {
                @Override public void onSubscribe(Disposable d) {

                }

                @Override public void onNext(BaseResponse<String> response) {
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

    public interface CallBack<T>{
        void onSuccess(T data);
        void onFailed(Throwable e);
    }
}
