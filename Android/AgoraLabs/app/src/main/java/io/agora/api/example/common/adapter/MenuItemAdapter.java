package io.agora.api.example.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.agora.api.example.R;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;
import io.agora.api.example.utils.UIUtil;
import java.util.ArrayList;
import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.VH> {
    private List<OptionItem> data=new ArrayList<>();
    private OnItemClickListener onItemClickListener = null;
    private BGMode bgMode=BGMode.OVAL;
    public enum BGMode{
       OVAL,
        RECTANGLE
    }

    private int width=-1;



    public void setWidth(int width) {
        this.width = width;
    }

    public void setBgMode(BGMode bgMode) {
        this.bgMode = bgMode;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false));
    }

    @Override public void onBindViewHolder(@NonNull VH holder, int position) {
        OptionItem item=data.get(holder.getAbsoluteAdapterPosition());
        if(item.getImgRes()!=-1) {
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageResource(item.getImgRes());
        }else{
            holder.icon.setVisibility(View.GONE);
        }
        holder.title.setText(item.getTitleRes());
        holder.itemView.setOnClickListener(v -> {
            notifyDataSetChanged();
            if(onItemClickListener!=null){
                onItemClickListener.onItemClick(v,data.get(position),position);
            }
        });
        holder.title.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.menu_text_default_color));
        //holder.title.setTextColor(holder.itemView.getContext().getResources().getColor(item.isSelected()?R.color.white:R.color.menu_text_default_color));
        if(bgMode==BGMode.OVAL) {
            holder.icon.setBackgroundResource(
                item.isSelected() ? R.drawable.bg_menu_item_selected : R.drawable.bg_menu_item_normal);
        }else{
            int padding=UIUtil.dip2px(holder.icon.getContext(),10);
            if(width>0){
                holder.root.getLayoutParams().width=width;
            }

            holder.title.setPadding(padding,padding,padding,padding);
            holder.title.setSingleLine();
            holder.title.setBackgroundResource(
                item.isSelected() ? R.drawable.bg_rectangle_menu_selected : R.drawable.bg_rectangle_menu_normal);
        }
    }


    public void setSelectPos(int selectPos) {
        OptionItem item=data.get(selectPos);
        setSelected(item,true);
    }

    public void setData(List<OptionItem> data){
        setData(data,true);
    }

    public List<OptionItem> getData(){
        return data;
    }

    public void setData(List<OptionItem> data,boolean notify){
        this.data.clear();
        this.data.addAll(data);
        if(notify){
            notifyDataSetChanged();
        }
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }



    public void clearSelected(){
        for(OptionItem item:data){
            item.setSelected(false);
        }
        notifyDataSetChanged();
    }

    public void setSelected(List<OptionItem> items){
        for(OptionItem item:data){
            item.setSelected(false);
        }
        for(OptionItem item:items){
            item.setSelected(true);
        }
        notifyDataSetChanged();
    }



    public void setSelected(OptionItem item,boolean onlyOneSelected){
        if(onlyOneSelected){
            for(OptionItem optionItem:data){
                optionItem.setSelected(false);
            }
        }
        item.setSelected(true);
        notifyDataSetChanged();
    }

    public void setUnSelected(OptionItem item){
        item.setSelected(false);
        notifyDataSetChanged();
    }


    @Override public int getItemCount() {
        return data.size();
    }


    public static class VH extends RecyclerView.ViewHolder{
        View root;
        ImageView icon;
        TextView title;
        public VH(@NonNull View itemView) {
            super(itemView);
            root=itemView;
            icon=itemView.findViewById(R.id.icon);
            title=itemView.findViewById(R.id.title);
        }
    }
}
