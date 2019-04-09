package kr.ac.pknu.sme.myvoiceproject.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.pknu.sme.myvoiceproject.R;
import kr.ac.pknu.sme.myvoiceproject.activities.RecordViewActivity;
import kr.ac.pknu.sme.myvoiceproject.utils.AudioPlayer;
import kr.ac.pknu.sme.myvoiceproject.utils.PitchCalculator;


/**
 * A simple {@link Fragment} subclass.
 */

//class MySheetView extends View {
//    public MySheetView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        setBackgroundColor(Color.WHITE);
//
//    }
//
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        Paint paint = new Paint();
//        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.sheet_muic);
//        canvas.drawBitmap(b, 0, 0, null);
//    }
//}

public class RecordingPlayFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String LOG_TAG = RecordingPlayFragment.class.getSimpleName();
    private AudioPlayer player;


    public RecordingPlayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber
     * @return A new instance of fragment RecordingOverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordingPlayFragment newInstance(int sectionNumber) {
        RecordingPlayFragment fragment = new RecordingPlayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording_play, container, false);
    }


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

    public String HzToScale222(String x) {
        cont = false;

        double freq = 0.0;
        try {
            String s = x;
            if (Double.parseDouble(s) < 10) throw new Exception("invalidNumber");
            freq = Double.parseDouble(s);
            cont = true;
        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
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
        } else if (n == 0) {
            return "C";
        } else {
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (RecordViewActivity.currentRecord != null) {
            double average = RecordViewActivity.currentRecord.getRange().getAvg();
            PitchCalculator pitchCalculator = new PitchCalculator();
            pitchCalculator.setPitches(RecordViewActivity.currentRecord.getRange().getPitches());

            //String avg_test = HzToScale(Math.round(average));
            String avg_test2 = HzToScale222(Double.toString(Math.round(average)));
            //((TextView) view.findViewById(R.id.average)).setText(String.format("%sHz", Math.round(average)));
            ((TextView) view.findViewById(R.id.average)).setText(String.format("%s", avg_test2));

            //String min_avg_test = ((PitchCal)PitchCal.mContext).HzToScale222((int)(Math.round(RecordViewActivity.currentRecord.getRange().getMin())));
            //String min_avg_test = HzToScale(Math.round(RecordViewActivity.currentRecord.getRange().getMin()));
            //String max_avg_test = ((PitchCal)PitchCal.mContext).HzToScale222(((int)Math.round(RecordViewActivity.currentRecord.getRange().getMax())));
            //String max_avg_test = HzToScale(Math.round(RecordViewActivity.currentRecord.getRange().getMax()));


            Double minPitch = pitchCalculator.getMin();
            Double maxPitch = pitchCalculator.getMax();


            ///
            //String max_test = HzToScale(Math.round(maxPitch));
            //String min_test = HzToScale(Math.round(minPitch));


            String min_test2 = HzToScale222(Double.toString(Math.round(minPitch)));
            String max_test2 = HzToScale222(Double.toString(Math.round(maxPitch)));
            //String min_test2 = ((PitchCal) PitchCal.mContext).HzToScale222((int)(Math.round(minPitch)));
            ///
            Log.v("WWWW", "max_test2 확인 : " + max_test2);


            //((TextView) view.findViewById(R.id.pitch_min)).setText(String.format("%s", minPitch != null ? Math.round(minPitch) : "0"));
            ((TextView) view.findViewById(R.id.pitch_min)).setText(String.format("%s", min_test2));
            //((TextView) view.findViewById(R.id.pitch_max)).setText(String.format("%sHz", maxPitch != null ? Math.round(maxPitch) : "0"));
            ((TextView) view.findViewById(R.id.pitch_max)).setText(String.format("%s", max_test2));
            if (average > 0) {
                if (average < PitchCalculator.minFemalePitch) {
                    ((TextView) view.findViewById(R.id.personal_range)).setText(getResources().getString(R.string.male));
                } else if (average > PitchCalculator.maxMalePitch) {
                    ((TextView) view.findViewById(R.id.personal_range)).setText(getResources().getString(R.string.female));
                } else {
                    ((TextView) view.findViewById(R.id.personal_range)).setText(getResources().getString(R.string.in_between));
                }
            } else {
                ((TextView) view.findViewById(R.id.personal_range)).setText(getResources().getString(R.string.unknown));
            }

        }


    }

//    class MySheetView extends View {
//        public MySheetView (Context context) {
//            super(context);
//            setBackgroundColor(Color.WHITE);
//
//        }
//
//        @Override
//        protected void onDraw(Canvas canvas) {
//            Paint paint = new Paint();
//            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.sheet_muic);
//            canvas.drawBitmap(b,0,0,null);
//        }
//    }


//    private Drawable mDrawable;
//    Picture picture;
//    static void drawSheet(Canvas canvas) {
//        Paint p1 = new Paint(Paint.ANTI_ALIAS_FLAG);
//        p1.setColor(Color.BLACK);
//        canvas.drawCircle(50,50,50,p1);
//
//    }


}
