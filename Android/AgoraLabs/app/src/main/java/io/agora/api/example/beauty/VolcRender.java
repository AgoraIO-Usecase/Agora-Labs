package io.agora.api.example.beauty;

import android.content.Context;
import android.util.Log;
import io.agora.api.example.Constants;
import io.agora.api.example.R;
import io.agora.api.example.model.OptionItem;
import io.agora.rtc2.RtcEngineEx;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class VolcRender {
    private final String TAG="AgoraLab";
    private final int ID_VOLC_BUFFING = 9;
    private final int ID_VOLC_WHITEN = 10;
    private final int ID_VOLC_SHARPEN = 11;
    private final int ID_VOLC_THIN_FACE = 12;
    private final int ID_VOLC_NARROW_FACE = 13;
    private final int ID_VOLC_CHEEKBONE = 14;
    private final int ID_VOLC_LOWER_JAW = 15;
    private final int ID_VOLC_EYE = 16;
    private final int ID_VOLC_NOSE = 17;
    private final int ID_VOLC_MOUTH = 18;
    private final int ID_VOLC_LOVE_BEANS = 19;
    private final int ID_VOLC_WHITE_TEA = 20;
    private final int ID_VOLC_WHITE = 21;
    private final int ID_VOLC_GLACIER = 22;
    private final int ID_VOLC_CORE = 23;
    private final int ID_VOLC_BROKEN_EYE_BROW = 24;
    private final int ID_VOLC_KOREAN = 25;
    private final int ID_VOLC_NO_CUBS = 26;
    private final int ID_VOLC_WARM_MAN = 27;
    private final int ID_VOLC_OCCIDENT = 28;
    private final int ID_VOLC_LOOK = 29;
    private final int ID_VOLC_PROFOUND = 30;
    private final int ID_VOLC_SWEET = 31;
    private final int ID_VOLC_WARMTH = 32;
    private final int ID_VOLC_GRACE = 33;
    private final int ID_VOLC_VITALITY = 34;
    private final int ID_VOLC_TEXTURE = 35;
    private final int ID_VOLC_BABE = 36;
    private final int ID_VOLC_CONFESSION_TANABATA = 37;
    private final int ID_VOLC_DIRT_GIRL = 38;
    private final int ID_VOLC_ELECTRIC_GUITAR = 39;
    private final int ID_VOLC_PRINCESS_MASK = 40;
    private final int ID_VOLC_BLACK_CAT_GLASSES = 41;
    private final int ID_VOLC_MASK = 42;
    private final int ID_VOLC_LUCK_CAT = 43;
    private final int ID_VOLC_GOOD_MOOD = 44;
    private final int ID_VOLC_DINOSAUR_TEST = 45;
    private final int ID_VOLC_GENTLEMAN = 46;
    private final int ID_VOLC_DANCING_TURKEY = 47;
    private final int ID_VOLC_PIG_HEAD_PIG_EARS = 48;
    private final int ID_VOLC_PURPLE_CHARM = 49;
    private final int ID_VOLC_LIP_STICK = 50;
    private final int ID_VOLC_BLUSH = 51;
    private final int ID_VOLC_TRIMMING = 52;
    private final int ID_VOLC_EYELASH = 53;
    private final int ID_VOLC_EYEBROW = 54;
    private final int ID_VOLC_CIRCLE_LENSES = 55;

    private Context context;
    private RtcEngineEx rtcEngine;
    private int selectedFeatureID=-1;
    private Map<Integer,Integer> map=new HashMap<>();
    public VolcRender(Context context, RtcEngineEx rtcEngine) {
        this.context = context;
        this.rtcEngine = rtcEngine;
    }

    public void disableExtension(){
        rtcEngine.enableExtension("ByteDance", "Effect", false);
    }

    public void enableExtension(){
        rtcEngine.enableExtension("ByteDance", "Effect", true);
    }


    public void initExtension() {

        File destFile = context.getExternalFilesDir("assets");

        File licensePath = new File(
            destFile,
            "bytedance/Resource/LicenseBag.bundle/" + Constants.mLicenseName);
        // Check license
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("licensePath", licensePath.getPath());
            setExtensionProperty("bef_effect_ai_check_license", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        File strModelDir = new File(destFile, "bytedance/Resource/ModelResource.bundle");
        // Init
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("strModelDir", strModelDir.getPath());
            jsonObject.put("deviceName", "");
            setExtensionProperty("bef_effect_ai_init", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        // Enable composer and sticker
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mode", 1);
            jsonObject.put("orderType", 0);
            setExtensionProperty("bef_effect_ai_composer_set_mode", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void setExtensionProperty(String key, String property) {
        rtcEngine.setExtensionProperty("ByteDance", "Effect", key, property);
    }

    public List<List<OptionItem>> generatorOptionItems() {
        List<List<OptionItem>> data = new ArrayList<>();
        List<OptionItem> beautySkinOptionItems = new ArrayList<>();
        beautySkinOptionItems.add(new OptionItem(ID_VOLC_BUFFING, R.mipmap.ic_mopi, R.string.buffing));
        beautySkinOptionItems.add(new OptionItem(ID_VOLC_WHITEN, R.mipmap.ic_meibai, R.string.whiten));
        beautySkinOptionItems.add(new OptionItem(ID_VOLC_SHARPEN, R.mipmap.ic_sharpen, R.string.sharpen));
        data.add(beautySkinOptionItems);
        List<OptionItem> microPlasticOptionItems = new ArrayList<>();
        microPlasticOptionItems.add(new OptionItem(ID_VOLC_THIN_FACE, R.mipmap.ic_shoulian, R.string.thin_face));
        microPlasticOptionItems.add(new OptionItem(ID_VOLC_NARROW_FACE, R.mipmap.ic_mopi, R.string.narrow_face));
        microPlasticOptionItems.add(new OptionItem(ID_VOLC_CHEEKBONE, R.mipmap.ic_mopi, R.string.cheekbone));
        microPlasticOptionItems.add(new OptionItem(ID_VOLC_LOWER_JAW, R.mipmap.ic_mopi, R.string.lower_jaw));
        microPlasticOptionItems.add(new OptionItem(ID_VOLC_EYE, R.mipmap.ic_mopi, R.string.eye));
        microPlasticOptionItems.add(new OptionItem(ID_VOLC_NOSE, R.mipmap.ic_mopi, R.string.nose));
        microPlasticOptionItems.add(new OptionItem(ID_VOLC_MOUTH, R.mipmap.ic_mopi, R.string.mouth));
        data.add(microPlasticOptionItems);
        List<OptionItem> styleMakeupOptionItems = new ArrayList<>();
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_LOVE_BEANS, R.mipmap.ic_mopi, R.string.love_beans));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_WHITE_TEA, R.mipmap.ic_mopi, R.string.white_tea));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_WHITE, R.mipmap.ic_mopi, R.string.white));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_GLACIER, R.mipmap.ic_mopi, R.string.glacier));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_CORE, R.mipmap.ic_mopi, R.string.core));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_BROKEN_EYE_BROW, R.mipmap.ic_mopi, R.string.broken_eyebrow));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_KOREAN, R.mipmap.ic_mopi, R.string.korean));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_NO_CUBS, R.mipmap.ic_mopi, R.string.no_cubs));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_WARM_MAN, R.mipmap.ic_mopi, R.string.warm_man));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_OCCIDENT, R.mipmap.ic_mopi, R.string.occident));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_LOOK, R.mipmap.ic_mopi, R.string.look));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_PROFOUND, R.mipmap.ic_mopi, R.string.profound));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_SWEET, R.mipmap.ic_mopi, R.string.sweet));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_WARMTH, R.mipmap.ic_mopi, R.string.warmth));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_GRACE, R.mipmap.ic_mopi, R.string.grace));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_VITALITY, R.mipmap.ic_mopi, R.string.vitality));
        styleMakeupOptionItems.add(new OptionItem(ID_VOLC_TEXTURE, R.mipmap.ic_mopi, R.string.texture));
        data.add(styleMakeupOptionItems);
        List<OptionItem> stickers = new ArrayList<>();
        stickers.add(new OptionItem(ID_VOLC_BABE, R.mipmap.ic_mopi, R.string.babe));
        stickers.add(new OptionItem(ID_VOLC_CONFESSION_TANABATA, R.mipmap.ic_mopi, R.string.confession_tanabata));
        stickers.add(new OptionItem(ID_VOLC_DIRT_GIRL, R.mipmap.ic_mopi, R.string.dirt_girl));
        stickers.add(new OptionItem(ID_VOLC_ELECTRIC_GUITAR, R.mipmap.ic_mopi, R.string.electric_guitar));
        stickers.add(new OptionItem(ID_VOLC_PRINCESS_MASK, R.mipmap.ic_mopi, R.string.princess_mask));
        stickers.add(new OptionItem(ID_VOLC_BLACK_CAT_GLASSES, R.mipmap.ic_mopi, R.string.black_cat_glasses));
        stickers.add(new OptionItem(ID_VOLC_MASK, R.mipmap.ic_mopi, R.string.mask));
        stickers.add(new OptionItem(ID_VOLC_LUCK_CAT, R.mipmap.ic_mopi, R.string.luck_cat));
        stickers.add(new OptionItem(ID_VOLC_GOOD_MOOD, R.mipmap.ic_mopi, R.string.good_mood));
        stickers.add(new OptionItem(ID_VOLC_DINOSAUR_TEST, R.mipmap.ic_mopi, R.string.dinosaur_test));
        stickers.add(new OptionItem(ID_VOLC_GENTLEMAN, R.mipmap.ic_mopi, R.string.gentleman));
        stickers.add(new OptionItem(ID_VOLC_DANCING_TURKEY, R.mipmap.ic_mopi, R.string.dancing_turkey));
        stickers.add(new OptionItem(ID_VOLC_PIG_HEAD_PIG_EARS, R.mipmap.ic_mopi, R.string.pig_head_pig_ears));
        stickers.add(new OptionItem(ID_VOLC_PURPLE_CHARM, R.mipmap.ic_mopi, R.string.purple_charm));
        data.add(stickers);

        List<OptionItem> makeups = new ArrayList<>();

        makeups.add(new OptionItem(ID_VOLC_LIP_STICK, R.mipmap.ic_mopi, R.string.lip_stick));
        makeups.add(new OptionItem(ID_VOLC_BLUSH, R.mipmap.ic_mopi, R.string.blush));
        makeups.add(new OptionItem(ID_VOLC_TRIMMING, R.mipmap.ic_mopi, R.string.trimming));
        makeups.add(new OptionItem(ID_VOLC_EYELASH, R.mipmap.ic_mopi, R.string.eyelash));
        makeups.add(new OptionItem(ID_VOLC_EYEBROW, R.mipmap.ic_mopi, R.string.eyebrow));
        makeups.add(new OptionItem(ID_VOLC_CIRCLE_LENSES, R.mipmap.ic_mopi, R.string.circle_lenses));
        data.add(makeups);
        return data;
    }


    public void setSelectedFeatureID(int selectedFeatureID) {
        this.selectedFeatureID = selectedFeatureID;
        if(selectedFeatureID==ID_VOLC_LOVE_BEANS){
            setStyleMakeup("aidou");
        }else if(selectedFeatureID==ID_VOLC_WHITE_TEA){
            setStyleMakeup("baicha");
        }else if(selectedFeatureID==ID_VOLC_WHITE){
            setStyleMakeup("baixi");
        }else if(selectedFeatureID==ID_VOLC_GLACIER){
            setStyleMakeup("bingchuan");
        }else if(selectedFeatureID==ID_VOLC_CORE){
            setStyleMakeup("cwei");
        }else if(selectedFeatureID==ID_VOLC_BROKEN_EYE_BROW){
            setStyleMakeup("duanmei");
        }else if(selectedFeatureID==ID_VOLC_KOREAN){
            setStyleMakeup("hanxi");
        }else if(selectedFeatureID==ID_VOLC_NO_CUBS){
            setStyleMakeup("meiyouxiaoxiong");
        }else if(selectedFeatureID==ID_VOLC_WARM_MAN){
            setStyleMakeup("nuannan");
        }else if(selectedFeatureID==ID_VOLC_OCCIDENT){
            setStyleMakeup("oumei");
        }else if(selectedFeatureID==ID_VOLC_LOOK){
            setStyleMakeup("qise");
        }else if(selectedFeatureID==ID_VOLC_PROFOUND){
            setStyleMakeup("shensui");
        }else if(selectedFeatureID==ID_VOLC_SWEET){
            setStyleMakeup("tianmei");
        }else if(selectedFeatureID==ID_VOLC_WARMTH){
            setStyleMakeup("wennuan");
        }else if(selectedFeatureID==ID_VOLC_GRACE){
            setStyleMakeup("youya");
        }else if(selectedFeatureID==ID_VOLC_VITALITY){
            setStyleMakeup("yuanqi");
        }else if(selectedFeatureID==ID_VOLC_TEXTURE){
            setStyleMakeup("zhigan");
        }
    }

    public int getProgress(int featureID) {
        if(featureID==-1){
            return 0;
        }
        if(map.containsKey(featureID)) {
            return map.get(featureID);
        }
        return 0;
    }

    public void setCurrentProgress(int progress) {
        map.put(selectedFeatureID, progress);
        Log.d(TAG,"------setCurrentProgress:"+progress+"  selectedFeatureID:"+selectedFeatureID);
        if (selectedFeatureID == ID_VOLC_BUFFING) {
            setBeautySkin("smooth",1.0f*progress/100);
        }else if(selectedFeatureID==ID_VOLC_WHITEN){
            setBeautySkin("whiten",1.0f*progress/100);
        }else if(selectedFeatureID==ID_VOLC_SHARPEN){
            setBeautySkin("sharp",1.0f*progress/100);
        }
        else if(selectedFeatureID==ID_VOLC_THIN_FACE){
            setMicroPlastic("Internal_Deform_Overall",1.0f*progress/100);
        }else if(selectedFeatureID==ID_VOLC_NARROW_FACE){
            setMicroPlastic("Internal_Deform_CutFace",1.0f*progress/100);
        }else if(selectedFeatureID==ID_VOLC_CHEEKBONE){
            setMicroPlastic("Internal_Deform_Zoom_Cheekbone",1.0f*progress/100);
        }else if(selectedFeatureID==ID_VOLC_LOWER_JAW){
            setMicroPlastic("Internal_Deform_Zoom_Jawbone",1.0f*progress/100);
        }else if(selectedFeatureID==ID_VOLC_EYE){
            setMicroPlastic("Internal_Deform_Eye",1.0f*progress/100);
        }else if(selectedFeatureID==ID_VOLC_NOSE){
            setMicroPlastic("Internal_Deform_Nose",1.0f*progress/100);
        }else if(selectedFeatureID==ID_VOLC_MOUTH){
            setMicroPlastic("Internal_Deform_ZoomMouth",1.0f*progress/100);
        }
        else if(selectedFeatureID==ID_VOLC_BABE){
            setSticker("baby_gan");
        }else if(selectedFeatureID==ID_VOLC_CONFESSION_TANABATA){
            setSticker("biaobaiqixi");
        }else if(selectedFeatureID==ID_VOLC_DIRT_GIRL){
            setSticker("chitushaonv");
        }else if(selectedFeatureID==ID_VOLC_ELECTRIC_GUITAR){
            setSticker("dianjita");
        }else if(selectedFeatureID==ID_VOLC_PRINCESS_MASK){
            setSticker("gongzhumianju");
        }else if(selectedFeatureID==ID_VOLC_BLACK_CAT_GLASSES){
            setSticker("heimaoyanjing");
        }else if(selectedFeatureID==ID_VOLC_MASK){
            setSticker("jiamian");
        }else if(selectedFeatureID==ID_VOLC_LUCK_CAT){
            setSticker("zhaocaimao");
        }else if(selectedFeatureID==ID_VOLC_GOOD_MOOD){
            setSticker("meihaoxinqing");
        }else if(selectedFeatureID==ID_VOLC_DINOSAUR_TEST){
            setSticker("konglongceshi");
        }else if(selectedFeatureID==ID_VOLC_GENTLEMAN){
            setSticker("shenshi");
        }else if(selectedFeatureID==ID_VOLC_DANCING_TURKEY){
            setSticker("tiaowuhuoji");
        }else if(selectedFeatureID==ID_VOLC_PIG_HEAD_PIG_EARS){
            setSticker("zhutouzhuer");
        }else if(selectedFeatureID==ID_VOLC_PURPLE_CHARM){
            setSticker("zisemeihuo");
        }

    }



    private void setSticker(String nodePath){
        setExtensionProperty("bef_effect_ai_set_effect", context.getExternalFilesDir("assets")+"/bytedance/Resource/StickerResource.bundle/stickers/"+nodePath);
    }
    private void setStyleMakeup(String nodePath){
        try {
            File destFile = context.getExternalFilesDir("assets");
            File strModelDir = new File(destFile, "bytedance/Resource/ComposeMakeup.bundle/ComposeMakeup/style_makeup/"+nodePath);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nodePath",strModelDir.getAbsolutePath());
            Log.d(TAG,"------volcItemSetParam:"+jsonObject.toString());
            setExtensionProperty("bef_effect_ai_composer_update_node", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }
    
    private void setMicroPlastic(String name,float value){
        try {
            File destFile = context.getExternalFilesDir("assets");
            File strModelDir = new File(destFile, "bytedance/Resource/ComposeMakeup.bundle/ComposeMakeup/reshape_lite");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nodePath",strModelDir.getAbsolutePath());
            jsonObject.put("nodeTag", name);
            DecimalFormat fnum= new DecimalFormat("##0.0");
            jsonObject.put("value", Float.valueOf(fnum.format(value)));
            Log.d(TAG,"------volcItemSetParam:"+jsonObject.toString());
            setExtensionProperty("bef_effect_ai_composer_update_node", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void setBeautySkin(String name,float value){
        try {
            File destFile = context.getExternalFilesDir("assets");
            File strModelDir = new File(destFile, "bytedance/Resource/ComposeMakeup.bundle/ComposeMakeup/beauty_Android_lite");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nodePath",strModelDir.getAbsolutePath());
            jsonObject.put("nodeTag", name);
            DecimalFormat fnum= new DecimalFormat("##0.0");
            jsonObject.put("value", Float.valueOf(fnum.format(value)));
            Log.d(TAG,"------volcItemSetParam:"+jsonObject.toString());
            setExtensionProperty("bef_effect_ai_composer_update_node", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }
}
