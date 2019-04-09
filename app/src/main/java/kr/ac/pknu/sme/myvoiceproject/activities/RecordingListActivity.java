package kr.ac.pknu.sme.myvoiceproject.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import kr.ac.pknu.sme.myvoiceproject.R;
import kr.ac.pknu.sme.myvoiceproject.fragments.ProgressFragment;
import kr.ac.pknu.sme.myvoiceproject.fragments.RecordingFragment;
import kr.ac.pknu.sme.myvoiceproject.fragments.RecordingListFragment;
import kr.ac.pknu.sme.myvoiceproject.fragments.WelcomeFragment;
import kr.ac.pknu.sme.myvoiceproject.models.Recording;
import io.fabric.sdk.android.Fabric;


public class RecordingListActivity extends AppCompatActivity implements RecordingListFragment.OnFragmentInteractionListener {
    private static final String LOG_TAG = RecordingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_recording_list);

        if (savedInstanceState == null) {

            SharedPreferences sharedPref = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            if (sharedPref.getBoolean(getString(R.string.first_start), true))
            {

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new WelcomeFragment())
                        .commit();

                getSupportActionBar().hide();


                sharedPref.edit().putBoolean(getString(R.string.first_start), false).apply();
            }

            else
            {

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new RecordingListFragment())
                        .commit();
            }
        }
    }

    public void onNextClick(View view)
    {

        startActivity(new Intent(this, RecordingActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_record: {
                startActivity(new Intent(this, RecordingActivity.class));
                Toast.makeText(this, "레코딩 액티비티 시작됨", Toast.LENGTH_SHORT).show();
                return true;
            }

            case R.id.action_progress:
            {
                startActivity(new Intent(this, ProgressActivity.class));
                Toast.makeText(this, "프로그레스 액티비티 시작됨", Toast.LENGTH_SHORT).show();
                return true;
            }

            case R.id.action_about: {
                startActivity(new Intent(this, AboutActivity.class));
                Toast.makeText(this, "About 액티비티 시작됨", Toast.LENGTH_SHORT).show();
                return true;
            }


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(long recordID) {
        Intent intent = new Intent(this, RecordViewActivity.class);
        intent.putExtra(Recording.KEY, recordID);
        startActivity(intent);
    }
}
