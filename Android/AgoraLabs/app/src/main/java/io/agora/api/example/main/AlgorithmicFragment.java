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

public class AlgorithmicFragment extends BaseListFragment {
    private final int ID_RHYTHM= 18;
    private final int ID_3D_EFFECTS = 19;
    private final int ID_3D_LIGHTING = 20;
    private final int ID_360_BACKGROUND=21;
    private final int ID_VIRTUAL_HEADGEAR=22;

    @Override protected void onItemSelect(Feature feature, int position) {
        //todo
        if(!feature.isEnabled()){
            Vibrator vibrator= (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(30);
            AutoDissmissDialog dialog=new AutoDissmissDialog(getContext(),R.style.auto_dismiss_dialog);
            dialog.setResource(R.mipmap.ic_warning_circle,R.string.feature_not_supported);
            dialog.show();
            return;
        }
        if(feature.getId()==ID_RHYTHM){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_rhythm);
            reportEvent("Rhythm");
        }else if(feature.getId()==ID_3D_EFFECTS){
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_effect_3d);
            reportEvent("3dEffects");
        }else if(feature.getId()==ID_3D_LIGHTING) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_lighting_3d);
            reportEvent("3dLighting");
        }else if(feature.getId()==ID_360_BACKGROUND) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_background_360);
            reportEvent("360Background");
        }else if(feature.getId()==ID_VIRTUAL_HEADGEAR) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_homeFragment_to_virtual_headgear);
            reportEvent("VirtualHeadgear");
        }
    }

    @Override protected void initData() {
        List<Feature> data=new ArrayList<>();
        data.add(new Feature(ID_RHYTHM, R.mipmap.rhythm,R.string.rhythm,true));
        data.add(new Feature(ID_3D_EFFECTS,R.mipmap.effects_3d,R.string.effects_3d,true));
        data.add(new Feature(ID_3D_LIGHTING,R.mipmap.lighting_3d,R.string.lighting_3d,true));
        data.add(new Feature(ID_360_BACKGROUND,R.mipmap.background_360,R.string.background_360,true));
        data.add(new Feature(ID_VIRTUAL_HEADGEAR,R.mipmap.virtual_headgear,R.string.virtual_headgear,true));
        adapter.setData(data);
        adapter.notifyDataSetChanged();
    }





}
