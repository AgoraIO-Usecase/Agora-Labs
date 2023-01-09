package io.agora.api.example.common.widget.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import io.agora.api.example.common.widget.indicator.abs.IPagerNavigator;

public class MagicIndicator extends FrameLayout{
    private IPagerNavigator navigator;

    public MagicIndicator(Context context) {
        super(context);
    }

    public MagicIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (navigator != null) {
            navigator.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    public void onPageSelected(int position) {
        if (navigator != null) {
            navigator.onPageSelected(position);
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (navigator != null) {
            navigator.onPageScrollStateChanged(state);
        }
    }

    public IPagerNavigator getNavigator() {
        return navigator;
    }

    public void setNavigator(IPagerNavigator navigator) {
        if (this.navigator == navigator) {
            return;
        }
        if (this.navigator != null) {
            this.navigator.onDetachFromMagicIndicator();
        }
        this.navigator = navigator;
        removeAllViews();
        if (this.navigator instanceof View) {
            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView((View) this.navigator, lp);
            this.navigator.onAttachToMagicIndicator();
        }
    }
}
