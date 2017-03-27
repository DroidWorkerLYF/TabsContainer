package com.droidworker.tabscontainer;

import java.util.List;

import android.graphics.drawable.Drawable;

/**
 * Define public method for tab layout
 * @author luoyanfeng@le.com
 */

public interface TabLayout {

    /**
     * Use the given title list as data
     * @param titles titles to be displayed
     */
    void setTitles(List<String> titles);

    /**
     * Use the given drawable list as data
     * @param icons icons to be displayed
     */
    void setIcons(List<Drawable> icons);

    /**
     * Use the given titles and drawables as data
     * @param titles titles to be displayed
     * @param icons icons to be displayed
     */
    void setTitlesAndIcons(List<String> titles, List<Drawable> icons);

    /**
     * Get a title from given position
     * @param position position
     * @return title
     */
    String getTitle(int position);

    /**
     * Scroll to a certain position
     * @param tabPosition the position of this indicator
     */
    void scrollToTab(int tabPosition);

    /**
     * Set the background color of indicator
     * @param color the background color
     */
    void setIndicatorColor(int color);

    /**
     * Set height of indicator
     * @param height height of indicator
     */
    void setIndicatorHeight(int height);

    void reset();

    void setOnChangeListener(OnChangeListener onChangeListener);

    void removeOnChangeListener();

    void setOnOperateListener(onOperateListener operateListener);

    void removeOnOperateListener();



    interface OnChangeListener {

        void onChange(int position, String title);
    }

    interface onOperateListener {

        void onOperate(boolean isOpen);
    }
}
