package io.agora.api.example.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import io.agora.api.example.R;
import io.agora.api.example.common.adapter.OnItemClickListener;
import io.agora.api.example.databinding.ResolutionMenuBinding;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;
import java.util.ArrayList;
import java.util.List;

public class VideoFeatureMenu extends ConstraintLayout implements View.OnClickListener{
    public static final int RESOLUTION_360P=1;
    public static final int RESOLUTION_480P=2;
    public static final int RESOLUTION_540P=3;
    public static final int RESOLUTION_720P=4;



    private OnMenuOptionSelectedListener listener;


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
        List<OptionItem> data=new ArrayList<>();
        data.add(new OptionItem(RESOLUTION_360P,R.mipmap.ic_360p,R.string.resolutoion_360p));
        data.add(new OptionItem(RESOLUTION_480P,R.mipmap.ic_480p,R.string.resolutoion_480p));
        data.add(new OptionItem(RESOLUTION_540P,R.mipmap.ic_540p,R.string.resolutoion_540p));
        data.add(new OptionItem(RESOLUTION_720P,R.mipmap.ic_720p,R.string.resolutoion_720p));

        binding.menu.addMenuItems(data);
        binding.menu.setOnItemClickListener(new OnItemClickListener() {
            @Override public void onItemClick(View v, OptionItem optionItem, int position) {
                if(listener!=null){
                    listener.onResolutionSelected(optionItem.getId());
                    binding.menu.setSelected(optionItem);
                }

            }
        });




    }

    public interface OnMenuOptionSelectedListener{
        void onResolutionSelected(int resolution);
    }

    @Override public void onClick(View v) {
        if(listener==null){
            return;
        }

    }
}
