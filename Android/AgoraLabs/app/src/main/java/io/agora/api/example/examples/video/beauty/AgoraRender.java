package io.agora.api.example.examples.video.beauty;

import android.content.Context;
import io.agora.api.example.R;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.BeautyOptions;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kotlin.jvm.internal.PropertyReference0Impl;

public class AgoraRender {
    public static final int ID_AGORA_CONTRAST=1;
    public static final int ID_AGORA_LIGHTENING=2;
    public static final int ID_AGORA_SMOOTHNESS=3;
    public static final int ID_AGORA_REDNESS=4;
    public static final int ID_AGORA_SHARPNESS=5;

    private RtcEngineEx rtcEngine;
    private Context context;
    private int selectedFeatureID=-1;
    private BeautyOptions beautyOptions=new BeautyOptions();
    private Map<Integer,Integer> map=new HashMap<>();

    public  AgoraRender(Context context,RtcEngineEx rtcEngine) {
        this.context=context;
        this.rtcEngine = rtcEngine;
        beautyOptions.lighteningContrastLevel=2;
        beautyOptions.lighteningLevel=0.8f;
        beautyOptions.smoothnessLevel=0.7f;
        beautyOptions.rednessLevel=0.5f;
        beautyOptions.sharpnessLevel=0.3f;
        map.put(ID_AGORA_CONTRAST,60);
        map.put(ID_AGORA_LIGHTENING,80);
        map.put(ID_AGORA_SMOOTHNESS,70);
        map.put(ID_AGORA_REDNESS,50);
        map.put(ID_AGORA_SHARPNESS,30);
    }
    public void disableExtension(){
        rtcEngine.setBeautyEffectOptions(false,beautyOptions);
    }

    public void enableExtension(){
        rtcEngine.setBeautyEffectOptions(true,beautyOptions);
    }

    public void setSelectedID(int featureItem){
        this.selectedFeatureID=featureItem;
    }

    public int getCurrentProgress(){
        if(selectedFeatureID==-1){
            return 0;
        }
        if(map.containsKey(selectedFeatureID)) {
            return map.get(selectedFeatureID);
        }
        return 0;
    }

    private final int CONTRAST_MAX=2;


    public void setCurrentProgress(int progress) {
        map.put(selectedFeatureID, progress);
        DecimalFormat fnum = new DecimalFormat("##0.0");
        if (selectedFeatureID == ID_AGORA_CONTRAST) {
            beautyOptions.lighteningContrastLevel = progress == 100 ? 2 : 3 * progress / 100;
        } else if (selectedFeatureID == ID_AGORA_LIGHTENING) {
            beautyOptions.lighteningLevel = Float.valueOf(fnum.format(1.0f * progress / 100));
        } else if (selectedFeatureID == ID_AGORA_SMOOTHNESS) {
            beautyOptions.smoothnessLevel = Float.valueOf(fnum.format(1.0f * progress / 100));
        } else if (selectedFeatureID == ID_AGORA_REDNESS) {
            beautyOptions.rednessLevel = Float.valueOf(fnum.format(1.0f * progress / 100));
        } else if (selectedFeatureID == ID_AGORA_SHARPNESS) {
            beautyOptions.sharpnessLevel = Float.valueOf(fnum.format(1.0f * progress / 100));
        }
        rtcEngine.setBeautyEffectOptions(true,beautyOptions);
    }

    public List<OptionItem> generatorOptionItems(){
        List<OptionItem> optionItems = new ArrayList<>();
        optionItems.add(new OptionItem(ID_AGORA_SMOOTHNESS, R.mipmap.ic_mopi, R.string.buffing));
        optionItems.add(new OptionItem(ID_AGORA_LIGHTENING, R.mipmap.ic_meibai, R.string.lighten));
        optionItems.add(new OptionItem(ID_AGORA_REDNESS, R.mipmap.ic_hongrun, R.string.ruddy));
        optionItems.add(new OptionItem(ID_AGORA_CONTRAST, R.mipmap.ic_mopi, R.string.contrast));
        optionItems.add(new OptionItem(ID_AGORA_SHARPNESS, R.mipmap.ic_sharpen, R.string.sharpen));
        return optionItems;
    }

}
