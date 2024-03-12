package io.agora.api.example.examples.algorithmic;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
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
import io.agora.api.example.common.widget.VideoFeatureMenu;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;
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
import io.agora.rtc2.video.SegmentationProperty;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import io.agora.rtc2.video.VirtualBackgroundSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class VirtualHeadgearFragment extends Fragment implements View.OnClickListener{
    private String TAG="metakitx";
    protected RtcEngine rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private FragmentVirtualHeadgearBinding binding;

    private final int ID_DOG=0;
    private final int ID_GIRL=1;
    private final int ID_MASK=2;

    private boolean aiEnabled=false;
    private String headMaskName="dog";

    private MenuItemAdapter menuItemAdapter;
    private final String[] PERMISSIONS = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    private final int REQUEST_CODE_PERMISSIONS = 1;
    private MetaEngineHandler metaEngineHandler;
    private VideoCanvas gVideoCanvas;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    super.handleMessage(msg);
            }
        }
    };

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding= FragmentVirtualHeadgearBinding.inflate(inflater,container,false);
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
        initView();
    }
    private boolean isEffectModeSetPending=false;
    private void enableAi(boolean on){
        if(!isMetaAssetsResourceReady()){
            Toast.makeText(getContext(),R.string.resource_preparing,Toast.LENGTH_SHORT).show();
            return;
        }
        if(on){
            if(!metaEngineHandler.isMetaInitialized()){
                metaEngineHandler.updateAssets();
                metaEngineHandler.initializeMeta();
            }else{
                updateAi();
            }
        }else {
            Log.i(TAG, "metakitx to leave meta");
            isEffectModeSetPending=false;
            metaEngineHandler.disableHeadmask();

        }
    }

    private void updateAi(){
        Log.d(TAG,"updateAi isHeadmaskAvailable:"+metaEngineHandler.isHeadmaskAvailable());
        if(!aiEnabled){
            return;
        }
        if(metaEngineHandler.isHeadmaskAvailable()){
            metaEngineHandler.switchHeadmask(headMaskName);
        }else{
            isEffectModeSetPending=true;
        }
    }

    @Override public void onStart() {
        super.onStart();
        requestMorePermissions();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"fragment onDestroy");
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
                Log.d(TAG,"initializeFinish");
                //metaEngineHandler.createScene(0);
            }

            @Override public void onUnityLoadFinish() {
                Log.d(TAG,"onUnityLoadFinish");
                metaEngineHandler.enterScene();
            }

            @Override public void onLoadSceneResp() {
                Log.d(TAG, "---onLoadSceneResp----");
                metaEngineHandler.setSelectedHeadmask(headMaskName);
                metaEngineHandler.setMetaFeatureMode(MetaEngineHandler.FeatureType.FeatureHeadmask);
            }

            @Override public void onRequestTextureResp() {
                Log.d(TAG, "onRequestTextureResp:" + isEffectModeSetPending);
                if (isEffectModeSetPending) {
                    isEffectModeSetPending = false;
                    metaEngineHandler.switchHeadmask(headMaskName);
                }
            }

            @Override public void onUnloadSceneResp() {
                Log.d(TAG,"onUnloadSceneResp");
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
                Log.d(TAG,"onEvent provider："+provider+" extension:"+extension+" key:" + key + ", value: " + value);
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
            rtcEngine.enableVideo();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }






    private void initView(){
        List<OptionItem> data=new ArrayList<>();
        data.add(new OptionItem(ID_DOG,R.mipmap.dog,R.string.dog));
        data.add(new OptionItem(ID_GIRL,R.mipmap.girl,R.string.girl));
        data.add(new OptionItem(ID_MASK,R.mipmap.mask,R.string.virtual_headgear_mask));
        menuItemAdapter=new MenuItemAdapter();
        menuItemAdapter.setData(data);
        menuItemAdapter.setSelectPos(0);
        menuItemAdapter.setOnItemClickListener((v, optionItem, position) -> onMenuItemSelected(optionItem,position));
        binding.cvOptions.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.cvOptions.setAdapter(menuItemAdapter);
        binding.menuControler.setOnClickListener(v -> {
            if(binding.cvOptions.getVisibility()==View.GONE){
                binding.cvOptions.setVisibility(View.VISIBLE);
            }else{
                binding.cvOptions.setVisibility(View.GONE);
            }
        });
        View.OnTouchListener listener=new View.OnTouchListener() {
            private float lastY;
            @Override public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    lastY=event.getY();
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    float y=event.getY();
                    if(y-lastY>10){
                        if(binding.cvOptions.getVisibility()!=View.GONE) {
                            binding.cvOptions.setVisibility(View.GONE);
                        }
                    }else if(lastY-y>2){
                        if(binding.cvOptions.getVisibility()!=View.VISIBLE) {
                            binding.cvOptions.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return false;
            }
        };
        binding.menuControler.setOnTouchListener(listener);


    }



    private void onMenuItemSelected(OptionItem item,int position){
        SystemUtil.vibrator(getContext());
         if(item.getId()==ID_DOG){
             headMaskName="dog";
        }else if(item.getId()==ID_GIRL){
             headMaskName="girlhead";
        }else if(item.getId()==ID_MASK){
             headMaskName="arkit";
        }
         updateAi();
        setSelectedMenus(item);
    }

    private void setSelectedMenus(OptionItem item){
        if(item==null){
            menuItemAdapter.clearSelected();
        }else {
            menuItemAdapter.setSelected(item,true);
        }
        menuItemAdapter.notifyDataSetChanged();
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
        Log.d(TAG,"fragment start");
        initializeEngine();
        startPreview();
        copyResource();
    }

    private void startPreview(){

        VideoEncoderConfiguration configuration=new VideoEncoderConfiguration();
        configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(720,1280);
        configuration.bitrate= 2260;
        configuration.frameRate=30;
        configuration.mirrorMode= VideoEncoderConfiguration.MIRROR_MODE_TYPE.MIRROR_MODE_AUTO;
        rtcEngine.setVideoEncoderConfiguration(configuration);

        SurfaceView view = RtcEngine.CreateRendererView(getContext());
        view.setZOrderMediaOverlay(false);
        gVideoCanvas=new VideoCanvas(view, VideoCanvas.RENDER_MODE_HIDDEN, 100);
        rtcEngine.setupLocalVideo(gVideoCanvas);
        rtcEngine.startPreview();
        UserManager.getInstance().addCameraCount();
        binding.mainContainer.addView(view);
        //metaEngineHandler.createMainSceneTextureView(binding.metaVideoTextureview);
        Log.d("AgoraLab","startPreview:"+RtcEngineEx.getSdkVersion());
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
