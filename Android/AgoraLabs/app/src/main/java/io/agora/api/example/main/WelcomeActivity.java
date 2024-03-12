package io.agora.api.example.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import io.agora.api.example.App;
import io.agora.api.example.SplashActivity;
import io.agora.api.example.common.Constant;
import io.agora.api.example.common.UserManager;
import io.agora.api.example.common.base.component.BaseViewBindingActivity;
import io.agora.api.example.common.base.component.OnButtonClickListener;
import io.agora.api.example.common.dialog.UserAgreementDialog;
import io.agora.api.example.common.dialog.UserAgreementDialog2;
import io.agora.api.example.databinding.AppActivityWelcomeBinding;
import io.agora.api.example.utils.SPUtils;
import io.agora.api.example.utils.SystemUtil;

public class WelcomeActivity extends BaseViewBindingActivity<AppActivityWelcomeBinding> {
    private UserAgreementDialog userAgreementDialog;
    private UserAgreementDialog2 userAgreementDialog2;

    @Override
    protected AppActivityWelcomeBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return AppActivityWelcomeBinding.inflate(inflater);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        //getBinding().ivAppLogo.postDelayed(this::checkStatusToStart, 500);
        checkStatusToStart();
    }

    @Override
    public void initListener() {
    }

    @Override
    public boolean isBlackDarkStatus() {
        return false;
    }

    /**
     * 显示用户协议 隐私政策对话框
     */
    private void showUserAgreementDialog() {
        if (userAgreementDialog == null) {
            userAgreementDialog = new UserAgreementDialog(this);
            userAgreementDialog.setOnButtonClickListener(new OnButtonClickListener() {
                @Override
                public void onLeftButtonClick() {
                    showUserAgreementDialog2();
                    userAgreementDialog.dismiss();
                }

                @Override
                public void onRightButtonClick() {
                    goPhoneLoginRegister();
                    userAgreementDialog.dismiss();
                    finish();
                }
            });
        }
        userAgreementDialog.show();
    }

    /**
     * 显示用户协议 隐私政策对话框
     */
    private void showUserAgreementDialog2() {
        if (userAgreementDialog2 == null) {
            userAgreementDialog2 = new UserAgreementDialog2(this);
            userAgreementDialog2.setOnButtonClickListener(new OnButtonClickListener() {
                @Override
                public void onLeftButtonClick() {
                    goPhoneLoginRegister();
                    userAgreementDialog2.dismiss();
                    finish();
                }

                @Override
                public void onRightButtonClick() {
                    goPhoneLoginRegister();
                    userAgreementDialog2.dismiss();
                    finish();
                }
            });
        }
        userAgreementDialog2.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void getPermissions() {

    }

    private void checkStatusToStart() {
        startMainActivity();
    }

    private void startMainActivity() {
        if (UserManager.getInstance().isLogin()|| !SystemUtil.getCountry().equalsIgnoreCase("cn")) {
            Intent it=new Intent(this, SplashActivity.class);
            startActivity(it);
            finish();
        } else {
            if (!SPUtils.getInstance(App.getInstance()).getBoolean(Constant.IS_AGREE, false)) {
                showUserAgreementDialog();
            } else {
                goPhoneLoginRegister();
                finish();
            }
        }
    }

    private void goPhoneLoginRegister(){
        Intent it=new Intent(this,PhoneLoginRegisterActivity.class);
        startActivity(it);
    }



}
