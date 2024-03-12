package io.agora.api.example.examples.video;

import android.Manifest;
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
import io.agora.api.example.common.TokenGenerator;
import io.agora.api.example.databinding.FragmentVideoNoiseReductionBinding;
import io.agora.api.example.utils.AESUtils;
import io.agora.api.example.utils.PermissionUtils;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.ThreadUtils;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcConnection;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoDenoiserOptions;
import io.agora.rtc2.video.VideoEncoderConfiguration;

import static io.agora.rtc2.video.VideoDenoiserOptions.VIDEO_DENOISER_LEVEL_FAST;
import static io.agora.rtc2.video.VideoDenoiserOptions.VIDEO_DENOISER_LEVEL_HIGH_QUALITY;
import static io.agora.rtc2.video.VideoDenoiserOptions.VIDEO_DENOISER_LEVEL_STRENGTH;

public class VideoNoiseReductionFragment extends Fragment implements View.OnClickListener{
    private final String TAG="AgoraLab";

    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private RtcConnection rtcConnection;
    private FragmentVideoNoiseReductionBinding binding;
    private String channelName= String.valueOf((int) (Math.random() * 1000));
    private int senderUid =101;
    private int remoteUid=102;

    private SurfaceView remoteView;
    private boolean videoNoiseReductionEnabled;

    private VideoDenoiserOptions options=new VideoDenoiserOptions();


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding=FragmentVideoNoiseReductionBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }



    private void initView(){
        binding.ivBack.setOnClickListener(this);
        binding.ivSwitchCamera.setOnClickListener(this);

        binding.featureSwitch.setChecked(videoNoiseReductionEnabled);
        binding.featureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SystemUtil.vibrator(getContext());
            videoNoiseReductionEnabled =isChecked;
            updateVideoNoiseReduction();
        });


        binding.menuControler.setOnClickListener(v -> {
            if(binding.videoNoiseReductionSettingGroup.getVisibility()==View.GONE){
                binding.videoNoiseReductionSettingGroup.setVisibility(View.VISIBLE);
            }else{
                binding.videoNoiseReductionSettingGroup.setVisibility(View.GONE);
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
                        if(binding.videoNoiseReductionSettingGroup.getVisibility()!=View.GONE) {
                            binding.videoNoiseReductionSettingGroup.setVisibility(View.GONE);
                        }
                    }else if(lastY-y>5){
                        if(binding.videoNoiseReductionSettingGroup.getVisibility()!=View.VISIBLE) {
                            binding.videoNoiseReductionSettingGroup.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return false;
            }
        };
        binding.menuControler.setOnTouchListener(listener);
        binding.featureSwitch.setOnTouchListener(listener);
        binding.tvEquilibriumMode.setOnClickListener(this);
        binding.tvLowPowerMode.setOnClickListener(this);
        binding.tvStrongNoiseReductionMode.setOnClickListener(this);
        updateVideoNoiseReduction();
    }

    private void updateVideoNoiseReduction(){
        if(rtcEngine==null){
            return;
        }
        rtcEngine.setVideoDenoiserOptions(videoNoiseReductionEnabled,options);
        if (videoNoiseReductionEnabled) {
            binding.tvLowPowerMode.setSelected(options.denoiserLevel==VIDEO_DENOISER_LEVEL_HIGH_QUALITY);
            binding.tvEquilibriumMode.setSelected(options.denoiserLevel==VIDEO_DENOISER_LEVEL_FAST);
            binding.tvStrongNoiseReductionMode.setSelected(options.denoiserLevel==VIDEO_DENOISER_LEVEL_STRENGTH);
        } else {
            binding.tvEquilibriumMode.setSelected(false);
            binding.tvStrongNoiseReductionMode.setSelected(false);
            binding.tvLowPowerMode.setSelected(false);
        }
    }

    @Override public void onStart() {
        super.onStart();
        requestMorePermissions();
    }

    private final String[] PERMISSIONS = new String[]{ Manifest.permission.CAMERA};
    private final int REQUEST_CODE_PERMISSIONS = 1;
    private void requestMorePermissions() {
        PermissionUtils.checkMorePermissions(getActivity(), PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                startVideoNoiseReduction();
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
                        startVideoNoiseReduction();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        /*
                        Toast.makeText(getActivity(), getString(R.string.need_permissions, Arrays.toString(permission)), Toast.LENGTH_SHORT)
                            .show();*/
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        /*
                        Toast.makeText(getActivity(), getString(R.string.need_permissions, Arrays.toString(permission)), Toast.LENGTH_SHORT)
                            .show();*/
                        PermissionUtils.showToAppSettingDialog(getActivity());
                    }
                });
        }
    }


    private void startVideoNoiseReduction(){
        initializeEngine();
        setupSend();
        setupReceiver();
    }

    protected void initializeEngine() {
        if(rtcEngine!=null){
            return;
        }
        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = getContext().getApplicationContext();
        config.mAppId = AESUtils.getS1(App.getInstance());
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
        if(rtcEngine!=null){
            rtcEngine.stopPreview();
            RtcEngineEx.destroy();
            rtcEngine=null;
        }
    }

    private void setVideoConfig() {
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration();
        configuration.dimensions = new VideoEncoderConfiguration.VideoDimensions(960, 540);
        configuration.bitrate = 1450;
        configuration.frameRate = 15;
        rtcEngine.setVideoEncoderConfigurationEx(configuration, rtcConnection);
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
        TokenGenerator.INSTANCE.generateToken(rtcConnection.channelId, String.valueOf(rtcConnection.localUid),
            TokenGenerator.TokenGeneratorType.token006, TokenGenerator.AgoraTokenType.rtc,
            token -> {
                joinChannel(token,rtcConnection,mediaOptions,new IRtcEngineEventHandler() {

                    @Override
                    public void onUserJoined(int uid, int elapsed) {
                        //Log.d(TAG, "agora onUserJoined:" + uid);
                    }

                    @Override
                    public void onUserOffline(final int uid, final int reason) {
                        //Log.d(TAG, "onUserOffline:" + uid);
                        ThreadUtils.runOnUI(() -> {
                            if (remoteView != null && remoteView.getParent() != null) {
                                ((ViewGroup) remoteView.getParent()).removeAllViews();
                            }
                        });
                    }
                });
                return null;
            }, exception -> null);
        addView();
    }

    private void setupReceiver(){
        RtcConnection rtcc=new RtcConnection();
        rtcc.channelId=channelName;
        rtcc.localUid=remoteUid;

        ChannelMediaOptions mediaOptions=new ChannelMediaOptions();
        mediaOptions.channelProfile= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        mediaOptions.clientRoleType= Constants.CLIENT_ROLE_BROADCASTER;
        mediaOptions.publishMicrophoneTrack=false;
        TokenGenerator.INSTANCE.generateToken(rtcc.channelId, String.valueOf(rtcc.localUid),
            TokenGenerator.TokenGeneratorType.token006, TokenGenerator.AgoraTokenType.rtc,
            token -> {
                joinChannel(token,rtcc,mediaOptions,new IRtcEngineEventHandler() {
                    @Override
                    public void onUserJoined(int uid, int elapsed) {
                        ThreadUtils.runOnUI(() -> {
                            remoteView = new SurfaceView(getContext());
                            rtcEngine.setupRemoteVideoEx(
                                new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid), rtcc);
                            addView();
                        });
                    }

                    @Override
                    public void onUserOffline(final int uid, final int reason) {
                        ThreadUtils.runOnUI(() -> {
                            if (remoteView != null && remoteView.getParent() != null) {
                                ((ViewGroup) remoteView.getParent()).removeAllViews();
                            }
                        });
                    }
                });
                return null;
            }, exception -> null);

    }


    private void joinChannel(String token,RtcConnection connection,ChannelMediaOptions mediaOptions,IRtcEngineEventHandler iRtcEngineEventHandler){
        rtcEngine.joinChannelEx(token, connection, mediaOptions, iRtcEngineEventHandler);
    }

    @Override public void onClick(View v) {
        SystemUtil.vibrator(getContext());
        if(v.getId()== R.id.iv_back){
            Navigation.findNavController(v).popBackStack();
        }else if(v.getId()==R.id.iv_switch_camera){
            rtcEngine.switchCamera();
        }else if(v.getId()==R.id.tv_low_power_mode){
            options.denoiserLevel=VIDEO_DENOISER_LEVEL_HIGH_QUALITY;
            updateVideoNoiseReduction();
        }else if(v.getId()==R.id.tv_equilibrium_mode){
            options.denoiserLevel=VIDEO_DENOISER_LEVEL_FAST;
            updateVideoNoiseReduction();
        }else if(v.getId()==R.id.tv_strong_noise_reduction_mode){
            options.denoiserLevel=VIDEO_DENOISER_LEVEL_STRENGTH;
            updateVideoNoiseReduction();
        }
    }

    private void addView(){
        binding.mainContainer.removeAllViews();
        if(remoteView!=null) {
            binding.mainContainer.addView(remoteView);
            remoteView.setZOrderOnTop(false);
            remoteView.setZOrderMediaOverlay(false);
        }
    }

}
