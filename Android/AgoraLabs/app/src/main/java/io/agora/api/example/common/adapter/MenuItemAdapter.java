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
import java.util.ArrayList;
import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.VH> {
    private List<OptionItem> data=new ArrayList<>();
    private OnItemClickListener onItemClickListener = null;
    private int selectPos=-1;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false));
    }

    @Override public void onBindViewHolder(@NonNull VH holder, int position) {
        OptionItem item=data.get(holder.getAbsoluteAdapterPosition());
        holder.icon.setImageResource(item.getImgRes());
        holder.title.setText(item.getTitleRes());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                notifyDataSetChanged();
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(v,data.get(position),position);
                }
            }
        });
        holder.title.setTextColor(holder.itemView.getContext().getResources().getColor(item.isSelected()?R.color.white:R.color.menu_text_default_color));
        holder.icon.setBackgroundResource(item.isSelected()?R.drawable.bg_menu_item_selected:R.drawable.bg_menu_item_normal);
    }


    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
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



    public void setSelected(List<OptionItem> items){
        if(items==null){
            return;
        }
        for(OptionItem item:data){
            if(items.contains(item)){
                item.setSelected(true);
            }else{
                item.setSelected(false);
            }
        }
        notifyDataSetChanged();
    }


    @Override public int getItemCount() {
        return data.size();
    }


    public static class VH extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView title;
        public VH(@NonNull View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.icon);
            title=itemView.findViewById(R.id.title);
        }
    }
}
