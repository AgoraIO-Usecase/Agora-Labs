package io.agora.api.example.common.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.agora.api.example.R;
import io.agora.api.example.main.model.Feature;
import java.util.ArrayList;
import java.util.List;

public class FeatureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Feature> data=new ArrayList<>();

    private OnItemClickListener onItemClickListener = null;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==0){
            view=LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_list_title_item,parent,false);
            return new TitleVH(view);
        }else if(viewType==1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feature_list_feature_item,parent,false);
            return new FeatureVH(view);
        }
        return null;
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Feature feature=data.get(holder.getAbsoluteAdapterPosition());
        if(holder instanceof TitleVH){
            ((TitleVH) holder).title.setText(feature.getTitleRes());
        }else if(holder instanceof FeatureVH){
            ((FeatureVH) holder).title.setText(feature.getTitleRes());
            ((FeatureVH) holder).icon.setImageResource(feature.getIconRes());
            ((FeatureVH) holder).stayTuned.setVisibility(feature.isEnabled()?View.GONE:View.VISIBLE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(v,data.get(holder.getAbsoluteAdapterPosition()),holder.getAbsoluteAdapterPosition());
                }
            }
        });
    }


    public int getItemViewType(int position) {
        Feature feature=data.get(position);
        if(feature.getId()==-1){
            return 0;
        }
        return 1;
    }


    public void setData(List<Feature> data){
        setData(data,true);
    }

    public List<Feature> getData(){
        return data;
    }

    public void setData(List<Feature> data,boolean notify){
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

    public static class TitleVH extends RecyclerView.ViewHolder{
        private TextView title;
        public TitleVH(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, Feature feature, int position);
    }

    public static class FeatureVH extends RecyclerView.ViewHolder{
        private ImageView icon;
        private TextView title;
        private TextView stayTuned;

        public FeatureVH(@NonNull View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.icon);
            title=itemView.findViewById(R.id.title);
            stayTuned=itemView.findViewById(R.id.stay_tuned);
        }
    }
}

