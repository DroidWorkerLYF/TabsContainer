package com.droidworker.tabscontainer;

import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

/**
 * Data structure for a tab
 * @author luoyanfeng@le.com
 */

public class TabItem {
    /**
     * Tab's title
     */
    private String mTitle;
    /**
     * Tab's icon
     */
    private Drawable mIcon;
    /**
     * Tab's custom layout resource ID
     */
    private int mLayoutId;
    /**
     * Tab's type
     */
    private int mType;

    public void setTitle(@Nullable String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setIcon(@Nullable Drawable icon) {
        mIcon = icon;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setCustomLayout(@Nullable @LayoutRes int layoutId) {
        mLayoutId = layoutId;
    }

    public int getCustomLayout() {
        return mLayoutId;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }
}
