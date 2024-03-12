package io.agora.api.example.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.agora.api.example.R;
import io.agora.api.example.common.base.component.BaseViewBindingActivity;
import io.agora.api.example.databinding.AppActivityWebviewBinding;

public class WebViewActivity extends BaseViewBindingActivity<AppActivityWebviewBinding> {

    String url = "https://www.shengwang.cn/aboutus/";

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
        //Log.d("AgoraLabs","url:"+url);
        if (url.contains("service-term")) {
            getBinding().tvTitle.setText(getString(R.string.app_user_agreement));
        } else if (url.contains("about-us")) {
            getBinding().tvTitle.setText(getString(R.string.app_about_us));
        } else if (url.contains("privacy-term")) {
            getBinding().tvTitle.setText(getString(R.string.app_privacy_agreement));
        }else if(url.contains("3rd-party-SDK")){
            getBinding().tvTitle.setText(getString(R.string.third_party_data_sharing_list));
        }else if(url.contains("pages/manifest")){
            getBinding().tvTitle.setText(getString(R.string.personal_info_collect_checklist));
        }

        getBinding().webView.loadUrl(url);
        getBinding().ivBack.setOnClickListener(v -> finish());
    }
}
