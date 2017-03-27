package com.droidworker.tabscontainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.drawable.Drawable;

/**
 * Convert a certain list to the list of {@link TabItem}
 * @author luoyanfeng@le.com
 */

class ListConverter {

    static List<TabItem> onlyTitle(List<String> titles) {
        if (titles == null) {
            return Collections.emptyList();
        }
        List<TabItem> list = new ArrayList<>();
        for (String title : titles) {
            TabItem tabItem = new TabItem();
            tabItem.setType(TabType.TITLE);
            tabItem.setTitle(title);
            list.add(tabItem);
        }
        return list;
    }

    static List<TabItem> onlyIcon(List<Drawable> icons) {
        if (icons == null) {
            return Collections.emptyList();
        }
        List<TabItem> list = new ArrayList<>();
        for (Drawable icon : icons) {
            TabItem tabItem = new TabItem();
            tabItem.setType(TabType.ICON);
            tabItem.setIcon(icon);
            list.add(tabItem);
        }
        return list;
    }

    static List<TabItem> toList(List<String> titles, List<Drawable> icons) {
        if (titles == null || icons == null || titles.size() != icons.size()) {
            return Collections.emptyList();
        }
        List<TabItem> list = new ArrayList<>();
        final int size = titles.size();
        for (int i = 0; i < size; i++) {
            TabItem tabItem = new TabItem();
            tabItem.setType(TabType.BOTH);
            tabItem.setTitle(titles.get(i));
            tabItem.setIcon(icons.get(i));
            list.add(tabItem);
        }
        return list;
    }
}
