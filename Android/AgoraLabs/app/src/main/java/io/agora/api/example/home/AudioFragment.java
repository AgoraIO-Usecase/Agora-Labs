package io.agora.api.example.home;

import io.agora.api.example.R;
import io.agora.api.example.model.Feature;
import java.util.ArrayList;
import java.util.List;

public class AudioFragment extends BaseListFragment {
    private final int ID_AI_NOISE_REDUCTION=12;
    private final int ID_AI_ECHO_CANCELLATION=13;
    private final int ID_SPACIAL_AUDIO=14;
    private final int ID_STT=15;

    @Override protected void initData() {
        List<Feature> data=new ArrayList<Feature>();
        data.add(new Feature(ID_AI_NOISE_REDUCTION, R.mipmap.noise_reduction,R.string.ai_noise_reduction));
        data.add(new Feature(ID_AI_ECHO_CANCELLATION,R.mipmap.aiaec,R.string.ai_echo_cancellation));
        data.add(new Feature(ID_SPACIAL_AUDIO,R.mipmap.spacial_audio,R.string.spacial_audio));
        data.add(new Feature(ID_STT,R.mipmap.stt,R.string.stt));
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    @Override protected void onItemSelect(Feature feature, int position) {
        //todo
    }


}
