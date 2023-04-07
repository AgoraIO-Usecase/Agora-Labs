package io.agora.api.example;

import android.app.Application;

import io.agora.rtc2.RtcEngineConfig;

public class App extends Application {

    private String areaCodeStr = "GLOBAL";
    private static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

    public static App getInstance() {
        return instance;
    }



    public int getAreaCode() {
        if ("CN".equals(areaCodeStr)) {
            return RtcEngineConfig.AreaCode.AREA_CODE_CN;
        } else if ("NA".equals(areaCodeStr)) {
            return RtcEngineConfig.AreaCode.AREA_CODE_NA;
        } else if ("EU".equals(areaCodeStr)) {
            return RtcEngineConfig.AreaCode.AREA_CODE_EU;
        } else if ("AS".equals(areaCodeStr)) {
            return RtcEngineConfig.AreaCode.AREA_CODE_AS;
        } else if ("JP".equals(areaCodeStr)) {
            return RtcEngineConfig.AreaCode.AREA_CODE_JP;
        } else if ("IN".equals(areaCodeStr)) {
            return RtcEngineConfig.AreaCode.AREA_CODE_IN;
        } else {
            return RtcEngineConfig.AreaCode.AREA_CODE_GLOB;
        }
    }


}
