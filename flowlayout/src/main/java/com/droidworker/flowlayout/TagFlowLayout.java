package com.droidworker.flowlayout;

import java.util.LinkedList;
import java.util.Queue;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

/**
 * @author luoyanfeng@le.com
 */

public class TagFlowLayout extends FlowLayout {
    private int mLeftMargin;
    private int mTopMargin;
    private int mRightMargin;
    private int mBottomMargin;
    private TagAdapter mTagAdapter;
    private SparseArray<Queue<View>> mCachedViews = new SparseArray<>();

    public TagFlowLayout(Context context) {
        this(context, null, 0);
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        int margin = typedArray.getDimensionPixelSize(R.styleable.TagFlowLayout_tagMargin, 0);
        if (margin == 0) {
            mLeftMargin = typedArray.getDimensionPixelSize(R.styleable.TagFlowLayout_tagLeftMargin,
                    0);
            mTopMargin = typedArray.getDimensionPixelSize(R.styleable.TagFlowLayout_tagTopMargin,
                    0);
            mRightMargin = typedArray.getDimensionPixelSize(R.styleable.TagFlowLayout_tagRightMargin,
                    0);
            mBottomMargin = typedArray
                    .getDimensionPixelSize(R.styleable.TagFlowLayout_tagBottomMargin, 0);
        } else {
            mLeftMargin = mTopMargin = mRightMargin = mBottomMargin = margin;
        }
        typedArray.recycle();
    }

    public void setTagAdapter(TagAdapter tagAdapter) {
        mTagAdapter = tagAdapter;
        update();
    }

    public void update() {
        if (getChildCount() > 0) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                TagLayoutParams layoutParams = (TagLayoutParams) child.getLayoutParams();
                if (mCachedViews.get(layoutParams.viewType) == null) {
                    mCachedViews.put(layoutParams.viewType, new LinkedList<View>());
                }
                mCachedViews.get(layoutParams.viewType).add(child);
            }
            removeAllViews();
        }

        int size = mTagAdapter.getSize();
        for (int i = 0; i < size; i++) {
            int viewType = mTagAdapter.getItemViewType(i);
            Queue<View> queue = mCachedViews.get(viewType);
            View element = queue == null ? null : queue.poll();
            View view = mTagAdapter.getItemView(this, element, i);
            TagLayoutParams layoutParams = (TagLayoutParams) generateDefaultLayoutParams();
            layoutParams.leftMargin = mLeftMargin;
            layoutParams.topMargin = mTopMargin;
            layoutParams.rightMargin = mRightMargin;
            layoutParams.bottomMargin = mBottomMargin;
            layoutParams.viewType = viewType;
            addView(view, layoutParams);
        }
    }

    public void setItemMargin(int leftMargin, int topMargin, int rightMargin, int bottomMargin){
        mLeftMargin = leftMargin;
        mTopMargin = topMargin;
        mRightMargin = rightMargin;
        mBottomMargin = bottomMargin;
        update();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new TagLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new TagLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new TagLayoutParams(getContext(), attrs);
    }

    public static class TagLayoutParams extends MarginLayoutParams {
        int viewType;

        public TagLayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public TagLayoutParams(int width, int height) {
            super(width, height);
        }

        public TagLayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public TagLayoutParams(LayoutParams source) {
            super(source);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mCachedViews.clear();
    }
}
