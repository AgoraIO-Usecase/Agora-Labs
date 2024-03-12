package io.agora.api.example.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import io.agora.meta.renderer.unity.AgoraAvatarView;
import io.agora.rtc2.Constants;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.SegmentationProperty;
import io.agora.rtc2.video.VirtualBackgroundSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

public class MetaEngineHandler implements AGExtensionHandler{
    private static final String TAG ="metakitx";
    private final Activity context;
    private RtcEngine rtcEngine;
    public int runningState = IMetaRunningState.idle;
    public int currentSceneIdx = 0;
    public String currentAssetPath = "";
    private boolean isMetaInit = false;
    static private final int initial_flag = -1;
    private  int width=720;
    private int height=1280;

    public class FeatureType{
        public final static int FeatureAvatar = 0;
        public final static int FeatureHeadmask = 1 ;
        public final static int FeatureSpecialEffect = 2;
    };


    public class  BackgroundType{
        public final static int BGTypePano = 0;
        public final static int BGType2D = 1;
        public final static int BGType3D = 2;
        public final static int BGTypeNull = 3;
    };
    public class SpecialEffectType{
        public final static int SEType3DLight  = 0;
        public final static int SETypeRipple =1;
        public final static int SETypeAurora =2;
        public final static int SETypeFlame = 3;
        public final static int SETypeAmbLight = 4;
        public final static int SETypeAdvLight = 5;
    };

    public class IMetaRunningState {
        public final static int idle = 0;
        public final static int initialized = 1;
        public final static int unityLoaded = 2;
        public final static int sceneLoaded = 3;
        public final static int requestTextureResp=4;
        public final static int sceneUnloaded = 5;
        public final static int uninitializeFinish=6;
    };

    class RenderQualityLevel{
        final static int Low  = 0;
        final static int Middle =1;
        final static int High =2;
    };

    private int curFeatrueType;
    private String currentHeadmask ;
    private String defaultAvatar;


    static {
        System.loadLibrary("apm-plugin-video");
    }
    public MetaEngineHandler(Activity context) {
        this.context = context;
    }

    public void updateAssets(){
        currentAssetPath = context.getExternalFilesDir("assets")+"/metaAssets";
    }



    public native long getTextureViewHandler(AgoraAvatarView view);
    public native long getTextureViewHandler(TextureView view);
    public native long getContextHandler(Activity context);
    public native void destroyHandles();

    @Override public void onStart(String provider, String ext) {
        if (!provider.equals("agora_video_filters_metakit") || !ext.equals("metakit")) return;
        Log.i("[MetaKit]", "onStart");
    }

    @Override public void onStop(String provider, String ext) {
        if (!provider.equals("agora_video_filters_metakit") || !ext.equals("metakit")) return;
        Log.i("[MetaKit]", "onStop");
    }
    private OnMetaSceneLoadedListener onMetaSceneLoadedListener;

    public OnMetaSceneLoadedListener getOnMetaSceneLoadedListener() {
        return onMetaSceneLoadedListener;
    }

    public void setOnMetaSceneLoadedListener(
        OnMetaSceneLoadedListener onMetaSceneLoadedListener) {
        this.onMetaSceneLoadedListener = onMetaSceneLoadedListener;
    }

    public interface OnMetaSceneLoadedListener {
        void onInitializeFinish();
        void onUnityLoadFinish();
        void onLoadSceneResp();
        //void onAddSceneViewResp();
        void onRequestTextureResp();
        void onUnloadSceneResp();
        void onUninitializeFinish();
    }
    @Override public void onEvent(String provider, String ext, String key, String msg) {
        if (!provider.equals("agora_video_filters_metakit") || !ext.equals("metakit")) return;
        Log.i(TAG, "provider:"+provider+" onEvent: " + key + ", msg: " + msg);
        switch(key) {
            case "initializeFinish":
                runningState = IMetaRunningState.initialized;
                if(onMetaSceneLoadedListener!=null){
                    onMetaSceneLoadedListener.onInitializeFinish();
                }
                break;
            case "unityLoadFinish":
                runningState = IMetaRunningState.unityLoaded;
                if(onMetaSceneLoadedListener!=null){
                    onMetaSceneLoadedListener.onUnityLoadFinish();
                }
                break;
            case "loadSceneResp":
                runningState = IMetaRunningState.sceneLoaded;
                if(onMetaSceneLoadedListener!=null){
                    onMetaSceneLoadedListener.onLoadSceneResp();
                }
                break;
            case "setAvatarResp":
                break;
            case "requestTextureResp":
                runningState = IMetaRunningState.requestTextureResp;
                if(onMetaSceneLoadedListener!=null){
                    onMetaSceneLoadedListener.onRequestTextureResp();
                }
                break;
            case "unloadSceneResp":
                runningState = IMetaRunningState.sceneUnloaded;
                if(onMetaSceneLoadedListener!=null){
                    onMetaSceneLoadedListener.onUnloadSceneResp();
                }
                break;
            case "uninitializeFinish":
                runningState = IMetaRunningState.uninitializeFinish;
                if(onMetaSceneLoadedListener!=null){
                    onMetaSceneLoadedListener.onUninitializeFinish();
                }
                break;
        }
    }

    @Override public void onError(String provider, String ext, int key, String msg) {
        if (!provider.equals("agora_video_filters_metakit") || !ext.equals("metakit")) return;
        Log.i(TAG, "onError: " + key + ", msg: " + msg);
    }

    public void requestTextureAvatar(String avatar) {
        if (runningState < IMetaRunningState.unityLoaded) {
            return;
        }

        int resolutionW = width;
        int resolutionH = height;
        int index = 0;
        JSONObject valueObj = new JSONObject();
        try {
            valueObj.put("index", index);
            valueObj.put("enable",true);
            JSONObject configObj = new JSONObject();
            configObj.put("width", resolutionW);
            configObj.put("height", resolutionH);
            JSONObject extraObj = new JSONObject();
            extraObj.put("sceneIndex",0);
            extraObj.put("avatarMode",0);
            extraObj.put("avatar", avatar);
            extraObj.put("userId","123");
            configObj.put("extraInfo", extraObj.toString());
            valueObj.put("config", configObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit",
                "metakit", "requestTexture", valueObj.toString());
        }
    }

    public void switchHeadmask(String headmask){
        int index = 0;
        JSONObject valueObj = new JSONObject();
        try {
            valueObj.put("mode", 1);
            valueObj.put("avatar", headmask);
            valueObj.put("index", index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rtcEngine.setExtensionProperty("agora_video_filters_metakit", "metakit", "switchTextureAvatarMode", valueObj.toString());
    }
    public void requestTextureHeadmask(String  headmask) {
        if (runningState < IMetaRunningState.unityLoaded) {
            return;
        }

        int resolutionW = width;
        int resolutionH = height;
        int index = 0;
        JSONObject valueObj = new JSONObject();
        try {
            valueObj.put("index", index);
            valueObj.put("enable",true);
            JSONObject configObj = new JSONObject();
            configObj.put("width", resolutionW);
            configObj.put("height", resolutionH);
            JSONObject extraObj = new JSONObject();
            extraObj.put("sceneIndex",0);
            extraObj.put("avatarMode",1);
            extraObj.put("avatar", headmask);
            extraObj.put("userId","100");
            configObj.put("extraInfo", extraObj.toString());
            valueObj.put("config", configObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit",
                "metakit", "requestTexture", valueObj.toString());
        }
    }

    public void requestTextureVB() {
        if (runningState < IMetaRunningState.unityLoaded) {
            return;
        }

        int resolutionW = width;
        int resolutionH = height;
        int index = 0;
        JSONObject valueObj = new JSONObject();
        try {
            valueObj.put("index", index);
            valueObj.put("enable",true);
            JSONObject configObj = new JSONObject();
            configObj.put("width", resolutionW);
            configObj.put("height", resolutionH);
            JSONObject extraObj = new JSONObject();
            extraObj.put("sceneIndex", 0);
            extraObj.put("avatarMode",2);
            extraObj.put("backgroundEffect",true);
            extraObj.put("userId","100");
            configObj.put("extraInfo", extraObj.toString());
            valueObj.put("config", configObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit",
                "metakit", "requestTexture", valueObj.toString());
        }
    }


    public void setRtcEngine(RtcEngine engine) {
        rtcEngine = engine;
    }

    public boolean isMetaInitialized(){
        return isMetaInit;
    }
    public void initializeMeta() {
        if (isMetaInit == true)
            return;
        if (rtcEngine == null) {
            return;
        }
        //Log.d(TAG,"---initializeMeta---");
        rtcEngine.registerExtension("agora_video_filters_face_capture", "face_capture", Constants.MediaSourceType.PRIMARY_CAMERA_SOURCE);
        rtcEngine.enableExtension("agora_video_filters_face_capture", "face_capture", true);
        rtcEngine.setExtensionProperty("agora_video_filters_face_capture","face_capture", "authentication_information",
            "{\"company_id\":\"agoraDemo\"," +
                "\"license\":\"" +
                "PFeVRXMZ1hJd075NSZtrZD3tYYl154QTs1Ui2i5ztvOcgOhklZcZdl2f5dYmf6GuLUebbBs1xp5I" +
                "crywJ+NaZ8ncYAfekfg3oR34cYJ8Pe3z4EVzw+CshGOfL0hcSYmtXjkXxG298c64+SGZdP6/UBVJ" +
                "/wIOYJRMgEUTBTqewcQ=\"}", Constants.MediaSourceType.PRIMARY_CAMERA_SOURCE);
        JSONObject valueObj = new JSONObject();
        try {
            long address = getContextHandler(context);
            valueObj.put("activityContext", String.valueOf(address));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rtcEngine.setExtensionProperty("agora_video_filters_metakit", "metakit", "setActivityContext", valueObj.toString());
        rtcEngine.setExtensionProperty("agora_video_filters_metakit", "metakit", "initialize","{}");

        isMetaInit = true;
        curFeatrueType = initial_flag;
        currentHeadmask = "dog";
        defaultAvatar = "girl";

    }

    public void enableSegmentation(){
        Log.i(TAG, "metakitx enableSegmentation" );
        VirtualBackgroundSource source = new VirtualBackgroundSource();
        source.backgroundSourceType = 0; //0 for background none; 1 for background color
        source.color = 0xFFFFFF;//0xffffff;
        source.source = "";
        source.blurDegree = 1;
        SegmentationProperty param = new SegmentationProperty();
        param.modelType = 1;  //1 for AI, 2 for green screen
        param.greenCapacity = 0.5f;
        int ret = rtcEngine.enableVirtualBackground(true, source, param, Constants.MediaSourceType.PRIMARY_CAMERA_SOURCE);
        Log.i(TAG,"metakitx enable seg ret: "  + ret);
    }

    public void enterScene() {

        JSONObject valueObj = new JSONObject();
        try {
            JSONObject sceneObj = new JSONObject();
            sceneObj.put("scenePath", currentAssetPath);
            JSONObject customObj = new JSONObject();
            customObj.put("sceneIndex", currentSceneIdx);
            valueObj.put("sceneInfo", sceneObj);
            valueObj.put("assetManifest", "");
            valueObj.put("userId", "100");
            valueObj.put("extraCustomInfo", customObj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d(TAG,"enterScene:"+ valueObj);
        //Log.d(TAG,"runningState:"+runningState);
        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit", "metakit",
                "loadScene", valueObj.toString());
        }
    }



    public void leaveMetaScene() {
        //Log.d(TAG," removeMainSceneView");
        //Log.d(TAG,"setExtensionProperty unloadScene");
        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit", "metakit", "unloadScene", "{}");
            //Log.d(TAG,"setExtensionProperty unloadScene exit");
        }
    }

    public void destory(){
        destroyHandles();
        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit", "metakit", "destroy", "{}");
            runningState = IMetaRunningState.idle;
        }

    }






    //private Handler handler=new Handler();
    public void disableHeadmask(){
        if (runningState < IMetaRunningState.unityLoaded) {
            return;
        }
        int index = 0;
        JSONObject valueObj = new JSONObject();
        try {
            valueObj.put("mode", 2);
            valueObj.put("avatar", currentHeadmask);
            valueObj.put("index", index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rtcEngine.setExtensionProperty("agora_video_filters_metakit", "metakit", "switchTextureAvatarMode", valueObj.toString());

    }

    public void setSelectedHeadmask(String headmask){
        //Log.d(TAG, "setSelectedHeadmask: " + headmask+" curFeatrueType:"+curFeatrueType+" currentHeadmask:"+currentHeadmask);
        currentHeadmask=headmask;
    }
    public void setMetaBGMode(int bgMode){
        //Log.d(TAG, "metakitx setMetaBGMode: " + bgMode);
        String filePath="";
        String mode="";
        String gyroState = "off";
        switch (bgMode){
            case BackgroundType.BGTypePano:
                mode = "tex360";
                filePath = context.getExternalFilesDir("assets")+"/metaFiles/bg_pano.jpg";
                gyroState = "on";
                break;
            case BackgroundType.BGType2D:
                mode = "pic2d";
                filePath = context.getExternalFilesDir("assets")+"/metaFiles/bg_2d.png";
                gyroState  = "off";
                break;
            case BackgroundType.BGType3D:
                mode = "env3d";
                filePath = "";
                gyroState = "on";
                break;
            case BackgroundType.BGTypeNull:
                mode = "off";
                filePath = "";
                gyroState = "off";
                break;
            default:
                break;
        }
        JSONObject picObj = new JSONObject();
        try {
            picObj.put("mode", mode);
            JSONObject configObj = new JSONObject();
            configObj.put("path",filePath);
            picObj.put("param", configObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit",
                "metakit", "setBGVideo", picObj.toString());
        }
        JSONObject gyroObj = new JSONObject();
        try {
            gyroObj.put("state",gyroState);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit",
                "metakit", "setCameraGyro", gyroObj.toString());
        }
    }


    public void setMetaFeatureMode(int featureMode){
        //Log.d(TAG, "metakitx setMetaFeatureMode: " + featureMode+" curFeatrueType："+ curFeatrueType);

        switch (featureMode) {
            case FeatureType.FeatureAvatar:
                requestTextureAvatar(defaultAvatar);
                break;
            case FeatureType.FeatureHeadmask:
                requestTextureHeadmask(currentHeadmask);
                break;
            case FeatureType.FeatureSpecialEffect:
                requestTextureVB();
                break;
            default:
                break;
        }
        curFeatrueType = featureMode;
    }

    private void configEffect3DLight(boolean onoff){
        //Log.d(TAG, "metakitx config 3DLight, onoff: " + onoff );
        JSONObject configObj = new JSONObject();
        try {
            configObj.put("id",1001);
            configObj.put("enable", onoff);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit",
                "metakit", "setEffectVideo", configObj.toString());
        }
    }


    private void configEffectRipple(boolean onoff){
        //Log.d(TAG, "metakitx configEffect Ripple, onoff: " + onoff );
        JSONObject configObj = new JSONObject();
        try {
            configObj.put("id",1002);
            configObj.put("enable", onoff);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit",
                "metakit", "setEffectVideo", configObj.toString());
        }
    }
    private void configEffectPolarLight(boolean onoff){
        //Log.d(TAG, "metakitx configEffect polar light, onoff: " + onoff );
        JSONObject configObj = new JSONObject();
        try {
            configObj.put("id",1003);
            configObj.put("enable", onoff);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit",
                "metakit", "setEffectVideo", configObj.toString());
        }
    }
    private void configEffectPurpleFlame(boolean onoff){
        //Log.d(TAG, "metakitx configEffect purple flame, onoff: " + onoff );
        JSONObject configObj = new JSONObject();
        try {
            configObj.put("id",2001);
            configObj.put("enable", onoff);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit",
                "metakit", "setEffectVideo", configObj.toString());
        }
    }
    private void configEffectAmbientLight(boolean onoff){
        //Log.d(TAG, "metakitx configEffect ambiemt light, onoff: " + onoff );
        JSONObject configObj = new JSONObject();
        try {
            configObj.put("id",3001);
            configObj.put("enable", onoff);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit",
                "metakit", "setEffectVideo", configObj.toString());
        }
    }


    private void configEffectAdvertLight(boolean onoff){
        //Log.d(TAG, "metakitx configEffect advert light, mode: " + onoff );
        JSONObject configObj = new JSONObject();
        try {
            configObj.put("id",3002);
            configObj.put("enable", onoff);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (rtcEngine != null) {
            rtcEngine.setExtensionProperty("agora_video_filters_metakit",
                "metakit", "setEffectVideo", configObj.toString());
        }
    }

    public boolean isEffectModeAvailable() {
        return (curFeatrueType == FeatureType.FeatureSpecialEffect) && (runningState >= IMetaRunningState.sceneLoaded);
    };

    public boolean isHeadmaskAvailable() {
        //Log.d(TAG,"curFeatrueType:"+curFeatrueType+" runningState:"+runningState);
        return (curFeatrueType == FeatureType.FeatureHeadmask) && (runningState >= IMetaRunningState.sceneLoaded);
    };
    public int configMetaBackgroundEffectMode(int effectMode, boolean onoff){
        //Log.d(TAG,"---configMetaBackgroundEffectMode---:"+effectMode+" onoff:"+onoff);
        switch(effectMode){
            case SpecialEffectType.SEType3DLight:
                //3D light
                configEffect3DLight(onoff);
                break;
            case SpecialEffectType.SETypeRipple:
                //ripple
                configEffectRipple(onoff);
                break;
            case SpecialEffectType.SETypeAurora:
                //polar light
                configEffectPolarLight(onoff);
                break;
            case SpecialEffectType.SETypeFlame:
                //purple flame
                configEffectPurpleFlame(onoff);
                break;
            case SpecialEffectType.SETypeAmbLight:
                //ambient light
                configEffectAmbientLight(onoff);
                break;
            case SpecialEffectType.SETypeAdvLight:
                //advert light
                configEffectAdvertLight(onoff);
                break;
            default:
                break;
        }
        return 0;
    }


    public void enableMetaSceneVideoDisplay(int displayId, boolean enable) {
        if (runningState < IMetaRunningState.sceneLoaded) {
            return;
        }
        if (rtcEngine != null) {
            JSONObject valueObj = new JSONObject();
            try {
                valueObj.put("displayId", displayId);
                valueObj.put("enable", enable);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            rtcEngine.setExtensionProperty("agora_video_filters_metakit", "metakit", "enableVideoDisplay", valueObj.toString());
        }
    }


}
