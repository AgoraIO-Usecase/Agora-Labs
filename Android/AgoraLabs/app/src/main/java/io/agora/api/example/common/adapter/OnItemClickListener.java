package io.agora.api.example.common.adapter;

import android.view.View;
import io.agora.api.example.common.widget.slidingmenu.OptionItem;

public interface OnItemClickListener {
    void onItemClick(View v, OptionItem optionItem, int position);
}