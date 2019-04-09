package kr.ac.pknu.sme.myvoiceproject.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.data.Entry;

import java.util.LinkedList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import kr.ac.pknu.sme.myvoiceproject.R;
import kr.ac.pknu.sme.myvoiceproject.fragments.ReadingFragment;
import kr.ac.pknu.sme.myvoiceproject.fragments.RecordGraphFragment;
import kr.ac.pknu.sme.myvoiceproject.fragments.RecordingFragment;
import kr.ac.pknu.sme.myvoiceproject.models.Recording;
import kr.ac.pknu.sme.myvoiceproject.models.database.RecordingDB;
import kr.ac.pknu.sme.myvoiceproject.utils.PitchCalculator;



public class RecordingActivity extends AppCompatActivity implements RecordingFragment.OnFragmentInteractionListener, RecordGraphFragment.OnFragmentInteractionListener
{
    Intent finish_intent;
    long global_recordingID;
    String max_test;
    String min_test;

    private static final String LOG_TAG = RecordingActivity.class.getSimpleName();
    private FragmentTabHost tabHost;
    private PitchCalculator calculator = new PitchCalculator();



    Button mButton;
    EditText mEdit;
    TextView mText;
    TextView Hz;
    TextView desc;
    String result;

    private final double A = Math.pow(2, 1.0 / 12.0);
    private final double A4 = 440;
    private final double C4 = 261.626;
    private final String[] scale = {"C", "C♯/D♭", "D", "D♯/E♭", "E", "F",
            "F♯/G♭", "G", "G♯/A♭", "A", "A♯/B♭", "B"};



    private String note = "";
    private Boolean cont = false;

    public String HzToScale222 (String x) {
        cont = false;

        double freq = 0.0;
        try {
            String s = x;
            if (Double.parseDouble(s) < 10) throw new Exception("invalidNumber");
            freq = Double.parseDouble(s);
            cont = true;
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
        }
        if (cont) {
            result = (findNote(findSteps(freq)) + findOctave(findSteps(freq)));
        }
        return result;
    }

    public int findSteps(double fn) {
        double n = (Math.log(fn / C4) / Math.log(A));
        return (int) n;
    }
    public String findNote(int n) {
        if (n > 0) {
            return scale[(n + 1) % scale.length];
        }
        else if (n == 0){
            return "C";
        }
        else {
            return scale[scale.length - (Math.abs(n) % scale.length)];
        }
    }
    public int findOctave(int n) {
        if (n > 0) {
            return 4 + ((n + 1) / scale.length);
        } else if (n == 0) return 4;
        else {
            return 3 + ((n + 1) / scale.length);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_recording);

        tabHost = (FragmentTabHost)findViewById(R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        //this.addTab(ReadingFragment.class, getString(R.string.title_text));
        this.addTab(RecordGraphFragment.class, getString(R.string.realtime_graph));
        //this.addTab(RecordViewActivity.class, getString(R.string.title_section3));

        SharedPreferences sharedPref =
                getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        Integer tabIndex = sharedPref.getInt(getString(R.string.recording_tab_index), 0);
        tabHost.setCurrentTab(tabIndex);


//// cal 액티비티 실행시키는 리스너

        Button freqToNote = (Button) findViewById(R.id.freqToNote);
        freqToNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PitchCalculator pitchCalculator = new PitchCalculator();
                pitchCalculator.setPitches(RecordingActivity.currentRecord2.getRange().getPitches());

                Double minPitch = pitchCalculator.getMin();
                Double maxPitch = pitchCalculator.getMax();


                Intent intent = new Intent(getApplicationContext(), CalActivity.class);
                intent.putExtra("max",maxPitch);
                intent.putExtra("min",minPitch);

                Log.v("TTTTT", "확인 " + maxPitch);
                Log.v("TTTTT", "확인 " + minPitch);

                startActivity(intent);
            }
        });

//        Button goSheet = (Button) findViewById(R.id.goSheet);
//        freqToNote.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PitchCalculator pitchCalculator = new PitchCalculator();
//                pitchCalculator.setPitches(RecordingActivity.currentRecord2.getRange().getPitches());
//
//                Double minPitch = pitchCalculator.getMin();
//                Double maxPitch = pitchCalculator.getMax();
//
//
//                Intent intent = new Intent(getApplicationContext(), CalActivity.class);
//                intent.putExtra("max",maxPitch);
//                intent.putExtra("min",minPitch);
//
//                startActivity(intent);
//            }
//        });


    }

    private void addTab(final Class view, String tag) {
        View tabview = this.createStyledTabView(tabHost.getContext(), tag);
        TabHost.TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview);

        tabHost.addTab(setContent, view, null);
    }

    private static View createStyledTabView(final Context context, String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);

        TextView tv = (TextView) view.findViewById(R.id.tabsText);
        tv.setText(text.toUpperCase());

        return view;
    }

    @Override
    public void onCancel()
    {
        Intent intent = new Intent(this, RecordingListActivity.class);
        startActivity(intent);
    }

    public void onPitchDetected(float pitchInHz) {
        boolean pitchAccepted = calculator.addPitch((double) pitchInHz);

        RecordGraphFragment graph = (RecordGraphFragment) getSupportFragmentManager()
                .findFragmentByTag(getString(R.string.realtime_graph));

        if (graph != null && pitchAccepted)
            graph.addNewPitch(new Entry(pitchInHz, calculator.getPitches().size()));
    }






    public static Recording currentRecord2;
    @Override
    public void onRecordFinished(long recordingID)
    {
        savePreferences();
        ReadingFragment.incrementTextPage(this);


        //녹음 버튼 종료 눌렀을때
        //rocordview액티비티 실행하는 코드

        //Intent i = new Intent(this, RecordViewActivity.class);
        //i.putExtra(Recording.KEY, recordingID);
        //startActivity(intent);

        //녹음 종료와 동시에 RecordViewActivity(디테일창)이 바로 전환되는게 싫다. 이 화면에서 음역대를 먼저 보여주고 원할때 디테일창으로 넘어거가 싶다.
        //그럼 디테일 창 보기 버튼을 하나 만들고 그걸 누르면 디테일창이 뜨도록 하자.
        //RecordingID를 받은 인텐트가 전달되지 않는다.......
        //디테일보기 버튼(DetailButton)을 눌렀을때 팅긴다.....
        //문제점으로 의심되는 부분
        //1.onRecordFinished은 녹음이 종료되면 불리게 되고, 녹음 종료와 동시에 인텐트를 쏴준다. 이 인텐트에 방금 녹음한 레코딩정보들이 들어있다.
        //recordinviewactivity는 녹음된 정보가 없으면 실행 자체가 안된다.
        //fact.디테일 보기를 눌렀을때 팅기는 부분에 로그를 찍어보니 인텐트 자체가 전달이 안되고 있다.
        //fact.레코딩 리스트에서 디테일창으로 넘어가면 녹음된 정보가 저장되어있다.
        //..... 말이 안된다. 녹음 종료후에 인텐트가 전달이 안되는데 왜 저장이 되있는걸까....................


        //새로운 의견이 떠올랐다. 녹음 종료와 동시에 인텐트를 쏴주고, 나중에 버튼을 눌렀을때 액티비티를 시작할때 인텐트가 또 전달된다.
        //이 2개의 인텐트를 recordingViewactivity가 구별을 못하고 없는 값을 전달 한것일까 라는 생각이 든다.
        //로그를 찍어보니 이게 문제점이 맞는듯하다.

        //그렇다면........... 종료와 동시에 저장된 recordingID를 잠시 담아뒀다가 이를 버튼으로 보내주고, 버튼을 눌를때 액티비티 실행과 저장된 정보를 같이 넘겨주면 될려나...






        setRec(recordingID);


//////////
        //난 종료와 동시에 최고 음역대, 최저 음역대를 바로 보여주고싶다.
        //텍스트 뷰 만들어서 그 값을 먼저 보여주자,.

        RecordingDB db = new RecordingDB(this);
        RecordingActivity.currentRecord2 = db.getRecording(recordingID);

        if (RecordingActivity.currentRecord2 != null)
        {


            PitchCalculator pitchCalculator = new PitchCalculator();
            pitchCalculator.setPitches(RecordingActivity.currentRecord2.getRange().getPitches());

            Double minPitch = pitchCalculator.getMin();
            String min_test = HzToScale222(Double.toString(Math.round(minPitch)));
            ((TextView) findViewById(R.id.doremi4)).setText(String.format("%s", min_test));



            Double maxPitch = pitchCalculator.getMax();
            //max_test = ((PitchCal)PitchCal.mContext).HzToScale222(Math.round(RecordViewActivity.currentRecord.getRange().getMin()));
            String max_test = HzToScale222(Double.toString(Math.round(maxPitch)));
            Toast.makeText(this, "최대값 주파수 : " + maxPitch, Toast.LENGTH_SHORT).show();
            ((TextView) findViewById(R.id.doremi2)).setText(String.format("%s", max_test));


////////////

        }
        ((Button) findViewById(R.id.record_button)).setText("결과보기");


    }


    public List<Entry> startingPitchEntries() {
        return getPitchEntries();
    }

    private List<Entry> getPitchEntries() {
        List<Entry> pitchEntries = new LinkedList<>();

        List<Double> pitches = calculator.getPitches();
        for (int i = 0; i < pitches.size(); i++) {
            Double pitchOn = pitches.get(i);
            pitchEntries.add(new Entry(pitchOn.floatValue(), i));
        }

        return pitchEntries;
    }

    private void savePreferences() {
        SharedPreferences sharedPref =
                getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        sharedPref.edit().putInt(getString(R.string.recording_tab_index), tabHost.getCurrentTab()).apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePreferences();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        savePreferences();
    }


    ////// recordingID를 저장하고 디테일 버튼으로 인텐트를 쏴주는 메써드를 만들자.

    public Intent get_finish_intent(){
        return finish_intent;
    }

    public void setRec(long recordingID){
        Log.v("jojo", recordingID + "");
        global_recordingID = recordingID;
        Log.v("jojo2", global_recordingID + "");
    }

    public long getRec(){
        Log.v("jojo", global_recordingID + "");
        return global_recordingID;
    }

    public void startAc(){
        long recordingID = getRec();
        Log.v("jojo", recordingID + "");
        finish_intent = new Intent(this, RecordViewActivity.class);
        finish_intent.putExtra(Recording.KEY, recordingID);
        finish_intent.setFlags(12345);
        startActivity(finish_intent);
    }


}
