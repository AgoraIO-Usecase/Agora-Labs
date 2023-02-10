package io.agora.api.example.examples.video;

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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.widget.PopWindow;
import io.agora.api.example.common.widget.VideoFeatureMenu;
import io.agora.api.example.databinding.FragmentRoiBinding;
import io.agora.api.example.utils.ConstraintLayoutUtils;
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
import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

public class ROIFragment extends Fragment implements View.OnClickListener{
    private final String TAG="AgoraLab";

    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private RtcConnection rtcConnection;
    private FragmentRoiBinding binding;
    private String channelName= String.valueOf((int) (Math.random() * 1000));
    private int senderUid =101;
    private int remoteUid=102;



    private SurfaceView remoteView;

    private TextView tvROI;
    private TextView tvRemoteBitrate;

    private boolean roiEnabled;
    private int resolution=VideoFeatureMenu.RESOLUTION_360P;
    private VideoEncoderConfiguration configuration;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding= FragmentRoiBinding.inflate(inflater,container,false);
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
            ROIFragment.this.resolution=resolution;
            setVideoConfig();
        });
        binding.optionMenu.setCurrentResolution(resolution);
        binding.featureSwitch.setChecked(roiEnabled);
        binding.featureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SystemUtil.vibrator(getContext());
            roiEnabled =isChecked;
            updateROI();
        });

        int padding= UIUtil.dip2px(getContext(),6);

        tvROI =new TextView(getContext());
        tvROI.setGravity(Gravity.CENTER);
        tvROI.setTextColor(getResources().getColor(R.color.white));
        tvROI.setTextSize(COMPLEX_UNIT_SP,13);
        tvROI.setSingleLine();
        tvROI.setPadding(padding,padding,padding,padding);
        tvROI.setText(roiEnabled ?R.string.roi_enabled:R.string.roi_disabled);
        tvROI.setBackgroundResource(roiEnabled ?R.drawable.bg_rectangle_blue:R.drawable.bg_rectangle_grey);

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
    }

    private void updateROI(){
        tvROI.setText(roiEnabled ?R.string.roi_enabled:R.string.roi_disabled);
        tvROI.setBackgroundResource(roiEnabled ?R.drawable.bg_rectangle_blue:R.drawable.bg_rectangle_grey);
        if(roiEnabled){
            rtcEngine.setParameters("{\"che.video.roiEnable\":true}");
        }else{
            rtcEngine.setParameters("{\"che.video.roiEnable\":false}");
        }
    }

    @Override public void onStart() {
        super.onStart();
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
    }

    private void setupReceiver(){
        RtcConnection rtcc=new RtcConnection();
        rtcc.channelId=channelName;
        rtcc.localUid=remoteUid;

        ChannelMediaOptions mediaOptions=new ChannelMediaOptions();
        mediaOptions.channelProfile= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        mediaOptions.clientRoleType= Constants.CLIENT_ROLE_BROADCASTER;
        mediaOptions.publishMicrophoneTrack=false;

        int ret=rtcEngine.joinChannelEx("", rtcc, mediaOptions, new IRtcEngineEventHandler() {
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
            @Override
            public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
                ThreadUtils.runOnUI(() -> {
                    int bitrate=stats.rxVideoKBitRate;
                    tvRemoteBitrate.setText(getContext().getResources().getString(R.string.bitrate,bitrate));
                });
            }
        });

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
        if(v.getId()== R.id.iv_back) {
            Navigation.findNavController(v).popBackStack();
        }else if(v.getId()==R.id.iv_switch_camera){
            rtcEngine.switchCamera();
        }
    }

    private void setTopCenterLayout(View view,int topMargin){
        ConstraintLayout.LayoutParams params =
            new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.topToTop = PARENT_ID;
        params.leftToLeft = PARENT_ID;
        params.rightToRight = PARENT_ID;
        params.topMargin=topMargin;
        view.setLayoutParams(params);
    }

    private void addView(){
        binding.mainContainer.removeAllViews();
        if(remoteView!=null) {
            binding.mainContainer.addView(remoteView);
            remoteView.setZOrderOnTop(false);
            remoteView.setZOrderMediaOverlay(false);
            /*
            int topMargin = UIUtil.dip2px(getContext(), 60);
            setTopCenterLayout(tvROI, topMargin);
            ConstraintLayoutUtils.setTopRight(tvRemoteBitrate, topMargin);
            binding.mainContainer.addView(tvROI);
            binding.mainContainer.addView(tvRemoteBitrate);*/
        }
    }

}
