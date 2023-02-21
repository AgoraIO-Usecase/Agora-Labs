package io.agora.api.example.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.Constant;
import io.agora.api.example.common.UserManager;
import io.agora.api.example.common.base.component.OnButtonClickListener;
import io.agora.api.example.common.dialog.CommonDialog;
import io.agora.api.example.databinding.FragmentSettingsBinding;
import io.agora.api.example.utils.SPUtils;
import io.agora.api.example.utils.SystemUtil;

public class SettingsFragment extends Fragment implements View.OnClickListener{
    private FragmentSettingsBinding binding;
    private CommonDialog logoutDialog;
    private CommonDialog logoffAccountDialog;
    private MainViewModel mainViewModel;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.userAgreement.setOnClickListener(this);
        binding.userPrivacyPolicy.setOnClickListener(this);
        binding.userSignOut.setOnClickListener(this);
        binding.deactivate.setOnClickListener(this);
        binding.version.setText(SystemUtil.getVersionName(getContext()));
        binding.ivBack.setOnClickListener(this);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setLifecycleOwner(this);
    }

    @Override public void onClick(View v) {
        if(v.getId()== R.id.iv_back){
            Navigation.findNavController(v).popBackStack();
        }else if(v.getId()==R.id.user_agreement){
            openUrl("https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/meta/demo/fulldemoStatic/privacy/service.html");
        }else if(v.getId()==R.id.user_privacy_policy){
            openUrl("https://accktvpic.oss-cn-beijing.aliyuncs.com/pic/meta/demo/fulldemoStatic/privacy/privacy.html");
        } else if(v.getId()==R.id.user_sign_out){
            if (logoutDialog == null) {
                logoutDialog = new CommonDialog(requireContext());
                logoutDialog.setDialogTitle("确定退出登录吗？");
                logoutDialog.setDescText("退出登录后，我们还会继续保留您的账户数据，记得再来体验哦～");
                logoutDialog.setDialogBtnText(getString(R.string.app_exit), getString(R.string.cancel));
                logoutDialog.setOnButtonClickListener(new OnButtonClickListener() {
                    @Override
                    public void onLeftButtonClick() {
                        SPUtils.getInstance(App.getInstance()).put(Constant.IS_AGREE, false);
                        UserManager.getInstance().logout();
                        Intent it=new Intent(getContext(),WelcomeActivity.class);
                        startActivity(it);
                        requireActivity().finish();
                    }

                    @Override
                    public void onRightButtonClick() {

                    }
                });
            }
            logoutDialog.show();
        }else if(v.getId()==R.id.deactivate){
            if (logoffAccountDialog == null) {
                logoffAccountDialog = new CommonDialog(requireContext());
                logoffAccountDialog.setDialogTitle("确定注销账号？");
                logoffAccountDialog.setDescText("注销账号后，您将暂时无法使用该账号体验我们的服务，真的要注销吗？");
                logoffAccountDialog.setDialogBtnText(getString(R.string.app_logoff), getString(R.string.cancel));
                logoffAccountDialog.setOnButtonClickListener(new OnButtonClickListener() {
                    @Override
                    public void onLeftButtonClick() {
                        SPUtils.getInstance(App.getInstance()).put(Constant.IS_AGREE, false);
                        mainViewModel.requestCancellation(UserManager.getInstance().getUser().userNo);
                    }

                    @Override
                    public void onRightButtonClick() {
                        SPUtils.getInstance(App.getInstance()).put(Constant.IS_AGREE, true);
                    }
                });
            }
            logoffAccountDialog.show();
        }
    }

    private void openUrl(String url){
        Intent it=new Intent(getContext(),WebViewActivity.class);
        it.putExtra("url",url);
        startActivity(it);
    }
}
