package com.droidworker.flowlayout;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author https://github.com/DroidWorkerLYF
 */

public abstract class FlowItemAdapter {

    public abstract View getItemView(ViewGroup parent, View convertView, int position);

    public abstract int getSize();

    public int getItemViewType(int position) {
        return 0;
    }

}
