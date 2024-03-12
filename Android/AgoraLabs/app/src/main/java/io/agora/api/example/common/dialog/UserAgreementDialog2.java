package io.agora.api.example.common.dialog;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import io.agora.api.example.R;
import io.agora.api.example.common.base.component.BaseDialog;
import io.agora.api.example.databinding.DialogUserAgreementBinding;
import io.agora.api.example.main.WebViewActivity;
import io.agora.api.example.utils.UIUtil;

public class UserAgreementDialog2 extends BaseDialog<DialogUserAgreementBinding> {
    public UserAgreementDialog2(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected DialogUserAgreementBinding getViewBinding(@NonNull
        LayoutInflater inflater) {
        return DialogUserAgreementBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        setCancelable(false);
        getBinding().btnDisagree.setOnClickListener(view -> getOnButtonClickListener().onLeftButtonClick());
        getBinding().btnAgree.setText("同意并继续");
        getBinding().btnDisagree.setText(getContext().getString(R.string.disagree));
        ClickableSpan protocolClickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                openUrl("https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/meta/demo/fulldemoStatic/privacy/service.html");
            }
        };
        ClickableSpan protocolClickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                openUrl("https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/meta/demo/fulldemoStatic/privacy/privacy.html");
            }
        };


        ForegroundColorSpan spanColor = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.blue_9F));
        SpannableStringBuilder spannable = new SpannableStringBuilder(getContext().getString(R.string.privacy_protection_tip2));
        spannable.setSpan(protocolClickableSpan1, 3, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(spanColor, 3, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(protocolClickableSpan2, 10, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(spanColor, 10, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        getBinding().tvProtection.setText(spannable);
        getBinding().btnAgree.setOnClickListener(view -> {
            getOnButtonClickListener().onRightButtonClick();
        });
        getBinding().tvProtection.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void openUrl(String url){
        Intent it=new Intent(getContext(), WebViewActivity.class);
        it.putExtra("url",url);
        getContext().startActivity(it);
    }

    @Override
    protected void setGravity() {
        getWindow().setLayout(
            UIUtil.dip2px(getContext(),280),
            UIUtil.dip2px(getContext(),200)
        );
        getWindow().getAttributes().gravity = Gravity.CENTER;
    }
}
