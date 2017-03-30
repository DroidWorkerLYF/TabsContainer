package com.droidworker.tabscontainer;

import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

/**
 * Data structure for a tab
 * @author https://github.com/DroidWorkerLYF
 */

class TabItem {
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

    void setTitle(@Nullable String title) {
        mTitle = title;
    }

    String getTitle() {
        return mTitle;
    }

    void setIcon(@Nullable Drawable icon) {
        mIcon = icon;
    }

    Drawable getIcon() {
        return mIcon;
    }

    void setCustomLayout(@Nullable @LayoutRes int layoutId) {
        mLayoutId = layoutId;
    }

    int getCustomLayout() {
        return mLayoutId;
    }

    void setType(int type) {
        mType = type;
    }

    int getType() {
        return mType;
    }
}
