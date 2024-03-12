package io.agora.api.example.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.agora.api.example.common.Constant;
import io.agora.api.example.common.base.component.BaseRequestViewModel;
import io.agora.api.example.common.base.event.UserInfoChangeEvent;
import io.agora.api.example.common.base.event.UserLogoutEvent;
import io.agora.api.example.common.server.ApiManager;
import io.agora.api.example.common.server.api.ApiException;
import io.agora.api.example.common.server.api.ApiSubscriber;
import io.agora.api.example.common.server.model.BaseResponse;
import io.agora.api.example.utils.SchedulersUtil;
import io.agora.api.example.utils.ToastUtils;
import io.reactivex.disposables.Disposable;

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

    /**
     * 修改用户信息
     */

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

