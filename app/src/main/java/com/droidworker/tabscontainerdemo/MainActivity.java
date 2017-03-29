package com.droidworker.tabscontainerdemo;

import java.util.ArrayList;
import java.util.List;

import com.droidworker.flowlayout.TagAdapter;
import com.droidworker.flowlayout.TagFlowLayout;
import com.droidworker.tabscontainer.TabsContainer;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TagFlowLayout mTagFlowLayout;
    private List<String> mTags = new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TabsContainer titleTabs = (TabsContainer) findViewById(R.id.title_tabs_container);
        List<String> list = new ArrayList<>();
        list.add("推荐");
        list.add("电影");
        list.add("电视剧");
        list.add("动漫");
        list.add("综艺");
        list.add("资讯");
        list.add("音乐");
        list.add("纪录片");
        list.add("会员");
        list.add("排行榜");
        list.add("专题");
        titleTabs.setTitles(list);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), list);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(sectionsPagerAdapter);

        titleTabs.setOnChangeListener(new TabsContainer.OnChangeListener() {
            @Override
            public void onChange(int position, String title) {
                mViewPager.setCurrentItem(position, true);
                mTagFlowLayout.setVisibility(View.GONE);
            }
        });
        titleTabs.setOnOperateListener(new TabsContainer.onOperateListener() {
            @Override
            public void onOperate(boolean isOpen) {
                List<String> list = titleTabs.getVisibleTitles();
                if (list.size() != 0) {
                    mTags.clear();
                    mTags = list;
                    mTagFlowLayout.update();
                    mTagFlowLayout.setVisibility(isOpen ? View.VISIBLE : View.GONE);
                } else {
                    titleTabs.finishOperate();
                }
            }
        });

        mTagFlowLayout = (TagFlowLayout) findViewById(R.id.flow_layout);
        TagAdapter tagAdapter = new TagAdapter() {
            @Override
            public View getView(ViewGroup parent, final int position) {
                TextView textView = new TextView(parent.getContext());
                textView.setText(mTags.get(position));
                textView.setTextColor(Color.LTGRAY);
                textView.setTextSize(16);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                return textView;
            }

            @Override
            public int getSize() {
                return mTags.size();
            }
        };
        mTagFlowLayout.setTagAdapter(tagAdapter);

        List<Drawable> iconList = new ArrayList<>();
        iconList.add(getDrawable(R.drawable.ic_alarm_on_grey_24dp));
        iconList.add(getDrawable(R.drawable.ic_album_grey_24dp));
        iconList.add(getDrawable(R.drawable.ic_email_grey_24dp));
        iconList.add(getDrawable(R.drawable.ic_photo_grey_24dp));
        TabsContainer iconTabs = (TabsContainer) findViewById(R.id.icon_tabs_container);
        iconTabs.setIcons(iconList);

        List<String> titleList = new ArrayList<>();
        titleList.add("推荐");
        titleList.add("电影");
        titleList.add("电视剧");
        titleList.add("动漫");
        List<Drawable> iconList2 = new ArrayList<>();
        iconList2.add(getDrawable(R.drawable.ic_alarm_on_grey_24dp));
        iconList2.add(getDrawable(R.drawable.ic_album_grey_24dp));
        iconList2.add(getDrawable(R.drawable.ic_email_grey_24dp));
        iconList2.add(getDrawable(R.drawable.ic_photo_grey_24dp));
        TabsContainer tabsContainer = (TabsContainer) findViewById(R.id.tabs_container);
        tabsContainer.setTitlesAndIcons(titleList, iconList2);
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, getPageTitle(position).toString());
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mList.get(position);
        }
    }
}
