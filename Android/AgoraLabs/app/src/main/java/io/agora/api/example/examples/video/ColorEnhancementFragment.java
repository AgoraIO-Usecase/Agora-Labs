package io.agora.api.example.examples.video;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.TokenGenerator;
import io.agora.api.example.common.widget.VideoFeatureMenu;
import io.agora.api.example.databinding.FragmentColorEnhancementBinding;
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
import io.agora.rtc2.video.ColorEnhanceOptions;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import java.math.BigDecimal;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ColorEnhancementFragment extends Fragment implements View.OnClickListener {
    private final String TAG="AgoraLab";

    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private RtcConnection rtcConnection;
    private FragmentColorEnhancementBinding binding;
    private String channelName= String.valueOf((int) (Math.random() * 1000));
    private int senderUid =101;
    private int remoteUid=102;


    private SurfaceView remoteView;
    private SurfaceView localView;



    private boolean enhanceSaturationEnabled;
    private ColorEnhanceOptions options=new ColorEnhanceOptions();



    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options.strengthLevel=0.5f;
        options.skinProtectLevel=0.5f;
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding=FragmentColorEnhancementBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }



    private void initView(){
        binding.ivBack.setOnClickListener(this);
        binding.ivSwitchCamera.setOnClickListener(this);

        binding.featureSwitch.setChecked(enhanceSaturationEnabled);
        binding.featureSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SystemUtil.vibrator(getContext());
            enhanceSaturationEnabled =isChecked;
            updateColorEnhancement();
        });


        binding.menuControler.setOnClickListener(v -> {
            if(binding.colorSettingGroup.getVisibility()==View.GONE){
                binding.colorSettingGroup.setVisibility(View.VISIBLE);
            }else{
                binding.colorSettingGroup.setVisibility(View.GONE);
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
                        if(binding.colorSettingGroup.getVisibility()!=View.GONE) {
                            binding.colorSettingGroup.setVisibility(View.GONE);
                        }
                    }else if(lastY-y>5){
                        if(binding.colorSettingGroup.getVisibility()!=View.VISIBLE) {
                            binding.colorSettingGroup.setVisibility(View.VISIBLE);
                        }
                    }
                }
                return false;
            }
        };
        binding.menuControler.setOnTouchListener(listener);
        binding.featureSwitch.setOnTouchListener(listener);
        binding.seekbarSkinProtection.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                options.skinProtectLevel=(float) Math.round(0.01f*progress*10)/10;
                binding.tvSkinProtectionValue.setText(String.valueOf(options.skinProtectLevel));
                updateColorEnhanceValue();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.seekbarSkinProtection.setProgress(Math.round(options.skinProtectLevel*100));

        binding.tvSkinProtectionValue.setText(String.valueOf(options.skinProtectLevel));
        binding.seekbarColorProtection.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                options.strengthLevel=(float) Math.round(0.01f*progress*10)/10;
                binding.tvColorProtectionValue.setText(String.valueOf( options.strengthLevel));
                updateColorEnhanceValue();
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.seekbarColorProtection.setProgress(Math.round(options.strengthLevel*100));
        binding.tvColorProtectionValue.setText(String.valueOf(options.strengthLevel));
        updateColorEnhancement();
    }

    private void setVideoConfig() {
        VideoEncoderConfiguration configuration = new VideoEncoderConfiguration();
        configuration.dimensions = new VideoEncoderConfiguration.VideoDimensions(960, 540);
        configuration.bitrate = 1450;
        configuration.frameRate = 15;
        rtcEngine.setVideoEncoderConfigurationEx(configuration, rtcConnection);
    }

    private void updateColorEnhanceValue(){
        if(rtcEngine!=null) {
            rtcEngine.setColorEnhanceOptions(enhanceSaturationEnabled,options);
        }
    }

    private void updateColorEnhancement(){
        updateColorEnhanceValue();
        binding.tvColorProtectionValue.setEnabled(enhanceSaturationEnabled);
        binding.tvSkinProtectionValue.setEnabled(enhanceSaturationEnabled);
        binding.tvColorProtectionTip.setEnabled(enhanceSaturationEnabled);
        binding.tvSkinProtectionTip.setEnabled(enhanceSaturationEnabled);
        binding.seekbarColorProtection.setEnabled(enhanceSaturationEnabled);
        binding.seekbarSkinProtection.setEnabled(enhanceSaturationEnabled);
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
                startColorEnhancement();
            }

            @Override
            public void onUserHasAlreadyTurnedDown(String... permission) {
                PermissionUtils.showExplainDialog(getActivity(),permission, (dialog, which) -> requestPermissions(PERMISSIONS, REQUEST_CODE_PERMISSIONS));
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
                        startColorEnhancement();
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


    private void startColorEnhancement(){
        initializeEngine();
        startPreview();
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
        if(rtcEngine!=null){
            rtcEngine.stopPreview();
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
        if(localView!=null) {
            binding.mainContainer.addView(localView);
            localView.setZOrderOnTop(false);
            localView.setZOrderMediaOverlay(false);
        }
    }

}
