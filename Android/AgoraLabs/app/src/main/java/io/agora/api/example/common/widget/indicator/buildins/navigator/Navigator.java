package io.agora.api.example.common.widget.indicator.buildins.navigator;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import io.agora.api.example.R;
import io.agora.api.example.common.widget.indicator.NavigatorHelper;
import io.agora.api.example.common.widget.indicator.ScrollState;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.CommonNavigatorAdapter;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.IMeasurablePagerTitleView;
import io.agora.api.example.common.widget.indicator.abs.IPagerNavigator;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.IPagerTitleView;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.IPagerIndicator;
import io.agora.api.example.common.widget.indicator.buildins.navigator.model.PositionData;
import java.util.ArrayList;
import java.util.List;

public class Navigator extends FrameLayout implements IPagerNavigator, NavigatorHelper.OnNavigatorScrollListener {
    private HorizontalScrollView scrollView;
    private LinearLayout titleContainer;
    private LinearLayout indicatorContainer;
    private IPagerIndicator indicator;

    private CommonNavigatorAdapter adapter;
    private NavigatorHelper navigatorHelper;


    private boolean adjustMode;
    private boolean enablePivotScroll;
    private float scrollPivotX = 0.5f;
    private boolean smoothScroll = true;
    private boolean followTouch = true;
    private int rightPadding;
    private int leftPadding;
    private boolean indicatorOnTop;
    private boolean skimOver;
    private boolean reselectWhenLayout = true;

    private List<PositionData> positionDataList = new ArrayList<>();

    private DataSetObserver observer = new DataSetObserver() {

        @Override
        public void onChanged() {
            navigatorHelper.setTotalCount(adapter.getCount());
            init();
        }

        @Override
        public void onInvalidated() {
        }
    };

    public Navigator(Context context) {
        super(context);
        navigatorHelper = new NavigatorHelper();
        navigatorHelper.setNavigatorScrollListener(this);
    }

    @Override
    public void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public boolean isAdjustMode() {
        return adjustMode;
    }

    public void setAdjustMode(boolean is) {
        adjustMode = is;
    }

    public CommonNavigatorAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(CommonNavigatorAdapter adapter) {
        if (this.adapter == adapter) {
            return;
        }
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(observer);
        }
        this.adapter = adapter;
        if (this.adapter != null) {
            this.adapter.registerDataSetObserver(observer);
            navigatorHelper.setTotalCount(this.adapter.getCount());
            if (titleContainer != null) {  // adapter改变时，应该重新init，但是第一次设置adapter不用，onAttachToMagicIndicator中有init
                this.adapter.notifyDataSetChanged();
            }
        } else {
            navigatorHelper.setTotalCount(0);
            init();
        }
    }

    private void init() {
        removeAllViews();

        View root;
        if (adjustMode) {
            root = LayoutInflater.from(getContext()).inflate(R.layout.pager_navigator_layout_no_scroll, this);
        } else {
            root = LayoutInflater.from(getContext()).inflate(R.layout.pager_navigator_layout, this);
        }

        scrollView = root.findViewById(R.id.scroll_view);   // mAdjustMode为true时，mScrollView为null

        titleContainer = root.findViewById(R.id.title_container);
        titleContainer.setPadding(leftPadding, 0, rightPadding, 0);

        indicatorContainer = root.findViewById(R.id.indicator_container);
        if (indicatorOnTop) {
            indicatorContainer.getParent().bringChildToFront(indicatorContainer);
        }

        initTitlesAndIndicator();
    }

    /**
     * 初始化title和indicator
     */
    private void initTitlesAndIndicator() {
        for (int i = 0, j = navigatorHelper.getTotalCount(); i < j; i++) {
            IPagerTitleView v = adapter.getTitleView(getContext(), i);
            if (v instanceof View) {
                View view = (View) v;
                LinearLayout.LayoutParams lp;
                if (adjustMode) {
                    lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                    lp.weight = adapter.getTitleWeight(getContext(), i);
                } else {
                    lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                }
                lp.gravity= Gravity.CENTER;
                titleContainer.addView(view, lp);
            }
        }
        if (adapter != null) {
            indicator = adapter.getIndicator(getContext());
            if (indicator instanceof View) {
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                indicatorContainer.addView((View) indicator, lp);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (adapter != null) {
            preparePositionData();
            if (indicator != null) {
                indicator.onPositionDataProvide(positionDataList);
            }
            if (reselectWhenLayout && navigatorHelper.getScrollState() == ScrollState.SCROLL_STATE_IDLE) {
                onPageSelected(navigatorHelper.getCurrentIndex());
                onPageScrolled(navigatorHelper.getCurrentIndex(), 0.0f, 0);
            }
        }
    }

    /**
     * 获取title的位置信息，为打造不同的指示器、各种效果提供可能
     */
    private void preparePositionData() {
        positionDataList.clear();
        for (int i = 0, j = navigatorHelper.getTotalCount(); i < j; i++) {
            PositionData data = new PositionData();
            View v = titleContainer.getChildAt(i);
            if (v != null) {
                data.left = v.getLeft();
                data.top = v.getTop();
                data.right = v.getRight();
                data.bottom = v.getBottom();
                if (v instanceof IMeasurablePagerTitleView) {
                    IMeasurablePagerTitleView view = (IMeasurablePagerTitleView) v;
                    data.contentLeft = view.getContentLeft();
                    data.contentTop = view.getContentTop();
                    data.contentRight = view.getContentRight();
                    data.contentBottom = view.getContentBottom();
                } else {
                    data.contentLeft = data.left;
                    data.contentTop = data.top;
                    data.contentRight = data.right;
                    data.contentBottom = data.bottom;
                }
            }
            positionDataList.add(data);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (adapter != null) {

            navigatorHelper.onPageScrolled(position, positionOffset, positionOffsetPixels);
            if (indicator != null) {
                indicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            // 手指跟随滚动
            if (scrollView != null && positionDataList.size() > 0 && position >= 0 && position < positionDataList.size()) {
                if (followTouch) {
                    int currentPosition = Math.min(positionDataList.size() - 1, position);
                    int nextPosition = Math.min(positionDataList.size() - 1, position + 1);
                    PositionData current = positionDataList.get(currentPosition);
                    PositionData next = positionDataList.get(nextPosition);
                    float scrollTo = current.horizontalCenter() - scrollView.getWidth() * scrollPivotX;
                    float nextScrollTo = next.horizontalCenter() - scrollView.getWidth() * scrollPivotX;
                    scrollView.scrollTo((int) (scrollTo + (nextScrollTo - scrollTo) * positionOffset), 0);
                } else if (!enablePivotScroll) {
                    // TODO 实现待选中项完全显示出来
                }
            }
        }
    }

    public float getScrollPivotX() {
        return scrollPivotX;
    }

    public void setScrollPivotX(float scrollPivotX) {
        this.scrollPivotX = scrollPivotX;
    }

    @Override
    public void onPageSelected(int position) {
        if (adapter != null) {
            navigatorHelper.onPageSelected(position);
            if (indicator != null) {
                indicator.onPageSelected(position);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (adapter != null) {
            navigatorHelper.onPageScrollStateChanged(state);
            if (indicator != null) {
                indicator.onPageScrollStateChanged(state);
            }
        }
    }

    @Override
    public void onAttachToMagicIndicator() {
        init(); // 将初始化延迟到这里
    }

    @Override
    public void onDetachFromMagicIndicator() {
    }

    public IPagerIndicator getPagerIndicator() {
        return indicator;
    }

    public boolean isEnablePivotScroll() {
        return enablePivotScroll;
    }

    public void setEnablePivotScroll(boolean is) {
        enablePivotScroll = is;
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        if (titleContainer == null) {
            return;
        }
        View v = titleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onEnter(index, totalCount, enterPercent, leftToRight);
        }
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        if (titleContainer == null) {
            return;
        }
        View v = titleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onLeave(index, totalCount, leavePercent, leftToRight);
        }
    }

    public boolean isSmoothScroll() {
        return smoothScroll;
    }

    public void setSmoothScroll(boolean smoothScroll) {
        this.smoothScroll = smoothScroll;
    }

    public boolean isFollowTouch() {
        return followTouch;
    }

    public void setFollowTouch(boolean followTouch) {
        this.followTouch = followTouch;
    }

    public boolean isSkimOver() {
        return skimOver;
    }

    public void setSkimOver(boolean skimOver) {
        this.skimOver = skimOver;
        navigatorHelper.setSkimOver(skimOver);
    }

    @Override
    public void onSelected(int index, int totalCount) {
        if (titleContainer == null) {
            return;
        }
        View v = titleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onSelected(index, totalCount);
        }
        if (!adjustMode && !followTouch && scrollView != null && positionDataList.size() > 0) {
            int currentIndex = Math.min(positionDataList.size() - 1, index);
            PositionData current = positionDataList.get(currentIndex);
            if (enablePivotScroll) {
                float scrollTo = current.horizontalCenter() - scrollView.getWidth() * scrollPivotX;
                if (smoothScroll) {
                    scrollView.smoothScrollTo((int) (scrollTo), 0);
                } else {
                    scrollView.scrollTo((int) (scrollTo), 0);
                }
            } else {
                // 如果当前项被部分遮挡，则滚动显示完全
                if (scrollView.getScrollX() > current.left) {
                    if (smoothScroll) {
                        scrollView.smoothScrollTo(current.left, 0);
                    } else {
                        scrollView.scrollTo(current.left, 0);
                    }
                } else if (scrollView.getScrollX() + getWidth() < current.right) {
                    if (smoothScroll) {
                        scrollView.smoothScrollTo(current.right - getWidth(), 0);
                    } else {
                        scrollView.scrollTo(current.right - getWidth(), 0);
                    }
                }
            }
        }
    }

    @Override
    public void onDeselected(int index, int totalCount) {
        if (titleContainer == null) {
            return;
        }
        View v = titleContainer.getChildAt(index);
        if (v instanceof IPagerTitleView) {
            ((IPagerTitleView) v).onDeselected(index, totalCount);
        }
    }

    public IPagerTitleView getPagerTitleView(int index) {
        if (titleContainer == null) {
            return null;
        }
        return (IPagerTitleView) titleContainer.getChildAt(index);
    }

    public LinearLayout getTitleContainer() {
        return titleContainer;
    }

    public int getRightPadding() {
        return rightPadding;
    }

    public void setRightPadding(int rightPadding) {
        this.rightPadding = rightPadding;
    }

    public int getLeftPadding() {
        return leftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
    }

    public boolean isIndicatorOnTop() {
        return indicatorOnTop;
    }

    public void setIndicatorOnTop(boolean indicatorOnTop) {
        this.indicatorOnTop = indicatorOnTop;
    }

    public boolean isReselectWhenLayout() {
        return reselectWhenLayout;
    }

    public void setReselectWhenLayout(boolean reselectWhenLayout) {
        this.reselectWhenLayout = reselectWhenLayout;
    }
}
