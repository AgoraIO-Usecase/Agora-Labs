package io.agora.api.example.common.widget.indicator;

import android.util.SparseArray;
import android.util.SparseBooleanArray;

public class NavigatorHelper {
    private SparseBooleanArray deselectedItems = new SparseBooleanArray();
    private SparseArray<Float> leavedPercents = new SparseArray<>();

    private int totalCount;
    private int currentIndex;
    private int lastIndex;
    private float lastPositionOffsetSum;
    private int scrollState;

    private boolean mSkimOver;
    private NavigatorHelper.OnNavigatorScrollListener mNavigatorScrollListener;

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        float currentPositionOffsetSum = position + positionOffset;
        boolean leftToRight = lastPositionOffsetSum <= currentPositionOffsetSum;
        if (scrollState != ScrollState.SCROLL_STATE_IDLE) {
            if (currentPositionOffsetSum == lastPositionOffsetSum) {
                return;
            }
            int nextPosition = position + 1;
            boolean normalDispatch = true;
            if (positionOffset == 0.0f) {
                if (leftToRight) {
                    nextPosition = position - 1;
                    normalDispatch = false;
                }
            }
            for (int i = 0; i < totalCount; i++) {
                if (i == position || i == nextPosition) {
                    continue;
                }
                Float leavedPercent = leavedPercents.get(i, 0.0f);
                if (leavedPercent != 1.0f) {
                    dispatchOnLeave(i, 1.0f, leftToRight, true);
                }
            }
            if (normalDispatch) {
                if (leftToRight) {
                    dispatchOnLeave(position, positionOffset, true, false);
                    dispatchOnEnter(nextPosition, positionOffset, true, false);
                } else {
                    dispatchOnLeave(nextPosition, 1.0f - positionOffset, false, false);
                    dispatchOnEnter(position, 1.0f - positionOffset, false, false);
                }
            } else {
                dispatchOnLeave(nextPosition, 1.0f - positionOffset, true, false);
                dispatchOnEnter(position, 1.0f - positionOffset, true, false);
            }
        } else {
            for (int i = 0; i < totalCount; i++) {
                if (i == currentIndex) {
                    continue;
                }
                boolean deselected = deselectedItems.get(i);
                if (!deselected) {
                    dispatchOnDeselected(i);
                }
                Float leavedPercent = leavedPercents.get(i, 0.0f);
                if (leavedPercent != 1.0f) {
                    dispatchOnLeave(i, 1.0f, false, true);
                }
            }
            dispatchOnEnter(currentIndex, 1.0f, false, true);
            dispatchOnSelected(currentIndex);
        }
        lastPositionOffsetSum = currentPositionOffsetSum;
    }

    private void dispatchOnEnter(int index, float enterPercent, boolean leftToRight, boolean force) {
        if (mSkimOver || index == currentIndex || scrollState == ScrollState.SCROLL_STATE_DRAGGING || force) {
            if (mNavigatorScrollListener != null) {
                mNavigatorScrollListener.onEnter(index, totalCount, enterPercent, leftToRight);
            }
            leavedPercents.put(index, 1.0f - enterPercent);
        }
    }

    private void dispatchOnLeave(int index, float leavePercent, boolean leftToRight, boolean force) {
        if (mSkimOver || index == lastIndex || scrollState == ScrollState.SCROLL_STATE_DRAGGING || ((index == currentIndex
            - 1 || index == currentIndex + 1) && leavedPercents.get(index, 0.0f) != 1.0f) || force) {
            if (mNavigatorScrollListener != null) {
                mNavigatorScrollListener.onLeave(index, totalCount, leavePercent, leftToRight);
            }
            leavedPercents.put(index, leavePercent);
        }
    }

    private void dispatchOnSelected(int index) {
        if (mNavigatorScrollListener != null) {
            mNavigatorScrollListener.onSelected(index, totalCount);
        }
        deselectedItems.put(index, false);
    }

    private void dispatchOnDeselected(int index) {
        if (mNavigatorScrollListener != null) {
            mNavigatorScrollListener.onDeselected(index, totalCount);
        }
        deselectedItems.put(index, true);
    }

    public void onPageSelected(int position) {
        lastIndex = currentIndex;
        currentIndex = position;
        dispatchOnSelected(currentIndex);
        for (int i = 0; i < totalCount; i++) {
            if (i == currentIndex) {
                continue;
            }
            boolean deselected = deselectedItems.get(i);
            if (!deselected) {
                dispatchOnDeselected(i);
            }
        }
    }

    public void onPageScrollStateChanged(int state) {
        scrollState = state;
    }

    public void setNavigatorScrollListener(NavigatorHelper.OnNavigatorScrollListener navigatorScrollListener) {
        mNavigatorScrollListener = navigatorScrollListener;
    }

    public void setSkimOver(boolean skimOver) {
        mSkimOver = skimOver;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        deselectedItems.clear();
        leavedPercents.clear();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getScrollState() {
        return scrollState;
    }

    public interface OnNavigatorScrollListener {
        void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight);

        void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight);

        void onSelected(int index, int totalCount);

        void onDeselected(int index, int totalCount);
    }
}
