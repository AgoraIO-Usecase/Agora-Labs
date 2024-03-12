package io.agora.api.example.examples.video;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.UserManager;
import io.agora.api.example.common.adapter.MenuItemAdapter;
import io.agora.api.example.common.base.bean.CommonBean;
import io.agora.api.example.common.server.ApiManager;
import io.agora.api.example.common.server.api.ApiException;
import io.agora.api.example.common.server.api.ApiSubscriber;
import io.agora.api.example.common.server.model.BaseRes;
import io.agora.api.example.common.server.model.BaseResponse;
import io.agora.api.example.databinding.FragmentVirtualBgBinding;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;
import io.agora.api.example.common.widget.bubbleseekbar.BubbleSeekBar;
import io.agora.api.example.utils.AESUtils;
import io.agora.api.example.utils.FileUtils;
import io.agora.api.example.utils.GsonUtils;
import io.agora.api.example.utils.PermissionUtils;
import io.agora.api.example.utils.SPUtils;
import io.agora.api.example.utils.SchedulersUtil;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.ThreadUtils;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.SegmentationProperty;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VirtualBackgroundSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class VirtualBgFragment extends Fragment implements View.OnClickListener{
    private final String TAG="AgoraLab";
    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

    private FragmentVirtualBgBinding binding;
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

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding=FragmentVirtualBgBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.ivBack.setOnClickListener(this);
        binding.ivSwitchCamera.setOnClickListener(this);
        binding.optionOriginal.setOnClickListener(this);
        binding.splitGreenScreenSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            splitGreenEnabled=isChecked;
            setSplitGreenScreen(splitGreenEnabled);
        });
        initMenu();
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initMenu(){
        List<OptionItem> data=new ArrayList<>();
        data.add(new OptionItem(ID_BACKGROUND_BLUR,R.mipmap.ic_bg_blur,R.string.background_blur));
       // data.add(new OptionItem(ID_SPLIT_GREEN_SCREEN,R.mipmap.ic_split_green_screen,R.string.split_green_screen));
        data.add(new OptionItem(ID_MEETING_ROOM,R.mipmap.ic_meeting_room,R.string.meeting_room));
        data.add(new OptionItem(ID_LANDSCAPE,R.mipmap.ic_landscape,R.string.landscape));
        data.add(new OptionItem(ID_CUSTOMIZE,R.mipmap.ic_roi,R.string.customize));
        menuItemAdapter=new MenuItemAdapter();
        menuItemAdapter.setData(data);
        menuItemAdapter.setOnItemClickListener((v, optionItem, position) -> onMenuItemSelected(optionItem,position));
        binding.cvOptions.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.cvOptions.setAdapter(menuItemAdapter);

        binding.bgBlurSeekbar.setTickArray(getContext().getResources().getStringArray(R.array.blur_degree));
        binding.bgBlurSeekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            private int lastProgress=0;
            @Override public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                boolean fromUser) {
                super.onProgressChanged(bubbleSeekBar, progress, progressFloat, fromUser);
                if(binding.bgBlurSeekbar.getVisibility()!=View.VISIBLE){
                    return;
                }
                if(progress!=lastProgress) {
                    SystemUtil.vibrator(getContext());
                    lastProgress=progress;
                }
                setupBackgroundBlur(progressFloat);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                super.getProgressOnActionUp(bubbleSeekBar, progress, progressFloat);
            }

            @Override public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                boolean fromUser) {
                super.getProgressOnFinally(bubbleSeekBar, progress, progressFloat, fromUser);
            }
        });
        binding.originalTitle.setTextColor(getContext().getResources().getColor(R.color.white));
        binding.originalIcon.setBackgroundResource(R.drawable.bg_menu_item_selected);
    }

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
        if(item.getId()!=ID_BACKGROUND_BLUR && item.getId()!=ID_SPLIT_GREEN_SCREEN){
            binding.bgBlurSeekbar.hideBubble();
            binding.bgBlurSeekbar.getConfigBuilder().disableAlwaysShowBubble().build();
            binding.bgBlurSeekbar.setVisibility(View.GONE);
        }else if(item.getId()==ID_BACKGROUND_BLUR){
            binding.bgBlurSeekbar.setVisibility(View.VISIBLE);
        }
        SystemUtil.vibrator(getContext());
        if(item.getId()==ID_BACKGROUND_BLUR){
            binding.bgBlurSeekbar.getConfigBuilder().alwaysShowBubble().build();
            setupBackgroundBlur(binding.bgBlurSeekbar.getProgressFloat());
        }else if(item.getId()==ID_LANDSCAPE){
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
        } else {

            int ret=rtcEngine.enableVirtualBackground(true, virtualBackgroundSource, segmentationProperty);
            if(ret==-4){
                Toast.makeText(getContext(),R.string.device_performance_limitations,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setVirtualImgBg(String path,boolean report){
        virtualBackgroundSource.backgroundSourceType=VirtualBackgroundSource.BACKGROUND_IMG;
        virtualBackgroundSource.source=path;
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
                    //Log.d("AgoraLab","ApiManager---requestUploadPhoto---onSuccess");
                    if (UserManager.getInstance().getUser() != null && data != null && !TextUtils.isEmpty(data.getData().url)) {
                        ApiManager.getInstance()
                            .requestReportBackground(UserManager.getInstance().getUser().userNo, "agora_labs",
                                AESUtils.getS1(App.getInstance()), "agora_labs", data.getData().url)
                            .compose(SchedulersUtil.INSTANCE.applyApiSchedulers())
                            .subscribe(new ApiSubscriber<BaseResponse<BaseRes>>() {
                                @Override public void onSuccess(BaseResponse<BaseRes> baseResBaseResponse) {
                                    //Log.d("AgoraLab","ApiManager---requestReportBackground---success");
                                }

                                @Override public void onFailure(ApiException t) {
                                    //Log.d("AgoraLab","ApiManager---requestReportBackground---onFailure："+ GsonUtils.getGson().toJson(t));
                                }
                            });
                    }
                }

                @Override public void onFailure(ApiException t) {
                    //Log.d("AgoraLab","ApiManager---requestUploadPhoto---onFailure:"+GsonUtils.getGson().toJson(t));
                }
            }
        );
    }

    private void setVirtualVideoBg(String path){
        virtualBackgroundSource.backgroundSourceType=VirtualBackgroundSource.BACKGROUND_VIDEO;
        virtualBackgroundSource.source=path;
        int ret=rtcEngine.enableVirtualBackground(true,virtualBackgroundSource,segmentationProperty);
        if(ret==-4){
            Toast.makeText(getContext(),R.string.device_performance_limitations,Toast.LENGTH_SHORT).show();
        }
    }


    private void setupBackgroundBlur(float blur){
        virtualBackgroundSource.backgroundSourceType=VirtualBackgroundSource.BACKGROUND_BLUR;
        virtualBackgroundSource.color=0x000000;
        if(blur>=100) {
            virtualBackgroundSource.blurDegree=VirtualBackgroundSource.BLUR_DEGREE_HIGH;
        }else if(blur>=50){
            virtualBackgroundSource.blurDegree=VirtualBackgroundSource.BLUR_DEGREE_MEDIUM;
        }else if(blur>=0){
            virtualBackgroundSource.blurDegree=VirtualBackgroundSource.BLUR_DEGREE_LOW;
        }
        int ret=rtcEngine.enableVirtualBackground(true,virtualBackgroundSource,segmentationProperty);
        if(ret==-4){
            Toast.makeText(getContext(),R.string.device_performance_limitations,Toast.LENGTH_SHORT).show();
        }
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
                startVirtualBg();
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
                        startVirtualBg();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        //Toast.makeText(getActivity(), getString(R.string.need_permissions, Arrays.toString(permission)), Toast.LENGTH_SHORT)
                        //    .show();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        //Toast.makeText(getActivity(), getString(R.string.need_permissions, Arrays.toString(permission)), Toast.LENGTH_SHORT)
                        //    .show();
                        PermissionUtils.showToAppSettingDialog(getActivity());
                    }
                });
        }
    }




    private void startVirtualBg(){
        initializeEngine();
        startPreview();
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
        SurfaceView localView = RtcEngine.CreateRendererView(getContext());
        localView.setZOrderMediaOverlay(false);
        rtcEngine.enableVideo();
        rtcEngine.setupLocalVideo(new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 100));
        rtcEngine.startPreview();
        UserManager.getInstance().addCameraCount();
        binding.videoContainer.addView(localView);
        //Log.d("AgoraLab","startPreview:"+RtcEngineEx.getSdkVersion());
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
        }
    }

    private void disableVirtualBackground(){
        SystemUtil.vibrator(getContext());
        binding.bgBlurSeekbar.setVisibility(View.GONE);
        virtualBackgroundSource=new VirtualBackgroundSource();
        rtcEngine.enableVirtualBackground(false,null,null);
    }
    
    private void reset(){
        splitGreenEnabled=false;
        virtualBackgroundSource=new VirtualBackgroundSource();
        segmentationProperty.modelType = SegmentationProperty.SEG_MODEL_AI;
        menuItemAdapter.clearSelected();
        binding.splitGreenScreenSwitch.setChecked(false);
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
}
