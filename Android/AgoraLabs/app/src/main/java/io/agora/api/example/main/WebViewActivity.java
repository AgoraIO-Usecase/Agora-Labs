package io.agora.api.example.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.agora.api.example.R;
import io.agora.api.example.common.base.component.BaseViewBindingActivity;
import io.agora.api.example.databinding.AppActivityWebviewBinding;

public class WebViewActivity extends BaseViewBindingActivity<AppActivityWebviewBinding> {

    String url = "https://www.agora.io/cn/about-us/";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected AppActivityWebviewBinding getViewBinding(@NonNull LayoutInflater layoutInflater) {
        return AppActivityWebviewBinding.inflate(layoutInflater);
    }



    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        url=getIntent().getStringExtra("url");
        if (url.contains("user_agreement")) {
            getBinding().tvTitle.setText(getString(R.string.app_user_agreement));
        } else if (url.contains("about-us")) {
            getBinding().tvTitle.setText(getString(R.string.app_about_us));
        } else if (url.contains("privacy_policy")) {
            getBinding().tvTitle.setText(getString(R.string.app_privacy_agreement));
        }
        getBinding().webView.loadUrl(url);
        getBinding().ivBack.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                finish();
            }
        });
    }
}
