package io.agora.api.example.common.widget.indicator.buildins.navigator.model;

public class PositionData {
    public int left;
    public int top;
    public int right;
    public int bottom;
    public int contentLeft;
    public int contentTop;
    public int contentRight;
    public int contentBottom;

    public int width() {
        return right - left;
    }

    public int height() {
        return bottom - top;
    }

    public int contentWidth() {
        return contentRight - contentLeft;
    }

    public int contentHeight() {
        return contentBottom - contentTop;
    }

    public int horizontalCenter() {
        return left + width() / 2;
    }

    public int verticalCenter() {
        return top + height() / 2;
    }
}
