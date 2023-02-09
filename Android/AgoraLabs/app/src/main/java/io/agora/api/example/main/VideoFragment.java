package io.agora.api.example.main;

import android.content.Context;
import android.os.Vibrator;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import io.agora.api.example.R;
import io.agora.api.example.common.dialog.AutoDissmissDialog;
import io.agora.api.example.main.model.Feature;
import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends BaseListFragment {
    private final int ID_VIRTUAL_BG=1;
    private final int ID_BEAUTY=2;
    private final int ID_DENOISE=3;
    private final int ID_LIGHT_DARK=4;
    private final int ID_SHARPEN=5;
    private final int ID_COLOR_ENHANCE=6;
    private final int ID_PVC=7;
    private final int ID_ROI=8;
    private final int ID_SUPER_RES=9;
    private final int ID_SUPER_QUALITY=10;
    private final int ID_HDR=11;



    protected void initData(){
        List<Feature> data= new ArrayList<>();
        data.add(new Feature(-1,-1, R.string.special_effect));
        data.add(new Feature(ID_VIRTUAL_BG,R.mipmap.virtual_bg,R.string.virtul_bg,true));
        data.add(new Feature(ID_BEAUTY,R.mipmap.beauty,R.string.beauty,true));
        data.add(new Feature(-1,-1,R.string.video_quality));
        data.add(new Feature(ID_PVC,R.mipmap.pvc,R.string.pvc,true));
        data.add(new Feature(ID_ROI,R.mipmap.roi,R.string.roi,true));
        data.add(new Feature(ID_SUPER_RES,R.mipmap.super_res,R.string.super_res,true));
        data.add(new Feature(ID_DENOISE,R.mipmap.noise,R.string.denoise));
        data.add(new Feature(ID_LIGHT_DARK,R.mipmap.light_dark,R.string.light_dark));
        data.add(new Feature(ID_SHARPEN,R.mipmap.sharpen,R.string.adaptive_sharpen));
        data.add(new Feature(ID_COLOR_ENHANCE,R.mipmap.saturation,R.string.color_enhance));
        data.add(new Feature(ID_SUPER_QUALITY,R.mipmap.image,R.string.super_quality,true));
        data.add(new Feature(ID_HDR,R.mipmap.hdr,R.string.hdr));
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }

    @Override protected void onItemSelect(Feature feature, int position) {
        if(!feature.isEnabled()){
            Vibrator vibrator= (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(30);
            AutoDissmissDialog dialog=new AutoDissmissDialog(getContext(),R.style.auto_dismiss_dialog);
            dialog.setResource(R.mipmap.ic_warning_circle,R.string.feature_not_supported);
            dialog.show();
            return;
        }
        if(feature.getId()==ID_VIRTUAL_BG){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_virtual_bg);
        }else if(feature.getId()==ID_BEAUTY){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_beauty);
        }else if(feature.getId()==ID_PVC){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_pvc);
        }else if(feature.getId()==ID_ROI){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_roi);
        }else if(feature.getId()==ID_SUPER_RES){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_super_res);
        }else if(feature.getId()==ID_SUPER_QUALITY){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_super_quality);
        }

    }
}
