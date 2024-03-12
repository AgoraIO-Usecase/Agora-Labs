package io.agora.api.example.login;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.Constant;
import io.agora.api.example.common.UserManager;
import io.agora.api.example.common.base.component.BaseRequestViewModel;
import io.agora.api.example.common.server.ApiManager;
import io.agora.api.example.common.server.api.ApiException;
import io.agora.api.example.common.server.api.ApiSubscriber;
import io.agora.api.example.common.server.model.BaseRes;
import io.agora.api.example.common.server.model.BaseResponse;
import io.agora.api.example.common.server.model.User;
import io.agora.api.example.utils.AESUtils;
import io.agora.api.example.utils.GsonUtils;
import io.agora.api.example.utils.SchedulersUtil;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.ToastUtils;
import io.reactivex.disposables.Disposable;

public class LoginViewModel extends BaseRequestViewModel {

    /**
     * 登录
     *
     * @param account 账号
     * @param vCode   验证码
     */
    public void requestLogin(String account, String vCode) {
        if (!account.equals(phone)) {
            getISingleCallback().onSingleCallback(Constant.CALLBACK_TYPE_LOGIN_REQUEST_LOGIN_FAIL, null);
            ToastUtils.showToast("验证码错误");
            return;
        }
        ApiManager.getInstance().requestLogin(account, vCode)
            .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<User>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<User> data) {
                        ToastUtils.showToast(App.getInstance().getString(R.string.login_successful));
                        ApiManager.token = (data.getData().token);
                        UserManager.getInstance().saveUserInfo(data.getData());
                        getISingleCallback().onSingleCallback(Constant.CALLBACK_TYPE_LOGIN_REQUEST_LOGIN_SUCCESS, null);
                        reportDevice();
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        //Log.d("AgoraLab","----ApiException:"+t.errCode+" "+t.getLocalizedMessage());
                        ToastUtils.showToast(t.getMessage());
                    }
                }
            );
    }

    private void reportDevice(){
        UserManager userManager=UserManager.getInstance();
        if (userManager.getUser() == null || android.text.TextUtils.isEmpty(userManager.getUser().getUserNo())) {
            return;
        }
        ApiManager.getInstance().requestReportDevice(userManager.getUser().userNo,"agora_labs",
            AESUtils.getS1(App.getInstance()),"agora_labs", SystemUtil.getVersionName(App.getInstance()))
            .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(new ApiSubscriber<BaseResponse<BaseRes>>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    addDispose(d);
                }

                @Override
                public void onSuccess(BaseResponse<BaseRes> data) {
                    //Log.d("AgoraLab","-----reportDevice---:"+ GsonUtils.getGson().toJson(data));
                }

                @Override
                public void onFailure(@Nullable ApiException t) {
                    //Log.d("AgoraLab","----reportDevice ApiException:"+t.errCode+" "+t.getLocalizedMessage());
                    ToastUtils.showToast(t.getMessage());
                }
            });
    }

    private void reportBackground(String url){
        UserManager userManager=UserManager.getInstance();
        if (userManager.getUser() == null || android.text.TextUtils.isEmpty(userManager.getUser().getUserNo())) {
            return;
        }

        ApiManager.getInstance().requestReportBackground(userManager.getUser().userNo,"agora_labs",
                AESUtils.getS1(App.getInstance()),"agora_labs", url)
            .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(new ApiSubscriber<BaseResponse<BaseRes>>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    addDispose(d);
                }

                @Override
                public void onSuccess(BaseResponse<BaseRes> data) {
                    //Log.d("AgoraLab","-----reportBackground---:"+ GsonUtils.getGson().toJson(data));
                }

                @Override
                public void onFailure(@Nullable ApiException t) {
                    //Log.d("AgoraLab","----reportBackground ApiException:"+t.errCode+" "+t.getLocalizedMessage());
                    ToastUtils.showToast(t.getMessage());
                }
            });
    }

    private String phone;

    /**
     * 发送验证码
     *
     * @param phone 手机号
     */
    public void requestSendVCode(String phone) {
        this.phone = phone;
        ApiManager.getInstance().requestSendVerCode(phone)
            .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<String> stringBaseResponse) {
                        ToastUtils.showToast(App.getInstance().getString(R.string.verification_code_sent_successfully));
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(App.getInstance().getString(R.string.verification_code_sent_failed));
                    }
                }
            );
    }
}
