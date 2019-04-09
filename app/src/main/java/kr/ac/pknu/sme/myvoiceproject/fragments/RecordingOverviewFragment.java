package kr.ac.pknu.sme.myvoiceproject.fragments;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import kr.ac.pknu.sme.myvoiceproject.R;
import kr.ac.pknu.sme.myvoiceproject.activities.RecordViewActivity;
import kr.ac.pknu.sme.myvoiceproject.utils.PitchCalculator;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordingOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordingOverviewFragment extends Fragment implements SurfaceHolder.Callback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SECTION_NUMBER = "section_number";
    SurfaceView gradient;
    SurfaceHolder gradientHolder;

    public RecordingOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sectionNumber
     * @return A new instance of fragment RecordingOverviewFragment.
     */
    public static RecordingOverviewFragment newInstance(int sectionNumber) {
        RecordingOverviewFragment fragment = new RecordingOverviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.gradient = (SurfaceView) view.findViewById(R.id.gradient_canvas);
        this.gradientHolder = this.gradient.getHolder();
        this.gradientHolder.addCallback(this);
    }

    public final static float LOG2 = 0.6931472f;

    public static final float ftom(float frequency) {
        return Math.max(0f, (float)Math.log(frequency / 440.0f) / LOG2 * 12f + 69f);
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
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Canvas canvas = surfaceHolder.lockCanvas();
        Double pitchRange = PitchCalculator.maxPitch - PitchCalculator.minPitch;
        Double pxPerHz = this.gradient.getHeight() / pitchRange;

        // calculate relative amount of pixels for same display on all devices
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Paint p = new Paint();
        Paint textPaint = new Paint();

        textPaint.setColor(getResources().getColor(android.R.color.black));

        int labelPxSize = getResources().getDimensionPixelSize(R.dimen.medium_font_size);
        textPaint.setTextSize(labelPxSize);


        p.setColor(getResources().getColor(R.color.canvas_dark));
        p.setAlpha(128);
        textPaint.setColor(getResources().getColor(android.R.color.black));
        textPaint.setAlpha(255);
        canvas.drawARGB(255, 255, 255, 255);
        p.setStrokeWidth(10);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        //draw female pitch
        canvas.drawRect(0,
                this.gradient.getBottom() - (float) ((PitchCalculator.minFemalePitch - PitchCalculator.minPitch) * pxPerHz),
                this.gradient.getWidth(),
                this.gradient.getBottom() - (float) ((PitchCalculator.maxFemalePitch - PitchCalculator.minPitch) * pxPerHz),
                p
        );

        canvas.drawText(getResources().getString(R.string.female_range), this.gradient.getWidth() - 10, this.gradient.getHeight() - (float)
                ((PitchCalculator.maxFemalePitch - PitchCalculator.minPitch) * pxPerHz) + 20 * dm.density, textPaint);
        //draw male pitch
        p.setColor(getResources().getColor(R.color.canvas_light));
        p.setAlpha(128);
        textPaint.setColor(getResources().getColor(android.R.color.black));
        textPaint.setAlpha(255);
        canvas.drawRect(0,
                this.gradient.getHeight() - (float) ((PitchCalculator.minMalePitch - PitchCalculator.minPitch) * pxPerHz),
                this.gradient.getWidth(),
                this.gradient.getHeight() - (float) ((PitchCalculator.maxMalePitch - PitchCalculator.minPitch) * pxPerHz),
                p
        );

        canvas.drawText(getResources().getString(R.string.male_range), this.gradient.getWidth() - 10, this.gradient.getHeight() - (float)
                ((PitchCalculator.maxMalePitch - PitchCalculator.minPitch -
                        (PitchCalculator.maxMalePitch - PitchCalculator.minFemalePitch)) * pxPerHz) + 150 * dm.density, textPaint);


        //draw androgynous label
        canvas.drawText(getResources().getString(R.string.androgynous_range), this.gradient.getWidth() - 10, this.gradient.getHeight() - (float)
                ((PitchCalculator.maxMalePitch - PitchCalculator.minPitch) * pxPerHz) + 22 * dm.density, textPaint);
        //draw pitch labels
        textPaint.setTextSize(labelPxSize);
        textPaint.setTextAlign(Paint.Align.LEFT);
        //min_male
        canvas.drawText(String.valueOf(HzToScale222(Double.toString(PitchCalculator.minMalePitch))), 10, this.gradient.getHeight() - (float)
                ((PitchCalculator.minMalePitch - PitchCalculator.minPitch) * pxPerHz) - 20, textPaint);


        //////////////////////////

        //max_male

        canvas.drawText(String.valueOf(HzToScale222(Double.toString(PitchCalculator.maxMalePitch))), 10, this.gradient.getHeight() - (float)
                ((PitchCalculator.maxMalePitch - PitchCalculator.minPitch) * pxPerHz) - 20, textPaint);



        /////////////////////////////////





        //avg_male
//        canvas.drawText(String.valueOf(HzToScale222(Double.toString(PitchCalculator.minMalePitch + ((PitchCalculator.minFemalePitch - PitchCalculator.minMalePitch) / 2)))), 10, this.gradient.getHeight() - (float)
//                (((PitchCalculator.minMalePitch + (PitchCalculator.minFemalePitch - PitchCalculator.minMalePitch) / 2) - PitchCalculator.minPitch) * pxPerHz) + 10, textPaint);

        //min_female
        canvas.drawText(String.valueOf(HzToScale222(Double.toString(PitchCalculator.minFemalePitch))), 10, this.gradient.getHeight() - (float)
                ((PitchCalculator.minFemalePitch - PitchCalculator.minPitch) * pxPerHz) + 35, textPaint);


        //max_female
        canvas.drawText(String.valueOf(HzToScale222(Double.toString(PitchCalculator.maxFemalePitch))), 10, this.gradient.getHeight() - (float)
                ((PitchCalculator.maxFemalePitch - PitchCalculator.minPitch) * pxPerHz) + 35, textPaint);

        //avg_female
        canvas.drawText(String.valueOf(HzToScale222(Double.toString(PitchCalculator.maxMalePitch + ((PitchCalculator.maxFemalePitch - PitchCalculator.maxMalePitch) / 2)))), 10, this.gradient.getHeight() - (float)
                (((PitchCalculator.maxMalePitch + (PitchCalculator.maxFemalePitch - PitchCalculator.maxMalePitch) / 2) - PitchCalculator.minPitch) * pxPerHz) + 10, textPaint);


        //average
        String avg = HzToScale222(Double.toString(((PitchCalculator.maxMalePitch + PitchCalculator.minFemalePitch) / 2)));
        canvas.drawText(avg,
                10, this.gradient.getHeight() - (float)
                        (((PitchCalculator.maxMalePitch - PitchCalculator.minPitch) -
                                (PitchCalculator.maxMalePitch - PitchCalculator.minFemalePitch) / 2) * pxPerHz) + 10, textPaint);

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(android.R.color.black));
        //draw range
//        paint.setStrokeWidth((float) ((RecordViewActivity.currentRecord.getRange().getMax() - RecordViewActivity.currentRecord.getRange().getMin()) * pxPerHz));
        paint.setAlpha(120);
        canvas.drawRect(75 * dm.density,
                this.gradient.getBottom() - (float) ((RecordViewActivity.currentRecord.getRange().getMin() - PitchCalculator.minPitch) * pxPerHz),
                /*this.gradient.getWidth()*/175 * dm.density,
                this.gradient.getBottom() - (float) ((RecordViewActivity.currentRecord.getRange().getMax() - PitchCalculator.minPitch) * pxPerHz),
                paint);
        paint.setStrokeWidth(10);
        paint.setAlpha(255);
//        paint.setColor(getResources().getColor(android.R.color.white));
        paint.setColor(Color.RED);
        canvas.drawLine(75 * dm.density,
                this.gradient.getHeight() - (float) ((RecordViewActivity.currentRecord.getRange().getAvg() - PitchCalculator.minPitch) * pxPerHz),
                /*this.gradient.getWidth()*/175 * dm.density,
                this.gradient.getHeight() - (float) ((RecordViewActivity.currentRecord.getRange().getAvg() - PitchCalculator.minPitch) * pxPerHz),
                paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(getResources().getColor(android.R.color.black));
        textPaint.setAlpha(120);
        canvas.drawText(getResources().getString(R.string.your_range), /*this.gradient.getWidth() /2*/ 125 * dm.density,
                this.gradient.getHeight() - (float) ((RecordViewActivity.currentRecord.getRange().getMin() - PitchCalculator.minPitch) * pxPerHz) + 20 * dm.density,
                textPaint);

        surfaceHolder.unlockCanvasAndPost(canvas);
        Log.d("foo", String.valueOf(this.gradient.getHeight() - (float) (PitchCalculator.maxFemalePitch * pxPerHz)));
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
