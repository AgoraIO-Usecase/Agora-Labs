package io.agora.api.example.home;

import io.agora.api.example.R;
import io.agora.api.example.model.Feature;
import java.util.ArrayList;
import java.util.List;

public class NetOptimizeFragment extends BaseListFragment {
    private final int ID_MULTI_LINK=16;
    private final int ID_ANTI_WEAK_NET=17;
    @Override protected void onItemSelect(Feature feature, int position) {
        //todo
    }

    @Override protected void initData() {
        List<Feature> data=new ArrayList<>();
        data.add(new Feature(ID_MULTI_LINK, R.mipmap.multi_path,R.string.multi_link));
        data.add(new Feature(ID_ANTI_WEAK_NET,R.mipmap.signal_weak,R.string.anti_weak_network));
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }
}
