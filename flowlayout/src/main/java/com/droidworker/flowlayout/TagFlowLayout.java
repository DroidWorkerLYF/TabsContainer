package com.droidworker.flowlayout;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * @author luoyanfeng@le.com
 */

public class TagFlowLayout extends FlowLayout {
    private int mLeftMargin;
    private int mTopMargin;
    private int mEndMargin;
    private int mBottomMargin;
    private int mTextColor;
    private int mTextSize;
    private int mTagBackgroundResId;
    private List<String> mTags;

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
        mTextColor = typedArray.getColor(R.styleable.TagFlowLayout_tagTextColor, Color.WHITE);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.TagFlowLayout_tagTextSize, 12);
        mTagBackgroundResId = typedArray.getResourceId(R.styleable.TagFlowLayout_tagBackground, 0);
        typedArray.recycle();
    }

    public void setTags(List<String> tags) {
        mTags = tags;
        update();
    }

    private void update() {
        if(getChildCount() > 0){
            removeAllViews();
        }

        List<String> tags = mTags;
        for (String tag : tags) {
            TextView textView = new TextView(getContext());
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) generateDefaultLayoutParams();
            marginLayoutParams.leftMargin = mLeftMargin;
            marginLayoutParams.topMargin = mTopMargin;
            marginLayoutParams.rightMargin = mEndMargin;
            marginLayoutParams.bottomMargin = mBottomMargin;
            textView.setText(tag);
            textView.setTextColor(mTextColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            textView.setBackgroundResource(mTagBackgroundResId);
            addView(textView, marginLayoutParams);
        }
    }
}
