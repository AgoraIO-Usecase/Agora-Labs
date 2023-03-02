package io.agora.api.example.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import io.agora.api.example.R;
import io.agora.api.example.SplashActivity;
import io.agora.api.example.common.Constant;
import io.agora.api.example.common.base.component.BaseViewBindingActivity;
import io.agora.api.example.common.base.component.OnButtonClickListener;
import io.agora.api.example.common.dialog.SwipeCaptchaDialog;
import io.agora.api.example.databinding.AppActivityPhoneLoginBinding;
import io.agora.api.example.login.LoginViewModel;
import io.agora.api.example.utils.CountDownTimerUtils;
import io.agora.api.example.utils.ToastUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneLoginRegisterActivity  extends BaseViewBindingActivity<AppActivityPhoneLoginBinding> {

    /**
     * 登录模块统一ViewModel
     */
    private LoginViewModel phoneLoginViewModel;
    private SwipeCaptchaDialog swipeCaptchaDialog;

    /**
     * 记时器
     */
    private CountDownTimerUtils countDownTimerUtils;

    @Override
    protected AppActivityPhoneLoginBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return AppActivityPhoneLoginBinding.inflate(inflater);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        phoneLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        phoneLoginViewModel.setLifecycleOwner(this);
        phoneLoginViewModel.setISingleCallback((var1, var2) -> {
            if (var1 == Constant.CALLBACK_TYPE_LOGIN_REQUEST_LOGIN_SUCCESS) {
                // RoomManager.getInstance().loginOut();
                Intent it=new Intent(PhoneLoginRegisterActivity.this, SplashActivity.class);
                startActivity(it);
                finish();
            }
            hideLoadingView();
        });
        setAccountStatus();
        //        String account = SPUtil.getString(KtvConstant.ACCOUNT, null);
        //        if (!TextUtils.isEmpty(account)) {
        //            getBinding().etAccounts.setText(account);
        //            String password = SPUtil.getString(KtvConstant.V_CODE, null);
        //            if (!TextUtils.isEmpty(password)) {
        //                getBinding().etVCode.setText(password);
        //            }
        //        }
        countDownTimerUtils = new CountDownTimerUtils(getBinding().tvSendVCode, 300000, 1000);
    }

    /**
     * 设置帐号输入框输入状态
     */
    private void setAccountStatus() {
        //手机号登录
        getBinding().etAccounts.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
    }

    @Override
    protected boolean isCanExit() {
        return true;
    }



    @Override
    public void initListener() {
        getBinding().tvUserAgreement.setOnClickListener(view -> {
            openUrl("https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/meta/demo/fulldemoStatic/privacy/service.html");
        });
        getBinding().tvPrivacyAgreement.setOnClickListener(view -> {
            openUrl("https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/meta/demo/fulldemoStatic/privacy/privacy.html");
        });
        getBinding().btnLogin.setOnClickListener(view -> {
            if (getBinding().cvIAgree.isChecked()) {
                if (checkAccount()) {
                    showSwipeCaptchaDialog();
                }
            } else {
                ToastUtils.showToast(getString(R.string.please_agree_to_our_privacy_policy_and_user_agreement));
            }
        });
        getBinding().tvSendVCode.setOnClickListener(view -> {
            String account = getBinding().etAccounts.getText().toString();
            if (!checkPhoneNum(account)) {
                ToastUtils.showToast(getString(R.string.please_enter_the_correct_phone_number));
            } else {
                phoneLoginViewModel.requestSendVCode(account);
                countDownTimerUtils.start();
            }
        });
        getBinding().iBtnClearAccount.setOnClickListener(view -> {
            getBinding().etAccounts.setText("");
        });
        getBinding().etAccounts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && editable.length() > 0) {
                    getBinding().iBtnClearAccount.setVisibility(View.VISIBLE);
                } else {
                    getBinding().iBtnClearAccount.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showSwipeCaptchaDialog() {
        if (swipeCaptchaDialog == null) {
            swipeCaptchaDialog = new SwipeCaptchaDialog(this);
            swipeCaptchaDialog.setOnButtonClickListener(new OnButtonClickListener() {
                @Override
                public void onLeftButtonClick() {

                }

                @Override
                public void onRightButtonClick() {
                    //验证成功
                    showLoadingView();
                    String account = getBinding().etAccounts.getText().toString();
                    String vCode = getBinding().etVCode.getText().toString();
                    phoneLoginViewModel.requestLogin(account, vCode);
                    //                    SPUtil.putString(KtvConstant.ACCOUNT, account);
                }
            });
        }
        swipeCaptchaDialog.show();

    }

    private boolean checkAccount() {
        String account = getBinding().etAccounts.getText().toString();
        if (!checkPhoneNum(account)) {
            ToastUtils.showToast(getString(R.string.please_enter_the_correct_phone_number));
            return false;
        } else if (TextUtils.isEmpty(getBinding().etVCode.getText().toString())) {
            ToastUtils.showToast(getString(R.string.app_please_input_v_code));
        }
        return true;
    }

    private void openUrl(String url){
        Intent it=new Intent(PhoneLoginRegisterActivity.this, WebViewActivity.class);
        it.putExtra("url",url);
        startActivity(it);
    }

    public  boolean checkPhoneNum(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            return false;
        }
        String regExp =
            "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[0-9])|(18[0-9])|(19[8,9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(phoneNum);
        return m.matches();
    }
}
