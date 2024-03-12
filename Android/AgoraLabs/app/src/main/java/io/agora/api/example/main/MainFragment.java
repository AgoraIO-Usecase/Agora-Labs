package io.agora.api.example.main;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import io.agora.api.example.R;
import io.agora.api.example.databinding.FragmentMainBinding;
import io.agora.api.example.common.widget.indicator.ViewPagerHelper;
import io.agora.api.example.common.widget.indicator.buildins.navigator.Navigator;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.CommonNavigatorAdapter;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.IPagerIndicator;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.IPagerTitleView;
import io.agora.api.example.common.widget.indicator.buildins.navigator.indicators.LinePagerIndicator;
import io.agora.api.example.common.widget.indicator.buildins.navigator.titles.ClipPagerTitleView;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.UIUtil;
import java.util.ArrayList;
import java.util.List;

import static io.agora.api.example.common.widget.indicator.buildins.navigator.indicators.LinePagerIndicator.MODE_WRAP_CONTENT;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private List<Fragment> fragmentList;

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding=FragmentMainBinding.inflate(inflater,container,false);
        initIndicator();
        initView();
        return binding.getRoot();
    }


    @Override public void onStart() {
        super.onStart();
        //runOnPermissionGranted(() -> {
        //});
    }

    private void initIndicator(){
        List<String> titles=new ArrayList<>();
        titles.add(getString(R.string.tab_video));
        titles.add(getString(R.string.tab_audio));
        titles.add(getString(R.string.tab_metaverse));
        titles.add(getString(R.string.tab_net_optimize));
        Navigator commonNavigator = new Navigator(getContext());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setText(titles.get(index));
                clipPagerTitleView.setTextColor(Color.parseColor("#990A3D7B"));
                clipPagerTitleView.setClipColor(Color.WHITE);
                clipPagerTitleView.setOnClickListener(v -> binding.viewpager.setCurrentItem(index));
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
                indicator.setXOffset(0-UIUtil.dip2px(getContext(),10));
                indicator.setMode(MODE_WRAP_CONTENT);
                //indicator.setColors(Color.parseColor("#2787FF"));
                indicator.setStartColor(Color.parseColor("#91E2FF"));
                indicator.setEndColor(Color.parseColor("#2787FF"));
                //indicator.setBackgroundResource(R.drawable.bg_rectangle_blue);
                return indicator;
            }
        });
       //commonNavigator.setAdjustMode(true);

        binding.indicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(binding.indicator, binding.viewpager);
    }

    private void initView(){
        FragmentFactory factory= getChildFragmentManager().getFragmentFactory();
        fragmentList=new ArrayList<>();
        fragmentList.add(factory.instantiate(getContext().getClassLoader(), VideoFragment.class.getName()));
        fragmentList.add(factory.instantiate(getContext().getClassLoader(), AudioFragment.class.getName()));
        fragmentList.add(factory.instantiate(getContext().getClassLoader(), AlgorithmicFragment.class.getName()));
        fragmentList.add(factory.instantiate(getContext().getClassLoader(), NetOptimizeFragment.class.getName()));
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
        binding.ivSettings.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_settings);
        });
        binding.ivSettings.setVisibility(SystemUtil.getCountry().equalsIgnoreCase("cn")?View.VISIBLE:View.GONE);
    }
}
