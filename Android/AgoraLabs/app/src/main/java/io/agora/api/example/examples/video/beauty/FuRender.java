package io.agora.api.example.examples.video.beauty;

import android.content.Context;
import android.util.Log;
import io.agora.api.example.R;
import io.agora.api.example.authpack;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;
import io.agora.rtc2.RtcEngineEx;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FuRender {
    private final String TAG="AgoraLab";
    private RtcEngineEx rtcEngine;
    private Context context;


    public static final int ID_FACE_UNITY_BUFFING = 1;
    public static final int ID_FACE_UNITY_WHITEN = 2;
    public static final int ID_FACE_UNITY_RUDDY = 3;
    public static final int ID_FACE_UNITY_THIN_FACE = 4;
    public static final int ID_FACE_UNITY_BIG_EYE = 5;
    public static final int ID_FACE_UNITY_REMOVE_DARK_CIRCLES = 6;
    public static final int ID_FACE_UNITY_BEAUTY_TOOTH = 7;
    public static final int ID_FACE_UNITY_NASOLABIAL_FOLDS = 8;

    private int selectedFeatureID=-1;
    private Map<Integer,Integer> map=new HashMap<>();

    public  FuRender(Context context,RtcEngineEx rtcEngine) {
        this.context=context;
        this.rtcEngine = rtcEngine;
        map.put(ID_FACE_UNITY_BUFFING, 0);
        map.put(ID_FACE_UNITY_WHITEN, 0);
        map.put(ID_FACE_UNITY_RUDDY, 0);
        map.put(ID_FACE_UNITY_THIN_FACE, 0);
        map.put(ID_FACE_UNITY_BIG_EYE, 0);
        map.put(ID_FACE_UNITY_REMOVE_DARK_CIRCLES, 0);
        map.put(ID_FACE_UNITY_BEAUTY_TOOTH,0);
        map.put(ID_FACE_UNITY_NASOLABIAL_FOLDS,0);
    }


    public void initExtension() {
        // Setup
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (byte it : authpack.A()) {
                jsonArray.put(it);
            }
            jsonObject.put("authdata", jsonArray);
            setExtensionProperty("fuSetup", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void disableExtension(){
        rtcEngine.enableExtension("FaceUnity", "Effect", false);
    }

    public void enableExtension(){
        rtcEngine.enableExtension("FaceUnity", "Effect", true);
    }


    public void loadAIModels() {
        try {
            // Load AI model
            File modelDir = new File(context.getExternalFilesDir("assets"),
                "faceunity/Resource/model/ai_face_processor.bundle");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", modelDir.getAbsolutePath());
            jsonObject.put("type", 1 << 10);

            setExtensionProperty("fuLoadAIModelFromPackage", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        try {
            // Load AI model
            File modelDir = new File(context.getExternalFilesDir("assets"),
                "faceunity/Resource/model/ai_hand_processor.bundle");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", modelDir.getAbsolutePath());
            jsonObject.put("type", 1 << 3);
            setExtensionProperty("fuLoadAIModelFromPackage", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        try {
            // Load AI model
            File modelDir = new File(context.getExternalFilesDir("assets"),
                "faceunity/Resource/model/ai_human_processor_pc.bundle");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", modelDir.getAbsolutePath());
            jsonObject.put("type", 1 << 19);
            setExtensionProperty("fuLoadAIModelFromPackage", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        try {
            File modelDir = new File(context.getExternalFilesDir("assets"),
                "faceunity/Resource/graphics/aitype.bundle");

            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", modelDir.getAbsolutePath());
                setExtensionProperty("fuCreateItemFromPackage", jsonObject.toString());
            }

            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("obj_handle", modelDir);
                jsonObject.put("name", "aitype");
                jsonObject.put("value", 1 << 10 | 1 << 21 | 1 << 3);
                setExtensionProperty("fuItemSetParam", jsonObject.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void choiceComposer() {
        File composerDir = new File(context.getExternalFilesDir("assets"),
            "faceunity/Resource/graphics/face_beautification.bundle");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", composerDir.getAbsolutePath());
            setExtensionProperty("fuCreateItemFromPackage", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void setBeautyParamsValue(String name,float value){
        try {
            File composerDir = new File(context.getExternalFilesDir("assets"),
                "faceunity/Resource/graphics/face_beautification.bundle");
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("obj_handle", composerDir);
                jsonObject.put("name", "filter_name");
                jsonObject.put("value", "ziran2");
                setExtensionProperty("fuItemSetParam", jsonObject.toString());
            }
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("obj_handle", composerDir);
                jsonObject.put("name", name);
                DecimalFormat fnum= new DecimalFormat("##0.0");
                jsonObject.put("value", Float.valueOf(fnum.format(value)));
                //Log.d(TAG,"------fuItemSetParam:"+ jsonObject);
                setExtensionProperty("fuItemSetParam", jsonObject.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }


    public void setFaceShape(String name,float value){
        try {
            File composerDir = new File(context.getExternalFilesDir("assets"),
                "faceunity/Resource/graphics/face_beautification.bundle");
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("obj_handle", composerDir);
                jsonObject.put("name", "filter_name");
                jsonObject.put("value", "ziran2");
                setExtensionProperty("fuItemSetParam", jsonObject.toString());
            }
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("obj_handle", composerDir);
                jsonObject.put("name", "face_shape");
                jsonObject.put("value", 4);
                setExtensionProperty("fuItemSetParam", jsonObject.toString());
            }
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("obj_handle", composerDir);
                jsonObject.put("name",name);
                DecimalFormat fnum= new DecimalFormat("##0.0");
                jsonObject.put("value", Float.valueOf(fnum.format(value)));
                //Log.d(TAG,"------fuItemSetParam:"+ jsonObject);
                setExtensionProperty("fuItemSetParam", jsonObject.toString());
            }

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }


    private void setExtensionProperty(String key, String property) {
        rtcEngine.setExtensionProperty("FaceUnity", "Effect", key, property);
    }

    public List<OptionItem> generatorOptionItems(){
        List<OptionItem> optionItems = new ArrayList<>();
        optionItems.add(new OptionItem(ID_FACE_UNITY_BUFFING, R.mipmap.ic_mopi, R.string.buffing));
        optionItems.add(new OptionItem(ID_FACE_UNITY_WHITEN, R.mipmap.ic_meibai, R.string.whiten));
        optionItems.add(new OptionItem(ID_FACE_UNITY_RUDDY, R.mipmap.ic_hongrun, R.string.ruddy));
        optionItems.add(new OptionItem(ID_FACE_UNITY_THIN_FACE, R.mipmap.ic_shoulian, R.string.thin_face));
        optionItems.add(new OptionItem(ID_FACE_UNITY_BIG_EYE, R.mipmap.ic_dayan, R.string.big_eye));
        optionItems.add(new OptionItem(ID_FACE_UNITY_REMOVE_DARK_CIRCLES, R.mipmap.ic_quheiyanquan, R.string.remove_dark_circles));
        optionItems.add(new OptionItem(ID_FACE_UNITY_BEAUTY_TOOTH, R.mipmap.ic_meiya, R.string.beauty_tooth));
        optionItems.add(new OptionItem(ID_FACE_UNITY_NASOLABIAL_FOLDS, R.mipmap.ic_qufalingwen, R.string.nasolabial_folds));
        return optionItems;
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

    public void updateValue(){
        setBeautyParamsValue("blur_level",BLUR_LEVEL_MAX*(map.get(ID_FACE_UNITY_BUFFING))/100);
        setBeautyParamsValue("color_level",COLOR_LEVEL_MAX*(map.get(ID_FACE_UNITY_WHITEN))/100);
        setBeautyParamsValue("red_level",RED_LEVEL_MAX*map.get(ID_FACE_UNITY_RUDDY)/100);
        setBeautyParamsValue("remove_pouch_strength",REMOVE_POUCH_STRENGTH_MAX*map.get(ID_FACE_UNITY_REMOVE_DARK_CIRCLES)/100);
        setBeautyParamsValue("tooth_whiten",TOOTH_WHITEN_MAX*map.get(ID_FACE_UNITY_BEAUTY_TOOTH)/100);
        setBeautyParamsValue("remove_nasolabial_folds_strength",REMOVE_NASOLABIAL_FOLDS_STRENGTH_MAX*(map.get(ID_FACE_UNITY_NASOLABIAL_FOLDS))/100);
        setFaceShape("cheek_thinning",THINFACE_MAX*(map.get(ID_FACE_UNITY_THIN_FACE))/100);
        setFaceShape("eye_enlarging",EYE_ENLARGING_MAX*map.get(ID_FACE_UNITY_BIG_EYE)/100);
    }

    private final float BLUR_LEVEL_MAX=6.0f;
    private final float COLOR_LEVEL_MAX=2.0f;
    private final float RED_LEVEL_MAX=2.0f;
    private final float REMOVE_POUCH_STRENGTH_MAX=1.0f;
    private final float TOOTH_WHITEN_MAX=1.0f;
    private final float THINFACE_MAX=1.0F;
    private final float EYE_ENLARGING_MAX=1.0F;
    private final float REMOVE_NASOLABIAL_FOLDS_STRENGTH_MAX=1.0f;
    public void setCurrentProgress(int progress) {
        map.put(selectedFeatureID, progress);
        if (selectedFeatureID == ID_FACE_UNITY_BUFFING) {
            setBeautyParamsValue("blur_level",BLUR_LEVEL_MAX*progress/100);
        } else if (selectedFeatureID == ID_FACE_UNITY_WHITEN) {
            setBeautyParamsValue("color_level",COLOR_LEVEL_MAX*progress/100);
        } else if (selectedFeatureID == ID_FACE_UNITY_RUDDY) {
            setBeautyParamsValue("red_level",RED_LEVEL_MAX*progress/100);
        } else if (selectedFeatureID == ID_FACE_UNITY_THIN_FACE) {
            setFaceShape("cheek_thinning",THINFACE_MAX*progress/100);
        } else if (selectedFeatureID == ID_FACE_UNITY_BIG_EYE) {
            setFaceShape("eye_enlarging",EYE_ENLARGING_MAX*progress/100);
        } else if (selectedFeatureID == ID_FACE_UNITY_REMOVE_DARK_CIRCLES) {
            setBeautyParamsValue("remove_pouch_strength",REMOVE_POUCH_STRENGTH_MAX*progress/100);
        } else if (selectedFeatureID == ID_FACE_UNITY_BEAUTY_TOOTH) {
            setBeautyParamsValue("tooth_whiten",TOOTH_WHITEN_MAX*progress/100);
        } else if (selectedFeatureID == ID_FACE_UNITY_NASOLABIAL_FOLDS) {
            setBeautyParamsValue("remove_nasolabial_folds_strength",REMOVE_NASOLABIAL_FOLDS_STRENGTH_MAX*progress/100);
        }
    }


}
