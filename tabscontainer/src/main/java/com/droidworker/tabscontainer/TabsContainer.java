package com.droidworker.tabscontainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * TabsContainer provides a horizontal layout to display tabs.
 * TabsContainer support an addition operation which display at the end of container. You can use it
 * to support an expand or edit operation and so on.
 * You can set content by {@link #setTitles(List)}, {@link #setIcons(List)} and
 * {@link #setTitlesAndIcons(List, List)}.
 * You can set a listener via {@link #setOnChangeListener(OnChangeListener)} to be notified when
 * tab's selection state has been changed.
 * @author https://github.com/DroidWorkerLYF
 */

public class TabsContainer extends FrameLayout {
    // TODO 支持ViewPager，支持传入一个Adapter是的自定义布局更灵活，考虑标准布局也支持使用Adapter。
    private static final int DEFAULT_POSITION = 0;
    private static final int SPLIT_WRAP = 0;
    private static final int SPLIT_AVERAGE = 1;
    private float mTabTextSize;
    private int mTabColor;
    private int mTabSelectedColor;
    private int mTabPaddingStart;
    private int mTabPaddingTop;
    private int mTabPaddingEnd;
    private int mTabPaddingBottom;
    private int mTabMinWidth;
    private int mTabWidth;
    private int mTabTitleMarginTop;
    private int mTabBackgroundResId;
    private int mIndicatorColor;
    private int mIndicatorHeight;
    private int mIndicatorAnimDuration;
    private int mOpIconResId;
    private float mOpRotateAngle;
    private int mOpAnimDuration;
    private int mTabSplitWay;
    private RecyclerView mRecyclerView;
    private View mIndicator;
    private ImageView mOpView;
    private List<TabItem> mItemList = Collections.emptyList();
    private int mSelectedPosition = DEFAULT_POSITION;
    private OnChangeListener mOnChangeListener;
    private OnOperateListener mOnOperateListener;
    private boolean mIsOpen;
    private TabLayoutAdapter mTabLayoutAdapter;

    public TabsContainer(Context context) {
        this(context, null, 0);
    }

    public TabsContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // initialize tab's properties
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabsContainer);
        mTabTextSize = typedArray.getDimensionPixelSize(R.styleable.TabsContainer_tabTextSize, 12);
        mTabColor = typedArray.getColor(R.styleable.TabsContainer_tabColor, Color.LTGRAY);
        mTabSelectedColor = typedArray.getColor(R.styleable.TabsContainer_tabSelectedColor,
                Color.WHITE);
        final int tabPadding = typedArray
                .getDimensionPixelSize(R.styleable.TabsContainer_tabPadding, 0);
        if (tabPadding == 0) {
            mTabPaddingStart = typedArray
                    .getDimensionPixelSize(R.styleable.TabsContainer_tabPaddingStart, 0);
            mTabPaddingTop = typedArray
                    .getDimensionPixelSize(R.styleable.TabsContainer_tabPaddingTop, 0);
            mTabPaddingEnd = typedArray
                    .getDimensionPixelSize(R.styleable.TabsContainer_tabPaddingEnd, 0);
            mTabPaddingBottom = typedArray
                    .getDimensionPixelSize(R.styleable.TabsContainer_tabPaddingBottom, 0);
        } else {
            mTabPaddingStart = mTabPaddingTop = mTabPaddingEnd = mTabPaddingBottom = tabPadding;
        }
        mTabMinWidth = typedArray.getDimensionPixelSize(R.styleable.TabsContainer_tabMinWidth, 0);
        mTabWidth = typedArray.getDimensionPixelSize(R.styleable.TabsContainer_tabWidth, 0);
        mTabTitleMarginTop = typedArray.getDimensionPixelSize(
                R.styleable.TabsContainer_tabTitleMarginTop,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                        getResources().getDisplayMetrics()));

        mTabBackgroundResId = typedArray.getResourceId(R.styleable.TabsContainer_tabBackground, 0);
        mTabSplitWay = typedArray.getInt(R.styleable.TabsContainer_tabSplitWay, SPLIT_WRAP);
        // initialize indicator's properties
        mIndicatorColor = typedArray.getResourceId(R.styleable.TabsContainer_indicatorColor,
                Color.WHITE);
        mIndicatorHeight = typedArray.getDimensionPixelSize(
                R.styleable.TabsContainer_indicatorHeight,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                        getResources().getDisplayMetrics()));
        mIndicatorAnimDuration = typedArray
                .getInteger(R.styleable.TabsContainer_indicatorAnimDuration, 100);
        // initialize operation's properties
        mOpIconResId = typedArray.getResourceId(R.styleable.TabsContainer_operationIcon, 0);
        mOpRotateAngle = typedArray.getFloat(R.styleable.TabsContainer_operationRotateAngle, 0);
        mOpAnimDuration = typedArray.getInteger(R.styleable.TabsContainer_operationAnimDuration,
                200);

        typedArray.recycle();

        if (mTabMinWidth > mTabWidth && mTabWidth != 0) {
            throw new IllegalStateException("Tab's minimum with larger than it's width");
        }

        // create tabs container
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        mTabLayoutAdapter = new TabLayoutAdapter();
        mRecyclerView.setAdapter(mTabLayoutAdapter);
        addView(mRecyclerView);

        // create indicator
        mIndicator = new View(context);
        mIndicator.setBackgroundColor(mIndicatorColor);
        LayoutParams layoutParams = new LayoutParams(0, mIndicatorHeight);
        layoutParams.gravity = Gravity.BOTTOM;
        addView(mIndicator, layoutParams);

        if (mOpIconResId != 0) {
            mOpView = new ImageView(context);
            layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.END;
            addView(mOpView, layoutParams);
            mOpView.setImageResource(mOpIconResId);

            mOpView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mIsOpen = !mIsOpen;
                    Animation animation = getRotateAnim();
                    mOpView.startAnimation(animation);
                    if (mOnOperateListener != null) {
                        mOnOperateListener.onOperate(mIsOpen);
                    }
                }
            });
        }

        // we don't know mOpView'size and indicator's width so post to handler
        post(new Runnable() {
            @Override
            public void run() {
                if (mOpView != null) {
                    FrameLayout.LayoutParams layoutParams = (LayoutParams) mRecyclerView
                            .getLayoutParams();
                    layoutParams.rightMargin = mOpView.getWidth();
                    mRecyclerView.requestLayout();
                }

                moveIndicator(false);
            }
        });
    }

    private Animation getRotateAnim() {
        float startAngle = mIsOpen ? 0 : mOpRotateAngle;
        float endAngle = mIsOpen ? mOpRotateAngle : 0;
        Animation animation = new RotateAnimation(startAngle, endAngle, mOpView.getPivotX(),
                mOpView.getPivotY());
        animation.setDuration(mOpAnimDuration);
        animation.setFillAfter(true);
        animation.setFillBefore(true);
        return animation;
    }

    public void setTitles(List<String> titles) {
        mItemList = ListConverter.onlyTitle(titles);
        mTabLayoutAdapter.notifyDataSetChanged();
    }

    public void setIcons(List<Drawable> icons) {
        mItemList = ListConverter.onlyIcon(icons);
        mTabLayoutAdapter.notifyDataSetChanged();
    }

    public void setTitlesAndIcons(List<String> titles, List<Drawable> icons) {
        mItemList = ListConverter.toList(titles, icons);
        mTabLayoutAdapter.notifyDataSetChanged();
    }

    public String getTitle(int position) {
        return mItemList.get(position).getTitle();
    }

    public Drawable getIcon(int position) {
        return mItemList.get(position).getIcon();
    }

    public int getSelectedPosition() {
        return mSelectedPosition;
    }

    /**
     * Scroll to the given position
     * @param tabPosition position which you want to be selected
     */
    public void scrollToTab(final int tabPosition) {
        RecyclerView.ViewHolder targetViewHolder = mRecyclerView
                .findViewHolderForAdapterPosition(tabPosition);
        if (targetViewHolder == null) {
            mRecyclerView.scrollToPosition(tabPosition);
            post(new Runnable() {
                @Override
                public void run() {
                    scrollToTab(tabPosition);
                }
            });
            return;
        } else {
            int offset = 0;

            if (targetViewHolder.itemView
                    .getLeft() != ((LayoutParams) mIndicator.getLayoutParams()).leftMargin) {
                if (targetViewHolder.itemView.getLeft() < 0) {
                    offset = -targetViewHolder.itemView.getWidth();
                } else if (targetViewHolder.itemView.getRight() > mRecyclerView.getWidth()) {
                    offset = targetViewHolder.itemView.getWidth();
                }
            }

            mRecyclerView.scrollBy(offset, 0);
        }

        if (tabPosition != mSelectedPosition) {
            int prePos = mSelectedPosition;
            mSelectedPosition = tabPosition;
            mTabLayoutAdapter.notifyItemChanged(prePos);
            mTabLayoutAdapter.notifyItemChanged(tabPosition);

            if (mOnChangeListener != null) {
                mOnChangeListener.onChange(tabPosition);
            }
        }
        moveIndicator();
    }

    public void setTabTextSize(float textSize) {
        mTabTextSize = textSize;
        mTabLayoutAdapter.notifyDataSetChanged();
    }

    public void setTabColor(int color) {
        mTabColor = color;
        mTabLayoutAdapter.notifyDataSetChanged();
    }

    public void setTabSelectedColor(int color) {
        mTabSelectedColor = color;
        mTabLayoutAdapter.notifyDataSetChanged();
    }

    public void setTabBackground(int resId) {
        mTabBackgroundResId = resId;
        mTabLayoutAdapter.notifyDataSetChanged();
    }

    public void setIndicatorColor(int color) {
        mIndicatorColor = color;
        mIndicator.setBackgroundColor(color);
    }

    public void setIndicatorHeight(int height) {
        mIndicatorHeight = height;
        mIndicator.getLayoutParams().height = height;
        mIndicator.requestLayout();
    }

    public void setOperateIconResId(int resId) {
        mOpIconResId = resId;
        mOpView.setImageResource(mOpIconResId);
    }

    public void operationDone() {
        mIsOpen = false;
        Animation animation = getRotateAnim();
        mOpView.startAnimation(animation);
    }

    /**
     * Reset to initialize state.
     */
    public void reset() {
        scrollToTab(DEFAULT_POSITION);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        mOnChangeListener = onChangeListener;
    }

    public void removeOnChangeListener() {
        mOnChangeListener = null;
    }

    public void setOnOperateListener(OnOperateListener operateListener) {
        mOnOperateListener = operateListener;
    }

    public void removeOnOperateListener() {
        mOnOperateListener = null;
    }

    /**
     * Get a list of titles which is showing on screen currently
     * @return a list of titles
     */
    public List<String> getVisibleTitles() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView
                .getLayoutManager();
        int start = linearLayoutManager.findLastVisibleItemPosition();
        int end = mItemList.size();
        if (start < end) {
            start = start + 1;
        }
        List<TabItem> tabItems = mItemList.subList(start, end);
        List<String> titles = new ArrayList<>();
        for (TabItem tabItem : tabItems) {
            titles.add(tabItem.getTitle());
        }
        return titles;
    }

    /**
     * Get the last visible tab's position.
     * @return a position
     */
    public int getLastVisibleTabPosition() {
        return ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findLastVisibleItemPosition();
    }

    private void moveIndicator() {
        moveIndicator(true);
    }

    /**
     * Move the indicator to new position
     * @param animated true if use animation
     */
    private void moveIndicator(final boolean animated) {
        final RecyclerView.ViewHolder viewHolder = mRecyclerView
                .findViewHolderForAdapterPosition(mSelectedPosition);
        final LayoutParams layoutParams = (LayoutParams) mIndicator.getLayoutParams();
        layoutParams.width = viewHolder.itemView.getWidth();
        if (animated) {
            ValueAnimator valueAnimator = ValueAnimator
                    .ofInt(layoutParams.leftMargin, viewHolder.itemView.getLeft())
                    .setDuration(mIndicatorAnimDuration);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    updateIndicatorLayoutParams((Integer) valueAnimator.getAnimatedValue());
                }
            });
            valueAnimator.start();
        } else {
            updateIndicatorLayoutParams(viewHolder.itemView.getLeft());
        }
    }

    /**
     * Update layout params of indicator
     * @param left left margin
     */
    private void updateIndicatorLayoutParams(int left) {
        LayoutParams layoutParams = (LayoutParams) mIndicator.getLayoutParams();
        layoutParams.leftMargin = left;
        mIndicator.requestLayout();
    }

    /**
     * Adapter for Tabs.
     */
    private class TabLayoutAdapter extends RecyclerView.Adapter<ItemLayout> {

        @Override
        public ItemLayout onCreateViewHolder(ViewGroup parent, int viewType) {
            final ItemLayout itemLayout;
            if (viewType == TabType.CUSTOM) {
                itemLayout = new ItemLayout(new FrameLayout(parent.getContext()));
            } else {
                itemLayout = new ItemLayout(new LinearLayout(parent.getContext()));
            }
            itemLayout.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    scrollToTab(itemLayout.getLayoutPosition());
                }
            });
            return itemLayout;
        }

        @Override
        public void onBindViewHolder(ItemLayout holder, int position) {
            TabItem tabItem = mItemList.get(position);

            holder.inflate(tabItem.getType(), tabItem.getCustomLayout());
            holder.update(tabItem.getTitle(), tabItem.getIcon(), position == mSelectedPosition);
        }

        @Override
        public int getItemCount() {
            return mItemList.size();
        }

        @Override
        public int getItemViewType(int position) {
            TabItem tabItem = mItemList.get(position);
            if (tabItem == null) {
                return super.getItemViewType(position);
            }
            return tabItem.getType();
        }
    }

    /**
     * Layout container for a tab item
     */
    private class ItemLayout extends RecyclerView.ViewHolder {
        /**
         * View used for showing title
         */
        TextView mTitleView;
        /**
         * View used for showing icon
         */
        ImageView mIconView;

        ItemLayout(View itemView) {
            super(itemView);
        }

        /**
         * Inflate item view
         * @param type tab's type
         * @param layoutId custom layout resource id
         */
        void inflate(int type, @LayoutRes int layoutId) {
            if (type == TabType.CUSTOM) {
                inflateCustom(layoutId);
            } else {
                inflateDefault(type);
                itemView.setBackgroundResource(mTabBackgroundResId);
                if (mTabMinWidth != 0) {
                    itemView.setMinimumWidth(mTabMinWidth);
                }
                if (mTabWidth == 0) {
                    itemView.setPadding(mTabPaddingStart, mTabPaddingTop, mTabPaddingEnd,
                            mTabPaddingBottom);
                } else {
                    itemView.setPadding(0, mTabPaddingTop, 0, mTabPaddingBottom);
                }
            }
        }

        /**
         * Inflate custom layout for item
         * @param layoutId custom layout resource id
         */
        void inflateCustom(@LayoutRes int layoutId) {
            FrameLayout frameLayout = (FrameLayout) itemView;
            frameLayout.removeAllViews();
            LayoutInflater.from(itemView.getContext()).inflate(layoutId, (FrameLayout) itemView,
                    true);
            mTitleView = (TextView) itemView.findViewById(android.R.id.text1);
            mIconView = (ImageView) itemView.findViewById(android.R.id.icon);
        }

        /**
         * Inflate default layout for item and show views according to the type
         * @param type tab's type
         */
        void inflateDefault(int type) {
            LinearLayout linearLayout = (LinearLayout) itemView;
            if (linearLayout.getChildCount() > 0) {
                return;
            }
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            if ((type & TabType.ICON) == TabType.ICON) {
                ImageView iconView = new ImageView(itemView.getContext());
                LinearLayout.LayoutParams layoutParams = generateLayoutParams();
                layoutParams.gravity = Gravity.CENTER;
                linearLayout.addView(iconView, 0, layoutParams);
                mIconView = iconView;
            }

            if ((type & TabType.TITLE) == TabType.TITLE) {
                TextView textView = new TextView(itemView.getContext());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
                textView.setTextColor(mTabColor);
                textView.setGravity(Gravity.CENTER);
                LinearLayout.LayoutParams layoutParams = generateLayoutParams();
                layoutParams.gravity = Gravity.CENTER;
                if (mIconView != null) {
                    layoutParams.topMargin = mTabTitleMarginTop;
                }
                linearLayout.addView(textView, layoutParams);
                mTitleView = textView;
            }
        }

        private LinearLayout.LayoutParams generateLayoutParams() {
            LinearLayout.LayoutParams layoutParams;
            if (mTabSplitWay == SPLIT_AVERAGE) {
                layoutParams = new LinearLayout.LayoutParams(
                        mRecyclerView.getMeasuredWidth() / mTabLayoutAdapter.getItemCount(),
                        LinearLayout.LayoutParams.WRAP_CONTENT);
            } else if (mTabWidth != 0) {
                layoutParams = new LinearLayout.LayoutParams(mTabWidth,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
            } else {
                layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            return layoutParams;
        }

        /**
         * Update tab's content
         * @param title tab's title
         * @param icon tab's icon
         * @param isSelected if current tab is selected
         */
        void update(@Nullable String title, @Nullable Drawable icon, boolean isSelected) {
            if (mTitleView != null && !TextUtils.isEmpty(title)) {
                mTitleView.setText(title);
                mTitleView.setTextColor(isSelected ? mTabSelectedColor : mTabColor);
            }

            if (mIconView != null && icon != null) {
                DrawableCompat.setTint(icon, isSelected ? mTabSelectedColor : mTabColor);
                mIconView.setImageDrawable(icon);
            }
        }
    }

    /**
     * Interface definition for a callback to be invoked when tab changed.
     */
    public interface OnChangeListener {

        /**
         * Called when the tab changed
         * @param position selected position
         */
        void onChange(int position);
    }

    /**
     * Interface definition for a callback to be invoked when user click the addition operation.
     */
    public interface OnOperateListener {

        /**
         * Called when user click the addition operation.
         * @param isOpen status of operation
         */
        void onOperate(boolean isOpen);
    }
}
