package io.agora.api.example.common.adapter;

import android.view.View;
import io.agora.api.example.model.OptionItem;

public interface OnItemClickListener {
    void onItemClick(View v, OptionItem optionItem, int position);
}