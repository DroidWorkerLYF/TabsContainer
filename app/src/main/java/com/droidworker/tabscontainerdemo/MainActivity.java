package com.droidworker.tabscontainerdemo;

import java.util.ArrayList;
import java.util.List;

import com.droidworker.flowlayout.FlowItemAdapter;
import com.droidworker.flowlayout.FlowLayout;
import com.droidworker.tabscontainer.TabsContainer;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    private List<String> mTags = new ArrayList<>();
    private TabsContainer mTitleTabs;
    private TabsContainer mIconTabs;
    private TabsContainer mTabs;
    private FlowLayout mTagFlowLayout;
    private FlowItemAdapter mFlowItemAdapter;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            mTitleTabs.reset();
            mIconTabs.reset();
            mTabs.reset();
            mTagFlowLayout.setVisibility(View.GONE);
            mTitleTabs.operationDone();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        initTitleTabs();

        initIconTabs();

        initTabs();

    }

    private void initTitleTabs() {
        final int verticalMargin = getResources().getDimensionPixelSize(R.dimen.vertical_margin);
        final int horizontalMargin = getResources()
                .getDimensionPixelSize(R.dimen.horizontal_margin);

        final List<String> list = new ArrayList<>();
        list.add("推荐");
        list.add("电影");
        list.add("电视剧");
        list.add("动漫");
        list.add("综艺");
        list.add("资讯");
        list.add("纪录片");
        list.add("音乐");
        list.add("排行榜");
        list.add("会员");
        list.add("专题");
        list.add("专题22");
        list.add("专题3");

        mTitleTabs = (TabsContainer) findViewById(R.id.title_tabs_container);
        mTagFlowLayout = (FlowLayout) findViewById(R.id.flow_layout);

        mTitleTabs.setTitles(list);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(), list);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);

        mTitleTabs.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                    int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mTitleTabs.removeOnLayoutChangeListener(this);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewPager
                        .getLayoutParams();
                layoutParams.topMargin = layoutParams.topMargin + bottom - top;
                viewPager.requestLayout();

                mTags.addAll(list.subList(mTitleTabs.getLastCompleteVisibleTabPosition() + 1,
                        list.size()));
                mTagFlowLayout.setFlowItemAdapter(mFlowItemAdapter);
            }
        });

        mTitleTabs.setOnChangeListener(new TabsContainer.OnChangeListener() {
            @Override
            public void onChange(int position) {
                viewPager.setCurrentItem(position, true);
                if (mTagFlowLayout.getVisibility() == View.VISIBLE) {
                    mTagFlowLayout.setVisibility(View.GONE);
                    mTitleTabs.operationDone();
                }
            }
        });
        mTitleTabs.setOnOperateListener(new TabsContainer.OnOperateListener() {
            @Override
            public void onOperate(final boolean isOpen) {
                if (isOpen) {
                    mTitleTabs.scrollToTab(0, true);
                    mTitleTabs.post(new Runnable() {
                        @Override
                        public void run() {
                            int pos = mTitleTabs.getSelectedPosition()
                                    - mTitleTabs.getLastVisibleTabPosition() - 1;
                            if (pos >= 0) {
                                mTitleTabs.hideIndicator();
                            }
                            mTagFlowLayout.updateSelectedPosition(pos);
                            mTagFlowLayout.update();
                            mTagFlowLayout.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    mTitleTabs.scrollToTab(mTitleTabs.getSelectedPosition(), true);
                    mTitleTabs.showIndicator();
                    mTagFlowLayout.setVisibility(View.GONE);
                }
            }
        });

        mFlowItemAdapter = new FlowItemAdapter() {
            @Override
            public View getItemView(ViewGroup parent, View convertView, final int position) {
                final int viewType = getItemViewType(position);
                if (convertView == null) {
                    convertView = new TextView(parent.getContext());
                    FlowLayout.FlowLayoutParams flowLayoutParams = new FlowLayout.FlowLayoutParams(
                            FlowLayout.FlowLayoutParams.WRAP_CONTENT,
                            FlowLayout.FlowLayoutParams.WRAP_CONTENT);
                    convertView.setLayoutParams(flowLayoutParams);
                    convertView.setPadding(0, verticalMargin, 0, verticalMargin);

                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int pos = (int) v.getTag();
                            if (getItemViewType(pos) == 0) {
                                int targetPos = mTitleTabs.getLastVisibleTabPosition() + pos + 1;
                                mTitleTabs.scrollToTab(targetPos);
                            } else {
                                mTagFlowLayout.setVisibility(View.GONE);
                                Toast.makeText(v.getContext(), "Click add", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            mTitleTabs.operationDone();
                        }
                    });
                }
                TextView textView = (TextView) convertView;
                textView.setMinWidth(getResources().getDimensionPixelSize(R.dimen.item_width));
                textView.setGravity(Gravity.CENTER);
                if (viewType == 0) {
                    textView.setText(mTags.get(position));
                    if (position == mTitleTabs.getSelectedPosition()
                            - mTitleTabs.getLastVisibleTabPosition() - 1) {
                        textView.setTextColor(Color.WHITE);
                    } else {
                        textView.setTextColor(Color.LTGRAY);
                    }
                    textView.setTextSize(14);
                } else {
                    textView.setText("+");
                    textView.setTextSize(16);
                }
                convertView.setTag(position);
                return textView;
            }

            @Override
            public int getSize() {
                return mTags.size() + 1;
            }

            @Override
            public int getItemViewType(int position) {
                return position == getSize() - 1 ? 1 : super.getItemViewType(position);
            }
        };
        mTagFlowLayout.setItemMargin(horizontalMargin, verticalMargin, horizontalMargin,
                verticalMargin);
    }

    private void initIconTabs() {
        List<Drawable> iconList = new ArrayList<>();
        iconList.add(getDrawable(R.drawable.ic_alarm_on_grey_24dp));
        iconList.add(getDrawable(R.drawable.ic_album_grey_24dp));
        iconList.add(getDrawable(R.drawable.ic_email_grey_24dp));
        iconList.add(getDrawable(R.drawable.ic_photo_grey_24dp));

        mIconTabs = (TabsContainer) findViewById(R.id.icon_tabs_container);
        mIconTabs.setIcons(iconList);
        mIconTabs.setIndicatorHeight(
                getResources().getDimensionPixelSize(R.dimen.test_indicator_height));
        mIconTabs.setOnChangeListener(new TabsContainer.OnChangeListener() {
            @Override
            public void onChange(int position) {
                Log.d(TAG, position + " icon " + mIconTabs.getIcon(position));
            }
        });
    }

    private void initTabs() {
        List<String> titleList = new ArrayList<>();
        titleList.add("推荐");
        titleList.add("电影");
        titleList.add("电视剧");
        titleList.add("动漫");
        List<Drawable> iconList = new ArrayList<>();
        iconList.add(getDrawable(R.drawable.ic_alarm_on_grey_24dp));
        iconList.add(getDrawable(R.drawable.ic_album_grey_24dp));
        iconList.add(getDrawable(R.drawable.ic_email_grey_24dp));
        iconList.add(getDrawable(R.drawable.ic_photo_grey_24dp));

        mTabs = (TabsContainer) findViewById(R.id.tabs_container);
        mTabs.setTitlesAndIcons(titleList, iconList);

        mTabs.setTabColor(ResourcesCompat.getColor(getResources(), R.color.grey, null));
        mTabs.setTabSelectedColor(
                ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        mTabs.setIndicatorColor(
                ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        mTabs.setOnChangeListener(new TabsContainer.OnChangeListener() {
            @Override
            public void onChange(int position) {
                Log.d(TAG, position + " tabs " + mTabs.getTitle(position) + " icon "
                        + mTabs.getIcon(position));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeListeners(mTitleTabs);
        removeListeners(mIconTabs);
        removeListeners(mTabs);
    }

    private void removeListeners(TabsContainer tabsContainer) {
        tabsContainer.removeOnChangeListener();
        tabsContainer.removeOnOperateListener();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String ARG_SECTION_TITLE = "section_title";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String title) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_SECTION_TITLE, title);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(String.format(getString(R.string.section_format),
                    getArguments().getInt(ARG_SECTION_NUMBER),
                    getArguments().getString(ARG_SECTION_TITLE)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private List<String> mList;

        SectionsPagerAdapter(FragmentManager fm, List<String> list) {
            super(fm);

            mList = list;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1, getPageTitle(position).toString());
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mList.get(position);
        }
    }
}
