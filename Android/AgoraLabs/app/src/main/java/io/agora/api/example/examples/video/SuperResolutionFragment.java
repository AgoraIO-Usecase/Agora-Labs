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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.TokenGenerator;
import io.agora.api.example.common.widget.PopWindow;
import io.agora.api.example.common.widget.VideoFeatureMenu;
import io.agora.api.example.databinding.FragmentSuperResolutionBinding;
import io.agora.api.example.utils.ConstraintLayoutUtils;
import io.agora.api.example.utils.PermissionUtils;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.ThreadUtils;
import io.agora.api.example.utils.UIUtil;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaExtensionObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcConnection;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.json.JSONException;
import org.json.JSONObject;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

public class SuperResolutionFragment extends Fragment implements View.OnClickListener{
    private final String TAG="AgoraLab";

    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private RtcConnection rtcConnection;
    private FragmentSuperResolutionBinding binding;
    private String channelName= String.valueOf((int) (Math.random() * 1000));
    private int senderUid =101;
    private int remoteUid=102;

    private SurfaceView remoteView;

    private TextView tvSR;


    private boolean srEnabled;

    private boolean needShowSrFaiedToast=true;

    private int resolution=VideoFeatureMenu.RESOLUTION_360P;

    private VideoEncoderConfiguration configuration;

    private boolean srEnabledSuccess=true;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding= FragmentSuperResolutionBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView(){
        binding.ivBack.setOnClickListener(this);
        binding.ivSwitchCamera.setOnClickListener(this);
        binding.optionMenu.setListener(resolution -> {
            SuperResolutionFragment.this.resolution=resolution;
            setVideoConfig();
        });
        binding.optionMenu.setCurrentResolution(resolution);
        binding.featureSwitch.setChecked(srEnabled);
        binding.featureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SystemUtil.vibrator(getContext());
            srEnabled =isChecked;
            if(isChecked){
                needShowSrFaiedToast=true;
            }
            updateSR();
        });
        int padding= UIUtil.dip2px(getContext(),6);

        tvSR =new TextView(getContext());
        tvSR.setGravity(Gravity.CENTER);
        tvSR.setTextColor(getResources().getColor(R.color.white));
        tvSR.setTextSize(COMPLEX_UNIT_SP,13);
        tvSR.setSingleLine();
        tvSR.setPadding(padding,padding,padding,padding);
        tvSR.setText(srEnabled ?R.string.sr_enabled:R.string.sr_disabled);
        tvSR.setBackgroundResource(srEnabled ?R.drawable.bg_rectangle_blue:R.drawable.bg_rectangle_grey);

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

    }

    private void updateSR(){
        tvSR.setText(srEnabled ?R.string.sr_enabled:R.string.sr_disabled);
        tvSR.setBackgroundResource(srEnabled ?R.drawable.bg_rectangle_blue:R.drawable.bg_rectangle_grey);
        setSRValue();
    }


    private void setSRValue(){
        if(srEnabled) {
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
                startSuperResolution();
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
                        startSuperResolution();
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

    private void startSuperResolution(){
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
        config.mAppId = getString(R.string.agora_app_id);
        config.mChannelProfile = sceneMode;
        config.mEventHandler = new IRtcEngineEventHandler() {

        };
        config.mExtensionObserver=new IMediaExtensionObserver() {
            @Override public void onEvent(String provider, String extension, String key, String value) {
                Log.d(TAG,"-----onEvent---:"+provider+" extension:"+extension+" key:"+key+ " value:"+value);
                if (provider.equals("sr.io.agora.builtin") && key.equals("sr_type")) {
                    try {
                        JSONObject srJSON=new JSONObject(value);
                        int type=srJSON.optInt("type",0);
                        srEnabled= type != 0;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (provider.equals("agora_video_filters_clear_vision")
                    && extension.equals("sharpen")
                    && key.equals("sharpen_type")) {
                    try {
                        JSONObject sharpenJSON=new JSONObject(value);
                        int type=sharpenJSON.optInt("type",0);
                        if (type == 0 && !srEnabled) {
                            if (needShowSrFaiedToast) {
                                needShowSrFaiedToast = false;
                                ThreadUtils.runOnUI(() -> Toast.makeText(getContext(), R.string.sr_enabled_failed, Toast.LENGTH_SHORT)
                                    .show());
                            }
                            ThreadUtils.runOnUI(SuperResolutionFragment.this::updateSRCb);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override public void onStarted(String provider, String extension) {

            }

            @Override public void onStopped(String provider, String extension) {

            }



            @Override public void onError(String provider, String extension, int error, String message) {

            }
        };
        config.mAudioScenario = Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT);
        config.mAreaCode = ((App)getActivity().getApplication()).getAreaCode();
        try {
            rtcEngine = (RtcEngineEx) RtcEngineEx.create(config);
            setSRValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSRCb(){
        if(!srEnabled){
            binding.featureSwitch.setChecked(false);
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
        rtcEngine.enableVideo();
        rtcEngine.enableAudio();
        setVideoConfig();
        ChannelMediaOptions mediaOptions=new ChannelMediaOptions();
        mediaOptions.channelProfile= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        mediaOptions.clientRoleType= Constants.CLIENT_ROLE_BROADCASTER;
        mediaOptions.publishMicrophoneTrack = false;
        mediaOptions.publishCameraTrack = true;
        TokenGenerator.INSTANCE.generateToken(rtcConnection.channelId, String.valueOf(rtcConnection.localUid),
            TokenGenerator.TokenGeneratorType.token006, TokenGenerator.AgoraTokenType.rtc,
            new Function1<String, Unit>() {
                @Override public Unit invoke(String token) {
                    joinChannel(token,rtcConnection,mediaOptions,new IRtcEngineEventHandler() {

                        @Override
                        public void onUserJoined(int uid, int elapsed) {
                            Log.d(TAG, "agora onUserJoined:" + uid);
                        }

                        @Override
                        public void onUserOffline(final int uid, final int reason) {
                            Log.d(TAG, "onUserOffline:" + uid);
                            ThreadUtils.runOnUI(() -> {
                                if (remoteView != null && remoteView.getParent() != null) {
                                    ((ViewGroup) remoteView.getParent()).removeAllViews();
                                }
                            });
                        }

                        @Override
                        public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
                            Log.d(TAG, "onRtcStats");
                        }

                        @Override
                        public void onVideoSizeChanged(Constants.VideoSourceType source, int uid, int width, int height,
                            int rotation) {
                            super.onVideoSizeChanged(source, uid, width, height, rotation);
                            Log.d(TAG, "-----onVideoSizeChanged1---width:" + width + " height:" + height + " uid:" + uid);
                        }
                    });
                    return null;
                }
            }, exception -> null);
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
            new Function1<String, Unit>() {
                @Override public Unit invoke(String token) {
                    joinChannel(token,rtcc,mediaOptions,new IRtcEngineEventHandler() {
                        @Override public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                            super.onJoinChannelSuccess(channel, uid, elapsed);
                            Log.d(TAG,
                                "receiver onJoinChannelSuccess--channel:" + channel + " uid:" + uid + " elapsed:" + elapsed);
                        }

                        @Override
                        public void onUserJoined(int uid, int elapsed) {
                            Log.d(TAG, "---onUserJoined--uid:" + uid);
                            ThreadUtils.runOnUI(() -> {
                                remoteView = new SurfaceView(getContext());
                                rtcEngine.setupRemoteVideoEx(
                                    new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, 1, uid), rtcc);
                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
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

                        @Override
                        public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
                        }

                        @Override public void onError(int err) {
                            super.onError(err);
                        }

                        @Override
                        public void onVideoSizeChanged(Constants.VideoSourceType source, int uid, int width, int height,
                            int rotation) {
                            super.onVideoSizeChanged(source, uid, width, height, rotation);
                            Log.d(TAG, "-----onVideoSizeChanged2---width:" + width + " height:" + height + " uid:" + uid);
                        }
                    });
                    return null;
                }
            }, exception -> null);

    }

    private void setVideoConfig(){
        if(configuration==null) {
            configuration = new VideoEncoderConfiguration();
        }
        if(resolution==VideoFeatureMenu.RESOLUTION_360P){
            configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(640,360);
            configuration.bitrate=800;
            configuration.frameRate=15;
        }else if(resolution==VideoFeatureMenu.RESOLUTION_480P){
            configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(640,480);
            configuration.bitrate=1200;
            configuration.frameRate=15;
        }else if(resolution==VideoFeatureMenu.RESOLUTION_540P){
            configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(960,540);
            configuration.bitrate=1450;
            configuration.frameRate=15;
        }else if(resolution==VideoFeatureMenu.RESOLUTION_720P){
            configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(960,720);
            configuration.bitrate=2200;
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
            /*
            int topMargin = UIUtil.dip2px(getContext(), 60);
            setTopCenterLayout(tvSR, topMargin);
            binding.mainContainer.addView(tvSR);*/
        }
    }
}
