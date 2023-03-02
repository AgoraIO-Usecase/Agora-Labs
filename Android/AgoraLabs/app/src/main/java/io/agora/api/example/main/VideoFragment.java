package io.agora.api.example.main;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import io.agora.api.example.R;
import io.agora.api.example.common.dialog.AutoDissmissDialog;
import io.agora.api.example.common.server.ServerHelper;
import io.agora.api.example.common.server.model.ReportResponseData;
import io.agora.api.example.main.model.Feature;
import java.util.ArrayList;
import java.util.List;

public class VideoFragment extends BaseListFragment {
    private final int ID_VIRTUAL_BG=1;
    private final int ID_BEAUTY=2;
    private final int ID_DENOISE=3;
    private final int ID_DARK_LIGHT =4;
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
        data.add(new Feature(ID_COLOR_ENHANCE,R.mipmap.saturation,R.string.color_enhance,true));
        data.add(new Feature(ID_DARK_LIGHT,R.mipmap.light_dark,R.string.light_dark,true));
        data.add(new Feature(ID_DENOISE,R.mipmap.noise,R.string.denoise,true));

        //data.add(new Feature(ID_SHARPEN,R.mipmap.sharpen,R.string.adaptive_sharpen));

        data.add(new Feature(ID_SUPER_QUALITY,R.mipmap.image,R.string.super_quality));
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
            reportEvent("VirtualBackground");
        }else if(feature.getId()==ID_BEAUTY){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_beauty);
            reportEvent("BeautifyFilter");
        }else if(feature.getId()==ID_PVC){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_pvc);
            reportEvent("PVC");
        }else if(feature.getId()==ID_ROI){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_roi);
            reportEvent("ROI");
        }else if(feature.getId()==ID_SUPER_RES){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_super_res);
            reportEvent("Resolution");
        }else if(feature.getId()==ID_SUPER_QUALITY){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_super_quality);
            reportEvent("Image");
        }else if(feature.getId()==ID_COLOR_ENHANCE){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_color_enhancement);
            reportEvent("EnanceSaturation");
        }else if(feature.getId()== ID_DARK_LIGHT){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_dark_light_enhancement);
            reportEvent("DimEnvironment");
        }else if(feature.getId()==ID_DENOISE){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_video_noise_reduction);
            reportEvent("ReduceNoise");
        }
    }

    private void reportEvent(String event){
        ServerHelper.report(event, new ServerHelper.CallBack<ReportResponseData>() {
            @Override public void onSuccess(ReportResponseData data) {
                Log.d("AgoraLab","report success");
            }

            @Override public void onFailed(Throwable e) {
                Log.d("AgoraLab","report failed e:"+e.getLocalizedMessage());
            }
        });
    }
}
