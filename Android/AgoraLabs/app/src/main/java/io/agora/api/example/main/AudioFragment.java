package io.agora.api.example.main;

import io.agora.api.example.R;
import io.agora.api.example.common.dialog.AutoDissmissDialog;
import io.agora.api.example.main.model.Feature;
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
            AutoDissmissDialog dialog=new AutoDissmissDialog(getContext(),R.style.auto_dismiss_dialog);
            dialog.setResource(R.mipmap.ic_warning_circle,R.string.feature_not_supported);
            dialog.show();
    }


}
