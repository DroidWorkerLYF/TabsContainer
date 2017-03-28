package com.droidworker.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author luoyanfeng@le.com
 */

public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int childCount = getChildCount();
        // 计算出来的整个Group的宽和高
        int width = 0, height = 0;
        // 当前行的宽度和高度
        int lineWidth = 0, lineHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                if (i == childCount - 1) {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                }
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + layoutParams.leftMargin
                    + layoutParams.rightMargin;
            int childHeight = child.getMeasuredHeight() + layoutParams.topMargin
                    + layoutParams.bottomMargin;

            if (lineWidth + childWidth > widthSize - getPaddingStart() - getPaddingEnd()) {
                width = Math.max(width, lineWidth);
                // 算入下一行了
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth;
                lineHeight = Math.max(childHeight, lineHeight);
            }
            if (i == childCount - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }
        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY ? widthSize
                        : width + getPaddingStart() + getPaddingEnd(),
                heightMode == MeasureSpec.EXACTLY ? heightSize
                        : height + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getWidth();
        int lineWidth = 0, lineHeight = getPaddingTop();
        int curLineHeight = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + layoutParams.leftMargin
                    + layoutParams.rightMargin;
            int childHeight = child.getMeasuredHeight() + layoutParams.topMargin
                    + layoutParams.bottomMargin;

            if (lineWidth + childWidth > width - getPaddingStart() - getPaddingEnd()) {
                lineWidth = 0;
                lineHeight += curLineHeight;
            }

            int left = lineWidth + layoutParams.leftMargin;
            int top = lineHeight + layoutParams.topMargin;
            int right = left + child.getMeasuredWidth();
            int bottom = top + child.getMeasuredHeight();
            child.layout(left, top, right, bottom);

            lineWidth += childWidth;
            curLineHeight = Math.max(curLineHeight, childHeight);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
