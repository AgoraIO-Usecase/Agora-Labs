package io.agora.api.example.examples.video.beauty;

import android.os.Bundle;
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
import io.agora.api.example.App;
import io.agora.api.example.R;
import io.agora.api.example.common.adapter.OnItemClickListener;
import io.agora.api.example.common.widget.bubbleseekbar.BubbleSeekBar;
import io.agora.api.example.common.widget.slidingmenu.MenuPage;
import io.agora.api.example.common.widget.slidingmenu.SlidingMenuLayout;
import io.agora.api.example.databinding.FragmentBeautyBinding;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;
import io.agora.api.example.utils.FileUtils;
import io.agora.api.example.utils.SPUtils;
import io.agora.api.example.utils.SystemUtil;
import io.agora.api.example.utils.ThreadUtils;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaExtensionObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.RtcEngineEx;
import io.agora.rtc2.video.BeautyOptions;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rte.extension.faceunity.ExtensionManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.agora.rtc2.video.BeautyOptions.LIGHTENING_CONTRAST_HIGH;

public class BeautyFragment extends Fragment implements View.OnClickListener, IMediaExtensionObserver {
    private final String TAG="AgoraLab";

    protected RtcEngineEx rtcEngine;
    protected int sceneMode= Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;

    private FragmentBeautyBinding binding;
    private MenuPage xiangxinMenu;
    private MenuPage agoraMenu;
    private SlidingMenuLayout volcBeautyMenu;
    private Map<String,Integer> faceUnityMap=new HashMap<>();
    private FuRender fuRender;
    private VolcRender volcRender;
    private AgoraRender agoraRender;
    private String KEY_FACEUNITY_RESOURCE="faceunity_resource";
    private String KEY_VOLC_RESOURCE="volc_resource";

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
        config.mExtensionObserver=this;
        config.addExtension("AgoraFaceUnityExtension");
        config.addExtension("AgoraByteDanceExtension");
        config.mAudioScenario = Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT);
        config.mAreaCode = ((App)getActivity().getApplication()).getAreaCode();
        Log.d(TAG,"version:"+RtcEngine.getSdkVersion());
        try {
            rtcEngine = (RtcEngineEx)RtcEngineEx.create(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding = FragmentBeautyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.ivBack.setOnClickListener(this);
        binding.ivSwitchCamera.setOnClickListener(this);
        binding.tvAgora.setOnClickListener(this);
        binding.tvXiangxin.setOnClickListener(this);
        binding.tvVolc.setOnClickListener(this);
        binding.seekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                boolean fromUser) {
                super.onProgressChanged(bubbleSeekBar, progress, progressFloat, fromUser);
                if(binding.tvXiangxin.isSelected()){
                    fuRender.setCurrentProgress(progress);
                }else if(binding.tvVolc.isSelected()){
                    volcRender.setCurrentProgress(progress);
                }else if(binding.tvAgora.isSelected()){
                    agoraRender.setCurrentProgress(progress);
                }

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
    }

    @Override public void onStart() {
        super.onStart();
        initializeEngine();
        enableExtension(true);
        initExtension();
        startPreview();
        copyResource();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        rtcEngine.stopPreview();
        if(rtcEngine!=null){
            RtcEngineEx.destroy();
            rtcEngine=null;
        }
    }

    private void initExtension(){
        if(fuRender==null){
            fuRender=new FuRender(getContext().getApplicationContext(),rtcEngine);
            fuRender.initExtension();
        }
        if(volcRender==null){
            volcRender=new VolcRender(getContext().getApplicationContext(),rtcEngine);
            volcRender.initExtension();
        }
        if(agoraRender==null){
            agoraRender=new AgoraRender(getContext().getApplicationContext(),rtcEngine);
        }
    }

    private  void enableExtension(boolean enabled) {
        ExtensionManager.getInstance(rtcEngine).initialize();
        rtcEngine.enableExtension("FaceUnity", "Effect", enabled);
        io.agora.rte.extension.bytedance.ExtensionManager.getInstance(rtcEngine).initialize(getContext());
        rtcEngine.enableExtension("ByteDance", "Effect", enabled);
    }

    private void startPreview() {
        SurfaceView localView = RtcEngine.CreateRendererView(getContext());
        localView.setZOrderMediaOverlay(false);
        rtcEngine.enableVideo();
        rtcEngine.setupLocalVideo(new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 100));
        rtcEngine.startPreview();
        binding.videoContainer.removeAllViews();
        binding.videoContainer.addView(localView);
    }

    @Override public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            Navigation.findNavController(v).popBackStack();
        } else if (v.getId() == R.id.iv_switch_camera) {
            rtcEngine.switchCamera();
        } else if (v.getId() == R.id.tv_agora) {
            setSelectButton(v);
            showAgoraMenu();
        } else if (v.getId() == R.id.tv_xiangxin) {
            setSelectButton(v);
            showXiangxinMenu();
        } else if (v.getId() == R.id.tv_volc) {
            setSelectButton(v);
            showVolcMenu();
        }
    }

    private void setSelectButton(View button){
        binding.tvAgora.setSelected(button.getId()==R.id.tv_agora);
        binding.tvXiangxin.setSelected(button.getId()==R.id.tv_xiangxin);
        binding.tvVolc.setSelected(button.getId()==R.id.tv_volc);
    }

    private void showAgoraMenu(){
        fuRender.disableExtension();
        volcRender.disableExtension();

        binding.seekbar.setVisibility(View.GONE);
        if(agoraMenu==null) {
            agoraMenu = new MenuPage(getContext());
            agoraMenu.addMenuItems(agoraRender.generatorOptionItems());
            agoraMenu.addFixMenuItem(new OptionItem(-1, R.mipmap.ic_ban, R.string.original_image));
            agoraMenu.setOnItemClickListener(new OnItemClickListener() {
                @Override public void onItemClick(View v, OptionItem optionItem, int position) {
                    agoraMenu.setSelected(optionItem);
                    agoraRender.setSelectedID(optionItem.getId());
                    if(optionItem.getId()==-1){
                        agoraRender.disableExtension();
                        binding.seekbar.setVisibility(View.GONE);
                    }else{
                        agoraRender.enableExtension();
                        binding.seekbar.setVisibility(View.VISIBLE);
                        binding.seekbar.setProgress(agoraRender.getCurrentProgress());
                    }
                }
            });
        }
        binding.menuContainer.setVisibility(View.VISIBLE);
        binding.menuContainer.removeAllViews();
        binding.menuContainer.addView(agoraMenu);
    }

    private void showXiangxinMenu() {

        if(!isFuResourceReady()){
            Toast.makeText(getContext(),R.string.resource_preparing,Toast.LENGTH_SHORT).show();
            return;
        }
        rtcEngine.setBeautyEffectOptions(false,new BeautyOptions());
        volcRender.disableExtension();

        binding.seekbar.setVisibility(View.GONE);
        fuRender.enableExtension();
        fuRender.loadAIModels();
        fuRender.choiceComposer();
        fuRender.updateValue();
        if(xiangxinMenu==null) {
            xiangxinMenu = new MenuPage(getContext());
            xiangxinMenu.addMenuItems(fuRender.generatorOptionItems());
            xiangxinMenu.addFixMenuItem(new OptionItem(-1, R.mipmap.ic_ban, R.string.original_image));
            xiangxinMenu.setOnItemClickListener(new OnItemClickListener() {
                @Override public void onItemClick(View v, OptionItem optionItem, int position) {
                    xiangxinMenu.setSelected(optionItem);
                    fuRender.setSelectedID(optionItem.getId());
                    if(optionItem.getId()==-1){
                        rtcEngine.enableExtension("FaceUnity", "Effect", false);
                        binding.seekbar.setVisibility(View.GONE);
                    }else{
                        rtcEngine.enableExtension("FaceUnity", "Effect", true);
                        binding.seekbar.setVisibility(View.VISIBLE);
                    }
                    binding.seekbar.setProgress(fuRender.getCurrentProgress());
                }
            });
        }
        binding.menuContainer.setVisibility(View.VISIBLE);
        binding.menuContainer.removeAllViews();
        binding.menuContainer.addView(xiangxinMenu);
    }

    private void showVolcMenu() {
        if(!isVolcResourceReady()){
            Toast.makeText(getContext(),R.string.resource_preparing,Toast.LENGTH_SHORT).show();
            return;
        }
        fuRender.disableExtension();
        rtcEngine.setBeautyEffectOptions(false,new BeautyOptions());

        binding.seekbar.setVisibility(View.GONE);
        volcRender.enableExtension();
        if(volcBeautyMenu==null) {
            volcBeautyMenu=new SlidingMenuLayout(getContext());
            volcBeautyMenu.addFixMenuItem(new OptionItem(-1, R.mipmap.ic_ban, R.string.original_image));
            volcBeautyMenu.setData(volcRender.generatorOptionItems());
            List<String> titles=new ArrayList<>();
            titles.add(getResources().getString(R.string.beauty_skin));
            titles.add(getResources().getString(R.string.micro_plastic));
            titles.add(getResources().getString(R.string.style_makeup));
            titles.add(getResources().getString(R.string.sticker));
            titles.add(getResources().getString(R.string.makeups));
            volcBeautyMenu.setTitles(titles);
            volcBeautyMenu.setOnTitleClickLister(new SlidingMenuLayout.OnTitleClickLister() {
                @Override public void onTitleSelected(int index) {
                    if(index==0||index==1){
                        binding.seekbar.setVisibility(View.VISIBLE);
                    }else{
                        binding.seekbar.setVisibility(View.GONE);
                    }
                }
            });
            volcBeautyMenu.setOnItemClickListener(new OnItemClickListener() {
                @Override public void onItemClick(View v, OptionItem optionItem, int position) {
                    if(optionItem.getId()==-1){
                        rtcEngine.enableExtension("ByteDance", "Effect", false);
                    }else{
                        rtcEngine.enableExtension("ByteDance", "Effect", true);
                    }
                    volcBeautyMenu.setSelected(optionItem);
                    volcRender.setSelectedFeatureID(optionItem.getId());
                    int progress=volcRender.getProgress(optionItem.getId());
                    if(progress<0){
                        binding.seekbar.setVisibility(View.GONE);
                    }else {
                        binding.seekbar.setVisibility(View.VISIBLE);
                        binding.seekbar.setProgress(progress);
                    }
                }
            });
         }
        binding.menuContainer.setVisibility(View.VISIBLE);
        binding.menuContainer.removeAllViews();
        binding.menuContainer.addView(volcBeautyMenu);
    }


    public void copyResource() {
        ThreadUtils.runOnNonUI(new Runnable() {
            @Override public void run() {
                if (!isFuResourceReady()) {
                    File file=new File(getContext().getExternalFilesDir("assets")+"/faceunity");
                    if(file.exists()){
                        FileUtils.deleteFile(file);
                    }
                    FileUtils.copyFilesFromAssets(App.getInstance(),"faceunity",App.getInstance().getExternalFilesDir("assets")+"/faceunity");
                    SPUtils.getInstance(getContext(), "user").put(KEY_FACEUNITY_RESOURCE, true);
                    SPUtils.getInstance(getContext(), "user").put("versionCode", SystemUtil.getVersionCode(App.getInstance()));
                }
                if(!isVolcResourceReady()){
                    File file=new File(getContext().getExternalFilesDir("assets")+"/bytedance");
                    if(file.exists()){
                        FileUtils.deleteFile(file);
                    }
                    FileUtils.copyFilesFromAssets(App.getInstance(),"bytedance",App.getInstance().getExternalFilesDir("assets")+"/bytedance");
                    SPUtils.getInstance(getContext(), "user").put(KEY_VOLC_RESOURCE, true);
                    SPUtils.getInstance(getContext(), "user").put("versionCode", SystemUtil.getVersionCode(App.getInstance()));
                }
            }
        });
    }

    private boolean isFuResourceReady() {
        int versionCode= SystemUtil.getVersionCode(App.getInstance());
        boolean resourceReady = SPUtils.getInstance(getContext(), "user").getBoolean(KEY_FACEUNITY_RESOURCE, false);
        int preVersioncode = SPUtils.getInstance(getContext(), "user").getInt("versionCode", 0);
        return resourceReady && (versionCode == preVersioncode);
    }

    private boolean isVolcResourceReady() {
        int versionCode=SystemUtil.getVersionCode(App.getInstance());
        boolean resourceReady = SPUtils.getInstance(getContext(), "user").getBoolean(KEY_VOLC_RESOURCE, false);
        int preVersioncode = SPUtils.getInstance(getContext(), "user").getInt("versionCode", 0);
        return resourceReady && (versionCode == preVersioncode);
    }

    @Override public void onEvent(String provider, String extension, String key, String value) {
        Log.d(TAG,"onEvent:"+provider+" extension:"+extension+" key"+key+" value"+value);
    }

    @Override public void onStarted(String provider, String extension) {
        Log.d(TAG,"onStarted:"+provider+" extension:"+extension);
    }

    @Override public void onStopped(String provider, String extension) {
        Log.d(TAG,"onStopped:"+provider+" extension:"+extension);
    }

    @Override public void onError(String provider, String extension, int error, String message) {
        Log.d(TAG,"onError:"+provider+" extension:"+extension+" error:"+error+" message:"+message);
    }
}
