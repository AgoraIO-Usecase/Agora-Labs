package io.agora.api.example.common.widget.slidingmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.agora.api.example.R;
import io.agora.api.example.common.adapter.MenuItemAdapter;
import io.agora.api.example.common.adapter.OnItemClickListener;
import io.agora.api.example.utils.UIUtil;
import java.util.List;

public class MenuPage extends LinearLayout {
    private OnItemClickListener listener;
    private MenuItemAdapter menuItemAdapter;
    private OptionItem fixOptionItem;
    public MenuPage(Context context) {
        this(context, null, 0);
    }

    public MenuPage(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuPage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        initView(context,attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs){
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public void addFixMenuItem(OptionItem item){
        fixOptionItem =item;
        View view= LayoutInflater.from(getContext()).inflate(R.layout.menu_item,null);
        ImageView icon=view.findViewById(R.id.icon);
        icon.setImageResource(item.getImgRes());
        icon.setBackgroundResource(R.drawable.bg_menu_item_normal);
        TextView title=view.findViewById(R.id.title);
        title.setText(item.getTitleRes());
        LinearLayout.LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.CENTER_VERTICAL;
        params.leftMargin= UIUtil.dip2px(getContext(),8);
        params.rightMargin= UIUtil.dip2px(getContext(),8);
        view.setLayoutParams(params);
        view.setOnClickListener(v -> {
            if(listener!=null){
                listener.onItemClick(v, fixOptionItem,-1);
            }
        });
        addView(view,0);
    }

    public void addMenuItems(List<OptionItem> items){
        RecyclerView cv=new RecyclerView(getContext());
        cv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        menuItemAdapter=new MenuItemAdapter();
        menuItemAdapter.setData(items);
        cv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        cv.setAdapter(menuItemAdapter);
        addView(cv);
    }

    public void addMenuItem(OptionItem item){
        menuItemAdapter.getData().add(item);
    }

    public void delMenuItem(OptionItem del){
        menuItemAdapter.getData().remove(del);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
        menuItemAdapter.setOnItemClickListener(listener);
    }

    public void setSelected(OptionItem item){
        setSelected(item,true);
    }

    public void setSelected(OptionItem item,boolean onlyOneSelected){
        if(onlyOneSelected){
            menuItemAdapter.clearSelected();
        }
        item.setSelected(true);
        menuItemAdapter.notifyDataSetChanged();
    }

    public void setUnSelected(OptionItem item){
        item.setSelected(false);
        menuItemAdapter.notifyDataSetChanged();
    }

    public void refresh(){
        menuItemAdapter.notifyDataSetChanged();
    }

}
