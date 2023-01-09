package io.agora.api.example.common.widget.indicator.buildins.navigator.titles;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import io.agora.api.example.common.widget.indicator.buildins.navigator.abs.IMeasurablePagerTitleView;
import io.agora.api.example.utils.UIUtil;

public class ClipPagerTitleView extends View implements IMeasurablePagerTitleView {
    private String text;
    private int textColor;
    private int clipColor;
    private boolean leftToRight;
    private float clipPercent;

    private Paint paint;
    private Rect textBounds = new Rect();

    public ClipPagerTitleView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        int textSize = UIUtil.dip2px(context, 16);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        int padding = UIUtil.dip2px(context, 10);
        setPadding(padding, 0, padding, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureTextBounds();
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int result = size;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                int width = textBounds.width() + getPaddingLeft() + getPaddingRight();
                result = Math.min(width, size);
                break;
            case MeasureSpec.UNSPECIFIED:
                result = textBounds.width() + getPaddingLeft() + getPaddingRight();
                break;
            default:
                break;
        }
        return result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int result = size;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                int height = textBounds.height() + getPaddingTop() + getPaddingBottom();
                result = Math.min(height, size);
                break;
            case MeasureSpec.UNSPECIFIED:
                result = textBounds.height() + getPaddingTop() + getPaddingBottom();
                break;
            default:
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = (getWidth() - textBounds.width()) / 2;
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        int y = (int) ((getHeight() - fontMetrics.bottom - fontMetrics.top) / 2);

        // 画底层
        paint.setColor(textColor);
        canvas.drawText(text, x, y, paint);

        // 画clip层
        canvas.save();
        if (leftToRight) {
            canvas.clipRect(0, 0, getWidth() * clipPercent, getHeight());
        } else {
            canvas.clipRect(getWidth() * (1 - clipPercent), 0, getWidth(), getHeight());
        }
        paint.setColor(clipColor);
        canvas.drawText(text, x, y, paint);
        canvas.restore();
    }

    @Override
    public void onSelected(int index, int totalCount) {
    }

    @Override
    public void onDeselected(int index, int totalCount) {
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        this.leftToRight = !leftToRight;
        clipPercent = 1.0f - leavePercent;
        invalidate();
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        this.leftToRight = leftToRight;
        clipPercent = enterPercent;
        invalidate();
    }

    private void measureTextBounds() {
        paint.getTextBounds(text, 0, text == null ? 0 : text.length(), textBounds);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        requestLayout();
    }

    public float getTextSize() {
        return paint.getTextSize();
    }

    public void setTextSize(float textSize) {
        paint.setTextSize(textSize);
        requestLayout();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public int getClipColor() {
        return clipColor;
    }

    public void setClipColor(int clipColor) {
        this.clipColor = clipColor;
        invalidate();
    }

    @Override
    public int getContentLeft() {
        int contentWidth = textBounds.width();
        return getLeft() + getWidth() / 2 - contentWidth / 2;
    }

    @Override
    public int getContentTop() {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 - contentHeight / 2);
    }

    @Override
    public int getContentRight() {
        int contentWidth = textBounds.width();
        return getLeft() + getWidth() / 2 + contentWidth / 2;
    }

    @Override
    public int getContentBottom() {
        Paint.FontMetrics metrics = paint.getFontMetrics();
        float contentHeight = metrics.bottom - metrics.top;
        return (int) (getHeight() / 2 + contentHeight / 2);
    }
}
