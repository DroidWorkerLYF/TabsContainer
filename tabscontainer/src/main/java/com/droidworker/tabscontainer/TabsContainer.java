package com.droidworker.tabscontainer;

import java.util.Collections;
import java.util.List;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
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
 * @author luoyanfeng@le.com
 */

public class TabsContainer extends FrameLayout {
    // TODO 支持ViewPager，支持传入一个Adapter是的自定义布局更灵活，考虑标准布局也支持使用Adapter。
    private static final String EMPTY_TITLE = "";
    private static final int DEFAULT_POSITION = 0;
    private float mTabTextSize;
    private int mTabTextColor;
    private int mTabSelectedTextColor;
    private int mTabPaddingStart;
    private int mTabPaddingTop;
    private int mTabPaddingEnd;
    private int mTabPaddingBottom;
    private int mTabMinWidth;
    private int mTabMaxWidth;
    private int mTabBackgroundResId;
    private int mIndicatorColor;
    private int mIndicatorHeight;
    private int mIndicatorAnimDuration;
    private int mOpIconResId;
    private float mOpRotateAngle;
    private int mOpAnimDuration;
    private RecyclerView mRecyclerView;
    private View mIndicator;
    private ImageView mOpView;
    private List<TabItem> mItemList = Collections.emptyList();
    private int mSelectedPosition = DEFAULT_POSITION;
    private OnChangeListener mOnChangeListener;
    private onOperateListener mOnOperateListener;
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

        // initialize properties
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TabsContainer);
        mTabTextSize = typedArray.getDimensionPixelSize(R.styleable.TabsContainer_tabTextSize, 12);
        mTabTextColor = typedArray.getColor(R.styleable.TabsContainer_tabTextColor, Color.LTGRAY);
        mTabSelectedTextColor = typedArray.getColor(R.styleable.TabsContainer_tabSelectedTextColor,
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
        mTabMaxWidth = typedArray.getDimensionPixelSize(R.styleable.TabsContainer_tabMaxWidth, 0);
        if (mTabMaxWidth < mTabMinWidth) {
            throw new IllegalStateException("Tab's min width is larger than max width");
        }
        mTabBackgroundResId = typedArray.getResourceId(R.styleable.TabsContainer_tabBackground, 0);

        mIndicatorColor = typedArray.getResourceId(R.styleable.TabsContainer_indicatorColor,
                Color.WHITE);
        mIndicatorHeight = typedArray.getDimensionPixelSize(
                R.styleable.TabsContainer_indicatorHeight,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                        getResources().getDisplayMetrics()));
        mIndicatorAnimDuration = typedArray
                .getInteger(R.styleable.TabsContainer_indicatorAnimDuration, 100);
        mOpIconResId = typedArray.getResourceId(R.styleable.TabsContainer_operationIcon, 0);
        mOpRotateAngle = typedArray.getFloat(R.styleable.TabsContainer_operationRotateAngle, 0);
        mOpAnimDuration = typedArray.getInteger(R.styleable.TabsContainer_operationAnimDuration,
                300);
        typedArray.recycle();

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
                    if (mOnOperateListener != null) {
                        mOnOperateListener.onOperate(mIsOpen);
                    }
                    float startAngle = mIsOpen ? mOpRotateAngle : 0;
                    float endAngle = mIsOpen ? 0 : mOpRotateAngle;
                    Animation animation = new RotateAnimation(startAngle, endAngle,
                            mOpView.getPivotX(), mOpView.getPivotY());
                    animation.setDuration(mOpAnimDuration);
                    animation.setFillAfter(true);
                    animation.setFillBefore(true);
                    mOpView.startAnimation(animation);
                    mIsOpen = !mIsOpen;
                }
            });
        }

        post(new Runnable() {
            @Override
            public void run() {
                if (mOpView != null) {
                    mRecyclerView.setPadding(mRecyclerView.getPaddingLeft(),
                            mRecyclerView.getPaddingTop(),
                            mRecyclerView.getPaddingRight() + mOpView.getWidth(),
                            mRecyclerView.getPaddingBottom());
                }

                moveIndicator(false);
            }
        });
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
        return EMPTY_TITLE;
    }

    public void scrollToTab(int tabPosition) {
        onChange(tabPosition);
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
        mIndicator.invalidate();
    }

    public void reset() {
        onChange(DEFAULT_POSITION);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        mOnChangeListener = onChangeListener;
    }

    public void removeOnChangeListener() {
        mOnChangeListener = null;
    }

    public void setOnOperateListener(onOperateListener operateListener) {
        mOnOperateListener = operateListener;
    }

    public void removeOnOperateListener() {
        mOnOperateListener = null;
    }

    private void onChange(int position) {
        RecyclerView.ViewHolder targetViewHolder = mRecyclerView
                .findViewHolderForAdapterPosition(position);
        int offset = 0;
        int extra = 0;
        if (mOpView != null) {
            extra = mOpView.getWidth();
        }

        if (targetViewHolder.itemView
                .getLeft() != ((LayoutParams) mIndicator.getLayoutParams()).leftMargin) {
            if (targetViewHolder.itemView.getLeft() < 0) {
                offset = -targetViewHolder.itemView.getWidth();
            } else if (targetViewHolder.itemView
                    .getRight() > getResources().getDisplayMetrics().widthPixels - extra) {
                offset = targetViewHolder.itemView.getWidth();
            }
        }

        if (position != mSelectedPosition) {
            mTabLayoutAdapter.notifyItemChanged(mSelectedPosition);
            mTabLayoutAdapter.notifyItemChanged(position);
            mSelectedPosition = position;

            if (mOnChangeListener != null) {
                mOnChangeListener.onChange(position, getTitle(position));
            }
        }

        mRecyclerView.scrollBy(offset, 0);
        moveIndicator();
    }

    private void moveIndicator() {
        moveIndicator(true);
    }

    /**
     * Move the indicator to new position
     * @param animated true if use animation
     */
    private void moveIndicator(boolean animated) {
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
     * Container for a tab item
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
            }
            itemView.setBackgroundResource(mTabBackgroundResId);
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
                linearLayout.addView(iconView, 0);
                mIconView = iconView;
            }
            if ((type & TabType.TITLE) == TabType.TITLE) {
                TextView textView = new TextView(itemView.getContext());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
                textView.setTextColor(mTabTextColor);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(mTabPaddingStart, mTabPaddingTop, mTabPaddingEnd,
                        mTabPaddingBottom);
                if (mTabMinWidth != 0) {
                    textView.setMinWidth(mTabMinWidth);
                }
                if (mTabMaxWidth != 0) {
                    textView.setMaxWidth(mTabMaxWidth);
                }
                linearLayout.addView(textView,
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                mTitleView = textView;
            }
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
                mTitleView.setTextColor(isSelected ? mTabSelectedTextColor : mTabTextColor);
            }

            if (mIconView != null && icon != null) {
                mIconView.setImageDrawable(icon);
            }
        }
    }

    public interface OnChangeListener {

        void onChange(int position, String title);
    }

    public interface onOperateListener {

        void onOperate(boolean isOpen);
    }
}
