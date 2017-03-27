package com.droidworker.tabscontainer;

/**
 * Tab's type
 * @author luoyanfeng@le.com
 */

class TabType {
    /**
     * Tab with a title
     */
    static final int TITLE = 0x01;
    /**
     * Tab with an icon
     */
    static final int ICON = 0x10;
    /**
     * Tab which has both title and icon
     */
    static final int BOTH = 0x11;
    /**
     * Tab which use a custom layout but still just contains a title and icon
     */
    static final int CUSTOM = 0x00;
}
