package io.agora.api.example.examples.algorithmic;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.UserManager;
import io.agora.api.example.common.adapter.MenuItemAdapter;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;
import io.agora.api.example.databinding.FragmentBackground360Binding;
import io.agora.api.example.databinding.FragmentVirtualHeadgearBinding;
import io.agora.api.example.utils.AESUtils;
import io.agora.api.example.utils.FileUtils;
import io.agora.api.example.utils.MetaEngineHandler;
import io.agora.api.example.utils.PermissionUtils;
import io.agora.api.example.utils.SPUtils;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.ThreadUtils;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaExtensionObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Background360Fragment extends Fragment implements View.OnClickListener{
    private String TAG="metakitx";
    protected RtcEngine rtcEngine;
    private VideoCanvas gVideoCanvas;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private FragmentBackground360Binding binding;
    private boolean aiEnabled=false;
    private final String[] PERMISSIONS = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    private final int REQUEST_CODE_PERMISSIONS = 1;

    private MetaEngineHandler metaEngineHandler;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding= FragmentBackground360Binding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.ivBack.setOnClickListener(this);
        binding.ivSwitchCamera.setOnClickListener(this);
        binding.featureSwitch.setChecked(aiEnabled);
        binding.featureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SystemUtil.vibrator(getContext());
            aiEnabled =isChecked;
            enableAi(aiEnabled);
        });
    }

    private void enableAi(boolean on){
        if(!isMetaAssetsResourceReady()){
            Toast.makeText(getContext(),R.string.resource_preparing,Toast.LENGTH_SHORT).show();
            return;
        }
        if(on){
            if(!metaEngineHandler.isMetaInitialized()){
                metaEngineHandler.updateAssets();
                metaEngineHandler.initializeMeta();
            }else {
                metaEngineHandler.setMetaBGMode(MetaEngineHandler.BackgroundType.BGTypePano);
            }
        }else {
             metaEngineHandler.setMetaBGMode(MetaEngineHandler.BackgroundType.BGTypeNull);
        }
    }


    @Override public void onStart() {
        super.onStart();
        requestMorePermissions();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        if(rtcEngine!=null){
            if(metaEngineHandler.runningState>MetaEngineHandler.IMetaRunningState.idle){
                metaEngineHandler.leaveMetaScene();
            }else{
                rtcEngine.stopPreview();
                RtcEngine.destroy();
                rtcEngine=null;
            }
        }
    }


    protected void initializeEngine() {
        metaEngineHandler=new MetaEngineHandler(getActivity());
        metaEngineHandler.setOnMetaSceneLoadedListener(new MetaEngineHandler.OnMetaSceneLoadedListener() {
            @Override public void onInitializeFinish() {
                //Log.d(TAG,"initializeFinish");
                //metaEngineHandler.createScene(0);
            }

            @Override public void onUnityLoadFinish() {
                //Log.d(TAG,"onUnityLoadFinish");
                metaEngineHandler.enterScene();
            }

            @Override public void onLoadSceneResp() {
                //Log.d(TAG, "---onLoadSceneResp----");
                metaEngineHandler.setMetaFeatureMode(MetaEngineHandler.FeatureType.FeatureSpecialEffect);
            }

            @Override public void onRequestTextureResp() {
                //Log.d(TAG, "---onRequestTextureResp----");
                 metaEngineHandler.setMetaBGMode(MetaEngineHandler.BackgroundType.BGTypePano);
            }


            @Override public void onUnloadSceneResp() {
                //Log.d(TAG,"onUnloadSceneResp");
                mHandler.post(new Runnable() {
                    @Override public void run() {
                        metaEngineHandler.destory();
                    }
                });
            }

            @Override public void onUninitializeFinish() {
                mHandler.post(new Runnable() {
                    @Override public void run() {
                        RtcEngine.destroy();
                        rtcEngine=null;
                    }
                });
            }
        });

        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = getContext().getApplicationContext();
        config.mAppId = AESUtils.getS1(App.getInstance());
        config.mChannelProfile = sceneMode;
        config.mEventHandler = new IRtcEngineEventHandler() {
        };
        config.mAudioScenario = Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT);
        config.mAreaCode = ((App)getActivity().getApplication()).getAreaCode();
        config.mExtensionObserver=new IMediaExtensionObserver() {
            @Override public void onEvent(String provider, String extension, String key, String value) {
                //Log.d(TAG,"onEvent provider："+provider+" extension:"+extension+" key:" + key + ", value: " + value);
                metaEngineHandler.onEvent(provider,extension,key,value);
            }

            @Override public void onStarted(String provider, String extension) {
                metaEngineHandler.onStart(provider,extension);
            }

            @Override public void onStopped(String provider, String extension) {
                metaEngineHandler.onStop(provider,extension);
            }

            @Override public void onError(String provider, String extension, int error, String message) {
                metaEngineHandler.onError(provider,extension,error,message);
            }
        };
        config.addExtension("agora_face_capture_extension");
        config.addExtension("agora_metakit_extension");
        try {
            rtcEngine = RtcEngine.create(config);
            metaEngineHandler.setRtcEngine(rtcEngine);
            metaEngineHandler.enableSegmentation();
            rtcEngine.enableExtension("agora_video_filters_face_capture", "face_capture", true);
            rtcEngine.enableExtension("agora_video_filters_metakit", "metakit", true);

            rtcEngine.setChannelProfile(config.mChannelProfile);
            rtcEngine.setAudioProfile(Constants.AudioProfile.DEFAULT.ordinal());

            //metaEngineHandler.configSegmentation(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    @Override public void onClick(View v) {
        if(v.getId()== R.id.iv_back){
            Navigation.findNavController(v).popBackStack();
        }else if(v.getId()==R.id.iv_switch_camera){
            rtcEngine.switchCamera();
        }
    }

    private void requestMorePermissions() {
        PermissionUtils.checkMorePermissions(getActivity(), PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                start();
            }

            @Override
            public void onUserHasAlreadyTurnedDown(String... permission) {
                PermissionUtils.showExplainDialog(getActivity(),permission, (dialog, which) -> requestPermissions( PERMISSIONS, REQUEST_CODE_PERMISSIONS));
            }

            @Override
            public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                requestPermissions(PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            PermissionUtils.onRequestMorePermissionsResult(getActivity(), PERMISSIONS,
                new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        start();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {

                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        PermissionUtils.showToAppSettingDialog(getActivity());
                    }
                });
        }
    }

    private void start(){
        initializeEngine();
        startPreview();
        copyResource();
    }

    private void startPreview(){
        rtcEngine.enableVideo();
        rtcEngine.startPreview();

        VideoEncoderConfiguration configuration=new VideoEncoderConfiguration();
        configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(1280,720);
        configuration.bitrate= 2260;
        configuration.frameRate=30;
        configuration.mirrorMode= VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
        rtcEngine.setVideoEncoderConfiguration(configuration);

        SurfaceView localView = RtcEngine.CreateRendererView(getContext());
        localView.setZOrderMediaOverlay(false);
        gVideoCanvas=new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 100);
        rtcEngine.setupLocalVideo(gVideoCanvas);
        UserManager.getInstance().addCameraCount();
        binding.mainContainer.addView(localView);
        //Log.d("AgoraLab","startPreview:"+RtcEngine.getSdkVersion());
    }

    private String KEY_META_ASSETS_RESOURCE="meta_assets_resource";
    private boolean isMetaAssetsResourceReady() {
        int versionCode=SystemUtil.getVersionCode(App.getInstance());
        boolean resourceReady = SPUtils.getInstance(getContext(), "user").getBoolean(KEY_META_ASSETS_RESOURCE, false);
        int preVersioncode = SPUtils.getInstance(getContext(), "user").getInt("versionCode", 0);
        return resourceReady && (versionCode == preVersioncode);
    }
    private void copyResource(){
        ThreadUtils.runOnNonUI(() -> {
            if (!isMetaAssetsResourceReady()) {
                File file=new File(getContext().getExternalFilesDir("assets")+"/metaAssets");
                if(file.exists()){
                    FileUtils.deleteFile(file);
                }
                FileUtils.copyFilesFromAssets(App.getInstance(),"metaAssets",App.getInstance().getExternalFilesDir("assets")+"/metaAssets");
                FileUtils.copyFilesFromAssets(App.getInstance(),"metaFiles",App.getInstance().getExternalFilesDir("assets")+"/metaFiles");

                SPUtils.getInstance(getContext(), "user").put(KEY_META_ASSETS_RESOURCE, true);
                SPUtils.getInstance(getContext(), "user").put("versionCode", SystemUtil.getVersionCode(App.getInstance()));
            }
        });
    }


}
