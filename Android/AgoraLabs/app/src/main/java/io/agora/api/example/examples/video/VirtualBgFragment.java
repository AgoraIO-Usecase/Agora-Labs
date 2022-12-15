package io.agora.api.example.examples.video;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import io.agora.api.example.R;
import io.agora.api.example.common.adapter.MenuItemAdapter;
import io.agora.api.example.databinding.FragmentVirtualBgBinding;
import io.agora.api.example.examples.BaseFeatureFragment;
import io.agora.api.example.model.MenuItem;
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
        List<MenuItem> data=new ArrayList<>();
        data.add(new MenuItem(ID_BACKGROUND_BLUR,R.mipmap.ic_bg_blur,R.string.background_blur));
        data.add(new MenuItem(ID_SPLIT_GREEN_SCREEN,R.mipmap.ic_split_green_screen,R.string.split_green_screen));
        data.add(new MenuItem(ID_LANDSCAPE,R.mipmap.ic_landscape,R.string.landscape));
        data.add(new MenuItem(ID_MEETING_ROOM,R.mipmap.ic_meeting_room,R.string.meeting_room));
        data.add(new MenuItem(ID_CUSTOMIZE,R.mipmap.ic_roi,R.string.customize));
        menuItemAdapter=new MenuItemAdapter();
        menuItemAdapter.setData(data);
        menuItemAdapter.setOnItemClickListener(new MenuItemAdapter.OnItemClickListener() {
            @Override public void onItemClick(View v, MenuItem menuItem, int position) {
                onMenuItemSelected(menuItem);
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

    private void onMenuItemSelected(MenuItem item){
        binding.bgBlurSeekbar.setVisibility(item.getId()==ID_BACKGROUND_BLUR?View.VISIBLE:View.GONE);
        if(item.getId()==ID_BACKGROUND_BLUR){
            binding.bgBlurSeekbar.getConfigBuilder().alwaysShowBubble().build();
            setupBackgroundBlur(binding.bgBlurSeekbar.getProgressFloat());
        }else if(item.getId()==ID_LANDSCAPE){
            setVirtualImgBg(getContext().getExternalFilesDir("blur")+"/landscape.jpeg");
        }else if(item.getId()==ID_MEETING_ROOM){
            setVirtualImgBg(getContext().getExternalFilesDir("blur")+"/meeting_room.jpeg");
        }else if(item.getId()==ID_SPLIT_GREEN_SCREEN){
            setSplitGreenScreen();
        }else if(item.getId()==ID_CUSTOMIZE){
            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(pickIntent,GALLERY_REQUEST_CODE);
        }
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

    private void setSplitGreenScreen(){
        VirtualBackgroundSource virtualBackgroundSource=new VirtualBackgroundSource();
        virtualBackgroundSource.backgroundSourceType=VirtualBackgroundSource.BACKGROUND_COLOR;
        SegmentationProperty segmentationProperty=new SegmentationProperty();
        segmentationProperty.modelType=SegmentationProperty.SEG_MODEL_GREEN;
        segmentationProperty.greenCapacity=0.8f;
        rtcEngine.enableVirtualBackground(true,virtualBackgroundSource,segmentationProperty);
    }
    private void setVirtualImgBg(String path){
        VirtualBackgroundSource virtualBackgroundSource=new VirtualBackgroundSource();
        virtualBackgroundSource.backgroundSourceType=VirtualBackgroundSource.BACKGROUND_IMG;
        virtualBackgroundSource.source=path;
        SegmentationProperty segmentationProperty=new SegmentationProperty();
        segmentationProperty.modelType=SegmentationProperty.SEG_MODEL_AI;
        rtcEngine.enableVirtualBackground(true,virtualBackgroundSource,segmentationProperty);
    }


    private void setupBackgroundBlur(float blur){
        VirtualBackgroundSource virtualBackgroundSource=new VirtualBackgroundSource();
        virtualBackgroundSource.backgroundSourceType=VirtualBackgroundSource.BACKGROUND_BLUR;
        virtualBackgroundSource.color=0x000000;
        if(blur>2) {
            virtualBackgroundSource.blurDegree=VirtualBackgroundSource.BLUR_DEGREE_HIGH;
        }else if(blur>=1){
            virtualBackgroundSource.blurDegree=VirtualBackgroundSource.BLUR_DEGREE_MEDIUM;
        }else if(blur>=0){
            virtualBackgroundSource.blurDegree=VirtualBackgroundSource.BLUR_DEGREE_LOW;
        }
        SegmentationProperty segmentationProperty=new SegmentationProperty();
        segmentationProperty.modelType=SegmentationProperty.SEG_MODEL_AI;
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
            menuItemAdapter.setSelectPos(-1);
            menuItemAdapter.notifyDataSetChanged();
            binding.bgBlurSeekbar.setVisibility(View.GONE);
            rtcEngine.enableVirtualBackground(false,null,null);
        }
    }
}
