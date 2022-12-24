package io.agora.api.example.examples.video;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import io.agora.api.example.R;
import io.agora.api.example.common.adapter.OnItemClickListener;
import io.agora.api.example.common.widget.bubbleseekbar.BubbleSeekBar;
import io.agora.api.example.common.widget.slidingmenu.MenuPage;
import io.agora.api.example.common.widget.slidingmenu.SlidingMenuLayout;
import io.agora.api.example.databinding.FragmentBeautyBinding;
import io.agora.api.example.examples.BaseFeatureFragment;
import io.agora.api.example.beauty.FuRender;
import io.agora.api.example.beauty.VolcRender;
import io.agora.api.example.model.OptionItem;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.BeautyOptions;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rte.extension.faceunity.ExtensionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.agora.rtc2.video.BeautyOptions.LIGHTENING_CONTRAST_HIGH;

public class BeautyFragment extends BaseFeatureFragment implements View.OnClickListener {
    private final String TAG="AgoraLab";
    private FragmentBeautyBinding binding;




    private MenuPage xiangxinMenu;
    private SlidingMenuLayout volcBeautyMenu;
    private Map<String,Integer> faceUnityMap=new HashMap<>();
    private FuRender fuRender;
    private VolcRender volcRender;
    private OnItemClickListener onItemClickListener=new OnItemClickListener() {
        @Override public void onItemClick(View v, OptionItem optionItem, int position) {

        }
    };

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
        enableExtension(true);
        initExtension();
        startPreview();
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
    }

    private  void enableExtension(boolean enabled) {
        ExtensionManager.getInstance(rtcEngine).initialize();
        rtcEngine.enableExtension("FaceUnity", "Effect", enabled);
        //rtcEngine.enableExtension("ByteDance", "Effect", enabled);
        io.agora.rte.extension.bytedance.ExtensionManager.getInstance(rtcEngine).initialize(getContext());
        rtcEngine.enableExtension("ByteDance", "Effect", enabled);
    }

    private void startPreview() {
        SurfaceView localView = RtcEngine.CreateRendererView(getContext());
        localView.setZOrderMediaOverlay(false);
        rtcEngine.enableVideo();
        rtcEngine.setupLocalVideo(new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 100));
        rtcEngine.startPreview();
        binding.videoContainer.addView(localView);
    }


    @Override public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            Navigation.findNavController(v).popBackStack();
        } else if (v.getId() == R.id.iv_switch_camera) {
            rtcEngine.switchCamera();
        } else if (v.getId() == R.id.tv_agora) {
            setSelectButton(v);
            binding.seekbar.setVisibility(View.GONE);
            binding.menuContainer.setVisibility(View.GONE);
            fuRender.disableExtension();
            setAgoraBeauty();
        } else if (v.getId() == R.id.tv_xiangxin) {
            setSelectButton(v);
            showXiangxinMenu();
        } else if (v.getId() == R.id.tv_volc) {
            setSelectButton(v);
            showVolcMenu();
        }
    }


    private void setAgoraBeauty(){
        BeautyOptions beautyOptions=new BeautyOptions();
        beautyOptions.lighteningContrastLevel=LIGHTENING_CONTRAST_HIGH;
        beautyOptions.lighteningLevel=0.8f;
        beautyOptions.sharpnessLevel=0.3f;
        beautyOptions.rednessLevel=0.5f;
        beautyOptions.smoothnessLevel=0.7f;
        rtcEngine.setBeautyEffectOptions(true,beautyOptions);
    }

    private void setSelectButton(View button){
        binding.tvAgora.setSelected(button.getId()==R.id.tv_agora);
        binding.tvXiangxin.setSelected(button.getId()==R.id.tv_xiangxin);
        binding.tvVolc.setSelected(button.getId()==R.id.tv_volc);
    }

    private void showXiangxinMenu() {
        //initExtension();
        rtcEngine.setBeautyEffectOptions(false,new BeautyOptions());
        volcRender.disableExtension();
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
        rtcEngine.setBeautyEffectOptions(false,new BeautyOptions());
        fuRender.disableExtension();
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
                    volcBeautyMenu.setSelected(optionItem);
                    volcRender.setSelectedFeatureID(optionItem.getId());
                    if(optionItem.getId()==-1){
                        rtcEngine.enableExtension("ByteDance", "Effect", false);
                        binding.seekbar.setVisibility(View.GONE);
                    }else{
                        rtcEngine.enableExtension("ByteDance", "Effect", true);
                        binding.seekbar.setVisibility(View.VISIBLE);
                    }
                    binding.seekbar.setProgress(volcRender.getProgress(optionItem.getId()));
                }
            });
         }
        binding.menuContainer.setVisibility(View.VISIBLE);
        binding.menuContainer.removeAllViews();
        binding.menuContainer.addView(volcBeautyMenu);
    }


}
