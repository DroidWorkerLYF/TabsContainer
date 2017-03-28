package com.droidworker.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
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

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//
//        final int childCount = getChildCount();
//        int width, height;
//        int lineWidth, lineHeight;
//
//        for(int i = 0;i<childCount;i++){
//
//        }
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
