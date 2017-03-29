package com.droidworker.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author luoyanfeng@le.com
 */

public class TagFlowLayout extends FlowLayout {
    private int mLeftMargin;
    private int mTopMargin;
    private int mEndMargin;
    private int mBottomMargin;
    private TagAdapter mTagAdapter;

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
            mEndMargin = typedArray.getDimensionPixelSize(R.styleable.TagFlowLayout_tagRightMargin,
                    0);
            mBottomMargin = typedArray
                    .getDimensionPixelSize(R.styleable.TagFlowLayout_tagBottomMargin, 0);
        } else {
            mLeftMargin = mTopMargin = mEndMargin = mBottomMargin = margin;
        }
        typedArray.recycle();
    }

    public void setTagAdapter(TagAdapter tagAdapter) {
        mTagAdapter = tagAdapter;
        update();
    }

    public void update() {
        if (getChildCount() > 0) {
            removeAllViews();
        }

        int size = mTagAdapter.getSize();
        for(int i=0;i<size;i++){
            View view = mTagAdapter.getView(this, i);
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) generateDefaultLayoutParams();
            marginLayoutParams.leftMargin = mLeftMargin;
            marginLayoutParams.topMargin = mTopMargin;
            marginLayoutParams.rightMargin = mEndMargin;
            marginLayoutParams.bottomMargin = mBottomMargin;
            addView(view, marginLayoutParams);
        }
    }
}
