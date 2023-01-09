package io.agora.api.example.common.widget.indicator.buildins.navigator.abs;

import io.agora.api.example.common.widget.indicator.buildins.navigator.model.PositionData;
import java.util.List;

public interface IPagerIndicator {
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

    void onPositionDataProvide(List<PositionData> dataList);
}
