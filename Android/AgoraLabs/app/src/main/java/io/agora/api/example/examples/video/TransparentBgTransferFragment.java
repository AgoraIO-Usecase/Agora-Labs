package io.agora.api.example.examples.video;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.TokenGenerator;
import io.agora.api.example.common.UserManager;
import io.agora.api.example.common.adapter.MenuItemAdapter;
import io.agora.api.example.common.base.bean.CommonBean;
import io.agora.api.example.common.server.ApiManager;
import io.agora.api.example.common.server.api.ApiException;
import io.agora.api.example.common.server.api.ApiSubscriber;
import io.agora.api.example.common.server.model.BaseRes;
import io.agora.api.example.common.server.model.BaseResponse;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;
import io.agora.api.example.databinding.FragmentTransparentBgTransferBinding;
import io.agora.api.example.utils.AESUtils;
import io.agora.api.example.utils.FileUtils;
import io.agora.api.example.utils.PermissionUtils;
import io.agora.api.example.utils.SPUtils;
import io.agora.api.example.utils.SchedulersUtil;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.ThreadUtils;
import io.agora.api.example.utils.UIUtil;
import io.agora.mediaplayer.IMediaPlayer;
import io.agora.mediaplayer.data.MediaPlayerSource;
import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.LocalTranscoderConfiguration;
import io.agora.rtc2.RtcConnection;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.CameraCapturerConfiguration;
import io.agora.rtc2.video.SegmentationProperty;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VideoEncoderConfiguration;
import io.agora.rtc2.video.VirtualBackgroundSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static android.app.Activity.RESULT_OK;

public class TransparentBgTransferFragment extends Fragment implements View.OnClickListener{
    private final String TAG="AgoraLab";
    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

    private FragmentTransparentBgTransferBinding binding;
    private final int ID_ORIGINAL=1;
    private final int ID_BACKGROUND_BLUR=2;
    private final int ID_SPLIT_GREEN_SCREEN=3;
    private final int ID_LANDSCAPE=4;
    private final int ID_MEETING_ROOM=5;
    private final int ID_CUSTOMIZE=6;
    private final int GALLERY_REQUEST_CODE=1000;
    private final int VIDEO_REQUEST_CODE=1001;
    private final int MEDIA_REQUEST_CODE=1002;
    private MenuItemAdapter menuItemAdapter;
    private VirtualBackgroundSource virtualBackgroundSource=new VirtualBackgroundSource();
    private SegmentationProperty segmentationProperty=new SegmentationProperty();
    private boolean splitGreenEnabled=false;

    private RtcConnection rtcConnection;
    private String channelName= String.valueOf((int) (Math.random() * 1000));
    private int senderUid =101;
    private int remoteUid=102;
    private SurfaceView remoteView;
    private VideoEncoderConfiguration configuration;
    private boolean isBgTransferEnabled=false;
    LocalTranscoderConfiguration.TranscodingVideoStream bgStream;
    private String KEY_SHOW_GUIDE="show_guide";

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding= FragmentTransparentBgTransferBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.ivBack.setOnClickListener(this);
        binding.ivSwitchCamera.setOnClickListener(this);
        binding.optionOriginal.setOnClickListener(this);
        binding.ivGuide.setOnClickListener(this);
        initMenu();
    }



    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initMenu(){
        List<OptionItem> data=new ArrayList<>();
        data.add(new OptionItem(ID_LANDSCAPE,R.mipmap.ic_landscape,R.string.landscape));
        data.add(new OptionItem(ID_MEETING_ROOM,R.mipmap.ic_meeting_room,R.string.meeting_room));
        data.add(new OptionItem(ID_CUSTOMIZE,R.mipmap.ic_roi,R.string.customize));
        menuItemAdapter=new MenuItemAdapter();
        menuItemAdapter.setData(data);
        menuItemAdapter.setOnItemClickListener((v, optionItem, position) -> onMenuItemSelected(optionItem,position));
        binding.cvOptions.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.cvOptions.setAdapter(menuItemAdapter);
        binding.originalTitle.setTextColor(getContext().getResources().getColor(R.color.white));
        binding.originalIcon.setBackgroundResource(R.drawable.bg_menu_item_selected);
        binding.cover.setOnTouchListener((v, event) -> {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            if(event.getAction()==MotionEvent.ACTION_DOWN){
                ConstraintLayout.LayoutParams lParams = (ConstraintLayout.LayoutParams) v.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
            }else if(event.getAction()==MotionEvent.ACTION_MOVE){
                updateLocalTranscoderConfiguration(X - _xDelta,Y - _yDelta);
            }else if(event.getAction()==MotionEvent.ACTION_UP){
                lastX=lastX+(X - _xDelta);
                lastY=lastY+(Y - _yDelta);

                int x=lastX*480/UIUtil.getScreenWidth(getContext());
                int y=lastY*640/UIUtil.getScreenHeight(getContext());
                if(y<0){
                    lastY=0;
                }
                if(y>400){
                    lastY=400*UIUtil.getScreenHeight(getContext())/640;
                }
                if(x<60){
                    lastX=0;
                }
                if(x>240){
                    lastX=240*UIUtil.getScreenWidth(getContext())/480;
                }

            }
            return true;
        });
    }

    private int _xDelta;
    private int _yDelta;

    private void onMenuItemSelected(OptionItem item,int position){
        binding.originalTitle.setTextColor(getContext().getResources().getColor(R.color.menu_text_default_color));
        binding.originalIcon.setBackgroundResource(R.drawable.bg_menu_item_normal);
        if (item.isSelected() && item.getId() != ID_SPLIT_GREEN_SCREEN) {
            disableVirtualBackground();
            if(splitGreenEnabled){
                setSplitGreenScreen(true);
            }
            //selectedOptionItems.remove(item);
            menuItemAdapter.setUnSelected(item);
            return;
        }
        SystemUtil.vibrator(getContext());
        /*if(item.getId()==ID_BACKGROUND_BLUR){
            binding.bgBlurSeekbar.getConfigBuilder().alwaysShowBubble().build();
            setupBackgroundBlur(binding.bgBlurSeekbar.getProgressFloat());
        }else*/ if(item.getId()==ID_LANDSCAPE){
            //setVirtualImgBg(getContext().getExternalFilesDir("blur")+"/landscape.jpeg");
            setVirtualVideoBg(getContext().getExternalFilesDir("blur")+"/bg_video.mp4");
        }else if(item.getId()==ID_MEETING_ROOM){
            setVirtualImgBg(getContext().getExternalFilesDir("blur")+"/meeting_room.jpg",false);
        }else if(item.getId()==ID_CUSTOMIZE){
            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "*/*");
            startActivityForResult(pickIntent,MEDIA_REQUEST_CODE);
        }
        setSelectedMenus(item);
    }

    private void setSelectedMenus(OptionItem item){
        if(item==null){
            //selectedOptionItems.clear();
            menuItemAdapter.clearSelected();
        }else {
            menuItemAdapter.setSelected(item,true);
        }
        menuItemAdapter.notifyDataSetChanged();
    }

    private int getSelectedCount(){
        int count=0;
        List<OptionItem> list=menuItemAdapter.getData();
        for(OptionItem optionItem :list){
            if(optionItem.isSelected()){
                count++;
            }
        }
        return count;
    }


    @Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST_CODE &&resultCode==RESULT_OK){
            Uri uri= data.getData();
            String path= FileUtils.getRealPath(getContext().getApplicationContext(),uri);
            if (TextUtils.isEmpty(path) || ((!path.endsWith(".png")) && (!path.endsWith(".jpg") && (!path.endsWith(".jpeg"))))) {
                Toast.makeText(getContext(), R.string.imge_type_not_support, Toast.LENGTH_SHORT).show();
                return;
            }
            setVirtualImgBg(path,true);
        }else if(requestCode==VIDEO_REQUEST_CODE &&resultCode==RESULT_OK){
            Uri uri= data.getData();
            String path= FileUtils.getRealPath(getContext().getApplicationContext(),uri);
            if (TextUtils.isEmpty(path) || ((!path.endsWith(".mp4")) && (!path.endsWith(".mov") && (!path.endsWith(".MP4")&& (!path.endsWith(".MOV")))))) {
                Toast.makeText(getContext(), R.string.video_type_not_support, Toast.LENGTH_SHORT).show();
                return;
            }
            File file=new File(path);
            if(file.length()>10*1024*1024){
                Toast.makeText(getContext(), R.string.video_file_too_large, Toast.LENGTH_SHORT).show();
                return;
            }
            setVirtualVideoBg(path);
        }else if(requestCode==MEDIA_REQUEST_CODE &&resultCode==RESULT_OK){
            Uri uri= data.getData();
            String path= FileUtils.getRealPath(getContext().getApplicationContext(),uri);
            if(TextUtils.isEmpty(path)){
                Toast.makeText(getContext(), R.string.media_type_not_support, Toast.LENGTH_SHORT).show();
                return;
            }
            boolean isVideo=path.endsWith(".mp4")||path.endsWith(".mov")||path.endsWith(".MP4")||path.endsWith(".MOV");
            boolean isImage=path.endsWith(".png")||path.endsWith(".jpg")||path.endsWith(".jpeg");
            if(isVideo){
                File file=new File(path);
                if(file.length()>10*1024*1024){
                    Toast.makeText(getContext(), R.string.video_file_too_large, Toast.LENGTH_SHORT).show();
                    return;
                }
                setVirtualVideoBg(path);
            }else if(isImage){
                setVirtualImgBg(path,true);
            }else{
                Toast.makeText(getContext(), R.string.media_type_not_support, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setSplitGreenScreen(boolean enabled){
        if(enabled) {
            segmentationProperty.modelType = SegmentationProperty.SEG_MODEL_GREEN;
            segmentationProperty.greenCapacity = 0.5f;
        }else{
            segmentationProperty.modelType = SegmentationProperty.SEG_MODEL_AI;
        }
        if (!enabled && getSelectedCount() ==0) {
            rtcEngine.enableVirtualBackground(false, null, null);
            isBgTransferEnabled=false;
        } else {
            int ret=rtcEngine.enableVirtualBackground(true, virtualBackgroundSource, segmentationProperty);
            if(ret==-4){
                Toast.makeText(getContext(),R.string.device_performance_limitations,Toast.LENGTH_SHORT).show();
            }
            isBgTransferEnabled=true;
        }
    }

    private void setVirtualImgBg(String path,boolean report){
        isBgTransferEnabled=true;
        virtualBackgroundSource=new VirtualBackgroundSource();

        //virtualBackgroundSource.backgroundSourceType=VirtualBackgroundSource.BACKGROUND_IMG;
        segmentationProperty.modelType=SegmentationProperty.SEG_MODEL_AI;

        bgStream =new LocalTranscoderConfiguration.TranscodingVideoStream();
        bgStream.imageUrl=path;
        bgStream.sourceType= Constants.VideoSourceType.VIDEO_SOURCE_RTC_IMAGE_JPEG;
        bgStream.mirror=true;
        bgStream.zOrder=1;
        bgStream.x=0;
        bgStream.y=0;
        bgStream.width=480;
        bgStream.height=640;

        //Log.d(TAG,"------imageUrl-----:"+path);
        updateLocalTranscoderConfiguration(0,0);

        int ret=rtcEngine.enableVirtualBackground(true,virtualBackgroundSource,segmentationProperty);
        if(ret==-4){
            Toast.makeText(getContext(),R.string.device_performance_limitations,Toast.LENGTH_SHORT).show();
        }
        if(!report){
            return;
        }
        ApiManager.getInstance().requestUploadPhoto(new File(path)).compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
            new ApiSubscriber<BaseResponse<CommonBean>>() {
                @Override public void onSuccess(BaseResponse<CommonBean> data) {
                    ////Log.d("AgoraLab","ApiManager---requestUploadPhoto---onSuccess");
                    if (UserManager.getInstance().getUser() != null && data != null && !TextUtils.isEmpty(data.getData().url)) {
                        ApiManager.getInstance()
                            .requestReportBackground(UserManager.getInstance().getUser().userNo, "agora_labs",
                                AESUtils.getS1(App.getInstance()), "agora_labs", data.getData().url)
                            .compose(SchedulersUtil.INSTANCE.applyApiSchedulers())
                            .subscribe(new ApiSubscriber<BaseResponse<BaseRes>>() {
                                @Override public void onSuccess(BaseResponse<BaseRes> baseResBaseResponse) {
                                    ////Log.d("AgoraLab","ApiManager---requestReportBackground---success");
                                }

                                @Override public void onFailure(ApiException t) {
                                    ////Log.d("AgoraLab","ApiManager---requestReportBackground---onFailure："+ GsonUtils.getGson().toJson(t));
                                }
                            });
                    }
                }

                @Override public void onFailure(ApiException t) {
                    ////Log.d("AgoraLab","ApiManager---requestUploadPhoto---onFailure:"+GsonUtils.getGson().toJson(t));
                }
            }
        );
    }

    private void updateLocalTranscoderConfiguration(int moveX,int moveY){
        if(rtcEngine!=null) {
            LocalTranscoderConfiguration configuration = setupLocalTranscoderConfiguration(moveX,moveY);
            rtcEngine.updateLocalTranscoderConfiguration(configuration);
        }
    }

    private void setVirtualVideoBg(String path){
        virtualBackgroundSource=new VirtualBackgroundSource();
        segmentationProperty.modelType=SegmentationProperty.SEG_MODEL_AI;


        bgStream =new LocalTranscoderConfiguration.TranscodingVideoStream();
        //bgStream.imageUrl=path;
        bgStream.sourceType= Constants.VideoSourceType.VIDEO_SOURCE_MEDIA_PLAYER;
        MediaPlayerSource source=new MediaPlayerSource();
        source.setAutoPlay(true);
        source.setUrl(path);
        IMediaPlayer mediaPlayer=rtcEngine.createMediaPlayer();
        mediaPlayer.setLoopCount(999);
        mediaPlayer.openWithMediaSource(source);
        bgStream.mediaPlayerId=mediaPlayer.getMediaPlayerId();
        bgStream.mirror=true;
        bgStream.zOrder=1;
        bgStream.x=0;
        bgStream.y=0;
        bgStream.width=480;
        bgStream.height=640;
        isBgTransferEnabled=true;
        updateLocalTranscoderConfiguration(0,0);
        int ret=rtcEngine.enableVirtualBackground(true,virtualBackgroundSource,segmentationProperty);
        if(ret==-4){
            Toast.makeText(getContext(),R.string.device_performance_limitations,Toast.LENGTH_SHORT).show();
        }
    }
    private int lastX=60;
    private int lastY=0;

    private LocalTranscoderConfiguration setupLocalTranscoderConfiguration(int moveX,int moveY){
        LocalTranscoderConfiguration configuration = new LocalTranscoderConfiguration();
            LocalTranscoderConfiguration.TranscodingVideoStream videoStream =
                new LocalTranscoderConfiguration.TranscodingVideoStream();
            if(isBgTransferEnabled) {
                if (binding.cover.getWidth() > 0) {
                    // //Log.d(TAG,"----:"+binding.cover.getWidth()+"-----"+binding.cover.getHeight());
                    int x=lastX+moveX;
                    int y=lastY+moveY;
                    videoStream.x=x*480/UIUtil.getScreenWidth(getContext());
                    videoStream.y=y*640/UIUtil.getScreenHeight(getContext());
                    //Log.d(TAG,"videoStream x y,----:"+videoStream.x+"-----"+videoStream.y);
                    if(videoStream.y<0){
                        videoStream.y=0;
                    }
                    if(videoStream.y>400){
                        videoStream.y=400;
                    }
                    if(videoStream.x<60){
                        videoStream.x=60;
                    }
                    if(videoStream.x>240){
                        videoStream.x=240;
                    }



                } else {
                    videoStream.x = 0;
                    videoStream.y = 0;
                }
                videoStream.width = 150;
                videoStream.height = 200;
            }else{
                videoStream.x=0;
                videoStream.y=0;
                videoStream.width=480;
                videoStream.height=640;
            }
        videoStream.mirror = true;
        videoStream.zOrder = 10;
        videoStream.alpha = 1;
        configuration.transcodingVideoStreams.add(videoStream);
        if (bgStream != null) {
            configuration.transcodingVideoStreams.add(bgStream);
        }

        VideoEncoderConfiguration videoEncoderConfiguration=new VideoEncoderConfiguration();
        videoEncoderConfiguration.bitrate=2000;
        videoEncoderConfiguration.frameRate=15;
        videoEncoderConfiguration.dimensions=new VideoEncoderConfiguration.VideoDimensions(480,640);
        configuration.videoOutputConfiguration=videoEncoderConfiguration;
        return configuration;
    }




    @Override public void onStart() {
        super.onStart();
        requestMorePermissions();
    }

    private final String[] PERMISSIONS = new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    private final int REQUEST_CODE_PERMISSIONS = 1;
    private void requestMorePermissions() {
        PermissionUtils.checkMorePermissions(getActivity(), PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                showGuideIfNeed();
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

    private void showGuideIfNeed(){
        if(SPUtils.getInstance(getContext()).getBoolean(KEY_SHOW_GUIDE)){
            startVirtualBg();
        }else{
            binding.ivGuide.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            PermissionUtils.onRequestMorePermissionsResult(getActivity(), PERMISSIONS,
                new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        showGuideIfNeed();
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




    private void startVirtualBg(){
        initializeEngine();
        //startPreview();
        setupSend();
        setupReceiver();
        copyResource();
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
        SurfaceView localView = new SurfaceView(getContext());
        localView.setZOrderMediaOverlay(false);
        localView.setZOrderOnTop(true);
        //localView.setBackgroundColor(Color.BLACK);
        rtcEngine.setupLocalVideo(new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 100));
        rtcEngine.startPreview();
        UserManager.getInstance().addCameraCount();
        binding.localVideoContainer.addView(localView);
    }

    @Override public void onClick(View v) {
        if(v.getId()== R.id.iv_back){
            Navigation.findNavController(v).popBackStack();
        }else if(v.getId()==R.id.iv_switch_camera){
            rtcEngine.switchCamera();
        }else if(v.getId()==R.id.option_original){
            binding.originalTitle.setTextColor(getContext().getResources().getColor(R.color.white));
            binding.originalIcon.setBackgroundResource(R.drawable.bg_menu_item_selected);
            disableVirtualBackground();
            reset();
        }else if(v.getId()==R.id.iv_guide){
            SPUtils.getInstance(getContext()).put(KEY_SHOW_GUIDE,true);
            binding.ivGuide.setVisibility(View.GONE);
            startVirtualBg();
        }
    }

    private void disableVirtualBackground(){
        SystemUtil.vibrator(getContext());
        //binding.bgBlurSeekbar.setVisibility(View.GONE);
        bgStream=null;
        virtualBackgroundSource=new VirtualBackgroundSource();
        rtcEngine.enableVirtualBackground(false,null,null);
        isBgTransferEnabled=false;
        updateLocalTranscoderConfiguration(0,0);
    }

    private void reset(){
        splitGreenEnabled=false;
        virtualBackgroundSource=new VirtualBackgroundSource();
        segmentationProperty.modelType = SegmentationProperty.SEG_MODEL_AI;
        menuItemAdapter.clearSelected();
        //binding.splitGreenScreenSwitch.setChecked(false);
    }

    public void copyResource() {
        ThreadUtils.runOnNonUI(() -> {
            if(!isResourceReady()){
                File file= getContext().getExternalFilesDir("blur");
                if(file.exists()){
                    FileUtils.deleteFile(file);
                }
                FileUtils.copyFilesFromAssets(getContext().getApplicationContext(),"blur",getContext().getApplicationContext().getExternalFilesDir("blur").toString());
                SPUtils.getInstance(getContext(), "user").put("blur_resource", true);
                SPUtils.getInstance(getContext(), "user").put("versionCode", SystemUtil.getVersionCode(App.getInstance()));
            }
        });
    }

    public boolean isResourceReady() {
        int versionCode=SystemUtil.getVersionCode(App.getInstance());
        boolean resourceReady = SPUtils.getInstance(getContext(), "user").getBoolean("blur_resource", false);
        int preVersioncode = SPUtils.getInstance(getContext(), "user").getInt("versionCode", 0);
        return resourceReady && (versionCode == preVersioncode);
    }

    private void setupSend(){
        rtcConnection=new RtcConnection();
        rtcConnection.channelId=channelName;
        rtcConnection.localUid= senderUid;
        setVideoConfig();
        rtcEngine.enableVideo();
        rtcEngine.disableAudio();

        CameraCapturerConfiguration cameraCapturerConfiguration=new CameraCapturerConfiguration(
            CameraCapturerConfiguration.CAMERA_DIRECTION.CAMERA_FRONT);

        rtcEngine.startCameraCapture(Constants.VideoSourceType.VIDEO_SOURCE_CAMERA_PRIMARY,cameraCapturerConfiguration);

        LocalTranscoderConfiguration configuration=setupLocalTranscoderConfiguration(0,0);
        rtcEngine.startLocalVideoTranscoder(configuration);

        startPreview();
        ChannelMediaOptions mediaOptions=new ChannelMediaOptions();
        mediaOptions.channelProfile= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        mediaOptions.clientRoleType= Constants.CLIENT_ROLE_BROADCASTER;
        mediaOptions.publishMicrophoneTrack = true;
        mediaOptions.publishTranscodedVideoTrack=true;
        TokenGenerator.INSTANCE.generateToken(rtcConnection.channelId, String.valueOf(rtcConnection.localUid),
            TokenGenerator.TokenGeneratorType.token006, TokenGenerator.AgoraTokenType.rtc,
            new Function1<String, Unit>() {
                @Override public Unit invoke(String token) {
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

                        @Override
                        public void onRtcStats(IRtcEngineEventHandler.RtcStats stats) {
                            //Log.d(TAG, "onRtcStats");
                        }

                        @Override
                        public void onVideoSizeChanged(Constants.VideoSourceType source, int uid, int width, int height,
                            int rotation) {
                            super.onVideoSizeChanged(source, uid, width, height, rotation);
                            //Log.d(TAG, "-----onVideoSizeChanged1---width:" + width + " height:" + height + " uid:" + uid);
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
        configuration.dimensions = new VideoEncoderConfiguration.VideoDimensions(480, 640);
        configuration.bitrate = 800;
        configuration.frameRate = 15;
        rtcEngine.setVideoEncoderConfigurationEx(configuration,rtcConnection);
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
        mediaOptions.clientRoleType= Constants.CLIENT_ROLE_AUDIENCE;


        TokenGenerator.INSTANCE.generateToken(rtcc.channelId, String.valueOf(rtcc.localUid),
            TokenGenerator.TokenGeneratorType.token006, TokenGenerator.AgoraTokenType.rtc,
            new Function1<String, Unit>() {
                @Override public Unit invoke(String token) {
                    joinChannel(token,rtcc,mediaOptions,new IRtcEngineEventHandler() {
                        @Override public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                            super.onJoinChannelSuccess(channel, uid, elapsed);
                            //Log.d(TAG, "receiver onJoinChannelSuccess--channel:" + channel + " uid:" + uid + " elapsed:" + elapsed);
                        }

                        @Override
                        public void onUserJoined(int uid, int elapsed) {
                            //Log.d(TAG, "---onUserJoined--uid:" + uid);
                            ThreadUtils.runOnUI(() -> {
                                remoteView = new SurfaceView(getContext());
                                VideoCanvas videoCanvas=new VideoCanvas(remoteView, VideoCanvas.RENDER_MODE_HIDDEN, uid);
                                videoCanvas.mirrorMode=0;
                                rtcEngine.setupRemoteVideoEx(videoCanvas, rtcc);
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
                            //Log.d(TAG, "-----onVideoSizeChanged2---width:" + width + " height:" + height + " uid:" + uid);
                        }
                    });
                    return null;
                }
            }, exception -> null);

    }

    private void addView(){
        binding.videoContainer.removeAllViews();
        if(remoteView!=null) {
            binding.videoContainer.addView(remoteView);
            remoteView.setZOrderOnTop(false);
            remoteView.setZOrderMediaOverlay(false);
        }
    }
}
