package io.agora.api.example.examples.video;

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
import com.example.anan.AAChartCore.AAChartCoreLib.AAChartCreator.AAChartModel;
import com.example.anan.AAChartCore.AAChartCoreLib.AAChartCreator.AASeriesElement;
import com.example.anan.AAChartCore.AAChartCoreLib.AAChartEnum.AAChartAnimationType;
import com.example.anan.AAChartCore.AAChartCoreLib.AAChartEnum.AAChartType;
import com.example.anan.AAChartCore.AAChartCoreLib.AAOptionsModel.AAOptions;
import com.example.anan.AAChartCore.AAChartCoreLib.AAOptionsModel.AAStyle;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.widget.VideoFeatureMenu;
import io.agora.api.example.databinding.FragmentPvcBinding;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

public class PVCFragment extends Fragment implements View.OnClickListener{
    private final String TAG="AgoraLab";

    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private RtcConnection rtcConnection;
    private FragmentPvcBinding binding;
    private String channelName= String.valueOf((int) (Math.random() * 1000));
    private int senderUid =101;
    private int remoteUid=102;


    private SurfaceView remoteView;

    private TextView tvPVC;
    //private TextView tvRemoteBitrate;

    private boolean pvcEnabled;
    private int resolution=VideoFeatureMenu.RESOLUTION_360P;


    private AAChartModel chartModel;
    private AAOptions aaOptions;
    private List<Object> yData=new ArrayList<>();
    private List<String> xData=new ArrayList<>();
    private VideoEncoderConfiguration configuration;

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding=FragmentPvcBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initChartModel();
        initView();
    }


    private void initChartModel(){
        if(chartModel==null){
            AAStyle style=new AAStyle().color("#FFFFFF");
            chartModel=new AAChartModel()
                .chartType(AAChartType.Line)
                .animationType(AAChartAnimationType.Elastic)
                .animationDuration(1)
                .dataLabelsEnabled(false)
                .legendEnabled(false)
                .dataLabelsStyle(style)
                .axesTextColor("#FFFFFF")
                .titleStyle(style)
                .markerRadius(0)
                .tooltipValueSuffix("kbps")
                .categories(xData.toArray(new String[xData.size()]))
                .series(new AASeriesElement[]{new AASeriesElement().data(yData.toArray())})
                .xAxisTickInterval(xData.size()/5);


        }
    }
    private void updateChartView(){
        aaOptions=chartModel.aa_toAAOptions();
        aaOptions.tooltip.useHTML(true).formatter(String.format("function () {\n" +
                "        return '%s: '" +
                "        +  this.y" +
                "        + 'kbps';" +
                "        }",getContext().getString(R.string.bitrate)))
            .valueDecimals(0)
            .backgroundColor("#000000")
            .borderColor("#000000")
            .style(new AAStyle().color("#FFFFFF").fontSize(12));
        binding.chartView.aa_drawChartWithChartOptions(aaOptions);
    }

    private void initView(){
        binding.chartView.setIsClearBackgroundColor(true);
        binding.ivBack.setOnClickListener(this);
        binding.ivSwitchCamera.setOnClickListener(this);
        binding.optionMenu.setListener(resolution -> {
            PVCFragment.this.resolution=resolution;
            setVideoConfig();
        });
        binding.optionMenu.setCurrentResolution(resolution);

        binding.featureSwitch.setChecked(pvcEnabled);
        binding.featureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SystemUtil.vibrator(getContext());
            pvcEnabled=isChecked;
            updatePVC();
            setVideoConfig();
        });



        int padding=UIUtil.dip2px(getContext(),6);

        tvPVC=new TextView(getContext());
        tvPVC.setGravity(Gravity.CENTER);
        tvPVC.setTextColor(getResources().getColor(R.color.white));
        tvPVC.setTextSize(COMPLEX_UNIT_SP,13);
        tvPVC.setSingleLine();
        tvPVC.setPadding(padding,padding,padding,padding);
        tvPVC.setText(pvcEnabled?R.string.pvc_enabled:R.string.pvc_disabled);
        tvPVC.setBackgroundResource(pvcEnabled?R.drawable.bg_rectangle_blue:R.drawable.bg_rectangle_grey);
        /*
        tvRemoteBitrate=new TextView(getContext());
        tvRemoteBitrate.setGravity(Gravity.CENTER);
        tvRemoteBitrate.setTextColor(getResources().getColor(R.color.white));
        tvRemoteBitrate.setTextSize(COMPLEX_UNIT_SP,13);
        tvRemoteBitrate.setPadding(padding,padding,padding,padding);*/

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
        binding.tvBitrate.setOnClickListener(this);
        binding.tvBitrateContent.setOnClickListener(this);
    }

    private void updatePVC(){
        tvPVC.setText(pvcEnabled?R.string.pvc_enabled:R.string.pvc_disabled);
        tvPVC.setBackgroundResource(pvcEnabled?R.drawable.bg_rectangle_blue:R.drawable.bg_rectangle_grey);
        if(pvcEnabled){
            rtcEngine.setParameters("{\"rtc.video.enable_pvc\":true}");
        }else{
            rtcEngine.setParameters("{\"rtc.video.enable_pvc\":false}");
        }
    }

    @Override public void onStart() {
        super.onStart();
        initializeEngine();
        setupSend();
        setupReceiver();
        updateChartView();
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

        rtcEngine.enableVideo();
        rtcEngine.enableAudio();
        setVideoConfig();

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
            @Override
            public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
                ThreadUtils.runOnUI(() -> {
                    int bitrate=stats.rxVideoKBitRate;
                    updateBitrate(bitrate);
                    binding.tvBitrate.setText(bitrate+" kbps");
                    binding.tvBitrateContent.setText(getString(R.string.bitrate_value,bitrate));
                    //tvRemoteBitrate.setText(getContext().getResources().getString(R.string.bitrate_value,bitrate));
                });
            }
        });

    }

    private void updateBitrate(int bitrate){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss");
        String now=simpleDateFormat.format(new Date());
        if(xData.size()>=30){
            xData.remove(0);
        }
        xData.add(now);
        if(yData.size()>=30){
            yData.remove(0);
        }
        yData.add(bitrate);
        Log.d("AgoraLab","---xData size:"+xData.size()+" yData size:"+yData.size());
        chartModel.categories(xData.toArray(new String[xData.size()]))
            .series(new AASeriesElement[]{new AASeriesElement().color("#ffffff").data(yData.toArray())})
            .xAxisTickInterval((int)Math.ceil(yData.size()/5.0d));;
        updateChartView();
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
        }
        rtcEngine.setVideoEncoderConfigurationEx(configuration,rtcConnection);
    }

    @Override public void onClick(View v) {
        SystemUtil.vibrator(getContext());
        if(v.getId()== R.id.iv_back){
            Navigation.findNavController(v).popBackStack();
        }else if(v.getId()==R.id.iv_switch_camera){
            rtcEngine.switchCamera();
        }else if(v.getId()==R.id.tv_bitrate){
            binding.chartContainer.setVisibility(View.GONE);
            binding.tvBitrateContent.setVisibility(View.VISIBLE);
        }else if(v.getId()==R.id.tv_bitrate_content){
            binding.chartContainer.setVisibility(View.VISIBLE);
            binding.tvBitrateContent.setVisibility(View.GONE);
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
            setTopCenterLayout(tvPVC, topMargin);
            ConstraintLayoutUtils.setTopRight(tvRemoteBitrate, topMargin);
            binding.mainContainer.addView(tvPVC);
            binding.mainContainer.addView(tvRemoteBitrate);*/
        }
    }

}
