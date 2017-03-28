package com.droidworker.flowlayout;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author https://github.com/DroidWorkerLYF
 */

public abstract class TagAdapter {

    public abstract View getView(ViewGroup parent, int position);

    public abstract int getSize();

    public int getViewType(int position) {
        return 0;
    }

}
