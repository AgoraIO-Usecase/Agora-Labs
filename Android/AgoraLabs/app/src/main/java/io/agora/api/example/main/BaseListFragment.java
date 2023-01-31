package io.agora.api.example.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import io.agora.api.example.common.adapter.FeatureAdapter;
import io.agora.api.example.databinding.FragmentBaseListBinding;
import io.agora.api.example.main.model.Feature;
import java.util.List;

public abstract class BaseListFragment extends Fragment {
    protected FragmentBaseListBinding binding;
    protected FeatureAdapter adapter;


    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        binding=FragmentBaseListBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter=new FeatureAdapter();
        adapter.setOnItemClickListener((v, feature, position) -> onItemSelect(feature,position));
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){

            @Override public int getSpanSize(int position) {
                List<Feature> featureList=adapter.getData();
                if(featureList!=null){
                    if(featureList.get(position).getId()==-1){
                        return 2;
                    }
                }
                return 1;
            }
        });
        binding.cvFeatureList.setLayoutManager(layoutManager);
        binding.cvFeatureList.setAdapter(adapter);
    }

    protected abstract void onItemSelect(Feature feature,int position);

    @Override public void onStart() {
        super.onStart();
        initData();
    }

    protected abstract void initData();


}
