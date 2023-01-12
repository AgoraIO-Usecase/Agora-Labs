package io.agora.api.example.main;

import android.content.Context;
import android.os.Vibrator;
import io.agora.api.example.R;
import io.agora.api.example.common.dialog.AutoDissmissDialog;
import io.agora.api.example.main.model.Feature;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmicFragment extends BaseListFragment {
    private final int ID_FACE_CAPTURE = 18;
    private final int ID_MOTION_CAPTURE = 19;
    private final int ID_VOICE_DRIVE = 20;

    @Override protected void onItemSelect(Feature feature, int position) {
        //todo
        Vibrator vibrator= (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(30);
        AutoDissmissDialog dialog=new AutoDissmissDialog(getContext(),R.style.auto_dismiss_dialog);
        dialog.setResource(R.mipmap.ic_warning_circle,R.string.feature_not_supported);
        dialog.show();
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
