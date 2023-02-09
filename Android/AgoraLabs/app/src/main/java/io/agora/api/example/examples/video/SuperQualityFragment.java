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
import io.agora.api.example.common.widget.bubbleseekbar.BubbleSeekBar;
import io.agora.api.example.databinding.FragmentSuperQualityBinding;
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
import static androidx.constraintlayout.widget.ConstraintSet.GONE;
import static androidx.constraintlayout.widget.ConstraintSet.PARENT_ID;

public class SuperQualityFragment extends Fragment implements View.OnClickListener{
    private final String TAG="AgoraLab";

    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private RtcConnection rtcConnection;
    private FragmentSuperQualityBinding binding;
    private String channelName= String.valueOf((int) (Math.random() * 1000));
    private int senderUid =101;
    private int remoteUid=102;

    private final int LAYOUT_HALF=1;
    private final int LAYOUT_LOCAL_BIG=2;
    private final int LAYOUT_LOCAL_SMALL=3;
    private int curLayout=LAYOUT_HALF;

    private SurfaceView localView;
    private SurfaceView remoteView;

    private TextView tvOriginVideo;
    private TextView tvSuperQuality;
    private TextView tvLocalBitrate;
    private TextView tvRemoteBitrate;


    private boolean superQualityEnabled;
    private int superQualityValue;
    private int resolution= VideoFeatureMenu.RESOLUTION_360P;



    private VideoEncoderConfiguration configuration;
    private PopWindow popWindow;

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

    private void showLayoutPopWin(){
        if(popWindow==null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.video_layout_select, null);
            View.OnClickListener listener = v -> {
                SystemUtil.vibrator(getContext());
                if (v.getId() == R.id.iv_half) {
                    curLayout = LAYOUT_HALF;
                    binding.ivLayout.setImageResource(R.mipmap.ic_view_1);
                } else if (v.getId() == R.id.iv_local_big) {
                    curLayout = LAYOUT_LOCAL_BIG;
                    binding.ivLayout.setImageResource(R.mipmap.ic_view_2);
                } else if (v.getId() == R.id.iv_local_small) {
                    curLayout = LAYOUT_LOCAL_SMALL;
                    binding.ivLayout.setImageResource(R.mipmap.ic_view_3);
                }
                updateLayoutIvStatus();
                updateLayout();
                popWindow.dissmiss();
            };
            view.findViewById(R.id.iv_half).setOnClickListener(listener);
            view.findViewById(R.id.iv_local_big).setOnClickListener(listener);
            view.findViewById(R.id.iv_local_small).setOnClickListener(listener);


            popWindow = new PopWindow.PopupWindowBuilder(getContext())
                .setView(view)
                .create();
        }
        updateLayoutIvStatus();
        popWindow.showAsDropDown(binding.ivLayout, UIUtil.dip2px(getContext(),-60),UIUtil.dip2px(getContext(),-2));
    }

    private void updateLayoutIvStatus(){
        ((ImageView)popWindow.getPopupWindow().getContentView().findViewById(R.id.iv_half)).setImageAlpha(curLayout==LAYOUT_HALF ? 255 : 125);
        ((ImageView)popWindow.getPopupWindow().getContentView().findViewById(R.id.iv_local_big)).setImageAlpha(curLayout==LAYOUT_LOCAL_BIG ? 255 : 125);
        ((ImageView)popWindow.getPopupWindow().getContentView().findViewById(R.id.iv_local_small)).setImageAlpha(curLayout==LAYOUT_LOCAL_SMALL ? 255 : 125);
    }

    private void initView(){
        binding.superQualitySeekbar.setProgress(256);
        binding.ivBack.setOnClickListener(this);
        binding.ivLayout.setOnClickListener(this);
        binding.ivSwitchCamera.setOnClickListener(this);
        binding.optionMenu.addOption(VideoFeatureMenu.RESOLUTION_1080P,R.mipmap.ic_720p,R.string.resolutoion_1080p);
        binding.optionMenu.setListener(resolution -> {
            SuperQualityFragment.this.resolution=resolution;
            setVideoConfig();
        });
        binding.optionMenu.setCurrentResolution(resolution);
        binding.superQualitySeekbar.setVisibility(View.GONE);
        binding.featureSwitch.setChecked(superQualityEnabled);
        binding.featureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SystemUtil.vibrator(getContext());
            superQualityEnabled =isChecked;
            updateSuperQuality();
            setVideoConfig();
            binding.superQualitySeekbar.setVisibility(superQualityEnabled?View.VISIBLE:View.GONE);
        });
        binding.ivLayout.setOnLongClickListener(v -> {
            SystemUtil.vibrator(getContext());
            showLayoutPopWin();
            return false;
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
        binding.superQualitySeekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                boolean fromUser) {
                super.onProgressChanged(bubbleSeekBar, progress, progressFloat, fromUser);
                if(superQualityValue!=progress && fromUser ) {
                    superQualityValue=progress;
                    Log.d(TAG, "{\"rtc.video.ve_alpha_blending\":" + superQualityValue + "}");
                    rtcEngine.setParameters("{\"rtc.video.ve_alpha_blending\":" + superQualityValue + "}");
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                super.getProgressOnActionUp(bubbleSeekBar, progress, progressFloat);
                Log.d(TAG,"-----sdk log dir:"+getContext().getExternalFilesDir("").toString());
            }

            @Override public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                boolean fromUser) {
                super.getProgressOnFinally(bubbleSeekBar, progress, progressFloat, fromUser);
            }
        });
    }
    private boolean navigationShow=false;

    private void updateSuperQuality(){
        tvSuperQuality.setText(superQualityEnabled ?R.string.super_quality_enabled:R.string.super_quality_disabled);
        tvSuperQuality.setBackgroundResource(superQualityEnabled ?R.drawable.bg_rectangle_blue:R.drawable.bg_rectangle_grey);
        if(superQualityEnabled){
            rtcEngine.setParameters("{\"rtc.video.enable_sr\" : {\"enabled\":true, \"mode\":1}}");
            rtcEngine.setParameters("{\"rtc.video.sr_type\" : 20}");
            rtcEngine.setParameters("{\"rtc.video.ve_alpha_blending\":"+binding.superQualitySeekbar.getProgress()+"}");
        }else{
            rtcEngine.setParameters("{\"rtc.video.enable_sr\" : {\"enabled\":false, \"mode\":1}}");
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
            rtcEngine.setParameters("\"rtc.video.enable_sr\" : {\"enabled\":false, \"mode\":1}");
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
        localView = new SurfaceView(getContext()) ;
        rtcEngine.enableVideo();
        rtcEngine.enableAudio();
        rtcEngine.setupLocalVideo(new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, senderUid));
        rtcEngine.startPreview();


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

            @Override
            public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
                Log.d(TAG,"onRtcStats");
                ThreadUtils.runOnUI(() -> {
                    int bitrate=stats.txVideoKBitRate;
                    tvLocalBitrate.setText(getContext().getResources().getString(R.string.bitrate,bitrate));
                });
            }
        });
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
        }else if(v.getId()==R.id.iv_layout){
            if(curLayout==LAYOUT_HALF){
                curLayout=LAYOUT_LOCAL_SMALL;
                binding.ivLayout.setImageResource(R.mipmap.ic_view_3);
            }else if(curLayout==LAYOUT_LOCAL_BIG){
                curLayout=LAYOUT_HALF;
                binding.ivLayout.setImageResource(R.mipmap.ic_view_1);
            }else if(curLayout==LAYOUT_LOCAL_SMALL){
                curLayout=LAYOUT_LOCAL_BIG;
                binding.ivLayout.setImageResource(R.mipmap.ic_view_2);
            }
            updateLayout();
        }else if(v.getId()==R.id.iv_switch_camera){
            rtcEngine.switchCamera();
        }
    }

    public void updateLayout(){
        if(curLayout==LAYOUT_HALF){
            ConstraintLayout.LayoutParams mainContainerParams=new ConstraintLayout.LayoutParams(0,0);
            mainContainerParams.leftToLeft=PARENT_ID;
            mainContainerParams.rightToRight=PARENT_ID;
            mainContainerParams.topToTop=PARENT_ID;
            mainContainerParams.bottomToTop=binding.subContainer.getId();
            binding.mainContainer.setLayoutParams(mainContainerParams);
            ConstraintLayout.LayoutParams subContainerParams=new ConstraintLayout.LayoutParams(0,0);
            subContainerParams.leftToLeft=PARENT_ID;
            subContainerParams.rightToRight=PARENT_ID;
            subContainerParams.topToBottom=binding.mainContainer.getId();
            subContainerParams.bottomToTop=binding.menuControler.getId();
            binding.subContainer.setLayoutParams(subContainerParams);
            addView();
        }else if(curLayout==LAYOUT_LOCAL_BIG){
            ConstraintLayoutUtils.setMatchParentAndBottomToTopLayout(binding.mainContainer,binding.menuControler.getId());
            setSmallWin(binding.subContainer);
            addView();
        }else if(curLayout==LAYOUT_LOCAL_SMALL){
            ConstraintLayoutUtils.setMatchParentAndBottomToTopLayout(binding.mainContainer,binding.menuControler.getId());
            setSmallWin(binding.subContainer);
            addView();
        }
    }


    private void setSmallWin(View view){
        ConstraintLayout.LayoutParams params=new ConstraintLayout.LayoutParams(UIUtil.dip2px(getContext(),160),UIUtil.dip2px(getContext(),200));
        params.rightToRight=PARENT_ID;
        params.bottomToTop=binding.menuControler.getId();
        view.setLayoutParams(params);
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
        binding.subContainer.removeAllViews();

        if(curLayout==LAYOUT_HALF) {
            if(localView!=null) {
                binding.mainContainer.addView(localView);
                ConstraintLayoutUtils.setBottomCenter(tvOriginVideo);
                ConstraintLayoutUtils.setBottomRight(tvLocalBitrate);
                binding.mainContainer.addView(tvOriginVideo);
                binding.mainContainer.addView(tvLocalBitrate);
            }
            if(remoteView!=null) {
                binding.subContainer.addView(remoteView);
                int topMargin = UIUtil.dip2px(getContext(), 10);
                setTopCenterLayout(tvSuperQuality, topMargin);
                ConstraintLayoutUtils.setTopRight(tvRemoteBitrate, topMargin);
                binding.subContainer.addView(tvSuperQuality);
                binding.subContainer.addView(tvRemoteBitrate);
            }
        }else if(curLayout==LAYOUT_LOCAL_BIG){
            if(localView!=null) {
                binding.mainContainer.addView(localView);
                localView.setZOrderOnTop(false);
                localView.setZOrderMediaOverlay(false);
                int topMargin = UIUtil.dip2px(getContext(), 60);
                setTopCenterLayout(tvOriginVideo, topMargin);
                ConstraintLayoutUtils.setTopRight(tvLocalBitrate, topMargin);
                binding.mainContainer.addView(tvOriginVideo);
                binding.mainContainer.addView(tvLocalBitrate);
            }
            if(remoteView!=null) {
                binding.subContainer.addView(remoteView);
                remoteView.setZOrderOnTop(true);
                remoteView.setZOrderMediaOverlay(true);
                setTopCenterLayout(tvSuperQuality, UIUtil.dip2px(getContext(), 10));
                ConstraintLayoutUtils.setBottomCenter(tvRemoteBitrate);
                binding.subContainer.addView(tvSuperQuality);
                binding.subContainer.addView(tvRemoteBitrate);
            }
        }else if(curLayout==LAYOUT_LOCAL_SMALL){
            if(remoteView!=null) {
                binding.mainContainer.addView(remoteView);
                remoteView.setZOrderOnTop(false);
                remoteView.setZOrderMediaOverlay(false);
                int topMargin = UIUtil.dip2px(getContext(), 60);
                setTopCenterLayout(tvSuperQuality, topMargin);
                ConstraintLayoutUtils.setTopRight(tvRemoteBitrate, topMargin);
                binding.mainContainer.addView(tvSuperQuality);
                binding.mainContainer.addView(tvRemoteBitrate);
            }
            if(localView!=null) {
                binding.subContainer.addView(localView);
                localView.setZOrderOnTop(true);
                localView.setZOrderMediaOverlay(true);
                setTopCenterLayout(tvOriginVideo, UIUtil.dip2px(getContext(), 10));
                ConstraintLayoutUtils.setBottomCenter(tvLocalBitrate);
                binding.subContainer.addView(tvOriginVideo);
                binding.subContainer.addView(tvLocalBitrate);
            }
        }

    }




}
