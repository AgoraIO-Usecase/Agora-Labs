package io.agora.api.example.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.agora.api.example.R;
import io.agora.api.example.databinding.ResolutionMenuBinding;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;
import io.agora.api.example.utils.SystemUtil;
import java.util.ArrayList;
import java.util.List;

public class VideoFeatureMenu extends ConstraintLayout implements View.OnClickListener{
    public static final int RESOLUTION_360P=1;
    public static final int RESOLUTION_480P=2;
    public static final int RESOLUTION_540P=3;
    public static final int RESOLUTION_720P=4;
    public static final int RESOLUTION_1080P=5;


    private OnMenuOptionSelectedListener listener;
    private List<OptionItem> data;


    public void setListener(OnMenuOptionSelectedListener listener) {
        this.listener = listener;
    }

    private ResolutionMenuBinding binding;
    public VideoFeatureMenu(@NonNull Context context) {
        this(context,null);
    }

    public VideoFeatureMenu(@NonNull Context context,
        @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VideoFeatureMenu(@NonNull Context context,
        @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding=ResolutionMenuBinding.inflate(LayoutInflater.from(context),this,true);
        initView();
    }

    private void initView(){
        if(data==null) {
            data = new ArrayList<>();
        }
        data.clear();
        data.add(new OptionItem(RESOLUTION_360P,R.mipmap.ic_360p,R.string.resolutoion_360p));
        data.add(new OptionItem(RESOLUTION_480P,R.mipmap.ic_480p,R.string.resolutoion_480p));
        data.add(new OptionItem(RESOLUTION_540P,R.mipmap.ic_540p,R.string.resolutoion_540p));
        data.add(new OptionItem(RESOLUTION_720P,R.mipmap.ic_720p,R.string.resolutoion_720p));
        //data.add(new OptionItem(RESOLUTION_1080P,R.mipmap.ic_1080p,R.string.resolutoion_1080p));

        binding.menu.addMenuItems(data);
        binding.menu.setOnItemClickListener((v, optionItem, position) -> {
            if(listener!=null){
                SystemUtil.vibrator(getContext());
                listener.onResolutionSelected(optionItem.getId());
                binding.menu.setSelected(optionItem);
            }
        });
    }

    public void delResolutionOption(int resolution){
        boolean changed=false;
        for(OptionItem item:data){
            if(item.getId()==resolution){
                binding.menu.delMenuItem(item);
                changed=true;
                break;
            }
        }
        if(changed) {
            binding.menu.refresh();
        }
    }

    public void addResultionOption(int resolution){
        boolean exist=false;
        for(OptionItem item:data){
            if(item.getId()==resolution){
                exist=true;
                break;
            }
        }
        if(!exist){
            switch (resolution){
                case RESOLUTION_360P:
                    addOption(RESOLUTION_360P,R.mipmap.ic_360p,R.string.resolutoion_360p);
                    break;
                case RESOLUTION_480P:
                    addOption(RESOLUTION_480P,R.mipmap.ic_480p,R.string.resolutoion_480p);
                    break;
                case RESOLUTION_540P:
                    addOption(RESOLUTION_540P,R.mipmap.ic_540p,R.string.resolutoion_540p);
                    break;
                case RESOLUTION_720P:
                    addOption(RESOLUTION_720P,R.mipmap.ic_720p,R.string.resolutoion_720p);
                    break;
                case RESOLUTION_1080P:
                    addOption(RESOLUTION_1080P,R.mipmap.ic_1080p,R.string.resolutoion_1080p);
                    break;
            }
        }

    }

    public void addOption(int id,int resId,int titleRes){
        //data.add(new OptionItem(id,resId,titleRes));
        binding.menu.addMenuItem(new OptionItem(id,resId,titleRes));
        binding.menu.refresh();
    }

    public void setCurrentResolution(int resolution){
        binding.menu.setSelected(data.get(resolution-1));
    }

    public interface OnMenuOptionSelectedListener{
        void onResolutionSelected(int resolution);
    }

    @Override public void onClick(View v) {
        if(listener==null){
        }

    }
}
