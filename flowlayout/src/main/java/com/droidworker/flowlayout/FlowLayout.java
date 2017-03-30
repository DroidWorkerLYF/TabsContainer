package com.droidworker.flowlayout;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple flow layout
 * @author https://github.com/DroidWorkerLYF
 */

public class FlowLayout extends ViewGroup {
    private int mLeftMargin;
    private int mTopMargin;
    private int mRightMargin;
    private int mBottomMargin;
    private FlowItemAdapter mFlowItemAdapter;
    private SparseArray<Queue<View>> mCachedViews = new SparseArray<>();

    public FlowLayout(Context context) {
        this(context, null, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlowLayout);
        int margin = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_tagMargin, 0);
        if (margin == 0) {
            mLeftMargin = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_tagLeftMargin, 0);
            mTopMargin = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_tagTopMargin, 0);
            mRightMargin = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_tagRightMargin,
                    0);
            mBottomMargin = typedArray.getDimensionPixelSize(R.styleable.FlowLayout_tagBottomMargin,
                    0);
        } else {
            mLeftMargin = mTopMargin = mRightMargin = mBottomMargin = margin;
        }
        typedArray.recycle();
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
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mCachedViews.clear();
    }

    public void setFlowItemAdapter(FlowItemAdapter flowItemAdapter) {
        mFlowItemAdapter = flowItemAdapter;
        update();
    }

    public void update() {
        if (getChildCount() > 0) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                FlowLayoutParams layoutParams = (FlowLayoutParams) child.getLayoutParams();
                if (mCachedViews.get(layoutParams.viewType) == null) {
                    mCachedViews.put(layoutParams.viewType, new LinkedList<View>());
                }
                mCachedViews.get(layoutParams.viewType).add(child);
            }
            removeAllViews();
        }

        int size = mFlowItemAdapter.getSize();
        for (int i = 0; i < size; i++) {
            int viewType = mFlowItemAdapter.getItemViewType(i);
            Queue<View> queue = mCachedViews.get(viewType);
            View element = queue == null ? null : queue.poll();
            View view = mFlowItemAdapter.getItemView(this, element, i);
            FlowLayoutParams layoutParams = (FlowLayoutParams) generateDefaultLayoutParams();
            layoutParams.leftMargin = mLeftMargin;
            layoutParams.topMargin = mTopMargin;
            layoutParams.rightMargin = mRightMargin;
            layoutParams.bottomMargin = mBottomMargin;
            layoutParams.viewType = viewType;
            addView(view, layoutParams);
        }
    }

    public void setItemMargin(int leftMargin, int topMargin, int rightMargin, int bottomMargin) {
        mLeftMargin = leftMargin;
        mTopMargin = topMargin;
        mRightMargin = rightMargin;
        mBottomMargin = bottomMargin;
        update();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new FlowLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new FlowLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new FlowLayoutParams(getContext(), attrs);
    }

    public static class FlowLayoutParams extends MarginLayoutParams {
        int viewType;

        public FlowLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public FlowLayoutParams(int width, int height) {
            super(width, height);
        }

        public FlowLayoutParams(LayoutParams source) {
            super(source);
        }
    }
}
