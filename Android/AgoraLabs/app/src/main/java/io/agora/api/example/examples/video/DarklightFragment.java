package io.agora.api.example.examples.video;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.databinding.FragmentDarkLightEnhancementBinding;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.ThreadUtils;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcConnection;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.LowLightEnhanceOptions;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

import static io.agora.rtc2.video.LowLightEnhanceOptions.LOW_LIGHT_ENHANCE_LEVEL_FAST;
import static io.agora.rtc2.video.LowLightEnhanceOptions.LOW_LIGHT_ENHANCE_LEVEL_HIGH_QUALITY;

public class DarklightFragment extends Fragment implements View.OnClickListener {
    private final String TAG="AgoraLab";

    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private RtcConnection rtcConnection;
    private FragmentDarkLightEnhancementBinding binding;
    private String channelName= String.valueOf((int) (Math.random() * 1000));
    private int senderUid =101;
    private int remoteUid=102;

    //private boolean performancePriority=true;



    private SurfaceView remoteView;
    private SurfaceView localView;


    private boolean darkLightEnhancementEnabled;
    private LowLightEnhanceOptions options=new LowLightEnhanceOptions();



    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding=FragmentDarkLightEnhancementBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }



    private void initView(){
        binding.ivBack.setOnClickListener(this);
        binding.ivSwitchCamera.setOnClickListener(this);

        binding.featureSwitch.setChecked(darkLightEnhancementEnabled);
        binding.featureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SystemUtil.vibrator(getContext());
            darkLightEnhancementEnabled =isChecked;
            updateDarkLightEnhancement();
        });


        binding.menuControler.setOnClickListener(v -> {
            if(binding.darkLightSettingGroup.getVisibility()==View.GONE){
                binding.darkLightSettingGroup.setVisibility(View.VISIBLE);
            }else{
                binding.darkLightSettingGroup.setVisibility(View.GONE);
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
                        if(binding.darkLightSettingGroup.getVisibility()!=View.GONE) {
                            binding.darkLightSettingGroup.setVisibility(View.GONE);
                        }
                    }else if(lastY-y>5){
                        if(binding.darkLightSettingGroup.getVisibility()!=View.VISIBLE) {
                            binding.darkLightSettingGroup.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return false;
            }
        };
        binding.menuControler.setOnTouchListener(listener);
        binding.featureSwitch.setOnTouchListener(listener);
        binding.tvPerformancePriority.setOnClickListener(this);
        binding.tvPictureQualityPriority.setOnClickListener(this);
        updateDarkLightEnhancement();
    }

    private void updateDarkLightEnhancement(){
        if(rtcEngine==null){
            return;
        }
        rtcEngine.setLowlightEnhanceOptions(darkLightEnhancementEnabled,options);
        if (darkLightEnhancementEnabled) {
            binding.tvPerformancePriority.setSelected(options.lowlightEnhanceLevel == LOW_LIGHT_ENHANCE_LEVEL_FAST);
            binding.tvPictureQualityPriority.setSelected(options.lowlightEnhanceLevel == LOW_LIGHT_ENHANCE_LEVEL_HIGH_QUALITY);
        } else {
            binding.tvPerformancePriority.setSelected(false);
            binding.tvPictureQualityPriority.setSelected(false);
        }
    }

    @Override public void onStart() {
        super.onStart();
        initializeEngine();
        startPreview();
        setupSend();
        setupReceiver();
    }

    protected void initializeEngine() {
        if(rtcEngine!=null){
            return;
        }
        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = getContext().getApplicationContext();
        config.mAppId = getString(R.string.agora_app_id);
        config.mChannelProfile = sceneMode;
        config.mEventHandler = new IRtcEngineEventHandler() {
        };
        config.mAudioScenario = Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT);
        config.mAreaCode = ((App)getActivity().getApplication()).getAreaCode();
        try {
            rtcEngine = (RtcEngineEx)RtcEngineEx.create(config);
            rtcEngine.disableAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override public void onDestroy() {
        super.onDestroy();
        rtcEngine.stopPreview();
        if(rtcEngine!=null){
            RtcEngineEx.destroy();
            rtcEngine=null;
        }
    }

    private void startPreview(){
        localView = new SurfaceView(getContext());
        localView.setZOrderMediaOverlay(false);
        rtcEngine.enableVideo();
        rtcEngine.setupLocalVideo(new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, senderUid));
        rtcEngine.startPreview();
        addView();
    }

    private void setupSend(){
        rtcConnection=new RtcConnection();
        rtcConnection.channelId=channelName;
        rtcConnection.localUid= senderUid;
        setVideoConfig();
        rtcEngine.enableVideo();
        rtcEngine.enableAudio();

        ChannelMediaOptions mediaOptions=new ChannelMediaOptions();
        mediaOptions.channelProfile= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        mediaOptions.clientRoleType= Constants.CLIENT_ROLE_BROADCASTER;
        mediaOptions.publishMicrophoneTrack = false;
        mediaOptions.publishCameraTrack = true;

        rtcEngine.joinChannelEx("", rtcConnection, mediaOptions, new IRtcEngineEventHandler() {

            @Override
            public void onUserJoined(int uid, int elapsed) {
                Log.d(TAG,"agora onUserJoined:" + uid);
            }

            @Override
            public void onUserOffline(final int uid, final int reason) {
                Log.d(TAG,"onUserOffline:" + uid);
                ThreadUtils.runOnUI(() -> {
                    if(remoteView!=null&&remoteView.getParent()!=null){
                        ((ViewGroup)remoteView.getParent()).removeAllViews();
                    }
                });
            }
        });
        addView();
    }

    private void setVideoConfig() {
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration();
        configuration.dimensions = new VideoEncoderConfiguration.VideoDimensions(960, 540);
        configuration.bitrate = 1450;
        configuration.frameRate = 15;
        rtcEngine.setVideoEncoderConfigurationEx(configuration, rtcConnection);
    }

    private void setupReceiver(){
        RtcConnection rtcc=new RtcConnection();
        rtcc.channelId=channelName;
        rtcc.localUid=remoteUid;

        ChannelMediaOptions mediaOptions=new ChannelMediaOptions();
        mediaOptions.channelProfile= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        mediaOptions.clientRoleType= Constants.CLIENT_ROLE_BROADCASTER;
        mediaOptions.publishMicrophoneTrack=false;

        rtcEngine.joinChannelEx("", rtcc, mediaOptions, new IRtcEngineEventHandler() {
            @Override
            public void onUserJoined(int uid, int elapsed) {
                ThreadUtils.runOnUI(() -> {
                    remoteView= new SurfaceView(getContext()) ;
                    rtcEngine.setupRemoteVideoEx(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN,1, uid),rtcc);
                    addView();
                });
            }
            @Override
            public void onUserOffline(final int uid, final int reason) {
                ThreadUtils.runOnUI(() -> {
                    if(remoteView!=null&&remoteView.getParent()!=null){
                        ((ViewGroup)remoteView.getParent()).removeAllViews();
                    }
                });
            }
        });
    }



    @Override public void onClick(View v) {
        SystemUtil.vibrator(getContext());
        if(v.getId()== R.id.iv_back){
            Navigation.findNavController(v).popBackStack();
        }else if(v.getId()==R.id.iv_switch_camera){
            rtcEngine.switchCamera();
        }else if(v.getId()==R.id.tv_performance_priority){
            options.lowlightEnhanceLevel=LOW_LIGHT_ENHANCE_LEVEL_FAST;
            updateDarkLightEnhancement();
        }else if(v.getId()==R.id.tv_picture_quality_priority){
            options.lowlightEnhanceLevel=LOW_LIGHT_ENHANCE_LEVEL_HIGH_QUALITY;
            updateDarkLightEnhancement();
        }
    }

    private void addView(){
        binding.mainContainer.removeAllViews();
        if(localView!=null) {
            binding.mainContainer.addView(localView);
            localView.setZOrderOnTop(false);
            localView.setZOrderMediaOverlay(false);
        }
    }

}
