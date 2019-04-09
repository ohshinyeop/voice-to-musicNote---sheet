package kr.ac.pknu.sme.myvoiceproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.data.Entry;

import java.util.List;
import java.util.Locale;

import kr.ac.pknu.sme.myvoiceproject.R;
import kr.ac.pknu.sme.myvoiceproject.fragments.RecordGraphFragment;
import kr.ac.pknu.sme.myvoiceproject.fragments.RecordingOverviewFragment;
import kr.ac.pknu.sme.myvoiceproject.fragments.RecordingPlayFragment;
import kr.ac.pknu.sme.myvoiceproject.models.Recording;
import kr.ac.pknu.sme.myvoiceproject.models.database.RecordingDB;
import io.fabric.sdk.android.Fabric;


public class RecordViewActivity extends AppCompatActivity implements ActionBar.TabListener, RecordGraphFragment.OnFragmentInteractionListener {
    private static final String LOG_TAG = RecordViewActivity.class.getSimpleName();
    public static Recording currentRecord;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_record_view);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // get current recording from intent
        if (getIntent() != null) {
            RecordingDB db = new RecordingDB(this);
            Intent intent = getIntent();

            Log.v("RECORD", "jjjj1" + getIntent().getLongExtra(Recording.KEY, 999));

            RecordViewActivity.currentRecord = db.getRecording(getIntent().getLongExtra(Recording.KEY, 0));

            Log.v("RECORD", "jjjj2" + RecordViewActivity.currentRecord);

            //디테일보기 버튼과 상관없이 인텐트는 넘겨주고 있고 잘 받고 있음. 결국 레코딩프로그래먼트의 리스너에서 액티비티 실행하는게 문제다.

        }

        if (RecordViewActivity.currentRecord.getName() != null) {
            setTitle(RecordViewActivity.currentRecord.getName());
        } else {
            setTitle(RecordViewActivity.currentRecord.getDisplayDate(this));
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_record: {
                startActivity(new Intent(this, RecordingActivity.class));
                return true;
            }

            case R.id.action_progress:
            {
                startActivity(new Intent(this, ProgressActivity.class));
      //          this.getSupportFragmentManager().beginTransaction()
      //                  .add(R.id.container, new ProgressFragment())
      //                 .commit();
                return true;
            }

            case R.id.action_about: {
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public List<Entry> startingPitchEntries() {
        return currentRecord.getRange().getGraphEntries();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) { super(fm); }




                @Override
                public Fragment getItem(int position) {
                    // getItem is called to instantiate the fragment for the given page.
                    // Return a PlaceholderFragment (defined as a static inner class below).
                    switch (position) {
                        case 0: {
                            return RecordingOverviewFragment.newInstance(position + 1);
                        }
                        case 1: {
                            return RecordGraphFragment.newInstance(position + 1);
                        }
                        case 2: {
                            return RecordingPlayFragment.newInstance(position + 1);
                        }

                        default: {
                            return null;
                        }
                    }
                }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();

            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

}
