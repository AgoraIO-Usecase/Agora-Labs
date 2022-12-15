package io.agora.api.example.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.agora.api.example.R;
import io.agora.api.example.model.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.VH> {
    private List<MenuItem> data=new ArrayList<>();
    private OnItemClickListener onItemClickListener = null;
    private int selectPos=-1;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false));
    }

    @Override public void onBindViewHolder(@NonNull VH holder, int position) {
        MenuItem item=data.get(holder.getAbsoluteAdapterPosition());
        holder.icon.setImageResource(item.getImgRes());
        holder.title.setText(item.getTitleRes());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                selectPos=position;
                notifyDataSetChanged();
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(v,data.get(position),position);
                }
            }
        });
        boolean selected = selectPos == holder.getAbsoluteAdapterPosition();
        holder.title.setTextColor(holder.itemView.getContext().getResources().getColor(selected?R.color.white:R.color.menu_text_default_color));
        holder.icon.setBackgroundResource(selected?R.drawable.bg_menu_item_selected:R.drawable.bg_menu_item_normal);
    }


    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
    }

    public void setData(List<MenuItem> data){
        setData(data,true);
    }

    public List<MenuItem> getData(){
        return data;
    }

    public void setData(List<MenuItem> data,boolean notify){
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


    @Override public int getItemCount() {
        return data.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, MenuItem menuItem, int position);
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
