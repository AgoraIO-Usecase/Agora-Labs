package io.agora.api.example.main;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.agora.api.example.common.Constant;
import io.agora.api.example.common.UserManager;
import io.agora.api.example.common.base.bean.CommonBean;
import io.agora.api.example.common.base.component.BaseRequestViewModel;
import io.agora.api.example.common.base.event.UserInfoChangeEvent;
import io.agora.api.example.common.base.event.UserLogoutEvent;
import io.agora.api.example.common.server.ApiManager;
import io.agora.api.example.common.server.api.ApiException;
import io.agora.api.example.common.server.api.ApiSubscriber;
import io.agora.api.example.common.server.model.BaseResponse;
import io.agora.api.example.common.server.model.User;
import io.agora.api.example.utils.SchedulersUtil;
import io.agora.api.example.utils.ToastUtils;
import io.reactivex.disposables.Disposable;
import java.io.File;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
public class MainViewModel extends BaseRequestViewModel {
    @Override
    protected boolean isNeedEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@Nullable UserInfoChangeEvent event) {
        if (getISingleCallback() != null) {
            getISingleCallback().onSingleCallback(Constant.CALLBACK_TYPE_USER_INFO_CHANGE, null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@Nullable UserLogoutEvent event) {
        if (getISingleCallback() != null) {
            getISingleCallback().onSingleCallback(Constant.CALLBACK_TYPE_USER_LOGOUT, null);
        }
    }

    /**
     * 获取用户信息
     */
    /*
    public void requestUserInfo(String userNo) {
        ApiManager.getInstance().requestUserInfo(userNo)
            .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<User>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<User> data) {
                        UserManager.getInstance().getUser().name = data.getData().name;
                        UserManager.getInstance().getUser().headUrl = data.getData().headUrl;
                        UserManager.getInstance().saveUserInfo(UserManager.getInstance().getUser());
                        getISingleCallback().onSingleCallback(Constant.CALLBACK_TYPE_LOGIN_REQUEST_LOGIN_SUCCESS, null);
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
            );
    }*/

    /**
     * 修改用户信息
     */
    /*
    public void requestEditUserInfo(String headUrl,
        String name,
        String sex) {
        ApiManager.getInstance().requestUserUpdate(headUrl, name, sex, UserManager.getInstance().getUser().userNo).
            compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<User>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<User> data) {
                        ToastUtils.showToast("修改成功");
                        getISingleCallback().onSingleCallback(Constant.CALLBACK_TYPE_USER_INFO_CHANGE, null);
                        if (!TextUtils.isEmpty(name)) {
                            UserManager.getInstance().getUser().name = name;
                        }
                        if (!TextUtils.isEmpty(headUrl)) {
                            UserManager.getInstance().getUser().headUrl = headUrl;
                        }
                        UserManager.getInstance().saveUserInfo(UserManager.getInstance().getUser());
                        requestUserInfo(UserManager.getInstance().getUser().userNo);
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                });
    }*/
    /*
    public void updatePhoto(File file) {
        ApiManager.getInstance().requestUploadPhoto(file)
            .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<CommonBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<CommonBean> data) {
                        UserManager.getInstance().getUser().headUrl = data.getData().url;
                        requestEditUserInfo(data.getData().url, null, null);
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
            );
    }*/

    /**
     * 注销用户
     */
    public void requestCancellation(String userNo) {
        ApiManager.getInstance().requestCancellationUser(userNo)
            .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        if(getISingleCallback()!=null) {
                            getISingleCallback().onSingleCallback(Constant.CALLBACK_TYPE_USER_CANCEL_ACCOUNTS, null);
                        }
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
            );
    }
}

