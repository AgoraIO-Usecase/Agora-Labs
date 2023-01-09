package io.agora.api.example.common.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

public class FragmentViewPagerAdapter extends FragmentStateAdapter {
    private List<Fragment> fragmentList;

    public FragmentViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull @Override public Fragment createFragment(int position) {
        if(fragmentList!=null){
            return fragmentList.get(position);
        }
        return null;
    }

    @Override public int getItemCount() {
        return 0;
    }
}
