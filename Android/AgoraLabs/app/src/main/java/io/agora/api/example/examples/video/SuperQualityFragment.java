package io.agora.api.example.examples.video;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.TokenGenerator;
import io.agora.api.example.common.UserManager;
import io.agora.api.example.common.widget.VideoFeatureMenu;
import io.agora.api.example.databinding.FragmentSuperQualityBinding;
import io.agora.api.example.utils.AESUtils;
import io.agora.api.example.utils.PermissionUtils;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.ThreadUtils;
import io.agora.api.example.utils.UIUtil;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcConnection;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class SuperQualityFragment extends Fragment implements View.OnClickListener{
    private final String TAG="AgoraLab";

    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private RtcConnection rtcConnection;
    private FragmentSuperQualityBinding binding;
    private String channelName= String.valueOf((int) (Math.random() * 1000));
    private int senderUid =101;
    private int remoteUid=102;

    private SurfaceView remoteView;

    private TextView tvOriginVideo;
    private TextView tvSuperQuality;
    private TextView tvLocalBitrate;
    private TextView tvRemoteBitrate;


    private boolean superQualityEnabled;
    private int superQualityValue;
    private int resolution= VideoFeatureMenu.RESOLUTION_720P;



    private VideoEncoderConfiguration configuration;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding= FragmentSuperQualityBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void updateOptionMenuListener(){
        binding.optionMenu.setListener(resolution -> {
            SuperQualityFragment.this.resolution = resolution;
            setVideoConfig();
        });
    }
    private void initView(){
        binding.ivBack.setOnClickListener(this);
        binding.ivSwitchCamera.setOnClickListener(this);
        //binding.optionMenu.addOption(VideoFeatureMenu.RESOLUTION_1080P,R.mipmap.ic_720p,R.string.resolutoion_1080p);
        updateOptionMenuListener();
        binding.optionMenu.setCurrentResolution(resolution);
        binding.featureSwitch.setChecked(superQualityEnabled);
        binding.featureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SystemUtil.vibrator(getContext());
            superQualityEnabled =isChecked;
            updateSuperQuality();
            setVideoConfig();
        });

        tvOriginVideo=new TextView(getContext());
        tvOriginVideo.setText(R.string.original_video);
        tvOriginVideo.setGravity(Gravity.CENTER);
        tvOriginVideo.setTextColor(getResources().getColor(R.color.white));
        tvOriginVideo.setTextSize(COMPLEX_UNIT_SP,15);
        int padding=UIUtil.dip2px(getContext(),6);
        tvOriginVideo.setPadding(padding,padding,padding,padding);
        tvOriginVideo.setBackgroundResource(R.drawable.bg_rectangle_grey);

        tvSuperQuality =new TextView(getContext());
        tvSuperQuality.setGravity(Gravity.CENTER);
        tvSuperQuality.setTextColor(getResources().getColor(R.color.white));
        tvSuperQuality.setTextSize(COMPLEX_UNIT_SP,13);
        tvSuperQuality.setSingleLine();
        tvSuperQuality.setPadding(padding,padding,padding,padding);
        tvSuperQuality.setText(superQualityEnabled ?R.string.super_quality_enabled:R.string.super_quality_disabled);
        tvSuperQuality.setBackgroundResource(superQualityEnabled ?R.drawable.bg_rectangle_blue:R.drawable.bg_rectangle_grey);

        tvLocalBitrate=new TextView(getContext());
        tvLocalBitrate.setGravity(Gravity.CENTER);
        tvLocalBitrate.setTextColor(getResources().getColor(R.color.white));
        tvLocalBitrate.setTextSize(COMPLEX_UNIT_SP,13);
        tvLocalBitrate.setPadding(padding,padding,padding,padding);

        tvRemoteBitrate=new TextView(getContext());
        tvRemoteBitrate.setGravity(Gravity.CENTER);
        tvRemoteBitrate.setTextColor(getResources().getColor(R.color.white));
        tvRemoteBitrate.setTextSize(COMPLEX_UNIT_SP,13);
        tvRemoteBitrate.setPadding(padding,padding,padding,padding);

        binding.menuControler.setOnClickListener(v -> {
            if(binding.optionMenu.getVisibility()==View.GONE){
                binding.optionMenu.setVisibility(View.VISIBLE);
            }else{
                binding.optionMenu.setVisibility(View.GONE);
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
                        if(binding.optionMenu.getVisibility()!=View.GONE) {
                            binding.optionMenu.setVisibility(View.GONE);
                        }
                    }else if(lastY-y>5){
                        if(binding.optionMenu.getVisibility()!=View.VISIBLE) {
                            binding.optionMenu.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return false;
            }
        };
        binding.menuControler.setOnTouchListener(listener);
        binding.featureSwitch.setOnTouchListener(listener);
        binding.optionMenu.delResolutionOption(VideoFeatureMenu.RESOLUTION_480P);
        binding.optionMenu.addResultionOption(VideoFeatureMenu.RESOLUTION_1080P);
    }
    private boolean navigationShow=false;

    private void updateSuperQuality(){
        tvSuperQuality.setText(superQualityEnabled ?R.string.super_quality_enabled:R.string.super_quality_disabled);
        tvSuperQuality.setBackgroundResource(superQualityEnabled ?R.drawable.bg_rectangle_blue:R.drawable.bg_rectangle_grey);

        if(superQualityEnabled){
        }else{
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
                startSuperQuality();
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
                        startSuperQuality();
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

    private void startSuperQuality(){
        initializeEngine();
        setupSend();
        setupReceiver();
        UserManager.getInstance().addCameraCount();
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
                joinChannel(token,rtcConnection,mediaOptions, new IRtcEngineEventHandler() {

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

                    @Override
                    public void onRtcStats(RtcStats stats) {
                        //Log.d(TAG, "onRtcStats");
                        ThreadUtils.runOnUI(() -> {
                            int bitrate = stats.txVideoKBitRate;
                            tvLocalBitrate.setText(getContext().getResources().getString(R.string.bitrate_value, bitrate));
                        });
                    }
                });
                return null;
            }, exception -> null);
        addView();
    }

    private void joinChannel(String token,RtcConnection connection,ChannelMediaOptions mediaOptions,IRtcEngineEventHandler iRtcEngineEventHandler){
        rtcEngine.joinChannelEx(token, connection, mediaOptions, iRtcEngineEventHandler);
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
                            remoteView= new SurfaceView(getContext()) ;
                            rtcEngine.setupRemoteVideoEx(new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid),rtcc);
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
                    @Override
                    public void onRtcStats(RtcStats stats) {
                        ThreadUtils.runOnUI(() -> {
                            int bitrate=stats.rxVideoKBitRate;
                            tvRemoteBitrate.setText(getContext().getResources().getString(R.string.bitrate_value,bitrate));
                        });
                    }
                });
                return null;
            }, exception -> null);

    }

    private void setVideoConfig(){
        if(configuration==null) {
            configuration = new VideoEncoderConfiguration();
        }
        if(resolution==VideoFeatureMenu.RESOLUTION_360P){
            configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(640,360);
            configuration.bitrate= 800;
            configuration.frameRate=15;
        }else if(resolution==VideoFeatureMenu.RESOLUTION_480P){
            configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(640,480);
            configuration.bitrate= 1200;
            configuration.frameRate=15;
        }else if(resolution==VideoFeatureMenu.RESOLUTION_540P){
            configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(960,540);
            configuration.bitrate= 1470;
            configuration.frameRate=15;
        }else if(resolution==VideoFeatureMenu.RESOLUTION_720P){
            configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(960,720);
            configuration.bitrate= 2260;
            configuration.frameRate=15;
        }else if(resolution==VideoFeatureMenu.RESOLUTION_1080P){
            configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(1920,1080);
            configuration.bitrate= 3150;
            configuration.frameRate=15;
        }
        rtcEngine.setVideoEncoderConfigurationEx(configuration,rtcConnection);
    }

    @Override public void onClick(View v) {
        SystemUtil.vibrator(getContext());
        if(v.getId()== R.id.iv_back){
            Navigation.findNavController(v).popBackStack();
        }else if(v.getId()==R.id.iv_switch_camera){
            rtcEngine.switchCamera();
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
