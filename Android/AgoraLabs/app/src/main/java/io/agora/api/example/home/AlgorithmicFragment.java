package io.agora.api.example.home;

import io.agora.api.example.R;
import io.agora.api.example.model.Feature;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmicFragment extends BaseListFragment {
    private final int ID_FACE_CAPTURE = 18;
    private final int ID_MOTION_CAPTURE = 19;
    private final int ID_VOICE_DRIVE = 20;

    @Override protected void onItemSelect(Feature feature, int position) {
        //todo
    }

    @Override protected void initData() {
        List<Feature> data=new ArrayList<>();
        data.add(new Feature(ID_FACE_CAPTURE, R.mipmap.face_capture,R.string.face_capture));
        data.add(new Feature(ID_MOTION_CAPTURE,R.mipmap.motion_capture,R.string.motion_capture));
        data.add(new Feature(ID_VOICE_DRIVE,R.mipmap.avatar,R.string.voice_drive));
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }
}
