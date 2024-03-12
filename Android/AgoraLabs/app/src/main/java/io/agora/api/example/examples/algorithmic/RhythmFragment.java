package io.agora.api.example.examples.algorithmic;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.UserManager;
import io.agora.api.example.common.adapter.MenuItemAdapter;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;
import io.agora.api.example.databinding.FragmentRhythmBinding;
import io.agora.api.example.utils.AESUtils;
import io.agora.api.example.utils.PermissionUtils;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.UIUtil;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaExtensionObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import java.util.ArrayList;
import java.util.List;

import static io.agora.rtc2.Constants.MediaSourceType.UNKNOWN_MEDIA_SOURCE;

public class RhythmFragment extends Fragment implements View.OnClickListener {
    private String TAG="metakitx";
    protected RtcEngine rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
    private FragmentRhythmBinding binding;

    private final int ID_FACE=0;
    private final int ID_LIGHT_SHADOW=1;
    private final int ID_HEAT_BEAT =2;
    private final int ID_BEFORE_AFTER=3;
    private final int ID_LEFT_RIGHT=4;
    private final int ID_UP_DOWN=5;

    private int rhythmMode=2;

    private boolean aiEnabled=false;

    private MenuItemAdapter menuItemAdapter;
    private final String[] PERMISSIONS = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    private final int REQUEST_CODE_PERMISSIONS = 1;



    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding= FragmentRhythmBinding.inflate(inflater,container,false);
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
            updateAi();
        });
        initView();
    }

    private void updateAi() {
        Log.d("AgoraLabs","---updateAi--:"+aiEnabled+" rhythmMode:"+rhythmMode);
        rtcEngine.enableExtension("agora_video_filters_portrait_rhythm", "portrait_rhythm", aiEnabled);
        if (aiEnabled) {
            rtcEngine.setExtensionProperty("agora_video_filters_portrait_rhythm", "portrait_rhythm", "mode", String.valueOf(rhythmMode));
        }
    }


    @Override public void onStart() {
        super.onStart();
        requestMorePermissions();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        if(rtcEngine!=null){
            rtcEngine.stopPreview();
            RtcEngine.destroy();
            rtcEngine=null;
        }
    }


    protected void initializeEngine() {
        if(rtcEngine!=null){
            return;
        }
        System.loadLibrary("agora_portrait_rhythm_extension");
        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = getContext().getApplicationContext();
        config.mAppId = AESUtils.getS1(App.getInstance());
        config.mChannelProfile = sceneMode;
        config.mEventHandler = new IRtcEngineEventHandler() {
        };
        config.mAudioScenario = Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT);
        config.mAreaCode = ((App)getActivity().getApplication()).getAreaCode();
        config.mExtensionObserver=new IMediaExtensionObserver(){

            @Override public void onEvent(String provider, String extension, String key, String value) {
                Log.d(TAG,"onEvent:"+provider+" extension:"+extension+" key:"+key+" value:"+value);
            }

            @Override public void onStarted(String provider, String extension) {

            }

            @Override public void onStopped(String provider, String extension) {

            }

            @Override public void onError(String provider, String extension, int error, String message) {

            }
        };
        config.addExtension("agora_video_filters_portrait_rhythm");
        try {
            rtcEngine = RtcEngine.create(config);
            rtcEngine.registerExtension("agora_video_filters_portrait_rhythm", "portrait_rhythm",
                Constants.MediaSourceType.UNKNOWN_MEDIA_SOURCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView(){
        List<OptionItem> data=new ArrayList<>();
        data.add(new OptionItem(ID_FACE,-1,R.string.rhythm_face));
        data.add(new OptionItem(ID_LIGHT_SHADOW,-1,R.string.rhythm_light_shadow));
        data.add(new OptionItem(ID_HEAT_BEAT,-1,R.string.rhythm_heat_beat));
        data.add(new OptionItem(ID_BEFORE_AFTER,-1,R.string.rhythm_front_after));
        data.add(new OptionItem(ID_LEFT_RIGHT,-1,R.string.rhythm_left_right));
        data.add(new OptionItem(ID_UP_DOWN,-1,R.string.rhythm_up_down));

        menuItemAdapter=new MenuItemAdapter();
        menuItemAdapter.setBgMode(MenuItemAdapter.BGMode.RECTANGLE);
        menuItemAdapter.setWidth(UIUtil.dip2px(getContext(),100));
        menuItemAdapter.setData(data);
        menuItemAdapter.setOnItemClickListener((v, optionItem, position) -> onMenuItemSelected(optionItem,position));
        menuItemAdapter.setSelectPos(1);
        binding.cvOptions.setLayoutManager(new GridLayoutManager(getContext(),3,GridLayoutManager.VERTICAL,false));
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
        if(item.getId()==ID_FACE){
            rhythmMode=6;
        }else if(item.getId()==ID_LIGHT_SHADOW){
            rhythmMode=2;
        }else if(item.getId()==ID_HEAT_BEAT){
            rhythmMode=1;
        }else if(item.getId()==ID_BEFORE_AFTER){
            rhythmMode=3;
        }else if(item.getId()==ID_LEFT_RIGHT){
            rhythmMode=5;
        }else if(item.getId()==ID_UP_DOWN){
            rhythmMode=4;
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
        initializeEngine();
        startPreview();
    }

    private void startPreview(){
        rtcEngine.enableVideo();

        VideoEncoderConfiguration configuration=new VideoEncoderConfiguration();
        configuration.dimensions=new VideoEncoderConfiguration.VideoDimensions(720,1280);
        configuration.bitrate= 2260;
        configuration.frameRate=30;
        rtcEngine.setVideoEncoderConfiguration(configuration);

        SurfaceView localView = RtcEngine.CreateRendererView(getContext());
        localView.setZOrderMediaOverlay(false);

        rtcEngine.setupLocalVideo(new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 100));
        rtcEngine.startPreview();
        UserManager.getInstance().addCameraCount();
        binding.mainContainer.addView(localView);
        Log.d("AgoraLab","startPreview:"+RtcEngineEx.getSdkVersion());
    }



}
