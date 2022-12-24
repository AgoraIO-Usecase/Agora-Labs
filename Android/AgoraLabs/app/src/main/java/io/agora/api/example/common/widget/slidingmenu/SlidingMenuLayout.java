package io.agora.api.example.common.widget.slidingmenu;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import io.agora.api.example.R;
import io.agora.api.example.common.adapter.OnItemClickListener;
import io.agora.api.example.common.widget.indicator.ViewPagerHelper;
import io.agora.api.example.common.widget.indicator.buildins.navigator.Navigator;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.CommonNavigatorAdapter;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.IPagerIndicator;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.IPagerTitleView;
import io.agora.api.example.common.widget.indicator.buildins.navigator.indicators.LinePagerIndicator;
import io.agora.api.example.common.widget.indicator.buildins.navigator.titles.ColorTransitionPagerTitleView;
import io.agora.api.example.common.widget.indicator.buildins.navigator.titles.SimplePagerTitleView;
import io.agora.api.example.databinding.SlidingMenuBinding;
import io.agora.api.example.model.OptionItem;
import io.agora.api.example.utils.UIUtil;
import io.agora.rte.extension.faceunity.ExtensionManager;
import java.util.ArrayList;
import java.util.List;

public class SlidingMenuLayout extends ConstraintLayout {
    private List<List<OptionItem>> data=new ArrayList<>();
    private List<String> titles=new ArrayList<>();
    private List<MenuPage> menuPages=new ArrayList<>();
    private PagerAdapter pagerAdapter;
    private static int FIXED_MENU_ID=-1;
    private OnItemClickListener listener;
    private SlidingMenuBinding binding;
    private int currentPage=0;
    private OnTitleClickLister onTitleClickLister;

    public SlidingMenuLayout(@NonNull Context context) {
        this(context,null,0);
    }

    public SlidingMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SlidingMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs){
        binding=SlidingMenuBinding.inflate(LayoutInflater.from(context),this,true);
        /*
        pagerAdapter=new PagerAdapter();
        binding.viewpager.setAdapter(pagerAdapter);*/
       // binding.viewpager.setUserInputEnabled(false);
    }

    private void initMagicIndicator() {

        Navigator commonNavigator = new Navigator(getContext());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles == null ? 0 : titles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(titles.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#88ffffff"));
                simplePagerTitleView.setSelectedColor(Color.WHITE);
                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //binding.viewpager.setCurrentItem(index,false);
                        currentPage=index;
                        binding.indicator.onPageSelected(index);
                        binding.indicator.onPageScrolled(index,0,0);
                        updateCurrentPage();
                        if(onTitleClickLister!=null){
                            onTitleClickLister.onTitleSelected(index);
                        }
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(Color.parseColor("#ffffff"));
                return indicator;
            }
        });
        commonNavigator.setAdjustMode(true);
        binding.indicator.setNavigator(commonNavigator);
        //ViewPagerHelper.bind(binding.indicator, binding.viewpager);
    }

    public void updateCurrentPage(){
        binding.container.removeAllViews();
        binding.container.addView(menuPages.get(currentPage));
    }

    public void setTitles(List<String> titles){
        if(titles==null){
            return;
        }
        this.titles.clear();
        this.titles.addAll(titles);
        initMagicIndicator();
    }

    public void addFixMenuItem(OptionItem item){
        View view= LayoutInflater.from(getContext()).inflate(R.layout.menu_item,null);
        ImageView icon=view.findViewById(R.id.icon);
        icon.setImageResource(item.getImgRes());
        icon.setBackgroundResource(R.drawable.bg_menu_item_normal);
        TextView title=view.findViewById(R.id.title);
        title.setText(item.getTitleRes());
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.CENTER_VERTICAL;
        params.leftMargin= UIUtil.dip2px(getContext(),8);
        params.rightMargin= UIUtil.dip2px(getContext(),8);
        view.setLayoutParams(params);
        view.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(v,item,-1);
                }
            }
        });
        binding.fixedMenuContainer.setVisibility(VISIBLE);
        binding.fixedMenuContainer.addView(view);
    }

    public interface OnTitleClickLister{
        void onTitleSelected(int index);
    }

    public void setOnTitleClickLister(OnTitleClickLister onTitleClickLister) {
        this.onTitleClickLister = onTitleClickLister;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
        for(MenuPage menuPage:menuPages){
            menuPage.setOnItemClickListener(listener);
        }
    }

    public void setData(List<List<OptionItem>> data){
        if(data==null){
            return;
        }
        this.data=data;

        for(List<OptionItem> pageData:data){
            MenuPage menuPage=new MenuPage(getContext());
            menuPage.addMenuItems(pageData);
            menuPages.add(menuPage);
        }
        updateCurrentPage();
        //pagerAdapter.setData(data);
    }

    public   void setSelected(OptionItem item){
        for(MenuPage menuPage:menuPages){
            menuPage.setSelected(item);
        }
    }

    public static class PagerAdapter extends RecyclerView.Adapter<PagerAdapter.VH>{
        private List<List<OptionItem>> data=new ArrayList<>();


        public void setData(List<List<OptionItem>> data) {
            this.data = data;
        }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(new MenuPage(parent.getContext()));
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.root.addMenuItems(data.get(position));
        }

        @Override public int getItemCount() {
            return data.size();
        }

        public static class VH extends RecyclerView.ViewHolder{
            public MenuPage root;
            public VH(@NonNull MenuPage itemView) {
                super(itemView);
                this.root=itemView;
            }
        }
    }


}
