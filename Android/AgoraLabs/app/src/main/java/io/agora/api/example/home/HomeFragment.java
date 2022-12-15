package io.agora.api.example.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import io.agora.api.example.R;
import io.agora.api.example.databinding.FragmentHomeBinding;
import io.agora.api.example.common.widget.indicator.ViewPagerHelper;
import io.agora.api.example.common.widget.indicator.buildins.navigator.Navigator;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.CommonNavigatorAdapter;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.IPagerIndicator;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.IPagerTitleView;
import io.agora.api.example.common.widget.indicator.buildins.navigator.indicators.LinePagerIndicator;
import io.agora.api.example.common.widget.indicator.buildins.navigator.titles.ClipPagerTitleView;
import io.agora.api.example.utils.UIUtil;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private List<Fragment> fragmentList;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(inflater,container,false);
        initIndicator();
        initView();
        return binding.getRoot();
    }


    @Override public void onStart() {
        super.onStart();
        runOnPermissionGranted(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    private void initIndicator(){
        List<String> titles=new ArrayList<>();
        titles.add(getString(R.string.tab_video));
        titles.add(getString(R.string.tab_audio));
        titles.add(getString(R.string.tab_net_optimize));
        titles.add(getString(R.string.tab_algorithmic));
        Navigator commonNavigator = new Navigator(getContext());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles == null ? 0 : titles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setText(titles.get(index));
                clipPagerTitleView.setTextColor(Color.parseColor("#990A3D7B"));
                clipPagerTitleView.setClipColor(Color.WHITE);
                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.viewpager.setCurrentItem(index);
                    }
                });
                return clipPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                float navigatorHeight = context.getResources().getDimension(R.dimen.navigator_height);
                float borderWidth = UIUtil.dip2px(context, 1);
                float lineHeight = navigatorHeight - 2 * borderWidth;
                indicator.setLineHeight(lineHeight);
                indicator.setRoundRadius(lineHeight / 2);
                indicator.setYOffset(borderWidth);
                indicator.setColors(Color.parseColor("#2787FF"));
                return indicator;
            }
        });
        binding.indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(binding.indicator, binding.viewpager);
    }

    private void initView(){
        FragmentFactory factory= getChildFragmentManager().getFragmentFactory();
        fragmentList=new ArrayList<>();
        fragmentList.add(factory.instantiate(getContext().getClassLoader(), VideoFragment.class.getName()));
        fragmentList.add(factory.instantiate(getContext().getClassLoader(), AudioFragment.class.getName()));
        fragmentList.add(factory.instantiate(getContext().getClassLoader(), NetOptimizeFragment.class.getName()));
        fragmentList.add(factory.instantiate(getContext().getClassLoader(), AlgorithmicFragment.class.getName()));
        FragmentStateAdapter adapter=new FragmentStateAdapter(this) {
            @NonNull @Override public Fragment createFragment(int position) {
                return fragmentList.get(position);
            }

            @Override public int getItemCount() {
                return fragmentList.size();
            }
        };
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.setOffscreenPageLimit(4);
    }

    @SuppressLint("WrongConstant")
    private void runOnPermissionGranted(@NonNull Runnable runnable) {
        List<String> permissionList = new ArrayList<>();
        permissionList.add(Permission.READ_EXTERNAL_STORAGE);
        permissionList.add(Permission.WRITE_EXTERNAL_STORAGE);
        permissionList.add(Permission.RECORD_AUDIO);
        permissionList.add(Permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionList.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        String[] permissionArray = new String[permissionList.size()];
        permissionList.toArray(permissionArray);

        if (AndPermission.hasPermissions(this, permissionArray)) {
            runnable.run();
            return;
        }
        AndPermission.with(this).runtime().permission(
            permissionArray
        ).onGranted(permissions ->
        {
            runnable.run();
        }).start();
    }

}
