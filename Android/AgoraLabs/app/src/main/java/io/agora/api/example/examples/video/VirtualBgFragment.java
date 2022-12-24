package io.agora.api.example.examples.video;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.agora.api.example.R;
import io.agora.api.example.common.adapter.MenuItemAdapter;
import io.agora.api.example.common.adapter.OnItemClickListener;
import io.agora.api.example.databinding.FragmentVirtualBgBinding;
import io.agora.api.example.examples.BaseFeatureFragment;
import io.agora.api.example.model.OptionItem;
import io.agora.api.example.common.widget.bubbleseekbar.BubbleSeekBar;
import io.agora.api.example.utils.FileUtils;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.video.SegmentationProperty;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.video.VirtualBackgroundSource;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class VirtualBgFragment extends BaseFeatureFragment implements View.OnClickListener{
    private FragmentVirtualBgBinding binding;
    private final int ID_ORIGINAL=1;
    private final int ID_BACKGROUND_BLUR=2;
    private final int ID_SPLIT_GREEN_SCREEN=3;
    private final int ID_LANDSCAPE=4;
    private final int ID_MEETING_ROOM=5;
    private final int ID_CUSTOMIZE=6;
    private final int GALLERY_REQUEST_CODE=1000;
    private MenuItemAdapter menuItemAdapter;
    private VirtualBackgroundSource virtualBackgroundSource=new VirtualBackgroundSource();
    private SegmentationProperty segmentationProperty=new SegmentationProperty();
    private boolean splitGreenEnabled=false;
    private List<OptionItem> selectedOptionItems =new ArrayList<>();
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
        initMenu();
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initMenu(){
        List<OptionItem> data=new ArrayList<>();
        data.add(new OptionItem(ID_BACKGROUND_BLUR,R.mipmap.ic_bg_blur,R.string.background_blur));
        data.add(new OptionItem(ID_SPLIT_GREEN_SCREEN,R.mipmap.ic_split_green_screen,R.string.split_green_screen));
        data.add(new OptionItem(ID_LANDSCAPE,R.mipmap.ic_landscape,R.string.landscape));
        data.add(new OptionItem(ID_MEETING_ROOM,R.mipmap.ic_meeting_room,R.string.meeting_room));
        data.add(new OptionItem(ID_CUSTOMIZE,R.mipmap.ic_roi,R.string.customize));
        menuItemAdapter=new MenuItemAdapter();
        menuItemAdapter.setData(data);
        menuItemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override public void onItemClick(View v, OptionItem optionItem, int position) {
                onMenuItemSelected(optionItem,position);
            }
        });
        binding.cvOptions.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.cvOptions.setAdapter(menuItemAdapter);

        binding.bgBlurSeekbar.setTickArray(getContext().getResources().getStringArray(R.array.blur_degree));
        binding.bgBlurSeekbar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                boolean fromUser) {
                super.onProgressChanged(bubbleSeekBar, progress, progressFloat, fromUser);
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
    }

    private void onMenuItemSelected(OptionItem item,int position){
        binding.bgBlurSeekbar.setVisibility(item.getId()==ID_BACKGROUND_BLUR?View.VISIBLE:View.GONE);
        if(item.getId()==ID_BACKGROUND_BLUR){
            binding.bgBlurSeekbar.getConfigBuilder().alwaysShowBubble().build();
            setupBackgroundBlur(binding.bgBlurSeekbar.getProgressFloat());
        }else if(item.getId()==ID_LANDSCAPE){
            setVirtualImgBg(getContext().getExternalFilesDir("blur")+"/landscape.jpeg");
        }else if(item.getId()==ID_MEETING_ROOM){
            setVirtualImgBg(getContext().getExternalFilesDir("blur")+"/meeting_room.jpeg");
        }else if(item.getId()==ID_SPLIT_GREEN_SCREEN){
            splitGreenEnabled=!splitGreenEnabled;
            setSplitGreenScreen(splitGreenEnabled);
        }else if(item.getId()==ID_CUSTOMIZE){
            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(pickIntent,GALLERY_REQUEST_CODE);
        }
        setSelectedMenus(item);
    }

    private void setSelectedMenus(OptionItem item){
        if(item==null){
           selectedOptionItems.clear();
        }else if(item.getId()==ID_SPLIT_GREEN_SCREEN){
            if(splitGreenEnabled){
                selectedOptionItems.add(item);
            }else{
                selectedOptionItems.remove(item);
            }
        }else {
            selectedOptionItems.clear();
            selectedOptionItems.add(item);
            if(splitGreenEnabled){
                List<OptionItem> list=menuItemAdapter.getData();
                for(OptionItem optionItem :list){
                    if(optionItem.getId()==ID_SPLIT_GREEN_SCREEN){
                        selectedOptionItems.add(optionItem);
                        break;
                    }
                }
            }
        }
        menuItemAdapter.setSelected(selectedOptionItems);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!= RESULT_OK){
            return;
        }
        if(requestCode==GALLERY_REQUEST_CODE){
           Uri uri= data.getData();
           String path= FileUtils.getRealPath(getContext().getApplicationContext(),uri);
           setVirtualImgBg(path);
        }
    }

    private void setSplitGreenScreen(boolean enabled){
        virtualBackgroundSource.backgroundSourceType=VirtualBackgroundSource.BACKGROUND_COLOR;
        if(enabled) {
            segmentationProperty.modelType = SegmentationProperty.SEG_MODEL_GREEN;
            segmentationProperty.greenCapacity = 0.8f;
        }else{
            segmentationProperty.modelType = SegmentationProperty.SEG_MODEL_AI;
        }
        rtcEngine.enableVirtualBackground(true,virtualBackgroundSource,segmentationProperty);
    }
    private void setVirtualImgBg(String path){
        virtualBackgroundSource.backgroundSourceType=VirtualBackgroundSource.BACKGROUND_IMG;
        virtualBackgroundSource.source=path;
        rtcEngine.enableVirtualBackground(true,virtualBackgroundSource,segmentationProperty);
    }


    private void setupBackgroundBlur(float blur){
        virtualBackgroundSource.backgroundSourceType=VirtualBackgroundSource.BACKGROUND_BLUR;
        virtualBackgroundSource.color=0x000000;
        if(blur>2) {
            virtualBackgroundSource.blurDegree=VirtualBackgroundSource.BLUR_DEGREE_HIGH;
        }else if(blur>=1){
            virtualBackgroundSource.blurDegree=VirtualBackgroundSource.BLUR_DEGREE_MEDIUM;
        }else if(blur>=0){
            virtualBackgroundSource.blurDegree=VirtualBackgroundSource.BLUR_DEGREE_LOW;
        }
        rtcEngine.enableVirtualBackground(true,virtualBackgroundSource,segmentationProperty);
    }

    @Override public void onStart() {
        super.onStart();
        startPreview();
    }


    private void startPreview(){
        SurfaceView localView = RtcEngine.CreateRendererView(getContext());
        localView.setZOrderMediaOverlay(false);
        rtcEngine.enableVideo();
        rtcEngine.setupLocalVideo(new VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 100));
        rtcEngine.startPreview();
        binding.videoContainer.addView(localView);
    }

    @Override public void onClick(View v) {
        if(v.getId()== R.id.iv_back){
            Navigation.findNavController(v).popBackStack();
        }else if(v.getId()==R.id.iv_switch_camera){
            rtcEngine.switchCamera();
        }else if(v.getId()==R.id.option_original){
            binding.bgBlurSeekbar.setVisibility(View.GONE);
            rtcEngine.enableVirtualBackground(false,null,null);
            reset();
        }
    }
    
    private void reset(){
        splitGreenEnabled=false;
        segmentationProperty.modelType = SegmentationProperty.SEG_MODEL_AI;
        setSelectedMenus(null);
    }
}
